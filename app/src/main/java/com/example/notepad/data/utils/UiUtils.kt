package com.example.notepad.data.utils

import android.content.res.Resources

val Int.toPx get() = (this * Resources.getSystem().displayMetrics.density).toInt()