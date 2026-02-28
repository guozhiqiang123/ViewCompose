package com.gzq.uiframework

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.Visibility
import com.gzq.uiframework.renderer.modifier.align
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.offset
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.textColor
import com.gzq.uiframework.renderer.modifier.visibility
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.renderer.modifier.width
import com.gzq.uiframework.renderer.modifier.zIndex
import com.gzq.uiframework.runtime.MutableState
import com.gzq.uiframework.runtime.derivedStateOf
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.AndroidView
import com.gzq.uiframework.widget.core.Box
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.DisposableEffect
import com.gzq.uiframework.widget.core.Divider
import com.gzq.uiframework.widget.core.EmailField
import com.gzq.uiframework.widget.core.Environment
import com.gzq.uiframework.widget.core.FlexibleSpacer
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.NumberField
import com.gzq.uiframework.widget.core.PasswordField
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.SideEffect
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.TabPager
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextArea
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.TextField
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.UiEnvironment
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTheme
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.key
import com.gzq.uiframework.widget.core.produceState
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.renderInto
import com.gzq.uiframework.widget.core.sp
import java.util.Locale

private val DEMO_TABS = listOf(
    "Overview",
    "Layout",
    "Input",
    "State",
    "Collection",
)

private const val TAB_OVERVIEW = 0
private const val TAB_LAYOUT = 1
private const val TAB_INPUT = 2
private const val TAB_STATE = 3
private const val TAB_COLLECTION = 4

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val root = findViewById<ViewGroup>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        renderInto(
            container = root,
            debug = true,
            debugTag = "UIFrameworkSample",
        ) {
            DemoRoot(root)
        }
    }
}

