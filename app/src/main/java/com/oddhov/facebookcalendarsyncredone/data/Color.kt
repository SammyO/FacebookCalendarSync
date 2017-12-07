package com.oddhov.facebookcalendarsyncredone.data

/**
 * Created by sammy on 07/12/2017.
 */
sealed class Color(val hexValue: String) {
    object Green : Color("#00ff00")
    object Orange : Color("#ffa500")
    object Purple : Color("#8b008b")
    object Blue : Color("#0000ff")
    object Red : Color("#ff0000")
}