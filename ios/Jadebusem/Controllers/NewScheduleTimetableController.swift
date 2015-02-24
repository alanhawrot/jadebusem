//
//  NewScheduleTimetableController.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 16.11.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit
import CoreData

class NewScheduleTimetableController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    @IBOutlet weak var daysSegmentedControl: UISegmentedControl!
    @IBOutlet weak var timePicker: UIDatePicker!
    @IBOutlet weak var departureTimesTableView: UITableView!
    
    var user : NSManagedObject!
    var schedule : NSManagedObject!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        departureTimesTableView.dataSource = self
        departureTimesTableView.delegate = self
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        daysSegmentedControl.selectedSegmentIndex = 0
    }

    @IBAction func dayChanged(sender: UISegmentedControl) {
        departureTimesTableView.reloadData()
    }
    
    @IBAction func saveSchedule(sender: UIBarButtonItem) {
        if (schedule.valueForKey("departureTimes") as NSOrderedSet).count == 0 {
            let alert = UIAlertView(title: NSLocalizedString("FAILURE", comment: "Failure alert title"), message: NSLocalizedString("ONE_DEPARTURE_TIME", comment: "Failure alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
            alert.show()
            return
        }
        
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        var error: NSError?
        if !managedContext.save(&error) {
            let alert = UIAlertView(title: NSLocalizedString("ERROR", comment: "Error alert title"), message: NSLocalizedString("COULD_NOT_SAVE", comment: "Error alert message") + " \(error), \(error?.userInfo)", delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
            alert.show()
            return
        }
        
        self.navigationController?.popToRootViewControllerAnimated(true)
    }
    
    @IBAction func addDepartureTime(sender: UIButton) {
        let departureTime = NSEntityDescription.insertNewObjectForEntityForName("DepartureTimes", inManagedObjectContext: (UIApplication.sharedApplication().delegate as AppDelegate).managedObjectContext!) as NSManagedObject
        
        let outputFormatter = NSDateFormatter()
        outputFormatter.locale = NSLocale.currentLocale()
        outputFormatter.dateFormat = "HH:mm"
        
        departureTime.setValue(daysSegmentedControl.selectedSegmentIndex, forKey: "day")
        departureTime.setValue(outputFormatter.stringFromDate(timePicker.date), forKey: "time")
        
        let scheduleDepartureTimes = schedule.valueForKey("departureTimes") as NSOrderedSet
        let mutableScheduleDepartureTimes = NSMutableOrderedSet(orderedSet: scheduleDepartureTimes)
        mutableScheduleDepartureTimes.addObject(departureTime)
        let comparator = {
            (item1: AnyObject!, item2: AnyObject!) -> NSComparisonResult in
            let item1String: NSString = item1.valueForKey("time") as NSString
            let item2String: NSString = item2.valueForKey("time") as NSString
            
            return item1String.compare(item2String)
        }
        mutableScheduleDepartureTimes.sortUsingComparator(comparator)
        schedule.setValue(mutableScheduleDepartureTimes, forKey: "departureTimes")
        
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        var error: NSError?
        managedContext.save(&error)
        
        departureTimesTableView.reloadData()
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let departureTimes = schedule.valueForKey("departureTimes") as? NSOrderedSet {
            let day = daysSegmentedControl.selectedSegmentIndex
            let filteredSet = departureTimes.filteredOrderedSetUsingPredicate(NSPredicate(format: "day == %@", argumentArray: [day]))
            return filteredSet.count
        } else {
            return 0
        }
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("Time", forIndexPath: indexPath) as UITableViewCell
        
        let departureTimes = schedule.valueForKey("departureTimes") as NSOrderedSet
        let day = daysSegmentedControl.selectedSegmentIndex
        let set = departureTimes.filteredOrderedSetUsingPredicate(NSPredicate(format: "day == %@", argumentArray: [day]))
        let departureTime = set.array[indexPath.row] as NSManagedObject
        cell.textLabel!.text = departureTime.valueForKey("time") as? String
        
        return cell
    }
    
    func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
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
