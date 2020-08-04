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
    @IBOutlet weak var editTextStartDate: EditTextDate!
    @IBOutlet weak var editTextEndDate: EditTextDate!
    
    private var editTaskId: Int64? = nil
    @IBOutlet weak var toolbar: UINavigationItem!
    @IBOutlet weak var segmentsPriority: UISegmentedControl!
    
    private var isTaskEditing: Bool { self.editTaskId != nil }
    
    // MARK: - Arguments
    func setEditTaskData(id: Int64) {
        editTaskId = id
    }
    
    // MARK: - Init
    override func createViewModel() -> ViewModelTaskCard { ViewModelTaskCard() }
    override func getProgressBar() -> UIActivityIndicatorView? { progressBar }
    
    override func initView() {
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.dismissKeyboard (_:)))
        self.view.addGestureRecognizer(tapGesture)
        
        editTextTitle.addTarget(self, action: #selector(onTextTitleChanged), for: .editingChanged)
        editTextDesc.addTarget(self, action: #selector(onTextDescChanged), for: .editingChanged)
        editTextStartDate.onDateChanged = { millis in
            self.onStartDateChanged(dateInMillis: millis)
        }
        editTextEndDate.onDateChanged = { millis in
            self.onEndDateChanged(dateInMillis: millis)
        }
    }
    
    override func initViewModel() {
        super.initViewModel()
        
        viewModel.isEnabledButtonSave.addObserver { value in
            self.buttonSave.isEnabled = value as? Bool ?? false
        }
        viewModel.taskTitle.addObserver { (value) in
            let title = value as? String ?? ""
            if (self.editTextTitle.text != title) {
                self.editTextTitle.text = title
            }
            if (self.isTaskEditing) {
                self.toolbar.title = title
            }
        }
        viewModel.taskDesc.addObserver { (value) in
            let desc = value as? String ?? ""
            if (self.editTextDesc.text != desc) {
                self.editTextDesc.text = desc
            }
        }
        viewModel.taskPriority.addObserver { value in
            let priority = Int(value as? Int ?? 0)
            if (self.segmentsPriority.selectedSegmentIndex != priority) {
                self.segmentsPriority.selectedSegmentIndex = priority
            }
            self.segmentsPriority.selectedSegmentTintColor = getPriorityColor(priority: priority)
        }
        viewModel.taskStart.addObserver { (value) in
            let startDate = value as? Int64 ?? 0
            self.editTextStartDate.setDate(millis: startDate)
        }
        viewModel.taskEnd.addObserver { (value) in
            let endDate = value as? Int64 ?? 0
            self.editTextEndDate.setDate(millis: endDate)
        }
        
        viewModel.load(editTaskId: intToKotlinLong(value: editTaskId))
    }
    
    // MARK: - User interacion
    @objc private func onTextTitleChanged() {
        viewModel.onChangedTitle(value: editTextTitle.text ?? "")
    }
    
    @objc private func onTextDescChanged() {
        viewModel.onChangedDesc(value: editTextDesc.text ?? "")
    }
    
    @IBAction func onPrioritySelected(_ sender: Any) {
        viewModel.onChangedPriority(value: Int32(self.segmentsPriority.selectedSegmentIndex))
    }
    
    private func onStartDateChanged(dateInMillis: Int64) {
        viewModel.onChangedStart(value: dateInMillis)
    }
    
    private func onEndDateChanged(dateInMillis: Int64) {
        viewModel.onChangedEnd(value: dateInMillis)
    }
    
    @IBAction func onSaveClicked(_ sender: Any) {
        view.endEditing(true)
        viewModel.onClickedSave()
    }
    
    @IBAction func onCancelClicked(_ sender: Any) {
        view.endEditing(true)
        viewModel.onClickedCancel()
    }
    
    @objc func dismissKeyboard(_ sender: UITapGestureRecognizer) {
        view.endEditing(true)
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
