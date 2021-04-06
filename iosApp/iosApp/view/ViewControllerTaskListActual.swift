//
//  ViewControllerTaskListActual.swift
//  iosApp
//
//  Created by Chechin Vadim on 08.08.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerTaskListActual: ViewControllerTaskList<IViewModelTaskListActual> {

    @IBOutlet weak var progressBarActual: UIActivityIndicatorView!
    @IBOutlet weak var tableViewTasksActual: UITableView!
    @IBOutlet weak var textNoTasksActual: UILabel!
    
    override func createViewModel() -> IViewModelTaskListActual { ViewModelCreator().createTaskListActualViewModel() }
    override var progressBar: UIActivityIndicatorView? { progressBarActual }
    override var tableViewTasks: UITableView? { tableViewTasksActual }
    override var textNoItems: UILabel? { textNoTasksActual }
    
    override func getCompleteMenuItem(id: String, isCompleted: Bool) -> UIContextualAction {
        let menuItem = UIContextualAction(style: .normal, title: NSLocalizedString("complete", comment: "")) { (contextualAction, view, boolValue) in
            self.viewModel.onClickedCompleteTask(id: id)
        }
        menuItem.backgroundColor = .green
        return menuItem
    }
    
    override func handleAction(action: Action, type: EventType) {
        if (type == EventType.goScreenLogin) {
            weak var presentingViewController = self.presentingViewController
            self.dismiss(animated: true) {
                presentingViewController?.performSegue(withIdentifier: "showAuth", sender: nil)
            }
        } else if (type == EventType.navigationDialogRepetitiveTaskComplete) {
            let id = action.args?["id"] as? String ?? ""
            
            let alert = UIAlertController(title: NSLocalizedString("complete_repetitive_task_desc", comment: ""), message: nil, preferredStyle: .alert)
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("only_this", comment: ""), style: .default, handler: { (UIAlertAction) in
                alert.dismiss(animated: true)
                self.viewModel.onClickedCompleteChildTask(id: id)
            }))
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("all_repetitive_tasks", comment: ""), style: .default, handler: { (UIAlertAction) in
                alert.dismiss(animated: true)
                self.viewModel.onClickedCompleteParentTask(id: id)
            }))
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("cancel", comment: ""), style: .cancel, handler: { (UIAlertAction) in
                alert.dismiss(animated: true)
            }))
            self.present(alert, animated: true)
        }
    }
}
