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
    @IBOutlet weak var progressBar: UIActivityIndicatorView!
    
    private var editTaskId: Int64?
    private var editTaskTitle: String?
    private var editTaskDesc: String?
    
    // MARK: - Init
    override func initView() {
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.dismissKeyboard (_:)))
        self.view.addGestureRecognizer(tapGesture)
        
        if (editTaskId != nil) {
            editTextTitle.text = editTaskTitle ?? ""
            editTextDesc.text = editTaskDesc ?? ""
        }
        
        buttonSave.isEnabled = editTaskId != nil
        editTextTitle.addTarget(self, action: #selector(onTextTitleChanged), for: .editingChanged)
    }
    
    override func initViewModel() {
        viewModel = ViewModelTaskCard()
        viewModel.state.addObserver { (state) in
            self.onStateChanged(state: (state as? TaskCreateState))
        }
    }
    
    // MARK: - View
    @objc func dismissKeyboard(_ sender: UITapGestureRecognizer) {
        view.endEditing(true)
    }
    
    @objc private func onTextTitleChanged() {
        buttonSave.isEnabled = !(editTextTitle.text?.isEmpty ?? false)
    }
    
    @IBAction func onSaveClicked(_ sender: Any) {
        view.endEditing(true)
        if (editTaskId != nil) {
            viewModel.updateTask(id: editTaskId!, title: editTextTitle.text ?? "", desc: editTextDesc.text ?? "")
        } else {
            viewModel.createTask(title: editTextTitle.text ?? "", desc: editTextDesc.text ?? "")
        }
    }
    
    @IBAction func onCancelClicked(_ sender: Any) {
        view.endEditing(true)
        dismiss(animated: true, completion: nil)
    }
    
    func setEditTaskData(id: Int64, title: String, desc: String) {
        editTaskId = id
        editTaskTitle = title
        editTaskDesc = desc
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
        if state is SuccessTaskCardState {
            progressBar.stopAnimating()
            dismiss(animated: true, completion: nil)
        } else if state is LoadingTaskCreateState {
            progressBar.startAnimating()
        } else if state is EmptyTaskCreateState {
            progressBar.stopAnimating()
        } else if state is ErrorTaskCardState {
            progressBar.stopAnimating()
            if let error = (state as? ErrorTaskCardState)?.response.exception?.message {
                showError(text: error)
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
