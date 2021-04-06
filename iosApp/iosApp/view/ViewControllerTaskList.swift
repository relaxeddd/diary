//
//  ViewController.swift
//  iosApp
//
//  Created by Chechin Vadim on 11.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerTaskList<VM : IViewModelTaskList> : ViewControllerBase<VM>, UITableViewDataSource, UITableViewDelegate {
    
    // MARK: - Fields
    internal var tasks: [Task] = []
    var isScrolledToCurrentDayTask = false
    
    internal var tableViewTasks: UITableView? { get { return nil } }
    internal var textNoItems: UILabel? { get { return nil } }
    internal func getCompleteMenuItem(id: String, isCompleted: Bool) -> UIContextualAction { return UIContextualAction() }
    
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
    
    override func viewDidAppear(_ animated: Bool) {
        scrollToCurrentDayTask()
    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        if (identifier == "taskEdit") {
            if let taskIx = tableViewTasks?.indexPathForSelectedRow?.row {
                let task = tasks[taskIx]
                return !task.isHidden() && !task.isDateTask
            }
        }
        return true
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
        let cellIndex = indexPath.row
        let task = tasks[cellIndex]
        var isShowDate = false
        var isShowSeparator = false
        
        var previousTaskIndex = cellIndex - 1
        var nextTaskIndex = cellIndex + 1
        var previousTask: Task? = nil
        var nextTask: Task? = nil
        
        while previousTaskIndex >= 0 && (previousTask == nil || previousTask?.isHidden() == true) {
            previousTask = tasks[previousTaskIndex]
            previousTaskIndex -= 1
        }
        if (previousTask == nil || roundMillisToDayDate(millis: previousTask!.start) != roundMillisToDayDate(millis: task.start)) {
            isShowDate = true
        }
        
        while nextTaskIndex < tasks.count && (nextTask == nil || nextTask?.isHidden() == true) {
            nextTask = tasks[nextTaskIndex]
            nextTaskIndex += 1
        }
        if (nextTask == nil || roundMillisToDayDate(millis: nextTask!.start) != roundMillisToDayDate(millis: task.start)) {
            isShowSeparator = true
        }
        
        cell.update(title: task.title, desc: task.desc, priority: Int(task.priority), startTime: task.start, endTime: task.end, date: isShowDate ? task.start : nil, isCompleted: task.isCompleted, isDateTask: task.isDateTask, isHidden: task.isHidden(), isPersistent: task.isPersistent, isShowSeparator: (isShowSeparator && !task.isHidden()) || task.isPersistent)
        return cell
    }
    
    func tableView(_ tableView: UITableView, trailingSwipeActionsConfigurationForRowAt indexPath: IndexPath) -> UISwipeActionsConfiguration? {
        let task = self.tasks[indexPath.row]
        let deleteItem = UIContextualAction(style: .destructive, title: NSLocalizedString("delete", comment: "")) { (contextualAction, view, boolValue) in
            let id = task.parentId.isEmpty ? task.id : task.parentId
            self.viewModel.deleteTask(id: id)
        }
        let completeItem = getCompleteMenuItem(id: task.id, isCompleted: task.isCompleted)
        let swipeActions = UISwipeActionsConfiguration(actions: [completeItem, deleteItem])

        return swipeActions
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        var cellHeight = UITableView.automaticDimension
        if (indexPath.row < tasks.count) {
            let task = self.tasks[indexPath.row]
            if (task.isHidden()) {
                cellHeight = 0
            }
        }
        
        return cellHeight
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
        scrollToCurrentDayTask()
    }
    
    func scrollToCurrentDayTask() {
        if (isScrolledToCurrentDayTask) {
            return
        }
        
        var taskIndex = 0
        for task in tasks {
            if (isToday(millis: task.start)) {
                DispatchQueue.main.async() {
                    self.tableViewTasks?.scrollToRow(at: IndexPath(row: taskIndex, section: 0), at: .top, animated: true)
                }
                isScrolledToCurrentDayTask = true
                break
            }
            taskIndex += 1
            if (taskIndex >= tasks.count) {
                break
            }
        }
    }
}
