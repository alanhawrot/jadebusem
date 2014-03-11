$(document).ready(function () {
    $(".pages #current").attr('data-current-page-id', 1);
    $(".pages .page-changer .change-page[data-page-id='1']").css('font-weight', 'bold')
    var pages = $(".pages .page").length
    if (parseInt(pages) <= 1) {
        $(".pages .page-changer").hide();
    } else {
        $(".pages .page").attr("hidden", "hidden");
        $(".pages .page[data-page-id='1']").removeAttr("hidden");

        function changePage(clickedPage) {
            $(".pages .page-changer .change-page").css('font-weight', '');
            $(".pages .page-changer .change-page[data-page-id='" + clickedPage + "']").css('font-weight', 'bold')
            $(".pages #current").attr('data-current-page-id', clickedPage);
            $(".pages .page").attr("hidden", "hidden")
            $(".pages .page[data-page-id='" + clickedPage + "']").removeAttr("hidden");
        }

        $(".pages .page-changer .previous-page").click(function (eventObj) {
            eventObj.preventDefault();
            currentPage = parseInt($(".pages #current").attr('data-current-page-id'))
            currentPage -= 1;
            if (currentPage > 0) {
                changePage(currentPage)
            }
        });

        $(".pages .page-changer .next-page").click(function (eventObj) {
            eventObj.preventDefault();
            currentPage = parseInt($(".pages #current").attr('data-current-page-id'))
            currentPage += 1;
            if (currentPage <= pages) {
                changePage(currentPage)
            }
        });

        $(".pages .page-changer .change-page").click(function (eventObj) {
            eventObj.preventDefault();
            $(".pages .page-changer .change-page").css('font-weight', '');
            clickedPage = $(this).css('font-weight', 'bold').attr('data-page-id')
            changePage(clickedPage)
        });
    }
    $(".schedule-link").click(function (eventObj) {
        eventObj.preventDefault();
        trace = $(this).parents(".trace")

        schedule_id = $(this).attr('data-schedule-id');

        trace_id = $(this).parents(".trace").attr("data-page-id");
        schedule = $(".pages .trace[data-page-id='" + trace_id + "']" + " .schedule[data-schedule-id='" + schedule_id + "']")
        if (schedule.is('[hidden]')) {
            $(".pages .trace .schedule").attr('hidden', 'hidden');
            schedule.removeAttr('hidden')
        } else {
            $(".pages .trace .schedule").attr('hidden', 'hidden');
        }
    });

});