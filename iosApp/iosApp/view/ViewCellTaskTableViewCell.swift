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
    @IBOutlet weak var constraintMarginEndTime: NSLayoutConstraint!
    @IBOutlet weak var constraintMarginStartTime: NSLayoutConstraint!
    
    internal func update(title: String, desc: String, priority: Int, startTime: Int64, endTime: Int64, date: Int64? = nil, isCompleted: Bool, isDateTask: Bool, isHidden: Bool, isPersistent: Bool, isShowSeparator: Bool) {
        
        textTitle?.attributedText = nil
        textDesc?.attributedText = nil
        textStartTime?.attributedText = nil
        textEndTime?.attributedText = nil
        
        if (isCompleted) {
            let strikethroughTitle: NSMutableAttributedString =  NSMutableAttributedString(string: title)
            strikethroughTitle.addAttribute(NSAttributedString.Key.strikethroughStyle, value: 1, range: NSMakeRange(0, strikethroughTitle.length))
            textTitle?.attributedText = strikethroughTitle
            
            let strikethroughDesc: NSMutableAttributedString =  NSMutableAttributedString(string: desc)
            strikethroughDesc.addAttribute(NSAttributedString.Key.strikethroughStyle, value: 1, range: NSMakeRange(0, strikethroughDesc.length))
            textDesc?.attributedText = strikethroughDesc
            
            let strikethroughStartTime: NSMutableAttributedString =  NSMutableAttributedString(string: millisToTimeString(millis: startTime))
            strikethroughTitle.addAttribute(NSAttributedString.Key.strikethroughStyle, value: 1, range: NSMakeRange(0, strikethroughStartTime.length))
            textStartTime?.attributedText = strikethroughStartTime
            
            let strikethroughEndTime: NSMutableAttributedString =  NSMutableAttributedString(string: millisToTimeString(millis: endTime))
            strikethroughTitle.addAttribute(NSAttributedString.Key.strikethroughStyle, value: 1, range: NSMakeRange(0, strikethroughEndTime.length))
            textEndTime?.attributedText = strikethroughEndTime
        } else {
            textTitle?.text = title
            textDesc?.text = desc
            textStartTime?.text = millisToTimeString(millis: startTime)
            textEndTime?.text = millisToTimeString(millis: endTime)
        }
        viewPriority.backgroundColor = getPriorityColor(priority: Int(priority))
        textDate.isHidden = date == nil || isHidden || isPersistent
        
        textTitle.isHidden = isHidden || isDateTask
        textDesc.isHidden = isHidden || isDateTask
        textStartTime.isHidden = isHidden || isDateTask || isPersistent
        textEndTime.isHidden = isHidden || isDateTask || isPersistent
        viewPriority.isHidden = isHidden || isDateTask
        self.isHidden = isHidden
        
        constraintMarginStartTime.constant = !isPersistent ? 4 : -30
        constraintMarginEndTime.constant = !isPersistent ? 8 : -30
        
        if let setDate = date {
            var textDayStr: String = ""
            var isTodayDate = false
            
            if (isToday(millis: setDate)) {
                textDayStr = NSLocalizedString("today", comment: "")
                isTodayDate = true
            } else if (isYesterday(millis: setDate)) {
                textDayStr = NSLocalizedString("yesterday", comment: "")
            } else if (isTomorrow(millis: setDate)) {
                textDayStr = NSLocalizedString("tomorrow", comment: "")
            } else if (isAfterTomorrow(millis: setDate)) {
                textDayStr = NSLocalizedString("after_tomorrow", comment: "")
            } else {
                
            }
            if (!textDayStr.isEmpty) {
                textDayStr += ", "
            }
            textDayStr += getWeekDay(millis: setDate) + ", " + millisToDateString(millis: setDate)
            if (!isCurrentYear(millis: setDate)) {
                textDayStr += ", " + String(getYear(millis: setDate))
            }
            
            textDate.textColor = isTodayDate ? UIColor.cyan : UIColor.gray
            textDate.text = textDayStr
            constraintHeightDate.constant = isHidden || isPersistent ? 0 : 20
            constraintTopContainerText.constant = isDateTask ? 14 : (isHidden || isPersistent ? 0 : 8)
        } else {
            constraintHeightDate.constant = 0
            constraintTopContainerText.constant = 0
        }
        self.separatorInset = .init(top: 0, left: (isShowSeparator ? 0 : 1000), bottom: 0, right: 0)
        
        layoutIfNeeded()
    }
}
