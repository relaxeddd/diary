//
//  ViewController.swift
//  iosApp
//
//  Created by Chechin Vadim on 11.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewController: UIViewController {
    
    //private var viewModel: ViewModelTaskList?
    
    @IBOutlet weak var progressBar: UIActivityIndicatorView!
    @IBOutlet weak var tasksList: UITableView!
    @IBOutlet weak var textNoTasks: UIActivityIndicatorView!
    
    @IBAction func click(_ sender: Any) {
        let viewModel = ViewModelTaskList()
//        viewModel.listTasks.addObserver { (state) in
//            if state is LoadingTaskListState {
//                self.tasksList.isHidden = true
//                self.textNoTasks.isHidden = true
//                self.progressBar.startAnimating()
//            } else if state is SuccessTaskListState {
//                self.tasksList.isHidden = false
//                self.textNoTasks.isHidden = true
//                self.progressBar.stopAnimating()
//
//                if let tasks = (state as? SuccessTaskListState)?.response.data {
//                    print(tasks)
//                } else {
//
//                }
//            } else if state is ErrorTaskListState {
//                self.tasksList.isHidden = true
//                self.textNoTasks.isHidden = false
//                self.progressBar.stopAnimating()
//
//                if let error = (state as? ErrorTaskListState)?.response.exception?.message {
//                    showToast(controller: self, message: error)
//                    print(error)
//                }
//            }
//        }
//
//        viewModel.loadTasks()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tasksList.isHidden = true
        self.textNoTasks.isHidden = false

//        viewModel = ViewModelTaskList()
//        viewModel.listTasks.addObserver { (state) in
//            if state is LoadingTaskListState {
//                self.tasksList.isHidden = true
//                self.textNoTasks.isHidden = true
//                self.progressBar.startAnimating()
//            } else if state is SuccessTaskListState {
//                self.tasksList.isHidden = false
//                self.textNoTasks.isHidden = true
//                self.progressBar.stopAnimating()
//
//                if let tasks = (state as? SuccessTaskListState)?.response.data {
//                    print(tasks)
//                } else {
//
//                }
//            } else if state is ErrorTaskListState {
//                self.tasksList.isHidden = true
//                self.textNoTasks.isHidden = false
//                self.progressBar.stopAnimating()
//
//                if let error = (state as? ErrorTaskListState)?.response.exception?.message {
//                    showToast(controller: self, message: error)
//                    print(error)
//                }
//            }
//        }
        
        //viewModel.loadTasks()
    }

    /*
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
}
