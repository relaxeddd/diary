//
//  ViewControllerTaskCard.swift
//  iosApp
//
//  Created by Chechin Vadim on 19.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerTaskCard: ViewControllerBase<ViewModelTaskCard> {

    @IBOutlet weak var editTextTitle: UITextField!
    @IBOutlet weak var editTextDesc: UITextField!
    @IBOutlet weak var buttonSave: UIBarButtonItem!
    @IBOutlet weak var buttonCancel: UIBarButtonItem!
    
    // MARK: - Init
    override func initView() {
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.dismissKeyboard (_:)))
        self.view.addGestureRecognizer(tapGesture)
        
        buttonSave.isEnabled = false
        
        editTextTitle.addTarget(self, action: #selector(onTextTitleChanged), for: .editingChanged)
    }
    
    override func initViewModel() {
        viewModel = ViewModelTaskCard()
        viewModel.state.addObserver { (state) in
            self.onStateChanged(state: (state as? TaskCreateState))
        }
    }
    
    // MARK: - View
    @objc func dismissKeyboard (_ sender: UITapGestureRecognizer) {
        view.endEditing(true)
    }
    
    @objc private func onTextTitleChanged() {
        buttonSave.isEnabled = !(editTextTitle.text?.isEmpty ?? false)
    }
    
    @IBAction func onSaveClicked(_ sender: Any) {
        viewModel.createTask(title: editTextTitle.text ?? "", desc: editTextDesc.text ?? "")
    }
    
    @IBAction func onCancelClicked(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    func getTaskData() -> (String, String) {
        return (editTextTitle.text ?? "", editTextDesc.text ?? "")
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    
    // MARK: - Common
    private func onStateChanged(state: TaskCreateState?) {
        if state is SuccessTaskCreateState {
            dismiss(animated: true, completion: nil)
        } else if state is LoadingTaskCreateState {
            
        } else if state is NothingTaskCreateState {
            
        } else if state is ErrorTaskCreateState {
            if let error = (state as? ErrorTaskCreateState)?.response.exception?.message {
                showToast(controller: self, message: error)
                print(error)
            }
        }
    }
}

extension ViewControllerTaskCard: UITextFieldDelegate {
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if (textField == editTextTitle) {
            editTextDesc.becomeFirstResponder()
        } else {
            textField.resignFirstResponder()
        }
        return true
    }
}
