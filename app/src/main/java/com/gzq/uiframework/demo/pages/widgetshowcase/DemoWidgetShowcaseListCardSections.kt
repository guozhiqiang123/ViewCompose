package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Card
import com.gzq.uiframework.widget.core.CardVariant
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Divider
import com.gzq.uiframework.widget.core.ElevatedCard
import com.gzq.uiframework.widget.core.Icon
import com.gzq.uiframework.widget.core.ListItem
import com.gzq.uiframework.widget.core.OutlinedCard
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.ShowcaseListItem() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "单行", subtitle = "仅 headlineText") {
            ListItem(headlineText = "单行列表项")
            Divider()
            ListItem(headlineText = "另一个单行列表项")
        }

        DemoSection(title = "双行", subtitle = "headlineText + supportingText") {
            ListItem(
                headlineText = "双行列表项",
                supportingText = "这是支持文本，提供额外信息",
            )
            Divider()
            ListItem(
                headlineText = "另一个双行列表项",
                supportingText = "另一段支持文本",
            )
        }

        DemoSection(title = "完整模式", subtitle = "overline + leading + trailing") {
            ListItem(
                headlineText = "完整列表项",
                supportingText = "支持文本详细描述",
                overlineText = "上方标签",
                leadingContent = {
                    Icon(source = ImageSource.Resource(R.drawable.demo_media_icon))
                },
                trailingContent = {
                    Text(
                        text = "详情",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                },
            )
        }

        DemoSection(title = "可点击", subtitle = "onClick 回调") {
            val clickCount = remember { mutableStateOf(0) }
            ListItem(
                headlineText = "点击我",
                supportingText = "已点击 ${clickCount.value} 次",
                leadingContent = {
                    Icon(source = ImageSource.Resource(R.drawable.demo_media_icon))
                },
                onClick = { clickCount.value++ },
            )
        }
    }
}

internal fun UiTreeBuilder.ShowcaseCard() {
    Column(
        spacing = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        DemoSection(title = "变体对比", subtitle = "Filled / Elevated / Outlined") {
            Card(
                variant = CardVariant.Filled,
                modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(text = "Filled Card", style = UiTextStyle(fontSizeSp = 16.sp))
                    Text(
                        text = "默认填充样式",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth().margin(bottom = 8.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(text = "Elevated Card", style = UiTextStyle(fontSizeSp = 16.sp))
                    Text(
                        text = "带阴影的卡片",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(text = "Outlined Card", style = UiTextStyle(fontSizeSp = 16.sp))
                    Text(
                        text = "边框样式的卡片",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }
        }

        DemoSection(title = "可点击", subtitle = "onClick 回调") {
            val clickCount = remember { mutableStateOf(0) }
            Card(
                onClick = { clickCount.value++ },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(text = "可点击的卡片", style = UiTextStyle(fontSizeSp = 16.sp))
                    Text(
                        text = "已点击 ${clickCount.value} 次",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }
        }

        DemoSection(title = "禁用态", subtitle = "enabled = false") {
            Card(
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Text(text = "禁用的卡片", style = UiTextStyle(fontSizeSp = 16.sp))
                    Text(
                        text = "此卡片不可交互",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                }
            }
        }
    }
}
