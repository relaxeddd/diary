//
//  ViewControllerTaskCard.swift
//  iosApp
//
//  Created by Chechin Vadim on 19.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerTaskCard: ViewControllerBase<IViewModelTaskCard> {

    @IBOutlet weak var editTextTitle: UITextField!
    @IBOutlet weak var editTextDesc: UITextField!
    @IBOutlet weak var buttonSave: UIBarButtonItem!
    @IBOutlet weak var buttonCancel: UIBarButtonItem!
    @IBOutlet weak var progressBarCard: UIActivityIndicatorView!
    @IBOutlet weak var editTextDate: EditTextDate!
    @IBOutlet weak var editTextStartTime: EditTextDate!
    @IBOutlet weak var editTextEndTime: EditTextDate!
    @IBOutlet weak var switchIsCompleted: UISwitch!
    @IBOutlet weak var switchIsPersistent: UISwitch!
    @IBOutlet weak var textIsPersistent: UILabel!
    @IBOutlet weak var textDate: UILabel!
    @IBOutlet weak var textStartTime: UILabel!
    @IBOutlet weak var textEndTime: UILabel!
    @IBOutlet weak var textRepeat: UILabel!
    @IBOutlet weak var constraintTopIsCompleted: NSLayoutConstraint!
    
    private var editTaskId: String? = nil
    private var startDate: Int64 = 0
    private var isPersistent: Bool = false
    @IBOutlet weak var toolbar: UINavigationItem!
    @IBOutlet weak var segmentsPriority: UISegmentedControl!
    @IBOutlet weak var segmentsRepeat: UISegmentedControl!
    
    private var isTaskEditing: Bool { self.editTaskId != nil }
    
    override func createViewModel() -> IViewModelTaskCard { ViewModelCreator().createTaskCardViewModel() }
    override var progressBar: UIActivityIndicatorView? { progressBarCard }
    
    // MARK: - Arguments
    func setEditTaskData(id: String, startDate: Int64) {
        editTaskId = id
        self.startDate = startDate
    }
    
    func setCreateTaskData(isPersistent: Bool) {
        self.isPersistent = isPersistent
    }
    
    // MARK: - Init
    override func initViewModel() {
        super.initViewModel()
        viewModel.load(editTaskParentId: editTaskId, startDate: startDate)
    }
    
    override func initView() {
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.dismissKeyboard (_:)))
        self.view.addGestureRecognizer(tapGesture)
        
        editTextTitle.addTarget(self, action: #selector(onTextTitleChanged), for: .editingChanged)
        editTextDesc.addTarget(self, action: #selector(onTextDescChanged), for: .editingChanged)
        editTextDate.onDateChanged = { millis in
            self.onStartDateChanged(dateInMillis: millis)
        }
        editTextStartTime.onDateChanged = { millis in
            self.onStartDateChanged(dateInMillis: millis)
        }
        editTextEndTime.onDateChanged = { millis in
            self.onEndDateChanged(dateInMillis: millis)
        }
        
        if (isPersistent) {
            viewModel.onChangedIsPersistent(value: true)
        }
    }
    
    override func observeViewModel() {
        super.observeViewModel()
        
        viewModel.isEnabledButtonSave.addObserver { value in
            self.buttonSave.isEnabled = value as? Bool ?? false
        }
        viewModel.taskTitle.addObserver { (value) in
            let title = value as String? ?? ""
            if (self.editTextTitle.text != title) {
                self.editTextTitle.text = title
            }
            if (self.isTaskEditing) {
                self.toolbar.title = title
            }
        }
        viewModel.taskDesc.addObserver { (value) in
            let desc = value as String? ?? ""
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
        viewModel.taskRepeat.addObserver { value in
            let priority = Int(value as? Int ?? 0)
            if (self.segmentsRepeat.selectedSegmentIndex != priority) {
                self.segmentsRepeat.selectedSegmentIndex = priority
            }
        }
        viewModel.taskStart.addObserver { (value) in
            let startDate = value as? Int64 ?? 0
            self.editTextDate.setDate(millis: startDate)
            self.editTextStartTime.setDate(millis: startDate)
        }
        viewModel.taskEnd.addObserver { (value) in
            let endDate = value as? Int64 ?? 0
            self.editTextEndTime.setDate(millis: endDate)
        }
        viewModel.taskIsCompleted.addObserver { (value) in
            let isCompleted = value as? Bool ?? false
            self.switchIsCompleted.isOn = isCompleted
        }
        viewModel.taskIsPersistent.addObserver { (value) in
            let isPersistent = value as? Bool ?? false
            self.switchIsPersistent.isOn = isPersistent
            self.textDate.isHidden = isPersistent
            self.editTextDate.isHidden = isPersistent
            self.textStartTime.isHidden = isPersistent
            self.editTextStartTime.isHidden = isPersistent
            self.textEndTime.isHidden = isPersistent
            self.editTextEndTime.isHidden = isPersistent
            self.textRepeat.isHidden = isPersistent
            self.segmentsRepeat.isHidden = isPersistent
            self.constraintTopIsCompleted.constant = isPersistent ? -158 : 16
        }
    }
    
    override func handleAction(action: Action, type: EventType) {
        if (type == EventType.navigationDialogRepetitiveTaskComplete) {
            let alert = UIAlertController(title: NSLocalizedString("complete_repetitive_task_desc", comment: ""), message: nil, preferredStyle: .alert)
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("only_this", comment: ""), style: .default, handler: { (UIAlertAction) in
                alert.dismiss(animated: true)
                self.viewModel.onClickedCompleteChildTask()
            }))
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("all_repetitive_tasks", comment: ""), style: .default, handler: { (UIAlertAction) in
                alert.dismiss(animated: true)
                self.viewModel.onClickedCompleteParentTask()
            }))
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("cancel", comment: ""), style: .cancel, handler: { (UIAlertAction) in
                alert.dismiss(animated: true)
            }))
            self.present(alert, animated: true)
        }
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
    
    @IBAction func onIsPersistentChanged(_ sender: Any) {
        viewModel.onChangedIsPersistent(value: switchIsPersistent.isOn)
    }
    
    @IBAction func onRepeatSelected(_ sender: Any) {
        viewModel.onChangedRepeat(value: Int32(self.segmentsRepeat.selectedSegmentIndex))
    }
    
    private func onStartDateChanged(dateInMillis: Int64) {
        viewModel.onChangedStart(value: dateInMillis)
    }
    
    private func onEndDateChanged(dateInMillis: Int64) {
        viewModel.onChangedEnd(value: dateInMillis)
    }
    
    @IBAction func onIsCompletedChanged(_ sender: Any) {
        viewModel.onChangedIsCompleted(value: switchIsCompleted.isOn)
    }
    
    @IBAction func onSaveClicked(_ sender: Any) {
        //TODO check multiple saving
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
