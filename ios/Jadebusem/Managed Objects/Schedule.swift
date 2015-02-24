//
//  Schedule.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 17.11.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import Foundation
import CoreData
import UIKit

@objc(Schedule)
class Schedule: NSManagedObject {

    @NSManaged var name: String
    @NSManaged var type: NSNumber
    @NSManaged var departureTimes: NSOrderedSet
    @NSManaged var tracepoints: NSOrderedSet
    @NSManaged var user: User
    
    convenience init(name: String, type: NSNumber, departureTimes: NSOrderedSet, tracepoints: NSOrderedSet) {
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        self.init(entity: NSEntityDescription.entityForName("Schedules", inManagedObjectContext: managedContext)!, insertIntoManagedObjectContext: nil)
        
        self.name = name
        self.type = type
        self.departureTimes = departureTimes
        self.tracepoints = tracepoints
    }

}
