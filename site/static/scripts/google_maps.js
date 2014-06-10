var directionsDisplay;
var directionsService = new google.maps.DirectionsService();
var map;

function initialize() {
    directionsDisplay = new google.maps.DirectionsRenderer();
    var cracow = new google.maps.LatLng(50.06465009999999, 19.94497990000002);
    var mapOptions = {
        zoom: 8,
        center: cracow
    }
    map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
    directionsDisplay.setMap(map);
    return map
}

function calcRoute(id) {
    var waypts = [];
    var route = document.getElementById('route' + id).value;
    var waypoints = route.split("->");
    var start;
    var end;

    start = waypoints[0].trim();
    end = waypoints[waypoints.length - 1].trim();
    for (var i = 1; i < waypoints.length - 1; i++) {
        waypoints[i] = waypoints[i].trim();
        if (waypoints[i].toLocaleLowerCase() == end.toLocaleLowerCase()) {
            break;
        } else {
            waypts.push({
                location: waypoints[i],
                stopover: true
            });
        }
    }

    var request = {
        origin: start,
        destination: end,
        waypoints: waypts,
        optimizeWaypoints: false,
        provideRouteAlternatives: false,
        avoidHighways: false,
        avoidTolls: false,
        region: "pl",
        unitSystem: google.maps.UnitSystem.METRIC,
        travelMode: google.maps.TravelMode.DRIVING
    };

    directionsService.route(request, function (response, status) {
        var mapErrorArray = document.getElementsByClassName('mapError');
//        var routeDetailsArray = document.getElementsByClassName('routeDetails');
//        for (var i = 0; i < mapErrorArray.length; i++) {
//            mapErrorArray[i].innerHTML = '';
//            //routeDetailsArray[i].innerHTML = '';
//        }
        if (status == google.maps.DirectionsStatus.OK) {
            directionsDisplay.setDirections(response);
            var route = response.routes[0];
            //var summaryPanel = document.getElementById('routeDetails' + id);
            //$('#routeDetails' + id).hide();
            //summaryPanel.innerHTML = '<br><b>' + gettext('Route details:') + '</b><br><br>';
            //for (var i = 0; i < route.legs.length; i++) {
            //    summaryPanel.innerHTML += gettext('Route segment: ') + (i + 1) + '.<br>';
            //    summaryPanel.innerHTML += route.legs[i].start_address + ' -> ';
            //    summaryPanel.innerHTML += route.legs[i].end_address + '<br>';
            //    summaryPanel.innerHTML += gettext('Duration: ') + route.legs[i].duration.text + '<br>';
            //    summaryPanel.innerHTML += gettext('Distance: ') + route.legs[i].distance.text + '<br><br>';
            //}
            //$('#routeDetails' + id).show();
            var routeDetails = $("#routeDetails" + parseInt(id));
            var routeFragment = routeDetails.find(".fragment").first()
            if (route.legs.length > 0) {
                routeFragment.detach()
                routeDetails.empty()
            }
            for (var i = 0; i < route.legs.length; i++) {
                var newRouteFragment = routeFragment.clone();
                newRouteFragment.find(".number").html(i + 1);
                newRouteFragment.find(".start").html(route.legs[i].start_address);
                newRouteFragment.find(".end").html(route.legs[i].end_address);
                newRouteFragment.find(".duration").html(route.legs[i].duration.text);
                newRouteFragment.find(".distance").html(route.legs[i].distance.text);
                routeDetails.append(newRouteFragment);
                newRouteFragment.show()
            }

        } else {
            document.getElementById('mapError' + id).innerHTML += gettext('Error in displaying route on map');
        }
    });

    //var bounds = map.getBounds();
    //map.fitBounds(bounds);
}

$(document).ready(function () {
    var map = initialize();
    $(".map").hide()
    $(".map-link").click(function (event) {
        $(".map").show()
        calcRoute(parseInt($(".pages #current").attr('data-current-page-id')) - 1);
        google.maps.event.trigger(map, 'resize');
        map.setZoom(map.getZoom());
    });

    $(".details-link").click(function (event) {
        calcRoute(parseInt($(".pages #current").attr('data-current-page-id')) - 1);
    });
});