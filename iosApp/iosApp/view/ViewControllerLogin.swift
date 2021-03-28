//
//  LoginViewController.swift
//  iosApp
//
//  Created by Chechin Vadim on 15.09.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerLogin: ViewControllerBase<IViewModelAuth> {

    @IBOutlet weak var editTextEmail: UITextField!
    @IBOutlet weak var editTextPassword: UITextField!
    @IBOutlet weak var editTextRepeatPassword: UITextField!
    @IBOutlet weak var textError: UILabel!
    @IBOutlet weak var buttonLogin: UIButton!
    @IBOutlet weak var buttonCreateAccount: UIButton!
    @IBOutlet weak var buttonHaveNotAccount: UIButton!
    @IBOutlet weak var buttonAlreadyHaveAccount: UIButton!
    @IBOutlet weak var progressBarAuth: UIActivityIndicatorView!
    
    override func createViewModel() -> IViewModelAuth { ViewModelCreator().createAuthViewModel() }
    override var progressBar: UIActivityIndicatorView? { progressBarAuth }
    
    override func initView() {
        super.initView()
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.dismissKeyboard (_:)))
        self.view.addGestureRecognizer(tapGesture)
        
        editTextEmail.delegate = self
        editTextPassword.delegate = self
        editTextRepeatPassword.delegate = self
        editTextEmail.addTarget(self, action: #selector(onTextEmailChanged), for: .editingChanged)
        editTextPassword.addTarget(self, action: #selector(onTextPasswordChanged), for: .editingChanged)
        editTextRepeatPassword.addTarget(self, action: #selector(onTextRepeatPasswordChanged), for: .editingChanged)
    }
    
    override func observeViewModel() {
        super.observeViewModel()
        
        viewModel.isRegistrationView.addObserver { value in
            let isRegistrationView = value as? Bool ?? false
            
            self.editTextRepeatPassword.isHidden = !isRegistrationView
            self.buttonLogin.isHidden = isRegistrationView
            self.buttonAlreadyHaveAccount.isHidden = !isRegistrationView
            self.buttonCreateAccount.isHidden = !isRegistrationView
            self.buttonHaveNotAccount.isHidden = isRegistrationView
            if (isRegistrationView) {
                self.navigationItem.title = "Registration"
            } else {
                self.navigationItem.title = "Login"
            }
        }
        viewModel.errorAuth.addObserver { value in
            let errorText = value as String? ?? ""
            self.textError.isHidden = errorText.isEmpty
            self.textError.text = errorText
        }
        viewModel.textEmail.addObserver { (value) in
            let email = value as String? ?? ""
            if (self.editTextEmail.text != email) {
                self.editTextEmail.text = email
            }
        }
        viewModel.textPassword.addObserver { (value) in
            let password = value as String? ?? ""
            if (self.editTextPassword.text != password) {
                self.editTextPassword.text = password
            }
        }
        viewModel.textRepeatPassword.addObserver { (value) in
            let repeatPassword = value as String? ?? ""
            if (self.editTextRepeatPassword.text != repeatPassword) {
                self.editTextRepeatPassword.text = repeatPassword
            }
        }
    }
    
    override func handleAction(action: Action, type: EventType) {
        if (type == EventType.goScreenList) {
            weak var presentingViewController = self.presentingViewController
            self.dismiss(animated: true) {
                presentingViewController?.performSegue(withIdentifier: "showList", sender: nil)
            }
        }
    }
    
    @IBAction func onClickedLogin(_ sender: Any) {
        viewModel.onClickedLogin()
    }
    
    @IBAction func onClickedCreateAccount(_ sender: Any) {
        viewModel.onClickedCreate()
    }
    
    @IBAction func onClickedNoAccountYet(_ sender: Any) {
        viewModel.onClickedHaveNotAccount()
    }
    
    @IBAction func onClickedAlreadyHaveAcoount(_ sender: Any) {
        viewModel.onClickedAlreadyHaveAccount()
    }
    
    @objc private func onTextEmailChanged() {
        viewModel.onChangedEmail(value: editTextEmail.text ?? "")
    }
    
    @objc private func onTextPasswordChanged() {
        viewModel.onChangedPassword(value: editTextPassword.text ?? "")
    }
    
    @objc private func onTextRepeatPasswordChanged() {
        viewModel.onChangedRepeatPassword(value: editTextRepeatPassword.text ?? "")
    }
    
    @objc func dismissKeyboard(_ sender: UITapGestureRecognizer) {
        view.endEditing(true)
    }
}

extension ViewControllerLogin: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if (textField == editTextEmail) {
            editTextPassword.becomeFirstResponder()
            return false
        } else if (textField == editTextPassword) {
            if (editTextRepeatPassword.isHidden == false) {
                editTextRepeatPassword.becomeFirstResponder()
                return false
            } else {
                viewModel.onClickedLogin()
                return false
            }
        } else if (textField == editTextRepeatPassword) {
            viewModel.onClickedCreate()
            return false
        }
        return true
    }
}
