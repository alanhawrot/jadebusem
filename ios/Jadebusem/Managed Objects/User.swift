//
//  User.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 17.11.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import Foundation
import CoreData
import UIKit

@objc(User)
class User: NSManagedObject {

    @NSManaged var name: String
    @NSManaged var password: String
    @NSManaged var type: NSNumber
    @NSManaged var schedules: NSOrderedSet
    
    init(name: String, password: String, type: NSNumber, schedules: NSOrderedSet) {
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        super.init(entity: NSEntityDescription.entityForName("Users", inManagedObjectContext: managedContext)!, insertIntoManagedObjectContext: nil)
        
        self.name = name
        self.password = password
        self.type = type
        self.schedules = schedules
    }

}
