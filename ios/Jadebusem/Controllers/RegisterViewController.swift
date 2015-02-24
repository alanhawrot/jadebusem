//
//  RegisterViewController.swift
//  Jadebusem
//
//  Created by Alan Hawrot on 19.10.2014.
//  Copyright (c) 2014 Alan Hawrot. All rights reserved.
//

import UIKit

class RegisterViewController: UIViewController {

    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var reEnterPasswordTextField: UITextField!
    @IBOutlet weak var firstnameTextField: UITextField!
    @IBOutlet weak var surnameTextField: UITextField!
    @IBOutlet weak var addressTextField: UITextField!
    @IBOutlet weak var companyTextField: UITextField!

    @IBAction func register(sender: UIButton) {
        let urlPath = "http://jadebusem1.herokuapp.com/users/registration/"
        let url = NSURL(string: urlPath)
        let request = NSMutableURLRequest(URL: url!)
        request.HTTPMethod = "POST"
        
        let email = emailTextField.text
        let password = passwordTextField.text
        let repassword = reEnterPasswordTextField.text
        let firstname = firstnameTextField.text
        let surname = surnameTextField.text
        let address = addressTextField.text
        let company = companyTextField.text
        
        let alert = UIAlertView(title: NSLocalizedString("ERROR", comment: "Error alert title"), message: "", delegate: self, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
        
        if countElements(password) < 6 {
            alert.message = NSLocalizedString("SHORT_PASSWORD", comment: "Error alert message")
            alert.show()
            return
        }
        
        if password != repassword {
            alert.message = NSLocalizedString("NOT_EQUAL_PASSWORDS", comment: "Error alert message")
            alert.show()
            return
        }
        
        let postData = "email=\(email)&password=\(password)&password2=\(repassword)&first_name=\(firstname)&last_name=\(surname)&address=\(address)&company_name=\(company)"
        
        request.HTTPBody = postData.dataUsingEncoding(NSUTF8StringEncoding)
        
        let completionBlock = {
            (response: NSURLResponse!, responseData: NSData!, error: NSError!) -> Void in
            
            var responseString = NSString(data: responseData, encoding: NSUTF8StringEncoding)
            
            if (error == nil) {
                if let serverResponse = responseString {
                    if serverResponse.containsString("This e-mail is already in use") {
                        alert.message = NSLocalizedString("EMAIL_IN_USE", comment: "Error alert message")
                    } else {
                        self.dismissViewControllerAnimated(true, completion: { () -> Void in
                            let successAlert = UIAlertView(title: NSLocalizedString("SUCCESS", comment: "Success alert title"), message: NSLocalizedString("REGISTERED_SUCCESSFULLY", comment: "Success alert message"), delegate: self.parentViewController, cancelButtonTitle: NSLocalizedString("OK", comment: "OK cancel button title"))
                            successAlert.show()
                        })
                        return
                    }
                } else {
                    alert.message = NSLocalizedString("TRY_AGAIN", comment: "Error alert message")
                }
            } else {
                alert.message = NSLocalizedString("NETWORK_PROBLEMS", comment: "Error alert message")
            }
            
            alert.show()
        }
        
        let queue = NSOperationQueue.mainQueue()
        NSURLConnection.sendAsynchronousRequest(request, queue: queue, completionHandler: completionBlock)
    }
    
    @IBAction func backgroundTapped(sender: UITapGestureRecognizer) {
        self.view.endEditing(true)
    }
    
}
