//
//  LoginViewController.swift
//  iosApp
//
//  Created by Chechin Vadim on 15.09.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerLogin: ViewControllerBase<ViewModelAuth> {

    @IBOutlet weak var editTextEmail: UITextField!
    @IBOutlet weak var editTextPassword: UITextField!
    @IBOutlet weak var editTextRepeatPassword: UITextField!
    @IBOutlet weak var textError: UILabel!
    @IBOutlet weak var progressBarAuth: UIActivityIndicatorView!
    
    override func createViewModel() -> ViewModelAuth { ViewModelAuth() }
    override var progressBar: UIActivityIndicatorView? { progressBarAuth }
    
    override func initView() {
        super.initView()
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.dismissKeyboard (_:)))
        self.view.addGestureRecognizer(tapGesture)
    }
    
    @IBAction func onLoginOrCreateClicked(_ sender: Any) {
        viewModel.onClickedLoginOrCreate(email: editTextEmail.text ?? "", password: editTextPassword.text ?? "")
    }
    
    @objc func dismissKeyboard(_ sender: UITapGestureRecognizer) {
        view.endEditing(true)
    }
}
