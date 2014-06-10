function initialize_panels(pageNumber) {
    $(".panel-option-link").removeClass("active")
    $(".panel-option").hide()

    var page = $(".pages .page[data-page-id=" + pageNumber + "]")
    $(document).find(".full-route-link").addClass("active")
    $(document).find(".full-route").show()
    page.find(".schedule-link").addClass("active")
    page.find(".schedule").show()
}

function changePage(clickedPageNumber) {
    var changeButtons = $(".page-changer .change-page")
    changeButtons.removeClass("active")
    changeButtons.css('font-weight', '');
    var changeButton = $(".page-changer .change-page[data-page-id='" + clickedPageNumber + "']")
    changeButton.addClass("active")
    changeButton.css('font-weight', 'bold')
    $(".pages #current").attr('data-current-page-id', clickedPageNumber);
    $(".pages .page").hide()
    var clickedPage = $(".pages .page[data-page-id='" + clickedPageNumber + "']")
    clickedPage.show()
    initialize_panels(clickedPageNumber)
}

$(document).ready(function () {

    $(".pages #current").attr('data-current-page-id', 1);
    var page = $(".page-changer .change-page[data-page-id='1']")
    page.addClass("active")
    page.css('font-weight', 'bold')

    var pages = $(".pages .page").length
    if (parseInt(pages) <= 1) {
        $(".pages .page-changer").hide();
    } else {
        $(".pages .page").attr("hidden", "hidden");
        $(".pages .page[data-page-id='1']").removeAttr("hidden");


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
        var panel = $(this).parents(".panel")
        var schedule = panel.find(".schedule")
        panel.find(".panel-option-link").removeClass("active")
        $(this).addClass("active")
        panel.find(".panel-option").hide()
        schedule.show()
    });

    $(".map-link").click(function (eventObj) {
        eventObj.preventDefault();
        var panel = $(this).parents(".panel")
        var map = $(document).find(".map")
        map.appendTo(panel.find(".map-container"));
        panel.find(".panel-option-link").removeClass("active")
        panel.find(".panel-option").hide()
        $(this).addClass("active")
        map.show()
    });


    $(".details-link").click(function (eventObj) {
        eventObj.preventDefault();
        console.log("klik details")
        var panel = $(this).parents(".panel")
        var details = panel.find(".details")
        panel.find(".panel-option-link").removeClass("active")
        panel.find(".panel-option").hide()
        $(this).addClass("active")
        details.show()
    });

    $(".full-route-link").click(function (eventObj) {
        eventObj.preventDefault();
        var panel = $(this).parents(".panel")
        var full_route = panel.find(".full-route")
        panel.find(".panel-option-link").removeClass("active")
        $(this).addClass("active")
        panel.find(".panel-option").hide()
        full_route.show()
    });

    $(".trace-points-link").click(function (eventObj) {
        eventObj.preventDefault();
        var panel = $(this).parents(".panel")
        var trace_points = panel.find(".trace-points")
        panel.find(".panel-option-link").removeClass("active")
        $(this).addClass("active")
        panel.find(".panel-option").hide()
        trace_points.show()
    });
    initialize_panels(1);

});