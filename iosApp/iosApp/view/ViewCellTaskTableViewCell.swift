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
    @IBOutlet weak var textDate: UILabel!
    @IBOutlet weak var constraintHeightDate: NSLayoutConstraint!
    @IBOutlet weak var constraintTopContainerText: NSLayoutConstraint!
    
    internal func update(title: String, desc: String, priority: Int, date: Int64? = nil, isShowSeparator: Bool) {
        textTitle?.text = title
        textDesc?.text = desc
        viewPriority.backgroundColor = getPriorityColor(priority: Int(priority))
        textDate.isHidden = date == nil
        if let setDate = date {
            textDate.text = millisToDateString(millis: setDate)
            constraintHeightDate.constant = 32
            constraintTopContainerText.constant = 12
        } else {
            constraintHeightDate.constant = 0
            constraintTopContainerText.constant = 0
        }
        self.separatorInset = .init(top: 0, left: (isShowSeparator ? 0 : 1000), bottom: 0, right: 0)
        
        layoutIfNeeded()
    }
}
