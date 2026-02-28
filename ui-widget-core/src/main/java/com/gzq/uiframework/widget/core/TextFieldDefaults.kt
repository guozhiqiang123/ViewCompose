package com.gzq.uiframework.widget.core

object TextFieldDefaults {
    fun textStyle(): UiTextStyle = TextDefaults.bodyStyle()

    fun textColor(): Int = TextDefaults.primaryColor()

    fun hintColor(): Int = TextDefaults.secondaryColor()
}
