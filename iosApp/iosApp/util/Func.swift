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

let SECOND_IN_MILLIS = 1000 * 60 * 60 * 24
let MINUTE_IN_MILLIS = 1000 * 60 * 60 * 24
let DAY_IN_MILLIS = 1000 * 60 * 60 * 24
let WEEK_IN_MILLIS = 1000 * 60 * 60 * 24
let MONTH_IN_MILLIS = 1000 * 60 * 60 * 24
let YEAR_IN_MILLIS = 1000 * 60 * 60 * 24

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
            return UIColor.orange
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
    formatter.timeStyle = .short
    return formatter.string(from: millisToDate(millis: millis))
}

func millisToTimeString(millis: Int64) -> String {
    let formatter = DateFormatter()
    formatter.dateStyle = .none
    formatter.timeStyle = .short
    return formatter.string(from: millisToDate(millis: millis))
}

func millisToDateString(millis: Int64) -> String {
    let formatter = DateFormatter()
    //formatter.dateStyle = .medium
    formatter.timeStyle = .none
    formatter.dateFormat = "d MMMM"
    return formatter.string(from: millisToDate(millis: millis))
}

func isToday(millis: Int64) -> Bool {
    let currentDayMillis = roundMillisToDayDate(millis: dateToMillis(date: Date()))
    return currentDayMillis == roundMillisToDayDate(millis: millis)
}

func isYesterday(millis: Int64) -> Bool {
    let currentDayMillis = roundMillisToDayDate(millis: dateToMillis(date: Date()))
    return currentDayMillis == (roundMillisToDayDate(millis: millis) + Int64(DAY_IN_MILLIS))
}

func isTomorrow(millis: Int64) -> Bool {
    let currentDayMillis = roundMillisToDayDate(millis: dateToMillis(date: Date()))
    return currentDayMillis == (roundMillisToDayDate(millis: millis) - Int64(DAY_IN_MILLIS))
}

func isAfterTomorrow(millis: Int64) -> Bool {
    let currentDayMillis = roundMillisToDayDate(millis: dateToMillis(date: Date()))
    return currentDayMillis == (roundMillisToDayDate(millis: millis) - Int64(2 * DAY_IN_MILLIS))
}

func getWeekDay(millis: Int64) -> String {
    let date = Date(timeIntervalSince1970: TimeInterval(millis / 1000))
    var calendar = Calendar(identifier: Calendar.Identifier.gregorian)
    let weekDayIx = calendar.component(Calendar.Component.weekday, from: date)
    calendar.locale = NSLocale(localeIdentifier: "ru") as Locale
    
    return calendar.shortWeekdaySymbols[weekDayIx - 1]
}

func getYear(millis: Int64) -> Int {
    let date = Date(timeIntervalSince1970: TimeInterval(millis / 1000))
    let calendar = Calendar(identifier: Calendar.Identifier.gregorian)
    let year = calendar.component(Calendar.Component.year, from: date)
    
    return year
}

func isCurrentYear(millis: Int64) -> Bool {
    let currentYear = Calendar(identifier: Calendar.Identifier.gregorian).component(Calendar.Component.year, from: Date())
    let year = getYear(millis: millis)
    
    return currentYear == year
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
