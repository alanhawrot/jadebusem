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
        $(this).parent().remove()
    }

    function addHourClickEvent(){
        day = $(this).parent().parent();
        dayIndex = day.attr('id');
        $(this).parent().find('#remove').show();
        $(this).parent().find('#add').hide();
        day.append(
                '<div class="hour">' +
                '<input type="text" class="smallMarginTR textInput scheduleHour" id="hourInput" name="hour['+dayIndex+']" placeholder="8:00"></input>' +
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

});