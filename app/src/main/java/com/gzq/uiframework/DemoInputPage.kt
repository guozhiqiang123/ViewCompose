package com.gzq.uiframework

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.runtime.derivedStateOf
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonSize
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.Checkbox
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.EmailField
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.NumberField
import com.gzq.uiframework.widget.core.PasswordField
import com.gzq.uiframework.widget.core.RadioButton
import com.gzq.uiframework.widget.core.Row
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
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.TextFieldImeAction

internal fun UiTreeBuilder.InputPage() {
    val nameState = remember { mutableStateOf("GZQ") }
    val emailState = remember { mutableStateOf("demo@uiframework.dev") }
    val passwordState = remember { mutableStateOf("") }
    val ageState = remember { mutableStateOf("3") }
    val bioState = remember { mutableStateOf("Built on virtual nodes, keyed diff, and Android View interop.") }
    val notificationsEnabledState = remember { mutableStateOf(true) }
    val analyticsEnabledState = remember { mutableStateOf(false) }
    val selectedTierState = remember { mutableStateOf("Alpha") }
    val intensityState = remember { mutableStateOf(32) }
    val stressExpandedState = remember { mutableStateOf(false) }
    val stressReadonlyState = remember { mutableStateOf(true) }
    val stressErrorState = remember { mutableStateOf(true) }
    val summaryState = remember {
        derivedStateOf {
            "Preview: ${nameState.value.ifBlank { "Anonymous" }} · " +
                "${emailState.value.ifBlank { "no-email" }} · " +
                "${ageState.value.ifBlank { "-" }}y"
        }
    }
    val selectedPageState = remember { mutableStateOf(0) }
    val pageItems = when (selectedPageState.value) {
        0 -> listOf("page", "page_filter", "intro", "form", "verify")
        1 -> listOf("page", "page_filter", "controls", "verify")
        2 -> listOf("page", "page_filter", "stress", "verify")
        else -> listOf("page", "page_filter", "summary", "verify")
    }

    LazyColumn(
        items = pageItems,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Input",
                goal = "Verify that text fields and selection controls stay declarative across value updates, error states, variants, and local theme overrides.",
                modules = listOf("TextField family", "selection widgets", "input defaults", "theme components"),
            )

            "page_filter" -> ChapterPageFilterSection(
                pages = listOf("Fields", "Selection", "Stress", "Summary"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "intro" -> ScenarioSection(
                kind = ScenarioKind.Guide,
                title = "Text And Input Family",
                subtitle = "The framework now maps multiple `EditText` variants: text, password, email, number, and multiline text.",
            ) {
                Text(
                    text = "Typography also uses the formal dp/sp DSL in sample code.",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
            }

            "form" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "Form Controls",
                subtitle = "All fields are state-driven and update the same render session.",
            ) {
                TextField(
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    hint = "Name",
                    label = "Display name",
                    supportingText = "Shown in your profile header",
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
                    hint = "Email",
                    label = "Work email",
                    supportingText = "Used for notifications only",
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
                    hint = "Password",
                    label = "Access key",
                    supportingText = "Blank keeps the current password",
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
                    hint = "Version age",
                    label = "Project age",
                    supportingText = "Semantic version generations",
                    variant = TextFieldVariant.Outlined,
                    size = TextFieldSize.Compact,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                EmailField(
                    value = "disabled@uiframework.dev",
                    onValueChange = {},
                    hint = "Disabled email",
                    label = "Readonly contact",
                    supportingText = "Inherited from organization settings",
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
                    hint = "Short bio",
                    label = "Summary",
                    supportingText = "Supports multiline notes and local state updates",
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
                    text = "Reset Form",
                    leadingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                    trailingIcon = ImageSource.Resource(R.drawable.demo_media_icon),
                    size = ButtonSize.Large,
                    onClick = {
                        nameState.value = "GZQ"
                        emailState.value = "demo@uiframework.dev"
                        passwordState.value = ""
                        ageState.value = "3"
                        bioState.value = "Built on virtual nodes, keyed diff, and Android View interop."
                    },
                )
            }

            "controls" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "Selection + Slider Controls",
                subtitle = "Checkbox, switch, radio button, and slider now live in the same declarative input family.",
            ) {
                Checkbox(
                    text = "Notifications",
                    checked = notificationsEnabledState.value,
                    onCheckedChange = { notificationsEnabledState.value = it },
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Switch(
                    text = "Analytics",
                    checked = analyticsEnabledState.value,
                    onCheckedChange = { analyticsEnabledState.value = it },
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                RadioButton(
                    text = "Alpha tier",
                    checked = selectedTierState.value == "Alpha",
                    onCheckedChange = { checked ->
                        if (checked) {
                            selectedTierState.value = "Alpha"
                        }
                    },
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                RadioButton(
                    text = "Beta tier",
                    checked = selectedTierState.value == "Beta",
                    onCheckedChange = { checked ->
                        if (checked) {
                            selectedTierState.value = "Beta"
                        }
                    },
                    modifier = Modifier.margin(bottom = 8.dp),
                )
                Text(
                    text = "Intensity: ${intensityState.value}",
                    modifier = Modifier.padding(bottom = 6.dp),
                )
                Slider(
                    value = intensityState.value,
                    min = 0,
                    max = 100,
                    onValueChange = { intensityState.value = it },
                    modifier = Modifier.fillMaxWidth(),
                )
                UiThemeOverride(
                    components = {
                        copy(
                            checkbox = checkbox.copy(
                                control = Theme.colors.accent,
                                controlDisabled = Theme.colors.divider,
                                label = Theme.colors.textPrimary,
                                labelDisabled = Theme.colors.textSecondary,
                            ),
                            switchControl = switchControl.copy(
                                control = Theme.colors.accent,
                                controlDisabled = Theme.colors.divider,
                                label = Theme.colors.textPrimary,
                                labelDisabled = Theme.colors.textSecondary,
                            ),
                            radioButton = radioButton.copy(
                                control = Theme.colors.accent,
                                controlDisabled = Theme.colors.divider,
                                label = Theme.colors.textPrimary,
                                labelDisabled = Theme.colors.textSecondary,
                            ),
                            slider = slider.copy(
                                control = Theme.colors.accent,
                                controlDisabled = Theme.colors.divider,
                            ),
                        )
                    },
                ) {
                    Column(
                        spacing = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .backgroundColor(SurfaceDefaults.backgroundColor())
                            .cornerRadius(SurfaceDefaults.cardCornerRadius())
                            .padding(12.dp),
                    ) {
                        Text(text = "Input Control Override")
                        Checkbox(
                            text = "Local Accent Checkbox",
                            checked = true,
                            onCheckedChange = {},
                        )
                        Switch(
                            text = "Disabled Accent Switch",
                            checked = false,
                            enabled = false,
                            onCheckedChange = {},
                        )
                        RadioButton(
                            text = "Local Accent Radio",
                            checked = true,
                            onCheckedChange = {},
                        )
                        Slider(
                            value = 56,
                            min = 0,
                            max = 100,
                            enabled = false,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            "stress" -> ScenarioSection(
                kind = ScenarioKind.Stress,
                title = "Input Edge Cases",
                subtitle = "This page focuses on long labels, read-only text, multiline growth, and persistent error styling under theme changes.",
            ) {
                BenchmarkRouteCallout(
                    route = "Catalog -> Open Input -> Stress page",
                    stableTargets = listOf(
                        "Expanded Copy / Compact Copy",
                        "Editable Notes / Readonly Notes",
                        "Clear Error / Show Error",
                        "Protected field",
                    ),
                )
                Row(
                    spacing = 8.dp,
                    modifier = Modifier.margin(bottom = 12.dp),
                ) {
                    Button(
                        text = if (stressExpandedState.value) "Compact Copy" else "Expanded Copy",
                        size = ButtonSize.Compact,
                        onClick = {
                            stressExpandedState.value = !stressExpandedState.value
                        },
                    )
                    Button(
                        text = if (stressReadonlyState.value) "Editable Notes" else "Readonly Notes",
                        size = ButtonSize.Compact,
                        variant = ButtonVariant.Outlined,
                        onClick = {
                            stressReadonlyState.value = !stressReadonlyState.value
                        },
                    )
                    Button(
                        text = if (stressErrorState.value) "Clear Error" else "Show Error",
                        size = ButtonSize.Compact,
                        variant = ButtonVariant.Tonal,
                        onClick = {
                            stressErrorState.value = !stressErrorState.value
                        },
                    )
                }
                TextField(
                    value = if (stressExpandedState.value) {
                        "A much longer project title that should still keep label, placeholder, and supporting text readable without clipping."
                    } else {
                        "Compact title"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = "Release channel display name",
                    supportingText = if (stressExpandedState.value) {
                        "Long supporting text should wrap cleanly and remain aligned with the field container even when the content spans multiple lines."
                    } else {
                        "Short supporting text"
                    },
                    variant = TextFieldVariant.Outlined,
                    size = TextFieldSize.Large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(bottom = 12.dp),
                )
                TextArea(
                    value = if (stressExpandedState.value) {
                        "Readonly stress note:\n- Local theme overrides stay active\n- Multiline container should keep padding stable\n- Long copy should not push helper text outside the card"
                    } else {
                        "Readonly note"
                    },
                    onValueChange = {},
                    label = "Reviewer notes",
                    supportingText = "Toggle readonly and expanded copy to inspect multiline stability.",
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
                    label = "Protected field",
                    supportingText = if (stressErrorState.value) {
                        "Error state must remain visible through theme switches and page changes."
                    } else {
                        "Resolved state should return to standard themed styling."
                    },
                    isError = stressErrorState.value,
                    variant = TextFieldVariant.Filled,
                    size = TextFieldSize.Medium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            "summary" -> ScenarioSection(
                kind = ScenarioKind.Benchmark,
                title = "Derived Summary",
                subtitle = "This section is driven by `derivedStateOf`, not duplicated imperative updates.",
            ) {
                Text(text = summaryState.value)
                Text(
                    text = "Notifications=${notificationsEnabledState.value}, " +
                        "Analytics=${analyticsEnabledState.value}, " +
                        "Tier=${selectedTierState.value}, " +
                        "Intensity=${intensityState.value}",
                    style = UiTextStyle(fontSizeSp = 13.sp),
                )
                Text(
                    text = bioState.value,
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    color = TextDefaults.secondaryColor(),
                )
            }

            else -> VerificationNotesSection(
                what = "Input chapter should prove that value, enabled/error state, component variants, and local overrides stay in sync with the runtime.",
                howToVerify = listOf(
                    "输入文本并点击 Reset Form，确认所有字段一起回到初始值。",
                    "观察空密码时的错误态，并切换 theme mode，确认错误色和容器色同步变化。",
                    "打开 Stress 页切换 expanded / readonly / error，确认长文案、多行和只读态不会把布局撑坏。",
                    "切换 selection controls 和 slider，确认摘要区立即反映变化。",
                ),
                expected = listOf(
                    "TextField label、supportingText、placeholder 和内容布局稳定。",
                    "禁用态和错误态不会丢失主题样式。",
                    "derived summary 始终和输入状态保持一致。",
                ),
                relatedGaps = listOf(
                    "还没有 focus 管理、IME 回调链和表单状态抽象。",
                ),
            )
        }
    }
}
