package com.onyx.tabatatimer.utils

sealed class TimerEvent {
    object START: TimerEvent()
    object PREVIOUS: TimerEvent()
    object PAUSE: TimerEvent()
    object NEXT: TimerEvent()
    object END: TimerEvent()
}