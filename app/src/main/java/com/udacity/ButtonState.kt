package com.udacity


sealed class ButtonState(var status: String = "",var fieName: String = "") {
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}