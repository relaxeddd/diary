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
    override func initView() {
        self.tasksList.isHidden = true
        self.textNoItems.isHidden = false
        self.progressBar.isHidden = true
    }
    
    override func initViewModel() {
        viewModel = ViewModelTaskList()
        viewModel.state.addObserver { (state) in
            self.onStateChanged(state: (state as? TaskListState))
        }
        viewModel.load()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "taskEdit") {
            if let taskIx = tasksList?.indexPathForSelectedRow?.row {
                let task = tasks[taskIx]
                let navigationController = segue.destination as? UINavigationController
                let viewControllerTaskCard = navigationController?.topViewController as? ViewControllerTaskCard
                
                viewControllerTaskCard?.setEditTaskData(id: task.id, title: task.title, desc: task.desc, priority: task.priority)
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
    private func onStateChanged(state: TaskListState?) {
        if state is NotLoadedTaskListState {
            self.tasksList.isHidden = true
            self.textNoItems.isHidden = false
            self.progressBar.stopAnimating()
        } else if state is LoadingTaskListState {
            let tasks = (state as? LoadingTaskListState)?.response?.data as? [Task] ?? []
            
            self.tasksList.isHidden = tasks.isEmpty
            self.textNoItems.isHidden = true
            self.progressBar.startAnimating()
            updateList(tasks: tasks)
        } else if state is SuccessTaskListState {
            let tasks = (state as? SuccessTaskListState)?.response.data as? [Task] ?? []
            
            self.progressBar.stopAnimating()
            self.tasksList.isHidden = false
            updateList(tasks: tasks)
        } else if state is ErrorTaskListState {
            self.tasksList.isHidden = true
            self.textNoItems.isHidden = false
            self.progressBar.stopAnimating()

            if let error = (state as? ErrorTaskListState)?.response.exception?.message {
                showError(text: error)
                print(error)
            }
        }
    }
    
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
            self.textNoItems.isHidden = !self.tasks.isEmpty
        }
    }
}
