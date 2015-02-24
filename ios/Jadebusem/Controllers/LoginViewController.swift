//
//  ViewController.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 19.10.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {

    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var loginTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    
    @IBAction func onSignInButtonClick(sender: UIButton) {
        loginTextField.endEditing(true)
        passwordTextField.endEditing(true)
        
        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
        UIApplication.sharedApplication().beginIgnoringInteractionEvents()
        
        activityIndicator.startAnimating()
    }
    
    @IBAction func backgroundTapped(sender: UITapGestureRecognizer) {
        self.view.endEditing(true)
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if segue.identifier == "GuestLoginSegue" {
            ((segue.destinationViewController as UINavigationController).topViewController as SchedulesTableViewController).username = ""
        }
    }
    
}

