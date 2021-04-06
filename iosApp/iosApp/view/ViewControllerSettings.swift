//
//  ViewControllerSettings.swift
//  iosApp
//
//  Created by Chechin Vadim on 16.11.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerSettings: ViewControllerBase<IViewModelSettings> {
    
    @IBOutlet weak var progressBarSettings: UIActivityIndicatorView!

    override func createViewModel() -> IViewModelSettings { ViewModelCreator().createSettingsViewModel() }
    override var progressBar: UIActivityIndicatorView? { progressBarSettings }
    
    @IBAction func onClickedSaveTasks(_ sender: Any) {
        viewModel.onClickedSaveTasks()
    }
    
    @IBAction func onClickedLoadTasks(_ sender: Any) {
        viewModel.onClickedLoadTasks()
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
        } else if (type == EventType.navigationDialogConfirmTasksSave) {
            let alert = UIAlertController(title: NSLocalizedString("tasks_save_desc", comment: ""), message: nil, preferredStyle: .alert)
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("yes", comment: ""), style: .default, handler: { (UIAlertAction) in
                alert.dismiss(animated: true)
                self.viewModel.onConfirmedSave()
            }))
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("no", comment: ""), style: .cancel, handler: { (UIAlertAction) in
                alert.dismiss(animated: true)
            }))
            self.present(alert, animated: true)
        } else if (type == EventType.navigationDialogConfirmTasksLoad) {
            let alert = UIAlertController(title: NSLocalizedString("tasks_load_desc", comment: ""), message: nil, preferredStyle: .alert)
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("yes", comment: ""), style: .default, handler: { (UIAlertAction) in
                alert.dismiss(animated: true)
                self.viewModel.onConfirmedLoad()
            }))
            alert.addAction(UIAlertAction.init(title: NSLocalizedString("no", comment: ""), style: .cancel, handler: { (UIAlertAction) in
                alert.dismiss(animated: true)
            }))
            self.present(alert, animated: true)
        }
    }
}