private fun UiTreeBuilder.DemoRoot(root: ViewGroup) {
    val selectedTabState = remember { mutableStateOf(TAB_OVERVIEW) }
    val activity = root.context as? AppCompatActivity
    UiEnvironment(androidContext = root.context) {
        UiTheme(androidContext = root.context) {
            val environmentLabel = "Env: ${Environment.localeTags.firstOrNull() ?: "und"} · " +
                "${Environment.layoutDirection.name} · " +
                "${"%.2f".format(Locale.US, Environment.density.density)}x"
            SideEffect {
                activity?.title = "UIFramework - ${DEMO_TABS[selectedTabState.value]}"
            }
            DisposableEffect {
                {
                    activity?.title = "UIFramework"
                }
            }
            Column(
                spacing = 8.dp,
                modifier = Modifier.Empty
                    .fillMaxSize()
                    .backgroundColor(Theme.colors.background)
                    .padding(24.dp),
            ) {
                Text(
                    text = "UIFramework Sample",
                    style = UiTextStyle(fontSizeSp = 30.sp),
                )
                Text(
                    text = "Declarative UI runtime on Android Views, regrouped by capability.",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
                Text(
                    text = environmentLabel,
                    style = UiTextStyle(fontSizeSp = 12.sp),
                    modifier = Modifier.Empty
                        .textColor(TextDefaults.secondaryColor())
                        .padding(vertical = 4.dp),
                )
                TabPager(
                    selectedTabIndex = selectedTabState.value,
                    onTabSelected = { index ->
                        selectedTabState.value = index
                    },
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .weight(1f)
                        .margin(top = 12.dp),
                ) {
                    Page(title = DEMO_TABS[TAB_OVERVIEW], key = "overview") {
                        OverviewPage(selectedTabState)
                    }
                    Page(title = DEMO_TABS[TAB_LAYOUT], key = "layout") {
                        LayoutPage()
                    }
                    Page(title = DEMO_TABS[TAB_INPUT], key = "input") {
                        InputPage()
                    }
                    Page(title = DEMO_TABS[TAB_STATE], key = "state") {
                        StatePage()
                    }
                    Page(title = DEMO_TABS[TAB_COLLECTION], key = "collection") {
                        CollectionPage()
                    }
                }
            }
        }
    }
}

private fun UiTreeBuilder.OverviewPage(
    selectedTabState: MutableState<Int>,
) {
    LazyColumn(
        items = listOf("intro", "jump", "surface"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "intro" -> DemoSection(
                title = "Why This Demo Changed",
                subtitle = "The sample is now split by category so the runtime surface is easier to read and extend.",
            ) {
                Text(
                    text = "Each tab isolates one concern: layout, text/input, state, and collection-level rendering.",
                )
                Text(
                    text = "The pager itself is also rendered through the framework as a mapped virtual control.",
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
            }

            "jump" -> DemoSection(
                title = "Jump To A Capability",
                subtitle = "These buttons drive the selected pager tab through framework state.",
            ) {
                Button(
                    text = "Open Layout",
                    modifier = Modifier.Empty.margin(bottom = 8.dp),
                    onClick = {
                        selectedTabState.value = TAB_LAYOUT
                    },
                )
                Button(
                    text = "Open Input",
                    modifier = Modifier.Empty.margin(bottom = 8.dp),
                    onClick = {
                        selectedTabState.value = TAB_INPUT
                    },
                )
                Button(
                    text = "Open State",
                    modifier = Modifier.Empty.margin(bottom = 8.dp),
                    onClick = {
                        selectedTabState.value = TAB_STATE
                    },
                )
                Button(
                    text = "Open Collection",
                    onClick = {
                        selectedTabState.value = TAB_COLLECTION
                    },
                )
            }

            else -> DemoSection(
                title = "Current Surface",
                subtitle = "The first vertical slice now includes the following framework controls.",
            ) {
                Text(text = "Text, TextField, EmailField, PasswordField, NumberField, TextArea")
                Text(text = "Row, Column, Box, Divider, Spacer, FlexibleSpacer, LazyColumn")
                Text(text = "AndroidView interop, TabPager, state runtime, effect runtime")
            }
        }
    }
}

private fun UiTreeBuilder.LayoutPage() {
    LazyColumn(
        items = listOf("row", "box", "column"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "row" -> DemoSection(
                title = "Row + Spacer + Cross Axis Alignment",
                subtitle = "Custom linear layout now supports spacing, arrangement, and child-level cross-axis override.",
            ) {
                Row(
                    arrangement = MainAxisArrangement.Start,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .backgroundColor(SurfaceDefaults.backgroundColor())
                        .padding(12.dp),
                ) {
                    Text(
                        text = "Top",
                        modifier = Modifier.Empty
                            .align(VerticalAlignment.Top)
                            .backgroundColor(Theme.colors.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                    FlexibleSpacer()
                    Text(
                        text = "Bottom",
                        modifier = Modifier.Empty
                            .align(VerticalAlignment.Bottom)
                            .backgroundColor(Theme.colors.accent)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            "box" -> DemoSection(
                title = "Box Overlay",
                subtitle = "Default alignment, child override, offset, and zIndex work together in a single container.",
            ) {
                Box(
                    contentAlignment = BoxAlignment.Center,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .height(140.dp)
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .padding(12.dp),
                ) {
                    Text(
                        text = "Centered surface",
                        modifier = Modifier.Empty
                            .backgroundColor(Theme.colors.primary)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                    Text(
                        text = "Pinned tag",
                        modifier = Modifier.Empty
                            .align(BoxAlignment.BottomEnd)
                            .offset(x = (-8).dp.toFloat(), y = (-8).dp.toFloat())
                            .zIndex(1f)
                            .backgroundColor(Theme.colors.accent)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            else -> DemoSection(
                title = "Column Arrangement",
                subtitle = "Main-axis arrangement and dividers are now stable inside the custom linear container.",
            ) {
                Column(
                    arrangement = MainAxisArrangement.SpaceEvenly,
                    horizontalAlignment = HorizontalAlignment.Center,
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .height(180.dp)
                        .backgroundColor(SurfaceDefaults.backgroundColor())
                        .padding(12.dp),
                ) {
                    Text(text = "One")
                    Divider()
                    Text(text = "Two")
                    Divider()
                    Text(text = "Three")
                }
            }
        }
    }
}

private fun UiTreeBuilder.InputPage() {
    val nameState = remember { mutableStateOf("GZQ") }
    val emailState = remember { mutableStateOf("demo@uiframework.dev") }
    val passwordState = remember { mutableStateOf("") }
    val ageState = remember { mutableStateOf("3") }
    val bioState = remember { mutableStateOf("Built on virtual nodes, keyed diff, and Android View interop.") }
    val summaryState = remember {
        derivedStateOf {
            "Preview: ${nameState.value.ifBlank { "Anonymous" }} · " +
                "${emailState.value.ifBlank { "no-email" }} · " +
                "${ageState.value.ifBlank { "-" }}y"
        }
    }

    LazyColumn(
        items = listOf("intro", "form", "summary"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "intro" -> DemoSection(
                title = "Text And Input Family",
                subtitle = "The framework now maps multiple `EditText` variants: text, password, email, number, and multiline text.",
            ) {
                Text(
                    text = "Typography also uses the formal dp/sp DSL in sample code.",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
            }

            "form" -> DemoSection(
                title = "Form Controls",
                subtitle = "All fields are state-driven and update the same render session.",
            ) {
                TextField(
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    hint = "Name",
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                EmailField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    hint = "Email",
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                PasswordField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    hint = "Password",
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                NumberField(
                    value = ageState.value,
                    onValueChange = { ageState.value = it },
                    hint = "Version age",
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                TextArea(
                    value = bioState.value,
                    onValueChange = { bioState.value = it },
                    hint = "Short bio",
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .height(120.dp)
                        .margin(bottom = 12.dp),
                )
                Button(
                    text = "Reset Form",
                    onClick = {
                        nameState.value = "GZQ"
                        emailState.value = "demo@uiframework.dev"
                        passwordState.value = ""
                        ageState.value = "3"
                        bioState.value = "Built on virtual nodes, keyed diff, and Android View interop."
                    },
                )
            }

            else -> DemoSection(
                title = "Derived Summary",
                subtitle = "This section is driven by `derivedStateOf`, not duplicated imperative updates.",
            ) {
                Text(text = summaryState.value)
                Text(
                    text = bioState.value,
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
            }
        }
    }
}

private fun UiTreeBuilder.StatePage() {
    val clickCountState = remember { mutableStateOf(0) }
    val panelVisibleState = remember { mutableStateOf(true) }
    val summaryState = remember {
        derivedStateOf {
            val value = clickCountState.value
            when {
                value == 0 -> "No clicks yet"
                value % 2 == 0 -> "Even clicks: $value"
                else -> "Odd clicks: $value"
            }
        }
    }
    val timelineState = produceState(
        initialValue = "Last update: waiting",
        clickCountState.value,
    ) {
        value = "Last update: ${clickCountState.value} tap(s) committed"
        null
    }

    LazyColumn(
        items = listOf("counter", "panel"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "counter" -> DemoSection(
                title = "remember + derivedStateOf + produceState",
                subtitle = "This block shows local state, derived labels, and a small produced status string.",
            ) {
                Text(text = "Clicks: ${clickCountState.value}")
                Text(
                    text = summaryState.value,
                    modifier = Modifier.Empty
                        .textColor(TextDefaults.secondaryColor())
                        .padding(vertical = 4.dp),
                )
                Text(
                    text = timelineState.value,
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier.Empty.textColor(TextDefaults.secondaryColor()),
                )
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty.margin(top = 12.dp),
                ) {
                    Button(
                        text = "Increment",
                        onClick = {
                            clickCountState.value = clickCountState.value + 1
                        },
                    )
                    Button(
                        text = "Reset",
                        onClick = {
                            clickCountState.value = 0
                        },
                    )
                }
            }

            else -> DemoSection(
                title = "key Scope + Conditional UI",
                subtitle = "The transient panel keeps its own state while visible, and is fully recreated when toggled back in.",
            ) {
                Button(
                    text = if (panelVisibleState.value) "Hide panel" else "Show panel",
                    modifier = Modifier.Empty.margin(bottom = 12.dp),
                    onClick = {
                        panelVisibleState.value = !panelVisibleState.value
                    },
                )
                Text(
                    text = "Visibility sample: hidden when the panel is off",
                    modifier = Modifier.Empty
                        .visibility(
                            if (panelVisibleState.value) {
                                Visibility.Visible
                            } else {
                                Visibility.Gone
                            },
                        )
                        .padding(bottom = 8.dp),
                )
                if (panelVisibleState.value) {
                    key("transient-panel") {
                        val panelTapState = remember { mutableStateOf(0) }
                        Column(
                            spacing = 8.dp,
                            modifier = Modifier.Empty
                                .fillMaxWidth()
                                .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                                .padding(12.dp),
                        ) {
                            Text(text = "Keyed transient panel")
                            Button(
                                text = "Panel taps: ${panelTapState.value}",
                                onClick = {
                                    panelTapState.value = panelTapState.value + 1
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun UiTreeBuilder.CollectionPage() {
    val reversedState = remember { mutableStateOf(false) }
    val alternateLabelsState = remember { mutableStateOf(false) }
    val listOrderState = produceState(
        initialValue = "List order: A-B-C",
        reversedState.value,
    ) {
        value = if (reversedState.value) "List order: C-B-A" else "List order: A-B-C"
        null
    }
    val keyedItems = if (reversedState.value) {
        listOf("C", "B", "A")
    } else {
        listOf("A", "B", "C")
    }.map { id ->
        DemoListItem(
            id = id,
            title = if (alternateLabelsState.value) {
                "Lazy item $id (alt)"
            } else {
                "Lazy item $id"
            },
        )
    }

    LazyColumn(
        items = listOf("controls", "interop", "list"),
        key = { it },
        modifier = Modifier.Empty.fillMaxSize(),
    ) { section ->
        when (section) {
            "controls" -> DemoSection(
                title = "Collection Controls",
                subtitle = "These buttons mutate the source list and labels while preserving keyed item state.",
            ) {
                Text(text = listOrderState.value)
                Row(
                    spacing = 8.dp,
                    verticalAlignment = VerticalAlignment.Center,
                    modifier = Modifier.Empty.margin(top = 12.dp),
                ) {
                    Button(
                        text = if (reversedState.value) "Show A-B-C" else "Show C-B-A",
                        onClick = {
                            reversedState.value = !reversedState.value
                        },
                    )
                    Button(
                        text = if (alternateLabelsState.value) "Primary labels" else "Alternate labels",
                        onClick = {
                            alternateLabelsState.value = !alternateLabelsState.value
                        },
                    )
                }
            }

            "interop" -> DemoSection(
                title = "AndroidView Interop",
                subtitle = "Legacy views still plug into the same declarative state flow.",
            ) {
                val summaryText = if (alternateLabelsState.value) {
                    "Legacy TextView mirror: alternate labels enabled"
                } else {
                    "Legacy TextView mirror: primary labels enabled"
                }
                AndroidView(
                    key = "legacy_summary",
                    modifier = Modifier.Empty.padding(vertical = 4.dp),
                    factory = { context ->
                        TextView(context)
                    },
                    update = { view ->
                        (view as TextView).text = summaryText
                    },
                )
            }

            else -> DemoSection(
                title = "LazyColumn",
                subtitle = "Each item keeps its own local state while keyed reorder and content updates pass through the diff layer.",
            ) {
                LazyColumn(
                    items = keyedItems,
                    key = { item -> item.id },
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .height(280.dp),
                ) { item ->
                    val itemCountState = remember { mutableStateOf(0) }
                    Column(
                        key = item.id,
                        spacing = 6.dp,
                        modifier = Modifier.Empty
                            .fillMaxWidth()
                            .backgroundColor(SurfaceDefaults.backgroundColor())
                            .padding(12.dp),
                    ) {
                        Text(text = item.title)
                        Button(
                            text = "Item ${item.id} taps: ${itemCountState.value}",
                            onClick = {
                                itemCountState.value = itemCountState.value + 1
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun UiTreeBuilder.DemoSection(
    title: String,
    subtitle: String,
    content: UiTreeBuilder.() -> Unit,
) {
    Column(
        spacing = 8.dp,
        modifier = Modifier.Empty
            .fillMaxWidth()
            .margin(bottom = 12.dp)
            .backgroundColor(SurfaceDefaults.backgroundColor())
            .padding(16.dp),
    ) {
        Text(
            text = title,
            style = UiTextStyle(fontSizeSp = 20.sp),
        )
        Text(
            text = subtitle,
            style = UiTextStyle(fontSizeSp = 13.sp),
            modifier = Modifier.Empty
                .textColor(TextDefaults.secondaryColor())
                .padding(bottom = 4.dp),
        )
        Divider()
        content()
    }
}

private data class DemoListItem(
    val id: String,
    val title: String,
)
