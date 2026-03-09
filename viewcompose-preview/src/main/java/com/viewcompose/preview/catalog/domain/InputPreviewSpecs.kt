package com.viewcompose.preview.catalog.domain

import com.viewcompose.preview.catalog.model.PreviewDomain
import com.viewcompose.preview.catalog.model.PreviewSpec
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.fillMaxWidth
import com.viewcompose.ui.modifier.margin
import com.viewcompose.ui.modifier.size
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
import com.viewcompose.ui.node.ImageSource

internal object InputPreviewSpecs {
    val all: List<PreviewSpec> = listOf(
        PreviewSpec(
            id = "input-controls",
            title = "Checkbox / Switch / Radio / Slider",
            domain = PreviewDomain.Input,
            content = {
                Column(
                    spacing = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Checkbox(
                        text = "启用通知",
                        checked = true,
                        onCheckedChange = {},
                    )
                    Switch(
                        text = "自动同步",
                        checked = true,
                        onCheckedChange = {},
                    )
                    RadioButton(
                        text = "选项 A",
                        checked = true,
                        onCheckedChange = {},
                    )
                    Slider(
                        value = 38,
                        onValueChange = {},
                    )
                }
            },
        ),
        PreviewSpec(
            id = "input-text-fields",
            title = "TextField / SearchBar",
            domain = PreviewDomain.Input,
            content = {
                Column(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    TextField(
                        value = "示例输入",
                        onValueChange = {},
                        label = "用户名",
                        supportingText = "支持 4-20 个字符",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    PasswordField(
                        value = "123456",
                        onValueChange = {},
                        label = "密码",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    TextArea(
                        value = "这是多行输入示例。",
                        onValueChange = {},
                        label = "备注",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    BasicTextField(
                        value = "基础输入",
                        onValueChange = {},
                        placeholder = "BasicTextField",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SearchBar(
                        query = "ViewCompose",
                        onQueryChange = {},
                        onSearch = {},
                        placeholder = "搜索组件",
                        leadingIcon = ImageSource.Resource(android.R.drawable.ic_menu_search),
                        trailingIcon = {
                            IconButton(
                                icon = ImageSource.Resource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = "清除",
                                onClick = {},
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
