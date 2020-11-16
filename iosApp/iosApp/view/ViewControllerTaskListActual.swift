//
//  ViewControllerTaskListActual.swift
//  iosApp
//
//  Created by Chechin Vadim on 08.08.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerTaskListActual: ViewControllerTaskList<ViewModelTaskListActual> {

    @IBOutlet weak var progressBarActual: UIActivityIndicatorView!
    @IBOutlet weak var tableViewTasksActual: UITableView!
    @IBOutlet weak var textNoTasksActual: UILabel!
    
    override func createViewModel() -> ViewModelTaskListActual { ViewModelTaskListActual() }
    override var progressBar: UIActivityIndicatorView? { progressBarActual }
    override var tableViewTasks: UITableView? { tableViewTasksActual }
    override var textNoItems: UILabel? { textNoTasksActual }
    
    override func getCompleteMenuItem(id: Int64) -> UIContextualAction {
        let menuItem = UIContextualAction(style: .normal, title: NSLocalizedString("complete", comment: "")) { (contextualAction, view, boolValue) in
            self.viewModel.completeTask(id: id)
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
        }
    }
}
