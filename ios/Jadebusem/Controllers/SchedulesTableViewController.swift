//
//  SchedulesTableViewController.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 19.10.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit
import CoreData

class SchedulesTableViewController: UITableViewController, UISearchBarDelegate, UISearchControllerDelegate, UISearchResultsUpdating {
    
    var username: String!
    var schedules = [NSManagedObject]()
    var serverSchedules = [NSManagedObject]()
    var currentPage = 1
    
    struct RestorationKeys {
        static let viewControllerTitle = "ViewControllerTitleKey"
        static let searchControllerIsActive = "SearchControllerIsActiveKey"
        static let searchBarText = "SearchBarTextKey"
        static let searchBarIsFirstResponder = "SearchBarIsFirstResponderKey"
    }
    
    struct SearchControllerRestorableState {
        var wasActive = false
        var wasFirstResponder = false
    }
    
    var searchController: UISearchController!
    var filteredSchedulesViewController: FilteredSchedulesTableViewController!
    var restoredState = SearchControllerRestorableState()
    
    @IBAction func signOut(sender: UIBarButtonItem) {
        self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.refreshControl = UIRefreshControl()
        self.refreshControl?.backgroundColor = UIColor.blueColor()
        self.refreshControl?.tintColor = UIColor.whiteColor()
        self.refreshControl?.addTarget(self, action: "getSchedulesFromServer", forControlEvents: UIControlEvents.ValueChanged)
        
        filteredSchedulesViewController = self.storyboard?.instantiateViewControllerWithIdentifier("FilteredSchedulesTableViewController") as FilteredSchedulesTableViewController
        filteredSchedulesViewController.tableView.delegate = self
        
        searchController = UISearchController(searchResultsController: filteredSchedulesViewController)
        searchController.searchResultsUpdater = self
        searchController.searchBar.sizeToFit()
        tableView.tableHeaderView = searchController.searchBar
        
        searchController.delegate = self
        searchController.dimsBackgroundDuringPresentation = false
        searchController.hidesNavigationBarDuringPresentation = false
        searchController.searchBar.delegate = self
        
        definesPresentationContext = true
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        if restoredState.wasActive {
            searchController.active = restoredState.wasActive
            restoredState.wasActive = false
            
            if restoredState.wasFirstResponder {
                searchController.searchBar.becomeFirstResponder()
                restoredState.wasFirstResponder = false
            }
        }
        
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        let fetchRequest = NSFetchRequest(entityName: "Schedules")
        var error: NSError?
        let fetchedResults = managedContext.executeFetchRequest(fetchRequest, error: &error) as [NSManagedObject]?
        
        if let results = fetchedResults {
            schedules = results
        } else {
            let alert = UIAlertView(title: NSLocalizedString("ERROR", comment: "Error alert title"), message: NSLocalizedString("DATABASE_ERROR", comment: "Error alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
            alert.show()
        }
        
        extendSchedulesWithServerSchedules()
    }
    
    func getSchedulesFromServer() {
        for i in 1...currentPage {
            let urlPath = "http://jadebusem1.herokuapp.com/schedules/all_schedules/\(i)"
            let url = NSURL(string: urlPath)
            let request = NSMutableURLRequest(URL: url!)
            request.HTTPMethod = "GET"
            
            UIApplication.sharedApplication().networkActivityIndicatorVisible = true
            
            let queue = NSOperationQueue.mainQueue()
            NSURLConnection.sendAsynchronousRequest(request, queue: queue) { (response, responseData, error) -> Void in
                if error == nil {
                    var jsonError: NSError?
                    let responseDictionary = NSJSONSerialization.JSONObjectWithData(responseData, options: NSJSONReadingOptions.MutableContainers, error: &jsonError) as NSDictionary
                    
                    let resultsKey = NSString(string: "results")
                    let responseSchedules = responseDictionary.objectForKey(resultsKey) as NSArray
                    
                    for externalSchedule in responseSchedules {
                        let externalID = externalSchedule.valueForKey("id") as NSNumber
                        let company_name = externalSchedule.valueForKey("company") as String
                        
                        let externalName = "\(company_name): \(externalID)"
                        
                        let tracepointsKey = NSString(string: "trace_points")
                        let externalTracepoints = externalSchedule.objectForKey(tracepointsKey) as NSArray
                        
                        let tracepoints = NSMutableOrderedSet()
                        
                        for externalTracepoint in externalTracepoints {
                            let tracepointAddress = externalTracepoint.valueForKey("address") as String
                            let tracepoint = ScheduleTracepoint(name: tracepointAddress)
                            tracepoints.addObject(tracepoint)
                        }
                        
                        let departuresKey = NSString(string: "departures")
                        let externalDepartures = externalSchedule.objectForKey(departuresKey) as NSArray
                        
                        let departureTimes = NSMutableOrderedSet()
                        
                        for externalDepartureTime in externalDepartures {
                            let departureTime = externalDepartureTime.valueForKey("time") as NSString
                            let departureDay = externalDepartureTime.valueForKey("day") as NSNumber
                            
                            let formattedDepartureTime = departureTime.substringToIndex(5) as String
                            
                            let departure = ScheduleDepartureTime(day: departureDay, time: formattedDepartureTime)
                            departureTimes.addObject(departure)
                        }
                        
                        let scheduleType = NSNumber(short: ScheduleTypes.EXTERNAL.rawValue)
                        let serverSchedule = Schedule(name: externalName, type: scheduleType, departureTimes: departureTimes, tracepoints: tracepoints)
                        
                        if self.serverSchedules.filter({ (s: NSManagedObject) -> Bool in
                            return s.valueForKey("name") as String! == externalName
                        }).count == 0 {
                            self.serverSchedules.append(serverSchedule)
                        }
                    }
                    
                    if i == self.currentPage && responseDictionary.valueForKey("next") != nil {
                        self.currentPage += 1
                    }
                    
                    self.extendSchedulesWithServerSchedules()
                    
                    self.refreshControl?.endRefreshing()
                } else {
                    let alert = UIAlertView(title: NSLocalizedString("ERROR", comment: "Error alert title"), message: NSLocalizedString("NETWORK_PROBLEMS", comment: "Error alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
                    alert.show()
                }
                
                UIApplication.sharedApplication().networkActivityIndicatorVisible = false
            }
        }
    }
    
    func extendSchedulesWithServerSchedules() {
        let schedulesSet = NSMutableOrderedSet(array: self.schedules)
        schedulesSet.addObjectsFromArray(serverSchedules)
        self.schedules = schedulesSet.array as [NSManagedObject]
        self.tableView.reloadData()
    }

    override func numberOfSectionsInTableView(tableView: (UITableView!)) -> Int {
        return 1
    }

    override func tableView(tableView: (UITableView!), numberOfRowsInSection section: Int) -> Int {
        return schedules.count
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("Schedule", forIndexPath: indexPath) as UITableViewCell

        let schedule = schedules[indexPath.row]
        cell.textLabel!.text = schedule.valueForKey("name") as? String

        return cell
    }

    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }

    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            if username == "" {
                let alert = UIAlertView(title: NSLocalizedString("FAILURE", comment: "Failure alert title"), message: NSLocalizedString("SIGNED_USERS_REMOVE", comment: "Error alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
                alert.show()
                return
            }
            
            var scheduleToDelete: NSManagedObject
            scheduleToDelete = schedules[indexPath.row]
            
            let scheduleType = scheduleToDelete.valueForKey("type") as NSNumber
            
            if ScheduleTypes(rawValue: scheduleType.shortValue) == ScheduleTypes.LOCAL {
                let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
                let managedContext = appDelegate.managedObjectContext!
                
                managedContext.deleteObject(scheduleToDelete)
                
                var error : NSError?
                managedContext.save(&error)
                
                if (error == nil) {
                    schedules.removeAtIndex(indexPath.row)
                    tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
                }
            } else {
                let alert = UIAlertView(title: NSLocalizedString("ERROR", comment: "Error alert title"), message: NSLocalizedString("EXTERNAL_SCHEDULE_REMOVE_ERROR", comment: "Error alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
                alert.show()
            }
        }
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        var schedule: NSManagedObject
        
        if tableView == self.tableView {
            schedule = schedules[indexPath.row]
        } else {
            schedule = filteredSchedulesViewController.filteredSchedules[indexPath.row]
        }
        
        tableView.deselectRowAtIndexPath(tableView.indexPathForSelectedRow()!, animated: false)
        
        let scheduleDetailsPageViewController = self.storyboard?.instantiateViewControllerWithIdentifier("PVC") as ScheduleDetailsPageViewController
        scheduleDetailsPageViewController.schedule = schedule
        scheduleDetailsPageViewController.username = username
        
        self.navigationController?.pushViewController(scheduleDetailsPageViewController, animated: true)
    }
    
    func searchBarSearchButtonClicked(searchBar: UISearchBar) {
        searchBar.resignFirstResponder()
    }
    
    func presentSearchController(searchController: UISearchController) {
        //NSLog(__FUNCTION__)
    }
    
    func willPresentSearchController(searchController: UISearchController) {
        //NSLog(__FUNCTION__)
    }
    
    func didPresentSearchController(searchController: UISearchController) {
        //NSLog(__FUNCTION__)
    }
    
    func willDismissSearchController(searchController: UISearchController) {
        //NSLog(__FUNCTION__)
    }
    
    func didDismissSearchController(searchController: UISearchController) {
        //NSLog(__FUNCTION__)
    }
    
    func updateSearchResultsForSearchController(searchController: UISearchController) {
        let resultsController = searchController.searchResultsController as FilteredSchedulesTableViewController
        resultsController.filteredSchedules.removeAll(keepCapacity: false)
        
        let searchText = searchController.searchBar.text as NSString?
        
        if let keywords: NSString = searchText {
            let keywordsArray = keywords.componentsSeparatedByString(" ") as NSArray
            
            if keywordsArray.count > 0 {
                var filteredSchedules = NSMutableArray(array: schedules)
                
                for keyword in keywordsArray {
                    let special = ".*" + (keyword as String) + ".*"
                    let predicate = NSPredicate(format: "name MATCHES[c] %@ OR ANY tracepoints.name MATCHES[c] %@ OR ANY departureTimes.time == %@", argumentArray: [special, special, keyword])
                    
                    filteredSchedules.filterUsingPredicate(predicate)
                }
                
                for filteredSchedule in filteredSchedules {
                    resultsController.filteredSchedules.append(filteredSchedule as NSManagedObject)
                }
                
                resultsController.tableView.reloadData()
            }
        }
    }
    
    override func encodeRestorableStateWithCoder(coder: NSCoder) {
        super.encodeRestorableStateWithCoder(coder)
        
        // Encode the title.
        coder.encodeObject(navigationItem.title, forKey:RestorationKeys.viewControllerTitle)
        
        // Encode the search controller's active state.
        coder.encodeBool(searchController.active, forKey:RestorationKeys.searchControllerIsActive)
        
        // Encode the first responser status.
        coder.encodeBool(searchController.searchBar.isFirstResponder(), forKey:RestorationKeys.searchBarIsFirstResponder)
        
        // Encode the search bar text.
        coder.encodeObject(searchController.searchBar.text, forKey:RestorationKeys.searchBarText)
    }
    
    override func decodeRestorableStateWithCoder(coder: NSCoder) {
        super.decodeRestorableStateWithCoder(coder)
        
        // Restore the title.
        if let decodedTitle = coder.decodeObjectForKey(RestorationKeys.viewControllerTitle) as? String {
            title = decodedTitle
        }
        else {
            fatalError("A title did not exist. In your app, handle this gracefully.")
        }
        
        // Restore the active state:
        // We can't make the searchController active here since it's not part of the view
        // hierarchy yet, instead we do it in viewWillAppear.
        //
        restoredState.wasActive = coder.decodeBoolForKey(RestorationKeys.searchControllerIsActive)
        
        // Restore the first responder status:
        // Like above, we can't make the searchController first responder here since it's not part of the view
        // hierarchy yet, instead we do it in viewWillAppear.
        //
        restoredState.wasFirstResponder = coder.decodeBoolForKey(RestorationKeys.searchBarIsFirstResponder)
        
        // Restore the text in the search field.
        searchController.searchBar.text = coder.decodeObjectForKey(RestorationKeys.searchBarText) as String
    }
    
    override func shouldPerformSegueWithIdentifier(identifier: String?, sender: AnyObject?) -> Bool {
        if identifier == "AddScheduleSegue" {
            if username == "" {
                let alert = UIAlertView(title: NSLocalizedString("FAILURE", comment: "Failure alert title"), message: NSLocalizedString("ADD_SCHEDULE_SIGNED_USERS", comment: "Failure alert message"), delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
                alert.show()
                return false
            }
        }
        return true
    }

}
