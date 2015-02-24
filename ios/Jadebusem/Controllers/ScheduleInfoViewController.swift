//
//  ScheduleInfoViewController.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 29.11.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit
import CoreData
import MessageUI

class ScheduleInfoViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, MFMessageComposeViewControllerDelegate {

    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var tracepointsTableView: UITableView!
    @IBOutlet weak var daysSegmentedControl: UISegmentedControl!
    @IBOutlet weak var departureTimesTableView: UITableView!
    
    var schedule: NSManagedObject!
    var username: String!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        tracepointsTableView.delegate = self
        tracepointsTableView.dataSource = self
        
        departureTimesTableView.delegate = self
        departureTimesTableView.dataSource = self
        
        nameLabel.text = schedule.valueForKey("name") as? String
    }

    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        daysSegmentedControl.selectedSegmentIndex = 0
    }
    
    @IBAction func dayChanged(sender: UISegmentedControl) {
        departureTimesTableView.reloadData()
    }
    
    @IBAction func uploadSchedule(sender: UIButton) {
        if username == "" {
            let alert = UIAlertView(title: NSLocalizedString("FAILURE", comment: "Failure alert title"), message: NSLocalizedString("SIGNED_USERS_UPLOAD", comment: "Failure alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
            alert.show()
            return
        }
        
        let scheduleType = schedule.valueForKey("type") as NSNumber
        
        if ScheduleTypes(rawValue: scheduleType.shortValue) == ScheduleTypes.LOCAL {
            upload()
        } else {
            let alert = UIAlertView(title: NSLocalizedString("FAILURE", comment: "Failure alert title"), message: NSLocalizedString("EXTERNAL_SCHEDULE_UPLOAD", comment: "Failure alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
            alert.show()
        }
    }
    
    @IBAction func removeSchedule(sender: UIButton) {
        if username == "" {
            let alert = UIAlertView(title: NSLocalizedString("FAILURE", comment: "Failure alert title"), message: NSLocalizedString("SIGNED_USERS_REMOVE", comment: "Failure alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
            alert.show()
            return
        }
        
        let scheduleType = schedule.valueForKey("type") as NSNumber
        
        if ScheduleTypes(rawValue: scheduleType.shortValue) == ScheduleTypes.LOCAL {
            let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
            let managedContext = appDelegate.managedObjectContext!
            
            managedContext.deleteObject(schedule)
            
            var error : NSError?
            managedContext.save(&error)
            
            if error == nil {
                self.navigationController?.popViewControllerAnimated(true)
            }
        } else {
            let alert = UIAlertView(title: NSLocalizedString("ERROR", comment: "Error alert title"), message: NSLocalizedString("EXTERNAL_SCHEDULE_REMOVE_ERROR", comment: "Error alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
            alert.show()
        }

    }
    
    @IBAction func sendSMS(sender: UIButton) {
        if !MFMessageComposeViewController.canSendText() {
            let alert = UIAlertView(title: NSLocalizedString("ERROR", comment: "Error alert title"), message: NSLocalizedString("SMS_SUPPORT", comment: "Error alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
            alert.show()
            return
        }
        
        let message = MFMessageComposeViewController()
        message.messageComposeDelegate = self
        var body = schedule.valueForKey("name") as String + "\nTracepoints: \n"
        var index = 1
        for tracepoint in (schedule.valueForKey("tracepoints") as NSOrderedSet).array {
            body = body + String(index) + ". " + (tracepoint.valueForKey("name") as String) + "\n"
            index += 1
        }
        for day in 0...6 {
            switch (day) {
            case 0:
                body = body + "Monday:\n"
                break
            case 1:
                body = body + "Tuesday:\n"
                break
            case 2:
                body = body + "Wednesday:\n"
                break
            case 3:
                body = body + "Thursday:\n"
                break
            case 4:
                body = body + "Friday:\n"
                break
            case 5:
                body = body + "Saturday:\n"
                break
            case 6:
                body = body + "Sunday:\n"
                break
            default:
                break
            }
            index = 1
            for departureTime in (schedule.valueForKey("departureTimes") as NSOrderedSet).array {
                if ((departureTime as ScheduleDepartureTime).valueForKey("day") as NSNumber) == day {
                    body = body + String(index) + ". " + (departureTime.valueForKey("time") as String) + "\n"
                    index += 1
                }
            }
        }
        let bodyNSString = NSString(string: body)
        message.body = bodyNSString.substringToIndex(bodyNSString.length - 2)
        self.presentViewController(message, animated: true, completion: nil)
    }
    
    func messageComposeViewController(controller: MFMessageComposeViewController!, didFinishWithResult result: MessageComposeResult) {
        let alert = UIAlertView(title: "SMS", message: "", delegate: self, cancelButtonTitle: "OK")
        if result.value == MessageComposeResultCancelled.value {
            alert.message = NSLocalizedString("SMS_CANCELED", comment: "SMS alert message")
        }
        if result.value == MessageComposeResultFailed.value {
            alert.message = NSLocalizedString("SMS_FAILED", comment: "SMS alert message")
        }
        if result.value == MessageComposeResultSent.value {
            alert.message = NSLocalizedString("SMS_SENT", comment: "SMS alert message")
        }
        alert.show()
        
        self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    func upload() {
        let urlPath = "http://jadebusem1.herokuapp.com/schedules/schedule/"
        let url = NSURL(string: urlPath)
        let request = NSMutableURLRequest(URL: url!)
        request.HTTPMethod = "POST"
        
        var root = NSMutableDictionary()
        root.setValue(nil, forKey: "schedule_id")
        root.setObject(username, forKey: "email")
        root.setObject(schedule.valueForKey("name") as String, forKey: "company_name")
        
        var tracepoints = NSMutableArray()
        var index = 1
        
        for tracepoint in (schedule.valueForKey("tracepoints") as NSOrderedSet).array {
            var trace_point = NSMutableDictionary()
            trace_point.setObject((tracepoint as ScheduleTracepoint).name, forKey: "address")
            trace_point.setObject(index, forKey: "id")
            
            tracepoints.addObject(trace_point)
            
            index += 1
        }
        
        root.setValue(tracepoints, forKey: "trace_points")
        
        var days = NSMutableArray()
        let scheduleDepartureTimes = schedule.valueForKey("departureTimes") as NSOrderedSet
        
        for i in 0...6 {
            var localDays = NSMutableArray()
            
            for departureTime in scheduleDepartureTimes.array {
                if (departureTime as ScheduleDepartureTime).day == i {
                    var hour = NSMutableDictionary()
                    hour.setObject((departureTime as ScheduleDepartureTime).time, forKey: "hour")
                    localDays.addObject(hour)
                }
            }
            
            days.addObject(localDays)
        }
        
        root.setValue(days, forKey: "days")
        
        var jsonError: NSError?
        
        let requestData = NSJSONSerialization.dataWithJSONObject(root, options: NSJSONWritingOptions.PrettyPrinted, error: &jsonError)
        
        request.HTTPBody = requestData
        
        let queue = NSOperationQueue.mainQueue()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
        NSURLConnection.sendAsynchronousRequest(request, queue: queue) { (response, data, error) -> Void in
            let alert = UIAlertView(title: "", message: "", delegate: self, cancelButtonTitle: "OK")
            
            if error == nil {
                alert.title = NSLocalizedString("SUCCESS", comment: "Success alert title")
                alert.message = NSLocalizedString("SCHEDULE_UPLOADED", comment: "Success alert message")
            } else {
                alert.title = NSLocalizedString("FAILURE", comment: "Failure alert title")
                alert.message = NSLocalizedString("CHECK_CONNECTION_TRY_AGAIN", comment: "Failure alert message")
            }
            
            UIApplication.sharedApplication().networkActivityIndicatorVisible = false
            alert.show()
        }
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if tableView == tracepointsTableView {
            if let tracepoints = schedule.valueForKey("tracepoints") as? NSOrderedSet {
                return tracepoints.count
            } else {
                return 0
            }
        } else {
            if let departureTimes = schedule.valueForKey("departureTimes") as? NSOrderedSet {
                let day = daysSegmentedControl.selectedSegmentIndex
                let filteredSet = departureTimes.filteredOrderedSetUsingPredicate(NSPredicate(format: "day == %@", argumentArray: [day]))
                return filteredSet.count
            } else {
                return 0
            }
        }
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        if tableView == tracepointsTableView {
            let cell = tableView.dequeueReusableCellWithIdentifier("InfoTracepoint", forIndexPath: indexPath) as UITableViewCell
            
            let tracepoints = schedule.valueForKey("tracepoints") as NSOrderedSet
            let tracepoint = tracepoints.array[indexPath.row] as NSManagedObject
            cell.textLabel!.text = tracepoint.valueForKey("name") as? String
            
            return cell
        } else {
            let cell = tableView.dequeueReusableCellWithIdentifier("InfoDepartureTime", forIndexPath: indexPath) as UITableViewCell
            
            let departureTimes = schedule.valueForKey("departureTimes") as NSOrderedSet
            let day = daysSegmentedControl.selectedSegmentIndex
            let set = departureTimes.filteredOrderedSetUsingPredicate(NSPredicate(format: "day == %@", argumentArray: [day]))
            let departureTime = set.array[indexPath.row] as NSManagedObject
            cell.textLabel!.text = departureTime.valueForKey("time") as? String
            
            return cell
        }
    }
    
    func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
    
    func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if tableView == tracepointsTableView {
            if editingStyle == .Delete {
                let tracepoints = schedule.valueForKey("tracepoints") as NSOrderedSet
                let mutableTracepoints = NSMutableOrderedSet(orderedSet: tracepoints)
                mutableTracepoints.removeObject(tracepoints.array[indexPath.row])
                schedule.setValue(mutableTracepoints, forKey: "tracepoints")
                
                let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
                let managedContext = appDelegate.managedObjectContext!
                
                managedContext.deleteObject(tracepoints.array[indexPath.row] as NSManagedObject)
                
                tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
            }
        } else {
            if editingStyle == .Delete {
                let departureTimes = schedule.valueForKey("departureTimes") as NSOrderedSet
                let mutableDepartureTimes = NSMutableOrderedSet(orderedSet: departureTimes)
                mutableDepartureTimes.removeObject(departureTimes.array[indexPath.row])
                schedule.setValue(mutableDepartureTimes, forKey: "departureTimes")
                
                let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
                let managedContext = appDelegate.managedObjectContext!
                
                managedContext.deleteObject(departureTimes.array[indexPath.row] as NSManagedObject)
                
                tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
            }
        }
    }

}
