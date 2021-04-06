//
//  ViewControllerTaskListPersistent.swift
//  iosApp
//
//  Created by Chechin Vadim on 27.11.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerTaskListPersistent: ViewControllerTaskList<IViewModelTaskListPersistent> {
    
    @IBOutlet weak var progressBarPersistent: UIActivityIndicatorView!
    @IBOutlet weak var textNoTasksPersistent: UILabel!
    @IBOutlet weak var tableViewTasksPersistent: UITableView!
    
    
    override func createViewModel() -> IViewModelTaskListPersistent { ViewModelCreator().createTaskListPersistentViewModel() }
    override var progressBar: UIActivityIndicatorView? { progressBarPersistent }
    override var tableViewTasks: UITableView? { tableViewTasksPersistent }
    override var textNoItems: UILabel? { textNoTasksPersistent }
    
    override func getCompleteMenuItem(id: String, isCompleted: Bool) -> UIContextualAction {
        let menuItem = UIContextualAction(style: .normal, title: NSLocalizedString(isCompleted ? "restore" : "complete", comment: "")) { (contextualAction, view, boolValue) in
            if (isCompleted) {
                self.viewModel.onClickedRestoreTask(id: id)
            } else {
                self.viewModel.onClickedCompleteTask(id: id)
            }
        }
        menuItem.backgroundColor = isCompleted ? .orange : .green
        
        return menuItem
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "taskCreate") {
            let navigationController = segue.destination as? UINavigationController
            let viewControllerTaskCard = navigationController?.topViewController as? ViewControllerTaskCard
            viewControllerTaskCard?.setCreateTaskData(isPersistent: true)
        } else {
            super.prepare(for: segue, sender: sender)
        }
    }
    
    override func handleAction(action: Action, type: EventType) {
        if (type == EventType.goScreenLogin) {
            weak var presentingViewController = self.presentingViewController
            self.dismiss(animated: true) {
                presentingViewController?.performSegue(withIdentifier: "showAuth", sender: nil)
            }
        }
    }
}
