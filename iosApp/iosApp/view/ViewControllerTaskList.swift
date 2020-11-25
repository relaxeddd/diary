//
//  ViewController.swift
//  iosApp
//
//  Created by Chechin Vadim on 11.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerTaskList<VM : ViewModelTaskList>: ViewControllerBase<VM>, UITableViewDataSource, UITableViewDelegate {
    
    // MARK: - Fields
    internal var tasks: [Task] = []
    
    internal var tableViewTasks: UITableView? { get { return nil } }
    internal var textNoItems: UILabel? { get { return nil } }
    internal func getCompleteMenuItem(id: String) -> UIContextualAction { return UIContextualAction() }
    
    // MARK: - View
    override func initViewModel() {
        super.initViewModel()
        viewModel.load()
    }
    
    override func observeViewModel() {
        super.observeViewModel()
        
        viewModel.isVisibleTaskList.addObserver { value in
            self.tableViewTasks?.isHidden = !(value as? Bool ?? false)
        }
        viewModel.isVisibleTextNoItems.addObserver { value in
            self.textNoItems?.isHidden = !(value as? Bool ?? true)
        }
        viewModel.tasks.addObserver { value in
            let tasks = value as? [Task] ?? []
            self.updateList(tasks: tasks)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "taskEdit") {
            if let taskIx = tableViewTasks?.indexPathForSelectedRow?.row {
                let task = tasks[taskIx]
                let navigationController = segue.destination as? UINavigationController
                let viewControllerTaskCard = navigationController?.topViewController as? ViewControllerTaskCard
                let id = task.parentId.isEmpty ? task.id : task.parentId
                
                viewControllerTaskCard?.setEditTaskData(id: id, startDate: task.start)
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
        var isShowDate = false
        var isShowSeparator = false
        
        if (indexPath.row < (tasks.count - 1)) {
            let nextTask = tasks[indexPath.row + 1]
            
            if (roundMillisToDayDate(millis: nextTask.start) != roundMillisToDayDate(millis: task.start)) {
                isShowSeparator = true
            }
        } else {
            isShowSeparator = true
        }
        if (indexPath.row > 0) {
            let previousTask = tasks[indexPath.row - 1]
            if (roundMillisToDayDate(millis: previousTask.start) != roundMillisToDayDate(millis: task.start)) {
                isShowDate = true
            }
        } else {
            isShowDate = true
        }
        
        cell.update(title: task.title, desc: task.desc, priority: Int(task.priority), startTime: task.start, endTime: task.end,
                    date: isShowDate ? task.start : nil, isCompleted: task.isCompleted, isHidden: (task.isDateTask || task.isHidden()), isShowSeparator: isShowSeparator)
        return cell
    }
    
    func tableView(_ tableView: UITableView, trailingSwipeActionsConfigurationForRowAt indexPath: IndexPath) -> UISwipeActionsConfiguration? {
        let task = self.tasks[indexPath.row]
        let deleteItem = UIContextualAction(style: .destructive, title: NSLocalizedString("delete", comment: "")) { (contextualAction, view, boolValue) in
            let id = task.parentId.isEmpty ? task.id : task.parentId
            self.viewModel.deleteTask(id: id)
        }
        let completeItem = getCompleteMenuItem(id: task.id)
        let swipeActions = UISwipeActionsConfiguration(actions: [completeItem, deleteItem])

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
            self.tableViewTasks?.reloadData()
        }
    }
}
