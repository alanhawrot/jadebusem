//
//  ScheduleTraceMapViewController.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 29.11.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit
import CoreData

class ScheduleTraceMapViewController: UIViewController {
    
    @IBOutlet weak var scheduleName: UILabel!
    @IBOutlet weak var map: GMSMapView!
    
    var schedule: NSManagedObject!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        scheduleName.text = schedule.valueForKey("name") as String!
        map.myLocationEnabled = true
        drawRoute()
    }
    
    func drawRoute() {
        let key = (UIApplication.sharedApplication().delegate as AppDelegate).googleMapsApiKey
        
        let tracepoints = schedule.valueForKey("tracepoints") as NSOrderedSet
        let tracepointsArray = tracepoints.array
        
        let origin = (tracepointsArray[0] as ScheduleTracepoint).name
        let destination = (tracepointsArray[tracepointsArray.count - 1] as ScheduleTracepoint).name
        
        let region = "pl"
        
        var url = "https://maps.googleapis.com/maps/api/directions/json?key=\(key)&origin=\(origin)&destination=\(destination)&region=\(region)"
        
        var waypointsBuilder = NSMutableString(string: "")
        
        if tracepointsArray.count > 2 {
            for i in 1...tracepointsArray.count - 2 {
                let waypoint = (tracepointsArray[i] as ScheduleTracepoint).name
                waypointsBuilder.appendString(waypoint + "|")
            }
            
            let waypoints = waypointsBuilder.substringToIndex(waypointsBuilder.length - 1)
            
            url = url + "&waypoints=\(waypoints)"
        }
        
        let urlEncoded = url.stringByAddingPercentEscapesUsingEncoding(NSUTF8StringEncoding)
        
        dispatch_async(dispatch_get_main_queue(), { () -> Void in
            UIApplication.sharedApplication().networkActivityIndicatorVisible = true
            if let googleURL = NSURL(string: urlEncoded!) {
                if let responseData = NSData(contentsOfURL: googleURL) {
                    var error: NSError?
                    let jsonData = NSJSONSerialization.JSONObjectWithData(responseData, options: nil, error: &error) as NSDictionary
                    
                    let status = jsonData.valueForKey("status") as String
                    
                    if status == "OK" {
                        let routes = jsonData.valueForKey("routes") as NSArray
                        let firstRoute = routes.objectAtIndex(0) as NSDictionary
                        let route = firstRoute.valueForKey("overview_polyline") as NSDictionary
                        let overview_route = route.valueForKey("points") as String
                        let path = GMSPath(fromEncodedPath: overview_route)
                        let polyline = GMSPolyline(path: path)
                        polyline.map = self.map
                        
                        let coordinateBounds = GMSCoordinateBounds(path: path)
                        let mapCameraBoundsUpdate = GMSCameraUpdate.fitBounds(coordinateBounds)
                        
                        self.map.animateWithCameraUpdate(mapCameraBoundsUpdate)
                    }
                } else {
                    self.showError()
                }
            } else {
                self.showError()
            }
            UIApplication.sharedApplication().networkActivityIndicatorVisible = false
        })
    }
    
    func showError() {
        let alert = UIAlertView(title: NSLocalizedString("ERROR", comment: "Error alert title"), message: NSLocalizedString("DRAW_ROUTE_MAP", comment: "Error alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
        alert.show()
    }
    
}
