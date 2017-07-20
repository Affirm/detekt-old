package com.affirm.central.ruleset

fun String.count(needle: String?): Int {
    if (needle == null || needle.isEmpty()) return 0

    var count = 0
    var i = 0
    while (indexOf(needle, i) >= 0) {
        count++
        i = indexOf(needle, i) + needle.length
    }
    return count
}
