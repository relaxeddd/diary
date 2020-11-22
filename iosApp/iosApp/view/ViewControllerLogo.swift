//
//  ViewControllerLogo.swift
//  iosApp
//
//  Created by Chechin Vadim on 15.11.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import SharedCode

class ViewControllerLogo: ViewControllerBase<ViewModelLogo> {
    
    @IBOutlet weak var imageLogo: UIImageView!
    
    override func createViewModel() -> ViewModelLogo { ViewModelLogo() }
    override var progressBar: UIActivityIndicatorView? { nil }

    override func initView() {
        super.initView()
        
        imageLogo.image = UIImage(named: "ic_diary")
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            self.viewModel.checkAuthorized()
        }
    }
    
    override func handleAction(action: Action, type: EventType) {
        if (type == EventType.goScreenList) {
            self.performSegue(withIdentifier: "showList", sender: nil)
        } else if (type == EventType.goScreenLogin) {
            self.performSegue(withIdentifier: "showAuth", sender: nil)
        }
    }
}
