$(document).ready(function () {

    $.fn.pressEnter = function (fn) {
        return this.each(function () {
            $(this).bind('enterPress', fn);
            $(this).keypress(function (e) {
                if (e.keyCode == 13 || e.which == 13) {
                    e.preventDefault()
                    $(this).trigger('enterPress');
                } else {
                }
            })
        });
    };


    function scheduleToJson() {
        var schedule = new Object();
        schedule['company_name'] = $("input[name='company-name']").val();
        schedule['trace_points'] = [];
        schedule['days'] = [];

        for (var i = 0; i < 7; i++) {
            schedule['days'][i] = [];
            $(".days .day[data-day-id=" + i + "] .departure").each(
                function () {
                    if (!$(this).hasClass('hidden')) {
                        var departure = new Object();
                        departure['hour'] = $(this).find("input[name='hour']").val();

                        if (departure['hour'])
                            schedule['days'][i].push(departure);
                    }
                });
        }

        $(".trace-points .trace-point").each(
            function (index) {
                if (!$(this).hasClass('hidden')) {
                    var tracePoint = new Object();
                    tracePoint['address'] = $(this).find("input[name='address']").val();
                    tracePoint['id'] = index
                    if (tracePoint['address'])
                        schedule['trace_points'].push(tracePoint);
                }
            }
        );
        console.log(schedule)
        return JSON.stringify(schedule);
    }

    function sendSchedule() {

        var schedule = scheduleToJson();
        var request = $.ajax({
            type: "POST",
            url: "",
            data: schedule,
            dataType: "json",
            success: function (response) {
                if (response['redirect']) {
                    window.location.pathname = response['redirect']
                    window.location.href.reload()
                }
            }
        });


        request.done(function (response, textStatus, jqXHR) {
            var success_block = $(".response-success")
            var error_block = $(".response-errors")

            if (response['success']) {
                success_block.text(response['message'])

                if (!error_block.hasClass('hidden'))
                    error_block.addClass('hidden')

                if (success_block.hasClass('hidden'))
                    success_block.removeClass('hidden')

            } else {

                var success_block = $(".response-success")
                var error_block = $(".response-errors")
                var error_elements = error_block.find(".response-error")
                var error_element = error_elements.first().detach()
                error_elements.remove()
                error_block.empty()
                for (var key in response['errors']) {
                    var new_error_element = error_element.clone()
                    new_error_element.find(".error-label").text(key);
                    new_error_element.find(".error-message").text(response['errors'][key]);
                    error_block.append(new_error_element)
                }

                if (error_block.hasClass('hidden'))
                    error_block.removeClass('hidden')

                if (!success_block.hasClass('hidden'))
                    success_block.addClass('hidden')
            }

        });


        request.fail(function (jqXHR, textStatus, errorThrown) {
            var success_block = $(".response-success")
            var error_block = $(".response-errors")
            var error_element = error_block.find(".response-error").first()

            error_element.detach()
            error_block.empty()

            error_element.find(".error-label").text(textStatus);
            error_element.find(".error-message").text(errorThrown);
            error_block.append(error_element)

            if (error_block.hasClass('hidden'))
                error_block.removeClass('hidden')

            if (!success_block.hasClass('hidden'))
                success_block.addClass('hidden')
        });

        request.always(function () {
        });

    }

    function recalculateTracePointsIds() {
        $(".trace-points .trace-point-number").each(function (index) {
            $(this).text(index)
        })

    }

    function removeTracePointClickHandler(event) {
        event.preventDefault();
        $(this).closest('.trace-point').remove()
        recalculateTracePointsIds()
    }

    $("input[name='add-trace-point']").click(function (event) {
        event.preventDefault();
        var trace_points = $(".trace-points")
        var element = trace_points.find(".trace-point").first().clone().appendTo(trace_points)
        element.pressEnter(sendSchedule)
        element.find("input[name='remove-trace-point']").click(removeTracePointClickHandler)
        if (element.hasClass('hidden'))
            element.removeClass('hidden')
        recalculateTracePointsIds()
    });

    $("input[name='remove-trace-point']").click(removeTracePointClickHandler);
    function removeHourClickHandler(event) {
        event.preventDefault();
        var departure = $(this).closest(".departure")
        departure.remove()
    }

    $("input[name='add-hour']").click(function (event) {
        event.preventDefault();
        var day = $(this).closest(".day")
        var element = day.find(".departure").first().clone().appendTo(day.find(".departures"))
        element.pressEnter(sendSchedule)
        element.find("input[name='remove-hour']").click(removeHourClickHandler);
        if (element.hasClass('hidden'))
            element.removeClass('hidden')
    });

    $("input[name='remove-hour']").click(removeHourClickHandler);


    $("input").pressEnter(function (event) {
        event.preventDefault();
        sendSchedule()

    });

    $("input[name='submit-schedule']").click(function (event) {
        event.preventDefault();
        sendSchedule()
    });

});