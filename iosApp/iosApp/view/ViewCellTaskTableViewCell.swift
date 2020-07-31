//
//  ViewCellTaskTableViewCell.swift
//  iosApp
//
//  Created by Chechin Vadim on 18.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit

class ViewCellTaskTableViewCell: UITableViewCell {

    @IBOutlet weak var textTitle: UILabel!
    @IBOutlet weak var textDesc: UILabel!
    @IBOutlet weak var viewPriority: UIView!
    
    internal func update(title: String, desc: String, priority: Int) {
        textTitle?.text = title
        textDesc?.text = desc
        viewPriority.backgroundColor = getPriorityColor(priority: Int(priority))
    }
}
