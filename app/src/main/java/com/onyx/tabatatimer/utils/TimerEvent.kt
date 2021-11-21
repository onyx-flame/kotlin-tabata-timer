package com.onyx.tabatatimer.utils

sealed class TimerEvent {
    object START: TimerEvent()
    object END: TimerEvent()
}