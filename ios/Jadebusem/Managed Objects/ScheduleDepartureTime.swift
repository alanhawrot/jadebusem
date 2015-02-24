//
//  ScheduleDepartureTime.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 17.11.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import Foundation
import CoreData
import UIKit

@objc(ScheduleDepartureTime)
class ScheduleDepartureTime: NSManagedObject {

    @NSManaged var day: NSNumber
    @NSManaged var time: String
    @NSManaged var schedule: Schedule
    
    convenience init(day: NSNumber, time: String) {
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        self.init(entity: NSEntityDescription.entityForName("DepartureTimes", inManagedObjectContext: managedContext)!, insertIntoManagedObjectContext: nil)
        
        self.day = day
        self.time = time
    }

}
