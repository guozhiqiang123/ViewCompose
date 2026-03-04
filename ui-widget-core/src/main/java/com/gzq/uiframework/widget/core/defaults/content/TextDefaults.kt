package com.gzq.uiframework.widget.core

object TextDefaults {
    fun currentStyle(): UiTextStyle = TextStyle.current

    fun titleStyle(): UiTextStyle = Theme.typography.title

    fun bodyStyle(): UiTextStyle = Theme.typography.body

    fun labelStyle(): UiTextStyle = Theme.typography.label

    fun primaryColor(): Int = ContentColor.current

    fun secondaryColor(): Int = Theme.colors.textSecondary
}
