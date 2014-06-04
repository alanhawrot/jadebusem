$(document).ready(function () {
    $(".pages #current").attr('data-current-page-id', 1);
    var first_page = $(".page-changer .change-page[data-page-id='1']")
    first_page.addClass("active")
    first_page.css('font-weight', 'bold')

    var pages = $(".pages .page").length
    if (parseInt(pages) <= 1) {
        $(".pages .page-changer").hide();
    } else {
        $(".pages .page").attr("hidden", "hidden");
        $(".pages .page[data-page-id='1']").removeAttr("hidden");

        function changePage(clickedPage) {
            var change_buttons = $(".page-changer .change-page")
            change_buttons.removeClass("active")
            change_buttons.css('font-weight', '');
            var change_button = $(".page-changer .change-page[data-page-id='" + clickedPage + "']")
            change_button.addClass("active")
            change_button.css('font-weight', 'bold')
            $(".pages #current").attr('data-current-page-id', clickedPage);
            $(".pages .page").hide()
            var clicked_page = $(".pages .page[data-page-id='" + clickedPage + "']")
            clicked_page.show()
            $(".panel-option-link").removeClass("active")
            clicked_page.find(".schedule-link").addClass("active")
            clicked_page.find(".schedule").show()
        }

        $(".page-changer .previous-page").click(function (eventObj) {
            eventObj.preventDefault();
            currentPage = parseInt($(".pages #current").attr('data-current-page-id'))
            currentPage -= 1;
            if (currentPage > 0) {
                changePage(currentPage)
            }
        });

        $(".page-changer .next-page").click(function (eventObj) {
            eventObj.preventDefault();
            currentPage = parseInt($(".pages #current").attr('data-current-page-id'))
            currentPage += 1;
            if (currentPage <= pages) {
                changePage(currentPage)
            }
        });

        $(".page-changer .change-page").click(function (eventObj) {
            eventObj.preventDefault();
            $(".pages .page-changer .change-page").css('font-weight', '');
            clickedPage = $(this).css('font-weight', 'bold').attr('data-page-id')
            changePage(clickedPage)
        });
    }
    $(".schedule-link").click(function (eventObj) {
        eventObj.preventDefault();
        trace = $(this).parents(".transfer")

        schedule_id = $(this).attr('data-schedule-id');

        trace_id = $(this).parents(".page").attr("data-page-id");
        schedule = trace.find(".schedule")
        $(".panel-option-link").removeClass("active")
        schedule.addClass("active")
        trace.find(".panel-option").hide()
        schedule.show()
    });

    $(".map-link").click(function (eventObj) {
        eventObj.preventDefault();
        trace = $(this).parents(".transfer")

        schedule_id = $(this).attr('data-schedule-id');

        trace_id = $(this).parents(".page").attr("data-page-id");
        map = $(document).find(".map")
        map.appendTo(trace.find(".map-container"));
        $(".panel-option-link").removeClass("active")
        map.addClass("active")
        trace.find(".panel-option").hide()
        map.show()
    });


    $(".details-link").click(function (eventObj) {
        eventObj.preventDefault();
        trace = $(this).parents(".transfer")

        schedule_id = $(this).attr('data-schedule-id');

        trace_id = $(this).parents(".page").attr("data-page-id");
        details = trace.find(".details")
        $(".panel-option-link").removeClass("active")
        details.addClass("active")
        trace.find(".panel-option").hide()
        details.show()
    });

    $(".trace-points-link").click(function (eventObj) {
        eventObj.preventDefault();
        trace = $(this).parents(".transfer")

        schedule_id = $(this).attr('data-schedule-id');

        trace_id = $(this).parents(".page").attr("data-page-id");
        trace_points = trace.find(".trace-points")
        $(".panel-option-link").removeClass("active")
        trace_points.addClass("active")
        trace.find(".panel-option").hide()
        trace_points.show()
    });
    first_page = $(".pages .page[data-page-id='1']")
    first_page.find(".schedule-link").addClass("active")
    first_page.find(".schedule").show()
});