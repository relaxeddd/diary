//
//  Func.swift
//  iosApp
//
//  Created by Chechin Vadim on 12.07.2020.
//  Copyright © 2020 Chechin Vadim. All rights reserved.
//

import Foundation
import UIKit
import SharedCode

let DAY_IN_MILLIS = 1000 * 60 * 60 * 24

func showToast(controller: UIViewController, message : String) {
    let toast = UIAlertController(title: nil, message: message, preferredStyle: .alert)
    
    controller.present(toast, animated: true)
    DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 3) {
        toast.dismiss(animated: true)
    }
}

func getPriorityColor(priority: Int) -> UIColor {
    switch priority {
        case 1:
            return UIColor.yellow
        case 2:
            return UIColor.red
        default:
            return UIColor.green
    }
}

func millisToDate(millis: Int64) -> Date {
    return Date(timeIntervalSince1970: TimeInterval(millis / 1000))
}

func millisToDateTimeString(millis: Int64) -> String {
    let formatter = DateFormatter()
    //formatter.dateFormat = "HH:mm:ss dd-MM-yyyy"
    formatter.dateStyle = .medium
    formatter.timeStyle = .medium
    return formatter.string(from: millisToDate(millis: millis))
}

func millisToDateString(millis: Int64) -> String {
    let formatter = DateFormatter()
    //formatter.dateFormat = "HH:mm:ss dd-MM-yyyy"
    formatter.dateStyle = .medium
    formatter.timeStyle = .none
    return formatter.string(from: millisToDate(millis: millis))
}

func roundMillisToDayDate(millis: Int64) -> Int64 {
    return Int64(Int(millis) / DAY_IN_MILLIS * DAY_IN_MILLIS)
}

func dateToMillis(date: Date) -> Int64 {
    return Int64((date.timeIntervalSince1970 * 1000.0).rounded())
}

func intToKotlinLong(value: Int64?) -> KotlinLong? {
    var convertedValue: KotlinLong? = nil
    if let nonNilValue = value {
        convertedValue = KotlinLong(value: nonNilValue)
    }
    return convertedValue
}

func intToKotlinLong(value: Int64) -> KotlinLong {
    return KotlinLong(value: value)
}
