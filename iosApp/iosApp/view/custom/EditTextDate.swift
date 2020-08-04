//
//  EditTextDate.swift
//  iosApp
//
//  Created by Chechin Vadim on 02.08.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import UIKit
import CoreGraphics

class EditTextDate: UITextField, UITextFieldDelegate {
    
    private let BORDER_RADIUS: CGFloat = 5
    private let BORDER_WIDTH_UNSELECTED: CGFloat = 0.25
    private let BORDER_WIDTH_SELECTED: CGFloat = 1.2
    private let BORDER_COLOR_SELECTED = UIColor.systemBlue.cgColor
    private let BORDER_COLOR_UNSELECTED = UIColor.lightGray.cgColor
    private let datePicker = UIDatePicker.init()
    
    private var currentTimeMillis: Int64 = dateToMillis(date: Date())
    internal var onDateChanged: ((_: Int64) -> ())?
    
    // MARK: - View Init
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)!
        initView()
    }
    
    required override init(frame: CGRect) {
        super.init(frame: frame)
        initView()
    }
    
    private func initView() {
        delegate = self
        inputView = datePicker
        layer.cornerRadius = BORDER_RADIUS
        clipsToBounds = true
        layer.borderWidth = BORDER_WIDTH_UNSELECTED
        layer.borderColor = BORDER_COLOR_UNSELECTED
        
        datePicker.addTarget(self, action: #selector(onDateChanged(_:)), for: .valueChanged)
    }
    
    override func caretRect(for position: UITextPosition) -> CGRect {
        return CGRect.zero
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        layer.borderWidth = BORDER_WIDTH_SELECTED
        layer.borderColor = BORDER_COLOR_SELECTED
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        layer.borderWidth = BORDER_WIDTH_UNSELECTED
        layer.borderColor = BORDER_COLOR_UNSELECTED
    }
    
    // MARK: - Time logic
    @objc private func onDateChanged(_ sender: UIDatePicker) {
        onCurrentTimeChanged(newTimeMillis: dateToMillis(date: datePicker.date))
    }
    
    func setDate(millis: Int64) {
        onCurrentTimeChanged(newTimeMillis: millis, isNotify: false)
    }
    
    private func onCurrentTimeChanged(newTimeMillis: Int64, isNotify: Bool = true) {
        if (newTimeMillis != currentTimeMillis) {
            currentTimeMillis = newTimeMillis
            text = millisToDateString(millis: newTimeMillis)
            datePicker.date = millisToDate(millis: newTimeMillis)
            if (isNotify) {
                onDateChanged?.self(newTimeMillis)
            }
        }
    }
}

protocol ListenerDateChanged {
    func onDateChanged(millis: Int64?)
}
