//
//  LoginViewController.swift
//  iosApp
//
//  Created by Chechin Vadim on 15.09.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit

class ViewControllerLogin: UIViewController {

    @IBOutlet weak var editTextEmail: UITextField!
    @IBOutlet weak var editTextPassword: UITextField!
    @IBOutlet weak var editTextRepeatPassword: UITextField!
    @IBOutlet weak var textError: UILabel!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    @IBAction func onLoginOrCreateClicked(_ sender: Any) {
        
    }
}
