package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.focusFollowKeyboard
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.testTag
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.runtime.derivedStateOf
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonSize
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.Checkbox
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.EmailField
import com.gzq.uiframework.widget.core.Icon
import com.gzq.uiframework.widget.core.IconButton
import com.gzq.uiframework.widget.core.InputControlColorOverride
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.NumberField
import com.gzq.uiframework.widget.core.PasswordField
import com.gzq.uiframework.widget.core.ProvideCheckboxColors
import com.gzq.uiframework.widget.core.ProvideRadioButtonColors
import com.gzq.uiframework.widget.core.ProvideSliderColors
import com.gzq.uiframework.widget.core.ProvideSwitchColors
import com.gzq.uiframework.widget.core.PullToRefresh
import com.gzq.uiframework.widget.core.RadioButton
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.SearchBar
import com.gzq.uiframework.widget.core.ScrollableColumn
import com.gzq.uiframework.widget.core.Slider
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.Switch
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextArea
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.TextField
import com.gzq.uiframework.widget.core.TextFieldSize
import com.gzq.uiframework.widget.core.TextFieldVariant
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiThemeOverride
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.VerticalPager
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp

internal fun UiTreeBuilder.InputPage(
    initialPageIndex: Int = 0,
) {
    val benchmarkExpandedState = remember { mutableStateOf(false) }
    val nameState = remember { mutableStateOf("GZQ") }
    val emailState = remember { mutableStateOf("demo@uiframework.dev") }
    val passwordState = remember { mutableStateOf("") }
    val ageState = remember { mutableStateOf("3") }
    val bioState = remember { mutableStateOf("基于虚拟节点、键控 diff 和 Android View 互操作构建。") }
    val notificationsEnabledState = remember { mutableStateOf(true) }
    val analyticsEnabledState = remember { mutableStateOf(false) }
    val selectedTierState = remember { mutableStateOf("Alpha") }
    val intensityState = remember { mutableStateOf(32) }
    val stressExpandedState = remember { mutableStateOf(false) }
    val stressReadonlyState = remember { mutableStateOf(true) }
    val stressErrorState = remember { mutableStateOf(true) }
    val searchQueryState = remember { mutableStateOf("") }
    val searchResultState = remember { mutableStateOf("") }
    val scrollableSearchQueryState = remember { mutableStateOf("") }
    val verticalPagerSearchQueryState = remember { mutableStateOf("") }
    val pullRefreshSearchQueryState = remember { mutableStateOf("") }
    val focusFollowVerticalPagerPageState = remember { mutableStateOf(0) }
    val pullRefreshFocusRefreshingState = remember { mutableStateOf(false) }
    val summaryState = remember {
        derivedStateOf {
            "预览: ${nameState.value.ifBlank { "匿名" }} · " +
                "${emailState.value.ifBlank { "无邮箱" }} · " +
                "${ageState.value.ifBlank { "-" }}y"
        }
    }
    val selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 4)) }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("benchmark", "page", "page_filter", "intro", "form", "verify")
        1 -> listOf("page", "page_filter", "controls", "verify")
        2 -> listOf("page", "page_filter", "stress", "verify")
        3 -> listOf("page", "page_filter", "search", "verify")
        else -> listOf("page", "page_filter", "summary", "verify")
    }

    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (selectedPageState.value == 3) {
                    Modifier
                        .focusFollowKeyboard()
                } else {
                    Modifier
                },
            ),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "输入组件",
                goal = "验证文本输入、选择控件和搜索栏在值更新、错误态、变体和主题覆盖下的声明式行为。",
                modules = listOf("TextField family", "selection widgets", "SearchBar", "input defaults", "theme components"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("字段", "选择", "压力", "搜索", "摘要"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "benchmark" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "输入 Benchmark 锚点",
                subtitle = "默认字段页的 benchmark 控件。",
            ) {
                Text(
                    text = "稳定路径: launcher -> input -> benchmark anchor",
                    style = UiTextStyle(fontSizeSp = 12.sp),
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Button(
                    text = if (benchmarkExpandedState.value) "输入 Benchmark 已展开" else "输入 Benchmark 已收起",
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 8.dp)
                        .testTag(DemoTestTags.INPUT_BENCHMARK_TOGGLE),
                    onClick = { benchmarkExpandedState.value = !benchmarkExpandedState.value },
                )
                Button(
                    text = "重置输入 Benchmark",
                    variant = ButtonVariant.Outlined,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 8.dp)
                        .testTag(DemoTestTags.INPUT_BENCHMARK_RESET),
                    onClick = { benchmarkExpandedState.value = false },
                )
                TextField(
                    value = if (benchmarkExpandedState.value) "展开的 benchmark 数据" else "紧凑数据",
                    onValueChange = {},
                    label = "Benchmark 字段",
                    supportingText = if (benchmarkExpandedState.value) {
                        "展开的辅助文案保持场景确定性，同时压力测试 TextField 容器布局。"
                    } else {
                        "紧凑辅助文案。"
                    },
                    readOnly = true,
                    variant = TextFieldVariant.Outlined,
                    size = TextFieldSize.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(DemoTestTags.INPUT_BENCHMARK_FIELD),
                )
            }

            "intro" -> ScenarioSection(
                kind = ScenarioKind.Guide,
                title = "文本和输入家族",
                subtitle = "框架现在映射多个 EditText 变体: text, password, email, number, multiline。",
            ) {
                Text(
                    text = "排版也使用正式的 dp/sp DSL。",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
            }

            "form" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "表单控件",
                subtitle = "所有字段由状态驱动，更新同一个 render session。",
            ) {
                TextField(
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    hint = "姓名",
                    label = "显示名称",
                    supportingText = "显示在个人资料头部",
                    imeAction = TextFieldImeAction.Next,
                    variant = TextFieldVariant.Filled,
                    size = TextFieldSize.Large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                EmailField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    hint = "邮箱",
                    label = "工作邮箱",
                    supportingText = "仅用于通知",
                    imeAction = TextFieldImeAction.Next,
                    variant = TextFieldVariant.Tonal,
                    size = TextFieldSize.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                PasswordField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    hint = "密码",
                    label = "访问密钥",
                    supportingText = "留空保持当前密码",
                    imeAction = TextFieldImeAction.Done,
                    variant = TextFieldVariant.Outlined,
                    size = TextFieldSize.Medium,
                    isError = passwordState.value.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                NumberField(
                    value = ageState.value,
                    onValueChange = { ageState.value = it },
                    hint = "版本年龄",
                    label = "项目年龄",
                    supportingText = "语义版本代数",
                    variant = TextFieldVariant.Outlined,
                    size = TextFieldSize.Compact,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                EmailField(
                    value = "disabled@uiframework.dev",
                    onValueChange = {},
                    hint = "禁用邮箱",
                    label = "只读联系人",
                    supportingText = "从组织设置继承",
                    variant = TextFieldVariant.Tonal,
                    size = TextFieldSize.Medium,
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                TextArea(
                    value = bioState.value,
                    onValueChange = { bioState.value = it },
                    hint = "简介",
                    label = "摘要",
                    supportingText = "支持多行编辑和本地状态更新",
                    maxLines = 6,
                    imeAction = TextFieldImeAction.Done,
                    variant = TextFieldVariant.Filled,
                    size = TextFieldSize.Large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .margin(bottom = 12.dp),
                )
                Button(
                    text = "重置表单",
                    leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                    trailingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                    size = ButtonSize.Large,
                    onClick = {
                        nameState.value = "GZQ"
                        emailState.value = "demo@uiframework.dev"
                        passwordState.value = ""
                        ageState.value = "3"
                        bioState.value = "基于虚拟节点、键控 diff 和 Android View 互操作构建。"
                    },
                )
            }

            "controls" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "选择 + Slider 控件",
                subtitle = "Checkbox、Switch、RadioButton 和 Slider 属于同一声明式输入家族。",
            ) {
                Checkbox(
                    text = "通知",
                    checked = notificationsEnabledState.value,
                    onCheckedChange = { notificationsEnabledState.value = it },
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Switch(
                    text = "数据分析",
                    checked = analyticsEnabledState.value,
                    onCheckedChange = { analyticsEnabledState.value = it },
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                RadioButton(
                    text = "Alpha 层级",
                    checked = selectedTierState.value == "Alpha",
                    onCheckedChange = { checked -> if (checked) selectedTierState.value = "Alpha" },
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                RadioButton(
                    text = "Beta 层级",
                    checked = selectedTierState.value == "Beta",
                    onCheckedChange = { checked -> if (checked) selectedTierState.value = "Beta" },
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Text(
                    text = "强度: ${intensityState.value}",
                    modifier = Modifier.padding(bottom = 6.dp),
                )
                Slider(
                    value = intensityState.value,
                    min = 0,
                    max = 100,
                    onValueChange = { intensityState.value = it },
                    modifier = Modifier.fillMaxWidth(),
                )
                ProvideCheckboxColors(
                    InputControlColorOverride(
                        control = Theme.colors.accent,
                        controlDisabled = Theme.colors.divider,
                        label = Theme.colors.textPrimary,
                        labelDisabled = Theme.colors.textSecondary,
                    ),
                ) {
                ProvideSwitchColors(
                    InputControlColorOverride(
                        control = Theme.colors.accent,
                        controlDisabled = Theme.colors.divider,
                        label = Theme.colors.textPrimary,
                        labelDisabled = Theme.colors.textSecondary,
                    ),
                ) {
                ProvideRadioButtonColors(
                    InputControlColorOverride(
                        control = Theme.colors.accent,
                        controlDisabled = Theme.colors.divider,
                        label = Theme.colors.textPrimary,
                        labelDisabled = Theme.colors.textSecondary,
                    ),
                ) {
                ProvideSliderColors(
                    InputControlColorOverride(
                        control = Theme.colors.accent,
                        controlDisabled = Theme.colors.divider,
                    ),
                ) {
                    Column(
                        spacing = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .backgroundColor(SurfaceDefaults.backgroundColor())
                            .cornerRadius(SurfaceDefaults.cardCornerRadius())
                            .padding(12.dp),
                    ) {
                        Text(text = "输入控件颜色覆盖")
                        Checkbox(text = "本地 Accent Checkbox", checked = true, onCheckedChange = {})
                        Switch(text = "禁用 Accent Switch", checked = false, enabled = false, onCheckedChange = {})
                        RadioButton(text = "本地 Accent Radio", checked = true, onCheckedChange = {})
                        Slider(value = 56, min = 0, max = 100, enabled = false, onValueChange = {}, modifier = Modifier.fillMaxWidth())
                    }
                }
                }
                }
                }
            }

            "stress" -> ScenarioSection(
                kind = ScenarioKind.Stress,
                title = "输入边界用例",
                subtitle = "长标签、只读文本、多行增长和持久错误样式的压力测试。",
            ) {
                BenchmarkRouteCallout(
                    route = "Catalog -> Input -> 压力页",
                    stableTargets = listOf("展开/紧凑", "可编辑/只读", "清除错误/显示错误"),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier.margin(bottom = 12.dp),
                ) {
                    Button(
                        text = if (stressExpandedState.value) "紧凑文案" else "展开文案",
                        size = ButtonSize.Compact,
                        modifier = Modifier.testTag(DemoTestTags.INPUT_STRESS_EXPAND),
                        onClick = { stressExpandedState.value = !stressExpandedState.value },
                    )
                    Button(
                        text = if (stressReadonlyState.value) "可编辑" else "只读",
                        size = ButtonSize.Compact,
                        variant = ButtonVariant.Outlined,
                        modifier = Modifier.testTag(DemoTestTags.INPUT_STRESS_READONLY),
                        onClick = { stressReadonlyState.value = !stressReadonlyState.value },
                    )
                    Button(
                        text = if (stressErrorState.value) "清除错误" else "显示错误",
                        size = ButtonSize.Compact,
                        variant = ButtonVariant.Tonal,
                        modifier = Modifier.testTag(DemoTestTags.INPUT_STRESS_ERROR),
                        onClick = { stressErrorState.value = !stressErrorState.value },
                    )
                }
                TextField(
                    value = if (stressExpandedState.value) {
                        "一个很长的项目标题，应该仍然保持标签、占位符和辅助文案可读而不被裁切。"
                    } else {
                        "紧凑标题"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = "发布渠道显示名称",
                    supportingText = if (stressExpandedState.value) {
                        "长辅助文案应该整齐换行，并与字段容器保持对齐。"
                    } else {
                        "短辅助文案"
                    },
                    variant = TextFieldVariant.Outlined,
                    size = TextFieldSize.Large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                TextArea(
                    value = if (stressExpandedState.value) {
                        "只读压力笔记:\n- 本地主题覆盖保持活跃\n- 多行容器应保持 padding 稳定\n- 长文案不应把辅助文本推出卡片"
                    } else {
                        "只读笔记"
                    },
                    onValueChange = {},
                    label = "审阅者笔记",
                    supportingText = "切换只读和展开文案检查多行稳定性。",
                    readOnly = stressReadonlyState.value,
                    maxLines = 6,
                    variant = TextFieldVariant.Tonal,
                    size = TextFieldSize.Large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp)
                        .margin(bottom = 12.dp),
                )
                PasswordField(
                    value = if (stressErrorState.value) "" else "stable-password",
                    onValueChange = {},
                    label = "受保护字段",
                    supportingText = if (stressErrorState.value) {
                        "错误态必须在主题切换和页面变化中保持可见。"
                    } else {
                        "解决态应恢复标准主题样式。"
                    },
                    isError = stressErrorState.value,
                    variant = TextFieldVariant.Filled,
                    size = TextFieldSize.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(DemoTestTags.INPUT_STRESS_PROTECTED_FIELD),
                )
            }

            "search" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "SearchBar 搜索栏",
                subtitle = "SearchBar 提供搜索输入框，支持 query 绑定、onSearch 回调和清除按钮。",
            ) {
                Text(
                    text = "基础搜索栏",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                SearchBar(
                    query = searchQueryState.value,
                    onQueryChange = { searchQueryState.value = it },
                    onSearch = { query -> searchResultState.value = "搜索: $query" },
                    placeholder = "搜索商品…",
                    leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp)
                        .testTag(DemoTestTags.INPUT_SEARCH_PRIMARY),
                )
                if (searchResultState.value.isNotEmpty()) {
                    Text(
                        text = searchResultState.value,
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                        modifier = Modifier.margin(bottom = 12.dp),
                    )
                }
                Text(
                    text = "带清除按钮的搜索栏",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                SearchBar(
                    query = searchQueryState.value,
                    onQueryChange = { searchQueryState.value = it },
                    onSearch = { query -> searchResultState.value = "搜索: $query" },
                    placeholder = "搜索历史…",
                    leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                    trailingIcon = {
                        if (searchQueryState.value.isNotEmpty()) {
                            IconButton(
                                icon = ImageSource.Resource(R.drawable.demo_media_icon),
                                contentDescription = "清除",
                                onClick = { searchQueryState.value = "" },
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                Text(
                    text = "禁用搜索栏",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                SearchBar(
                    query = "",
                    onQueryChange = {},
                    placeholder = "搜索不可用",
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "ScrollableColumn + focusFollowKeyboard",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(top = 12.dp, bottom = 8.dp),
                )
                ScrollableColumn(
                    spacing = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(188.dp)
                        .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                        .cornerRadius(SurfaceDefaults.cardCornerRadius())
                        .padding(12.dp)
                        .focusFollowKeyboard()
                        .margin(bottom = 12.dp),
                ) {
                    Text(
                        text = "滚动容器内聚焦输入框时，焦点跟随策略应只影响当前垂直容器。",
                        style = UiTextStyle(fontSizeSp = 13.sp),
                        color = TextDefaults.secondaryColor(),
                    )
                    SearchBar(
                        query = scrollableSearchQueryState.value,
                        onQueryChange = { scrollableSearchQueryState.value = it },
                        placeholder = "ScrollableColumn 内搜索…",
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(DemoTestTags.INPUT_FOCUS_SCROLLABLE_SEARCH),
                    )
                    (1..4).forEach { index ->
                        Text(
                            text = "滚动占位行 $index",
                            style = UiTextStyle(fontSizeSp = 13.sp),
                            color = TextDefaults.secondaryColor(),
                        )
                    }
                }
                Text(
                    text = "VerticalPager + focusFollowKeyboard",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                VerticalPager(
                    currentPage = focusFollowVerticalPagerPageState.value,
                    onPageChanged = { focusFollowVerticalPagerPageState.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(188.dp)
                        .focusFollowKeyboard()
                        .margin(bottom = 12.dp),
                ) {
                    Page(key = "focus-follow-vertical-pager-search", contentToken = "focus-follow-vertical-pager-search") {
                        Column(
                            spacing = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                                .cornerRadius(SurfaceDefaults.cardCornerRadius())
                                .padding(12.dp),
                        ) {
                            Text(
                                text = "第一页用于回归输入框焦点跟随。",
                                style = UiTextStyle(fontSizeSp = 13.sp),
                                color = TextDefaults.secondaryColor(),
                            )
                            SearchBar(
                                query = verticalPagerSearchQueryState.value,
                                onQueryChange = { verticalPagerSearchQueryState.value = it },
                                placeholder = "VerticalPager 页内搜索…",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag(DemoTestTags.INPUT_FOCUS_VERTICAL_PAGER_SEARCH),
                            )
                        }
                    }
                    Page(key = "focus-follow-vertical-pager-note", contentToken = "focus-follow-vertical-pager-note") {
                        Column(
                            spacing = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                                .cornerRadius(SurfaceDefaults.cardCornerRadius())
                                .padding(12.dp),
                        ) {
                            Text(text = "第二页用于手动切换验证")
                            Text(
                                text = "切换页面后再聚焦输入框，应保持 page 内可见区域稳定。",
                                style = UiTextStyle(fontSizeSp = 13.sp),
                                color = TextDefaults.secondaryColor(),
                            )
                        }
                    }
                }
                Text(
                    text = "PullToRefresh 子容器 focus follow",
                    style = UiTextStyle(fontSizeSp = 14.sp),
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                PullToRefresh(
                    isRefreshing = pullRefreshFocusRefreshingState.value,
                    onRefresh = { pullRefreshFocusRefreshingState.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(188.dp)
                        .margin(bottom = 8.dp),
                ) {
                    ScrollableColumn(
                        spacing = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .focusFollowKeyboard(),
                    ) {
                        SearchBar(
                            query = pullRefreshSearchQueryState.value,
                            onQueryChange = { pullRefreshSearchQueryState.value = it },
                            placeholder = "PullToRefresh 子容器搜索…",
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(DemoTestTags.INPUT_FOCUS_PULL_REFRESH_SEARCH),
                        )
                        Button(
                            text = if (pullRefreshFocusRefreshingState.value) "停止刷新" else "模拟刷新",
                            variant = ButtonVariant.Outlined,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                pullRefreshFocusRefreshingState.value = !pullRefreshFocusRefreshingState.value
                            },
                        )
                        Text(
                            text = "PullToRefresh 容器本身不处理 focus follow，行为由内部 ScrollableColumn 负责。",
                            style = UiTextStyle(fontSizeSp = 13.sp),
                            color = TextDefaults.secondaryColor(),
                        )
                    }
                }
                Text(
                    text = "聚焦以上输入框后，外层列表锚点不应跳变到顶部。",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
            }

            "summary" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "派生摘要",
                subtitle = "此区域由 derivedStateOf 驱动，非命令式重复更新。",
            ) {
                Text(text = summaryState.value)
                Text(
                    text = "通知=${notificationsEnabledState.value}, " +
                        "分析=${analyticsEnabledState.value}, " +
                        "层级=${selectedTierState.value}, " +
                        "强度=${intensityState.value}",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                )
                Text(
                    text = bioState.value,
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
            }

            else -> VerificationNotesSection(
                what = "输入组件应验证值、启用/错误状态、组件变体和本地覆盖与运行时的同步。",
                howToVerify = listOf(
                    "输入文本并点击重置表单，确认所有字段一起回到初始值。",
                    "观察空密码时的错误态，并切换 theme mode，确认错误色和容器色同步变化。",
                    "打开压力页切换展开/只读/错误，确认长文案和多行布局稳定。",
                    "在搜索栏输入文字并提交，确认 onSearch 回调正确触发。",
                    "点击清除按钮，确认查询内容被清空。",
                ),
                expected = listOf(
                    "TextField label、supportingText、placeholder 和内容布局稳定。",
                    "禁用态和错误态不会丢失主题样式。",
                    "SearchBar 输入/清除/提交流程完整。",
                    "派生摘要始终和输入状态保持一致。",
                ),
            )
        }
    }
}
