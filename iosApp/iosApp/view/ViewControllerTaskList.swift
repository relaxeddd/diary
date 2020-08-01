//
//  ViewController.swift
//  iosApp
//
//  Created by Chechin Vadim on 11.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode
import MaterialComponents

class ViewControllerTaskList: ViewControllerBase<ViewModelTaskList>, UITableViewDataSource, UITableViewDelegate {
    
    // MARK: - Fields
    @IBOutlet weak var tasksList: UITableView!
    @IBOutlet weak var textNoItems: UILabel!
    @IBOutlet weak var progressBar: UIActivityIndicatorView!
    
    internal var tasks: [Task] = []
    
    // MARK: - View
    override func createViewModel() -> ViewModelTaskList { ViewModelTaskList() }
    override func getProgressBar() -> UIActivityIndicatorView? { progressBar }
    
    override func initViewModel() {
        super.initViewModel()
        
        viewModel.isVisibleTaskList.addObserver { value in
            self.tasksList.isHidden = !(value as? Bool ?? false)
        }
        viewModel.isVisibleTextNoItems.addObserver { value in
            self.textNoItems.isHidden = !(value as? Bool ?? true)
        }
        viewModel.tasks.addObserver { value in
            let tasks = value as? [Task] ?? []
            self.updateList(tasks: tasks)
        }
        
        viewModel.load()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "taskEdit") {
            if let taskIx = tasksList?.indexPathForSelectedRow?.row {
                let task = tasks[taskIx]
                let navigationController = segue.destination as? UINavigationController
                let viewControllerTaskCard = navigationController?.topViewController as? ViewControllerTaskCard
                
                viewControllerTaskCard?.setEditTaskData(id: task.id)
            }
        }
    }
    
    // MARK: - Table view tasks list
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tasks.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "TaskCell") as! ViewCellTaskTableViewCell
        let task = tasks[indexPath.row]
        cell.update(title: task.title, desc: task.desc, priority: Int(task.priority))
        return cell
    }
    
    func tableView(_ tableView: UITableView, trailingSwipeActionsConfigurationForRowAt indexPath: IndexPath) -> UISwipeActionsConfiguration? {
        let deleteItem = UIContextualAction(style: .destructive, title: NSLocalizedString("delete", comment: "")) { (contextualAction, view, boolValue) in
            self.viewModel.deleteTask(id: self.tasks[indexPath.row].id)
        }
        let swipeActions = UISwipeActionsConfiguration(actions: [deleteItem])

        return swipeActions
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    // MARK: - Common
    private func updateList(tasks: [Task]) {
        var isNeedUpdate = false
        
        if (tasks.count == self.tasks.count) {
            for task in tasks {
                if (!self.tasks.contains(task)) {
                    isNeedUpdate = true
                    break
                }
            }
        } else {
            isNeedUpdate = true
        }
        
        if (isNeedUpdate) {
            self.tasks = tasks
            self.tasksList.reloadData()
        }
    }
}
