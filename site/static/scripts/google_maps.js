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
        var routeDetailsArray = document.getElementsByClassName('routeDetails');
        for (var i = 0; i < mapErrorArray.length; i++) {
            mapErrorArray[i].innerHTML = '';
            routeDetailsArray[i].innerHTML = '';
        }
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
            var summaryPanel =  $("#routeDetails"+ id);
            for (var i = 0; i < route.legs.length; i++) {

                summaryPanel.find(".number").val(i + 1);
                summaryPanel.find(".start").val(route.legs[i].start_address);
                summaryPanel.find(".end").val(route.legs[i].end_address);
                summaryPanel.find(".duration").val(route.legs[i].duration.text);
                summaryPanel.find(".distance").val(route.legs[i].distance.text);
            }

        } else {
            document.getElementById('mapError' + id).innerHTML += gettext('Error in displaying route on map');
        }
    });

    var bounds = map.getBounds();
    map.fitBounds(bounds);
}

$(document).ready(function () {
    initialize();
    $(".map-link").click(function(event){
        calcRoute(parseInt($(".pages #current").attr('data-current-page-id')) - 1);
    });
});