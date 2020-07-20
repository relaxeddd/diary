//
//  ViewControllerBase.swift
//  iosApp
//
//  Created by Chechin Vadim on 20.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode
import MaterialComponents

class ViewControllerBase<VM : ViewModelBase>: UIViewController {
    
    internal var viewModel: VM!

    override func viewDidLoad() {
        super.viewDidLoad()

        initView()
        initViewModel()
    }
    
    internal func initView() {
        fatalError("This method must be overridden")
    }
    
    internal func initViewModel() {
        fatalError("This method must be overridden")
    }
    
    internal func showError(text: String) {
        let action = MDCSnackbarMessageAction()
        action.title = NSLocalizedString("dismiss", comment: "")
        
        let message = MDCSnackbarMessage()
        message.automaticallyDismisses = false
        message.shouldDismissOnOverlayTap = false
        message.text = NSLocalizedString("error", comment: "") + ": " + text
        message.action = action
        MDCSnackbarManager.show(message)
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
