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
}

function calcRoute(id) {
    var start = document.getElementById('start').value.trim();
    var end = document.getElementById('end').value.trim();
    var waypts = [];
    var route = document.getElementById('route' + id).value;
    var waypoints = route.split("->");
    for (var i = 1; i < waypoints.length - 1; i++) {
        waypoints[i] = waypoints[i].trim();
        if (waypoints[i] == end) {
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
        var routeDetailsArray = document.getElementsByClassName('routeDetails');
        for (var i = 0; i < mapErrorArray.length; i++) {
            mapErrorArray[i].innerHTML = '';
            routeDetailsArray[i].innerHTML = '';
        }
        if (status == google.maps.DirectionsStatus.OK) {
            directionsDisplay.setDirections(response);
            var route = response.routes[0];
            var summaryPanel = document.getElementById('routeDetails' + id);
            summaryPanel.innerHTML = '';
            // For each route, display summary information.
            for (var i = 0; i < route.legs.length; i++) {
                var routeSegment = i + 1;
//                summaryPanel.innerHTML += '<b>' + gettext('Route Segment: ') + routeSegment + '</b><br>';
//                summaryPanel.innerHTML += gettext('From ') + route.legs[i].start_address + gettext(' to ');
//                summaryPanel.innerHTML += route.legs[i].end_address + '<br>';
                $(".trace[data-page-id='" + (parseInt(id) + 1) + "'] .tracePoint[id='" + i + "'] .duration a").html(route.legs[i].duration.text);
                $(".trace[data-page-id='" + (parseInt(id) + 1) + "'] .tracePoint[id='" + i + "'] .distance a").html(route.legs[i].distance.text);
//                summaryPanel.innerHTML += gettext('Estimated duration: ') + route.legs[i].duration.text + '<br>';
//                summaryPanel.innerHTML += gettext('Distance: ') + route.legs[i].distance.text + '<br><br>';
            }
        } else {
            document.getElementById('mapError' + id).innerHTML += 'Error in displaying route on map';
        }
    });
}

google.maps.event.addDomListener(window, 'load', initialize);
