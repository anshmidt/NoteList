package com.anshmidt.notelist.ui.uistate

sealed class ScreenMode {
    object Normal : ScreenMode()
    object Trash : ScreenMode()
}