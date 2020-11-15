//
//  ViewControllerBase.swift
//  iosApp
//
//  Created by Chechin Vadim on 20.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerBase<VM : ViewModelBase>: UIViewController {
    
    internal var viewModel: VM!
    internal var progressBar: UIActivityIndicatorView? { get { return nil } }
    
    internal func createViewModel() -> VM { fatalError("This method must be overridden") }
    internal func handleAction(action: Action, type: EventType) {}
    internal func initView() {}

    override func viewDidLoad() {
        super.viewDidLoad()

        initViewModel()
        initView()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        viewModel.onFill()
        observeViewModel()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        viewModel.onCleared()
    }
    
    internal func initViewModel() {
        viewModel = createViewModel()
    }
    
    internal func observeViewModel() {
        viewModel.isVisibleProgressBar.addObserver { value in
            if (value as? Bool ?? false) {
                self.progressBar?.startAnimating()
            } else {
                self.progressBar?.stopAnimating()
            }
        }
        viewModel.action.addObserver { value in
            guard let action = value else { return }
            let type = action.getTypeIfNotHandled()
            
            if (type == EventType.exit) {
                self.progressBar?.stopAnimating()
                self.dismiss(animated: true, completion: nil)
            } else if (type == EventType.error) {
                let errorText = (value)?.args?["errorText"] as? String ?? ""
                self.showError(text: errorText)
                print(errorText)
            } else if (type != nil) {
                self.handleAction(action: action, type: type!)
            }
        }
    }
    
    internal func showError(text: String) {
        let alert = UIAlertController(title: NSLocalizedString("error", comment: ""), message: text, preferredStyle: .alert)
        alert.addAction(UIAlertAction.init(title: NSLocalizedString("dismiss", comment: ""), style: .cancel, handler: { (UIAlertAction) in
            alert.dismiss(animated: true)
        }))
        self.present(alert, animated: true)
    }
}
