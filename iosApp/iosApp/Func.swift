//
//  Func.swift
//  iosApp
//
//  Created by Chechin Vadim on 12.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import Foundation
import UIKit

func showToast(controller: UIViewController, message : String) {
    let toast = UIAlertController(title: nil, message: message, preferredStyle: .alert)
    
    controller.present(toast, animated: true)
    DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 3) {
        toast.dismiss(animated: true)
    }
}
