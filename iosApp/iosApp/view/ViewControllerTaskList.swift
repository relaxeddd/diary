//
//  ViewController.swift
//  iosApp
//
//  Created by Chechin Vadim on 11.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerTaskList: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    // MARK: - Fields
    @IBOutlet weak var tasksList: UITableView!
    @IBOutlet weak var textNoItems: UILabel!
    @IBOutlet weak var progressBar: UIActivityIndicatorView!
    
    private var viewModel: ViewModelTaskList!
    
    internal var tasks: [Task] = []
    
    // MARK: - View
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tasksList.isHidden = true
        self.textNoItems.isHidden = false
        self.progressBar.isHidden = true

        viewModel = ViewModelTaskList()
        viewModel.listTasks.addObserver { (state) in
            self.onStateChanged(state: (state as? TaskListState))
        }
        viewModel.loadTasks()
    }
    
    /*
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    
    // MARK: - Table view tasks list
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tasks.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "TaskCell")
        cell?.textLabel?.text = tasks[indexPath.row].title
        cell?.detailTextLabel?.text = tasks[indexPath.row].desc
        return cell!
    }
    
    // MARK: - Common
    private func onStateChanged(state: TaskListState?) {
        if state is NotLoadedTaskListState {
            self.tasksList.isHidden = true
            self.textNoItems.isHidden = false
            self.progressBar.stopAnimating()
        } else if state is LoadingTaskListState {
            self.tasksList.isHidden = true
            self.textNoItems.isHidden = true
            self.progressBar.startAnimating()
        } else if state is SuccessTaskListState {
            self.tasksList.isHidden = false
            self.textNoItems.isHidden = true
            self.progressBar.stopAnimating()

            if let tasksAnswer = (state as? SuccessTaskListState)?.response.data {
                self.tasks = tasksAnswer as! [Task]
                print(self.tasks)
            } else {

            }
            self.tasksList.reloadData()
        } else if state is ErrorTaskListState {
            self.tasksList.isHidden = true
            self.textNoItems.isHidden = false
            self.progressBar.stopAnimating()

            if let error = (state as? ErrorTaskListState)?.response.exception?.message {
                showToast(controller: self, message: error)
                print(error)
            }
        }
    }
}