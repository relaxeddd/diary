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
    @IBOutlet weak var textStartTime: UILabel!
    @IBOutlet weak var textEndTime: UILabel!
    
    internal func update(title: String, desc: String, priority: Int, startTime: Int64, endTime: Int64, date: Int64? = nil, isShowSeparator: Bool) {
        textTitle?.text = title
        textDesc?.text = desc
        textStartTime?.text = millisToTimeString(millis: startTime)
        textEndTime?.text = millisToTimeString(millis: endTime)
        viewPriority.backgroundColor = getPriorityColor(priority: Int(priority))
        textDate.isHidden = date == nil
        if let setDate = date {
            var textDayStr: String
            
            if (isToday(millis: setDate)) {
                textDayStr = NSLocalizedString("today", comment: "")
            } else if (isYesterday(millis: setDate)) {
                textDayStr = NSLocalizedString("yesterday", comment: "")
            } else if (isTomorrow(millis: setDate)) {
                textDayStr = NSLocalizedString("tomorrow", comment: "")
            } else {
                textDayStr = millisToDateString(millis: setDate)
            }
            if (!isCurrentYear(millis: setDate)) {
                textDayStr += ", " + String(getYear(millis: setDate))
            }
            
            textDate.text = textDayStr
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
