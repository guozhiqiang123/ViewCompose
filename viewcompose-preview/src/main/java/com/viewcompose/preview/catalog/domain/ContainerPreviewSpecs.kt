package com.viewcompose.preview.catalog.domain

import com.viewcompose.preview.catalog.model.PreviewDomain
import com.viewcompose.preview.catalog.model.PreviewSpec
import com.viewcompose.ui.layout.BoxAlignment
import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.VerticalAlignment
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.backgroundColor
import com.viewcompose.ui.modifier.fillMaxWidth
import com.viewcompose.ui.modifier.height
import com.viewcompose.ui.modifier.margin
import com.viewcompose.ui.modifier.padding
import com.viewcompose.widget.core.Box
import com.viewcompose.widget.core.Column
import com.viewcompose.widget.core.FlowRow
import com.viewcompose.widget.core.Row
import com.viewcompose.widget.core.ScrollableRow
import com.viewcompose.widget.core.Surface
import com.viewcompose.widget.core.SurfaceVariant
import com.viewcompose.widget.core.Text
import com.viewcompose.widget.core.Theme
import com.viewcompose.widget.core.dp

internal object ContainerPreviewSpecs {
    val all: List<PreviewSpec> = listOf(
        PreviewSpec(
            id = "container-box-row-column",
            title = "Box / Row / Column / Surface",
            domain = PreviewDomain.Container,
            content = {
                Column(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Surface(
                        variant = SurfaceVariant.Variant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                    ) {
                        Row(
                            spacing = 8.dp,
                            verticalAlignment = VerticalAlignment.Center,
                        ) {
                            Box(
                                contentAlignment = BoxAlignment.Center,
                                modifier = Modifier
                                    .height(48.dp)
                                    .weight(1f)
                                    .backgroundColor(Theme.colors.primary),
                            ) {
                                Text(text = "Primary")
                            }
                            Box(
                                contentAlignment = BoxAlignment.Center,
                                modifier = Modifier
                                    .height(48.dp)
                                    .weight(1f)
                                    .backgroundColor(Theme.colors.secondary),
                            ) {
                                Text(text = "Secondary")
                            }
                        }
                    }
                }
            },
        ),
        PreviewSpec(
            id = "container-flow-scroll",
            title = "FlowRow / ScrollableRow",
            domain = PreviewDomain.Container,
            content = {
                Column(
                    spacing = 8.dp,
                    horizontalAlignment = HorizontalAlignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    FlowRow(
                        horizontalSpacing = 8.dp,
                        verticalSpacing = 8.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        (1..8).forEach { index ->
                            Surface(
                                variant = SurfaceVariant.Variant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(text = "标签 $index")
                            }
                        }
                    }
                    ScrollableRow(
                        spacing = 8.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        (1..6).forEach { index ->
                            Surface(
                                variant = SurfaceVariant.Variant,
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(horizontal = 12.dp),
                            ) {
                                Box(
                                    contentAlignment = BoxAlignment.Center,
                                    modifier = Modifier.align(BoxAlignment.Center),
                                ) {
                                    Text(text = "Item $index")
                                }
                            }
                        }
                    }
                }
            },
        ),
    )
}
