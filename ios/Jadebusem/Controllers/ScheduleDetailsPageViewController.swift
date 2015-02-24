//
//  ScheduleDetailsPageViewController.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 29.11.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit
import CoreData

class ScheduleDetailsPageViewController: UIPageViewController, UIPageViewControllerDataSource {
    
    var schedule: NSManagedObject!
    var username: String!
    var pageContentViewControllers = []
    var index = 0

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.automaticallyAdjustsScrollViewInsets = false
        
        let infoViewController = self.storyboard?.instantiateViewControllerWithIdentifier("SICV") as ScheduleInfoViewController
        let mapViewController = self.storyboard?.instantiateViewControllerWithIdentifier("STMVC") as ScheduleTraceMapViewController
        
        infoViewController.schedule = schedule
        infoViewController.username = username
        mapViewController.schedule = schedule
        
        pageContentViewControllers = [infoViewController, mapViewController]
        
        self.dataSource = self
        self.setViewControllers([infoViewController], direction: UIPageViewControllerNavigationDirection.Forward, animated: true, completion: nil)
    }
    
    func pageViewController(pageViewController: UIPageViewController, viewControllerBeforeViewController viewController: UIViewController) -> UIViewController? {
        if viewController == pageContentViewControllers[0] as? UIViewController {
            return nil
        } else {
            index = 0
            return pageContentViewControllers[0] as? UIViewController
        }
    }
    
    func pageViewController(pageViewController: UIPageViewController, viewControllerAfterViewController viewController: UIViewController) -> UIViewController? {
        if viewController == pageContentViewControllers[1] as? UIViewController {
            return nil
        } else {
            index = 1
            return pageContentViewControllers[1] as? UIViewController
        }
    }
    
    func presentationCountForPageViewController(pageViewController: UIPageViewController) -> Int {
        return 2
    }
    
    func presentationIndexForPageViewController(pageViewController: UIPageViewController) -> Int {
        return index
    }

}
