package com.example.notepad.data.utils

import java.text.SimpleDateFormat
import java.util.*

const val DD_MM_YYYY_HH_MM_SS = "dd.MM.yyyy HH:mm"

private fun simpleDateFormat(
    pattern: String,
    locale: Locale = Locale.getDefault()
): SimpleDateFormat {
    val local =
        if (locale.toString().lowercase(Locale.getDefault()) == "en_us") Locale("en") else locale
    return SimpleDateFormat(pattern, local)
}

private fun format(
    pattern: String,
    date: Date
): String = simpleDateFormat(pattern).format(date)

private fun currentDate(): Date = Calendar.getInstance().time

private fun date(timeStamp: Long): Date = Calendar.getInstance().apply {
    timeInMillis = timeStamp
}.time

fun currentTimeInSeconds() = currentDate().time

fun showCurrentDate(timeStamp: Long): String = format(DD_MM_YYYY_HH_MM_SS, date(timeStamp))