//
//  DismissSegue.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 30.10.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit

class DismissSegue: UIStoryboardSegue {
   
    override func perform() {
        let source : UIViewController = self.sourceViewController as UIViewController
        source.presentingViewController?.dismissViewControllerAnimated(true, completion: nil)
    }
    
}
