//
//  LoginSegue.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 15.11.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit

class LoginSegue: UIStoryboardSegue {
    
    override func perform() {
        let source : LoginViewController = self.sourceViewController as LoginViewController
        
        let urlPath = "http://jadebusem1.herokuapp.com/users/sign_in/"
        let url = NSURL(string: urlPath)
        let request = NSMutableURLRequest(URL: url!)
        request.HTTPMethod = "POST"
        
        let login = source.loginTextField.text
        let password = source.passwordTextField.text
        
        let postData = "email=\(login)&password=\(password)"
        
        request.HTTPBody = postData.dataUsingEncoding(NSUTF8StringEncoding)
        
        let alert = UIAlertView(title: NSLocalizedString("ERROR", comment: "Error alert title"), message: "", delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
        
        let completionBlock = {
            (response: NSURLResponse!, responseData: NSData!, error: NSError!) -> Void in
            
            var responseString = NSString(data: responseData, encoding: NSUTF8StringEncoding)
            
            if (error == nil) {
                if let serverResponse = responseString {
                    if serverResponse.containsString("Please correct the errors below.") {
                        alert.message = NSLocalizedString("CORRECT_ERRORS_MESSAGE", comment: "Error alert message")
                    } else if serverResponse.containsString("Please correct the error below.") {
                        alert.message = NSLocalizedString("CORRECT_ERROR_MESSAGE", comment: "Error alert message")
                    } else if serverResponse.containsString("Error, please check your email or password.") {
                        alert.message = NSLocalizedString("EMAIL_PASSWORD_ERROR", comment: "Error alert message")
                    } else {
                        self.stopAnimating(source)
                        ((self.destinationViewController as UINavigationController).topViewController as SchedulesTableViewController).username = login
                        self.sourceViewController.presentViewController(self.destinationViewController as UIViewController, animated: true, completion: nil)
                        return
                    }
                } else {
                    alert.message = NSLocalizedString("TRY_AGAIN", comment: "Error alert message")
                }
            } else {
                alert.message = NSLocalizedString("NETWORK_PROBLEMS", comment: "Error alert message")
            }
            
            self.stopAnimating(source)
            alert.show()
        }
        
        let queue = NSOperationQueue.mainQueue()
        NSURLConnection.sendAsynchronousRequest(request, queue: queue, completionHandler: completionBlock)
    }
    
    func stopAnimating(source: LoginViewController) {
        UIApplication.sharedApplication().networkActivityIndicatorVisible = false
        UIApplication.sharedApplication().endIgnoringInteractionEvents()
        source.activityIndicator.stopAnimating()
    }
    
}
