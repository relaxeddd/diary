//
//  ViewControllerTaskListArchiveViewController.swift
//  iosApp
//
//  Created by Chechin Vadim on 08.08.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerTaskListArchive: ViewControllerTaskList<ViewModelTaskListArchive> {

    @IBOutlet weak var progressBarArchive: UIActivityIndicatorView!
    @IBOutlet weak var tableViewTasksArchive: UITableView!
    @IBOutlet weak var textNoTasksArchive: UILabel!
    
    override func createViewModel() -> ViewModelTaskListArchive { ViewModelTaskListArchive() }
    override var progressBar: UIActivityIndicatorView? { progressBarArchive }
    override var tableViewTasks: UITableView? { tableViewTasksArchive }
    override var textNoItems: UILabel? { textNoTasksArchive }
    
    override func getCompleteMenuItem(id: Int64) -> UIContextualAction {
        let menuItem = UIContextualAction(style: .normal, title: NSLocalizedString("restore", comment: "")) { (contextualAction, view, boolValue) in
            self.viewModel.restoreTask(id: id)
        }
        menuItem.backgroundColor = .orange
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
