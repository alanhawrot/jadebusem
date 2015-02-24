//
//  ScheduleTracepoint.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 17.11.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import Foundation
import CoreData
import UIKit

@objc(ScheduleTracepoint)
class ScheduleTracepoint: NSManagedObject {

    @NSManaged var name: String
    @NSManaged var schedule: Schedule
    
    convenience init(name: String) {
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        self.init(entity: NSEntityDescription.entityForName("Tracepoints", inManagedObjectContext: managedContext)!, insertIntoManagedObjectContext: nil)
        
        self.name = name
    }

}
