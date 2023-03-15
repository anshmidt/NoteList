package com.anshmidt.notelist.ui.uistate

sealed class ScreenMode {
    object View : ScreenMode()
    object Edit : ScreenMode()
    object Trash : ScreenMode()
}