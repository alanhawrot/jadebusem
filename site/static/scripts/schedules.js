var SCHEDULE_ELEMENT =
    '<div class="hour">' +
        '<input type="text" class="smallMarginTR textInput scheduleHour" id="hourInput" name="" value="" placeholder="8:00"></input>' +
        '<input id="add" class="smallMarginTR submitInput scheduleHourNavigator" type="button" value="+"></input>' +
        '<input id="remove" class="smallMarginTR submitInput scheduleHourNavigator" type="button" value="-"></input>' +
    '</div>';

var TRACE_POINT_ELEMENT = '<input name="" class="hiddenTracePoint" value="" hidden="hidden"/>' +
                          '<span class="viewableTracePoint"></span>';

var SCHEDULE_SUCCESS = '<center><label style="color: green">{% trans "Schedule successfuly updated." %}</label></center>'
var SCHEDULE_ERROR = '<tr>' +
                         '<td class="error-label"><label style="color: red;"> error:</label></td>' +
                         '<td class="error-message"></td>' +
                      '</tr>'

$(document).ready(function(){

    $.fn.pressEnter = function(fn) {
        return this.each(function() {
            $(this).bind('enterPress', fn);
            $(this).keypress(function(e){
                if(e.keyCode == 13 || e.which == 13)
                {
                  e.preventDefault()
                  $(this).trigger('enterPress');
                } else {
                }
            })
        });
        };

    function removeHourClickEvent(){
        $(this).parent(".hour").attr("hidden", "hidden")
    }

    function addHourClickEvent(){
        day = $(this).parent().parent();
        dayIndex = day.attr('id');
        $(this).parent().find('#remove').show();
        $(this).parent().find('#add').hide();
        day.append(
                '<div class="hour">' +
                '<input type="text" class="smallMarginTR textInput scheduleHour" id="hourInput" data-id="" name="hour['+dayIndex+']" placeholder="8:00"></input>' +
                '<input id="add" class="smallMarginTR submitInput scheduleHourNavigator" type="button" value="+"></input>' +
                '<input id="remove" class="smallMarginTR submitInput scheduleHourNavigator" type="button" value="-"></input>' +
                '</div>');
        new_element = day.children().last();
        new_element.find('#add').click(addHourClickEvent).show();
        new_element.find('#hourInput').pressEnter(addHourClickEvent).focus().show();
        new_element.find('#remove').click(removeHourClickEvent).hide();
        new_element.show();
    }

    $(".days .day .hour #add").click(addHourClickEvent);
    $(".days .day .hour #hourInput").pressEnter(addHourClickEvent);
    $(".days .day .hour #remove").click(removeHourClickEvent);


    function newTracePoint() {
        tracepointsInput = $('#tracePointsControl #tracePointInput');
        tracePoints = $('#tracePoints');
        viewableTracepoints = $('#tracePoints .viewableTracePoint');
        if(tracepointsInput.val() != ''){
            if(viewableTracepoints.length > 1) {
                tracePoints.append('<span> - </span>');
            }
            newHiddenTracePoint = '<input name="tracePoint" class="hiddenTracePoint" value="' +
                                  tracepointsInput.val() + '" hidden="hidden"/>'
            tracePoints.append(newHiddenTracePoint)
            tracePoints.append('<span class="viewableTracePoint">' + tracepointsInput.val() + '</span>');

            console.log(tracepointsInput.val());
            tracepointsInput.removeAttr('value');
            tracepointsInput.val('');
            $('#tracePointsControl #tracePointInput').val('');
            $('#tracePointsControl #tracePointInput').removeAttr('value');
            $('#tracePointsControl #tracePointInput').html('')

        }

        console.log(tracepointsInput.val());
        console.log(tracepointsInput.html());

        $('#tracePointsControl #tracePointInput').focus()
    }

    $("#tracePointsControl #addTracePoint").click(newTracePoint);
    $("#tracePointsControl #tracePointInput").pressEnter(newTracePoint);

    function scheduleToJson() {
        var schedule = new Object();
        schedule['company_name'] = $("input[name=companyName]").val();
        schedule['trace_points'] = [];
        schedule['days'] = [];

        for(var i=0;i<7;i++) {
            schedule['days'][i] = [];
            $(".days .day[data-id="+i+"] .scheduleHour").each(
                function(){
                    var departure = new Object();
                    departure['hour'] = $(this).val();
                    departure['id'] = $(this).attr('data-id');
                    if($(this).parent(".hour").attr('hidden')) {
                        departure['remove'] = true;
                    }
                    if(departure['hour']){
                        schedule['days'][i].push(departure);
                    }
            });
        }

        $("#tracePoints input").each(
            function () {
                var tracePoint = new Object();
                tracePoint['address'] = $(this).val();
                tracePoint['id'] = $(this).attr('data-id');
                schedule['trace_points'].push(tracePoint);
            }
        );
        return JSON.stringify(schedule);
    }

    $("#submitSchedule").click(function(event){
        event.preventDefault();
        var schedule = scheduleToJson();
        console.log(schedule);
        var request = $.ajax({
            type: "POST",
            url: "",
            data: schedule,
            dataType: "json"
        });

    request.done(function (response, textStatus, jqXHR){
        console.log(response)
        if(response['success']) {
            $("#response-info").html($(SCHEDULE_SUCCESS))

        } else {
            $("#response-info").html($("<table></table>"));
            for (var key in response['errors']) {
                var element = $(SCHEDULE_ERROR);
                element.find(".error-label label").text(key + " error: ");
                element.find(".error-message").text(response['errors'][key]);
                $("#response-info table").append(element)
            }
        }

    });

    request.fail(function (jqXHR, textStatus, errorThrown){
        var element = $("The following error occurred: " + textStatus+ " " + errorThrown)
        $("#response-info").html(element)
    });

    request.always(function () {
    });

    });

});