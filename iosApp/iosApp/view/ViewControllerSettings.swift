//
//  ViewControllerSettings.swift
//  iosApp
//
//  Created by Chechin Vadim on 16.11.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerSettings: ViewControllerBase<ViewModelSettings> {
    
    @IBOutlet weak var progressBarSettings: UIActivityIndicatorView!

    override func createViewModel() -> ViewModelSettings { ViewModelSettings() }
    override var progressBar: UIActivityIndicatorView? { progressBarSettings }
    
    @IBAction func onClickedSaveTasks(_ sender: Any) {
        //TODO
    }
    
    @IBAction func onClickedLoadTasks(_ sender: Any) {
        //TODO
    }
    
    @IBAction func onClickedLogout(_ sender: Any) {
        viewModel.onClickedLogout()
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
