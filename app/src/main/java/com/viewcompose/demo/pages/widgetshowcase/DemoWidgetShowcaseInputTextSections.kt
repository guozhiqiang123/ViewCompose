package com.viewcompose

import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.modifier.fillMaxWidth
import com.viewcompose.renderer.modifier.margin
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.widget.core.Column
import com.viewcompose.widget.core.EmailField
import com.viewcompose.widget.core.NumberField
import com.viewcompose.widget.core.PasswordField
import com.viewcompose.widget.core.TextArea
import com.viewcompose.widget.core.TextField
import com.viewcompose.widget.core.TextFieldSize
import com.viewcompose.widget.core.TextFieldVariant
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.dp
import com.viewcompose.widget.core.remember

internal fun UiTreeBuilder.ShowcaseTextField() {
    val filledValue = remember { mutableStateOf("") }
    val tonalValue = remember { mutableStateOf("") }
    val outlinedValue = remember { mutableStateOf("") }
    val errorValue = remember { mutableStateOf("错误示例") }
    val readOnlyValue = remember { mutableStateOf("只读内容") }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "变体对比", subtitle = "Filled / Tonal / Outlined") {
            TextField(
                value = filledValue.value,
                onValueChange = { filledValue.value = it },
                label = "Filled",
                hint = "请输入",
                variant = TextFieldVariant.Filled,
                modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
            )
            TextField(
                value = tonalValue.value,
                onValueChange = { tonalValue.value = it },
                label = "Tonal",
                hint = "请输入",
                variant = TextFieldVariant.Tonal,
                modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
            )
            TextField(
                value = outlinedValue.value,
                onValueChange = { outlinedValue.value = it },
                label = "Outlined",
                hint = "请输入",
                variant = TextFieldVariant.Outlined,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "尺寸对比", subtitle = "Compact / Medium / Large") {
            TextFieldSize.entries.forEach { size ->
                val sizeValue = remember { mutableStateOf("") }
                TextField(
                    value = sizeValue.value,
                    onValueChange = { sizeValue.value = it },
                    label = size.name,
                    hint = "${size.name} size",
                    size = size,
                    modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
                )
            }
        }

        DemoSection(title = "辅助文本", subtitle = "label / hint / supportingText") {
            val v = remember { mutableStateOf("") }
            TextField(
                value = v.value,
                onValueChange = { v.value = it },
                label = "用户名",
                hint = "请输入用户名",
                supportingText = "用户名长度为 3-20 个字符",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "错误态", subtitle = "isError = true") {
            TextField(
                value = errorValue.value,
                onValueChange = { errorValue.value = it },
                label = "邮箱",
                isError = true,
                supportingText = "邮箱格式不正确",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "只读", subtitle = "readOnly = true") {
            TextField(
                value = readOnlyValue.value,
                onValueChange = {},
                label = "只读字段",
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "字数限制", subtitle = "maxLength = 20") {
            val v = remember { mutableStateOf("") }
            TextField(
                value = v.value,
                onValueChange = { v.value = it },
                label = "限制字数",
                maxLength = 20,
                supportingText = "${v.value.length}/20",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

internal fun UiTreeBuilder.ShowcaseTextFieldVariants() {
    val pwdValue = remember { mutableStateOf("") }
    val emailValue = remember { mutableStateOf("") }
    val numberValue = remember { mutableStateOf("") }
    val areaValue = remember { mutableStateOf("") }

    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "PasswordField", subtitle = "密码输入框") {
            PasswordField(
                value = pwdValue.value,
                onValueChange = { pwdValue.value = it },
                label = "密码",
                hint = "请输入密码",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "EmailField", subtitle = "邮箱输入框") {
            EmailField(
                value = emailValue.value,
                onValueChange = { emailValue.value = it },
                label = "邮箱",
                hint = "user@example.com",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "NumberField", subtitle = "数字输入框") {
            NumberField(
                value = numberValue.value,
                onValueChange = { numberValue.value = it },
                label = "数量",
                hint = "请输入数字",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DemoSection(title = "TextArea", subtitle = "多行文本输入") {
            TextArea(
                value = areaValue.value,
                onValueChange = { areaValue.value = it },
                label = "备注",
                hint = "请输入备注信息...",
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
