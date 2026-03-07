package com.gzq.uiframework

import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.offset
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.renderer.modifier.zIndex
import com.gzq.uiframework.widget.core.Box
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.ModifierOffsetZIndexSection() {
    ScenarioSection(
        kind = ScenarioKind.Visual,
        title = "offset + zIndex",
        subtitle = "offset 平移 View 位置，zIndex 控制绘制顺序。",
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                .cornerRadius(12.dp)
                .padding(12.dp),
        ) {
            Box(
                contentAlignment = BoxAlignment.Center,
                modifier = Modifier
                    .size(80.dp, 60.dp)
                    .backgroundColor(Theme.colors.primary)
                    .cornerRadius(8.dp)
                    .zIndex(1f),
            ) {
                Text(text = "zIndex=1")
            }
            Box(
                contentAlignment = BoxAlignment.Center,
                modifier = Modifier
                    .size(80.dp, 60.dp)
                    .backgroundColor(Theme.colors.accent)
                    .cornerRadius(8.dp)
                    .offset(x = 40.dp.toFloat(), y = 20.dp.toFloat())
                    .zIndex(2f),
            ) {
                Text(text = "zIndex=2")
            }
            Box(
                contentAlignment = BoxAlignment.Center,
                modifier = Modifier
                    .size(80.dp, 60.dp)
                    .backgroundColor(Theme.colors.surfaceVariant)
                    .cornerRadius(8.dp)
                    .offset(x = 80.dp.toFloat(), y = 40.dp.toFloat())
                    .zIndex(0f),
            ) {
                Text(text = "zIndex=0")
            }
        }
        Text(
            text = "三个方块分别设置不同的 offset 和 zIndex，高 zIndex 的方块绘制在上方。",
            style = UiTextStyle(fontSizeSp = 13.sp),
            color = TextDefaults.secondaryColor(),
            modifier = Modifier.margin(top = 8.dp),
        )
    }
}
