//
//  NewScheduleController.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 15.11.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit
import CoreData

class NewScheduleTracepointsController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    @IBOutlet weak var scheduleNameTextField: UITextField!
    @IBOutlet weak var tracepointTextField: UITextField!
    @IBOutlet weak var tracepointsTableView: UITableView!
    
    var user: NSManagedObject!
    var schedule = NSEntityDescription.insertNewObjectForEntityForName("Schedules", inManagedObjectContext: (UIApplication.sharedApplication().delegate as AppDelegate).managedObjectContext!) as NSManagedObject
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tracepointsTableView.dataSource = self
        tracepointsTableView.delegate = self
        
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        var error: NSError?
        managedContext.save(&error)
    }
    
    override func viewWillDisappear(animated: Bool) {
        let viewControllers = NSArray(array: self.navigationController!.viewControllers)
        
        if viewControllers.indexOfObject(self) == NSNotFound {
            let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
            let managedContext = appDelegate.managedObjectContext!
            managedContext.deleteObject(schedule)
        }
        
        super.viewWillDisappear(animated)
    }
    
    @IBAction func addTracepoint(sender: UIButton) {
        let tracepointName = tracepointTextField.text
        
        let tracepoint = NSEntityDescription.insertNewObjectForEntityForName("Tracepoints", inManagedObjectContext: (UIApplication.sharedApplication().delegate as AppDelegate).managedObjectContext!) as NSManagedObject
        
        tracepoint.setValue(tracepointName, forKey: "name")
        
        let scheduleTracepoints = schedule.valueForKey("tracepoints") as NSOrderedSet
        let mutableScheduleTracepoints = NSMutableOrderedSet(orderedSet: scheduleTracepoints)
        mutableScheduleTracepoints.addObject(tracepoint)
        schedule.setValue(mutableScheduleTracepoints, forKey: "tracepoints")
        
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        var error: NSError?
        managedContext.save(&error)
        
        tracepointTextField.text = ""
        
        tracepointsTableView.reloadData()
    }
    
    @IBAction func backgroundTapped(sender: UITapGestureRecognizer) {
        self.view.endEditing(true)
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let tracepoints = schedule.valueForKey("tracepoints") as? NSOrderedSet {
            return tracepoints.count
        } else {
            return 0
        }
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("Tracepoint", forIndexPath: indexPath) as UITableViewCell
        
        let tracepoints = schedule.valueForKey("tracepoints") as NSOrderedSet
        let tracepoint = tracepoints.array[indexPath.row] as NSManagedObject
        cell.textLabel!.text = tracepoint.valueForKey("name") as? String
        
        return cell
    }
    
    func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
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
    }
    
    override func shouldPerformSegueWithIdentifier(identifier: String?, sender: AnyObject?) -> Bool {
        if identifier == "TimetableSegue" {
            let scheduleTracepoints = schedule.valueForKey("tracepoints") as NSOrderedSet
            if scheduleTracepoints.count >= 2 {
                return true
            } else {
                let alert = UIAlertView(title: NSLocalizedString("FAILURE", comment: "Failure alert title"), message: NSLocalizedString("TWO_ROUTE_POINTS", comment: "Failure alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
                alert.show()
            }
        }
        return false
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        let scheduleName = scheduleNameTextField.text
        if scheduleName == "" {
            schedule.setValue("Blank", forKey: "name")
        } else {
            schedule.setValue(scheduleName, forKey: "name")
        }
        schedule.setValue(NSNumber(short: ScheduleTypes.LOCAL.rawValue), forKey: "type")
        (segue.destinationViewController as NewScheduleTimetableController).user = user
        (segue.destinationViewController as NewScheduleTimetableController).schedule = schedule
    }
    
}
