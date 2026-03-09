package com.viewcompose.preview.catalog.domain

import com.viewcompose.preview.catalog.model.PreviewDomain
import com.viewcompose.preview.catalog.model.PreviewSpec
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.fillMaxWidth
import com.viewcompose.ui.modifier.margin
import com.viewcompose.ui.modifier.size
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.widget.core.BasicTextField
import com.viewcompose.widget.core.Checkbox
import com.viewcompose.widget.core.Column
import com.viewcompose.widget.core.IconButton
import com.viewcompose.widget.core.PasswordField
import com.viewcompose.widget.core.RadioButton
import com.viewcompose.widget.core.SearchBar
import com.viewcompose.widget.core.Slider
import com.viewcompose.widget.core.Switch
import com.viewcompose.widget.core.TextArea
import com.viewcompose.widget.core.TextField
import com.viewcompose.widget.core.dp
import com.viewcompose.widget.core.remember
import com.viewcompose.ui.node.ImageSource

internal object InputPreviewSpecs {
    val all: List<PreviewSpec> = listOf(
        PreviewSpec(
            id = "input-controls",
            title = "Checkbox / Switch / Radio / Slider",
            domain = PreviewDomain.Input,
            content = {
                val checkboxState = remember { mutableStateOf(true) }
                val switchState = remember { mutableStateOf(true) }
                val radioState = remember { mutableStateOf(true) }
                val sliderState = remember { mutableStateOf(38) }
                Column(
                    spacing = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Checkbox(
                        text = "启用通知",
                        checked = checkboxState.value,
                        onCheckedChange = { checkboxState.value = it },
                    )
                    Switch(
                        text = "自动同步",
                        checked = switchState.value,
                        onCheckedChange = { switchState.value = it },
                    )
                    RadioButton(
                        text = "选项 A",
                        checked = radioState.value,
                        onCheckedChange = { radioState.value = it },
                    )
                    Slider(
                        value = sliderState.value,
                        onValueChange = { sliderState.value = it },
                    )
                }
            },
        ),
        PreviewSpec(
            id = "input-text-fields",
            title = "TextField / SearchBar",
            domain = PreviewDomain.Input,
            content = {
                val textFieldState = remember { mutableStateOf("示例输入") }
                val passwordState = remember { mutableStateOf("123456") }
                val textAreaState = remember { mutableStateOf("这是多行输入示例。") }
                val basicTextState = remember { mutableStateOf("基础输入") }
                val searchQueryState = remember { mutableStateOf("ViewCompose") }
                Column(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    TextField(
                        value = textFieldState.value,
                        onValueChange = { textFieldState.value = it },
                        label = "用户名",
                        supportingText = "支持 4-20 个字符",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    PasswordField(
                        value = passwordState.value,
                        onValueChange = { passwordState.value = it },
                        label = "密码",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    TextArea(
                        value = textAreaState.value,
                        onValueChange = { textAreaState.value = it },
                        label = "备注",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    BasicTextField(
                        value = basicTextState.value,
                        onValueChange = { basicTextState.value = it },
                        placeholder = "BasicTextField",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SearchBar(
                        query = searchQueryState.value,
                        onQueryChange = { searchQueryState.value = it },
                        onSearch = { query -> searchQueryState.value = query },
                        placeholder = "搜索组件",
                        leadingIcon = ImageSource.Resource(android.R.drawable.ic_menu_search),
                        trailingIcon = {
                            IconButton(
                                icon = ImageSource.Resource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = "清除",
                                onClick = { searchQueryState.value = "" },
                                modifier = Modifier.size(24.dp, 24.dp),
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
        ),
    )
}
