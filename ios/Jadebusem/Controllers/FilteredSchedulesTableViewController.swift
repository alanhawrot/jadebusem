//
//  FilteredSchedulesTableViewController.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 30.12.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit
import CoreData

class FilteredSchedulesTableViewController: UITableViewController {
    
    var filteredSchedules = [NSManagedObject]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func numberOfSectionsInTableView(tableView: (UITableView!)) -> Int {
        return 1
    }
    
    override func tableView(tableView: (UITableView!), numberOfRowsInSection section: Int) -> Int {
        return filteredSchedules.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("FilteredSchedule", forIndexPath: indexPath) as UITableViewCell
        
        let schedule = filteredSchedules[indexPath.row]
        cell.textLabel!.text = schedule.valueForKey("name") as? String
        
        return cell
    }
    
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
}

