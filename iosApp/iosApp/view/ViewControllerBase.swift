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
    
    internal func createViewModel() -> VM { fatalError("This method must be overridden") }
    internal func handleAction(action: Action, type: EventType) {}
    internal func initView() {}

    override func viewDidLoad() {
        super.viewDidLoad()

        initView()
        initViewModel()
    }
    
    internal func getProgressBar() -> UIActivityIndicatorView? {
        return nil
    }
    
    internal func initViewModel() {
        viewModel = createViewModel()
        
        viewModel.isVisibleProgressBar.addObserver { value in
            if (value as? Bool ?? false) {
                self.getProgressBar()?.startAnimating()
            } else {
                self.getProgressBar()?.stopAnimating()
            }
        }
        viewModel.action.addObserver { value in
            guard let action = value as? Action else { return }
            let type = action.getTypeIfNotHandled()
            
            if (type == EventType.exit) {
                self.getProgressBar()?.stopAnimating()
                self.dismiss(animated: true, completion: nil)
            } else if (type == EventType.error) {
                let errorText = (value as? Action)?.args?["errorText"] as? String ?? ""
                self.showError(text: errorText)
                print(errorText)
            } else if (type != nil) {
                self.handleAction(action: action, type: type!)
            }
        }
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
}
