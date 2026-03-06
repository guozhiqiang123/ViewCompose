# Theme System Architecture Refactoring Plan

## Status: Complete ✅

## Overview
Based on THEME_AUDIT.md findings, this refactoring eliminates pre-computed component tokens,
unifies props/spec dual storage, demotes UiInputColors, adds native View escape hatch,
and completes Android bridging.

Execution order: **A → B → C → D → E → F** (D and E ran in parallel)

---

## Sprint A: Remove Component Styles from Theme ☑ (`62b072f`)

**Goal:** Delete `UiThemeTokens.components`, derive colors at call time in Defaults objects. Delete `rebaseComponentStyles()` (~276 lines).

### A-1: Modify all Defaults to derive from Theme.colors directly ☑
- ButtonDefaults.kt → Theme.colors.* + contentColorFor()
- TextFieldDefaults.kt → Theme.colors.* + Theme.input.* (input kept until Sprint C)
- SegmentedControlDefaults.kt → Theme.colors.* + contentColorFor()
- InputControlDefaults.kt → Theme.input.*
- ProgressIndicatorDefaults.kt → Theme.colors.primary/divider
- TabPagerDefaults.kt → Theme.colors.*

### A-2: Add sparse LocalValue override mechanism ☑
- New file: ComponentColorOverrides.kt
- Per-component override data classes (e.g., ButtonColorOverride)
- Defaults check override first, then fall back to Theme.colors

### A-3: Delete Theme-layer component code ☑
- ThemeTokens.kt: removed `components` field
- Theme.kt: removed `Theme.components` accessor, removed `components` from UiThemeOverride
- ThemeRebase.kt: removed components rebase logic + rebaseComponentStyles()
- ThemeDefaults.kt: removed UiComponentStyleDefaults
- ComponentStyles.kt: removed 9 UiXxxStyles + UiComponentStyles (kept sizing classes)

### A-4: Update tests ☑
- ThemeTest.kt: rewrote component-referencing tests
- Other test files: removed Theme.components references

---

## Sprint B: Props → Spec Unification ☑ (`440f847`)

**Goal:** Move style attributes from Props map into typed NodeSpec fields.

### B-1: Extend NodeSpec with style fields ☑
### B-2: Modify DSL to write spec only, Props.Empty ☑
### B-3: Modify ViewModifierApplier to read from spec ☑
### B-4: Simplify NodeBindingDiffer — remove StyleSignature ☑
### B-5: Extend Patch Appliers for style fields ☑
### B-6: Clean up Props and TypedPropKeys ☑
### B-7: Update tests and demo ☑

---

## Sprint C: Demote UiInputColors (depends on A) ☑ (`44b82b4`)

### C-1: Inline input color derivation into Defaults ☑
### C-2: Clean up Theme layer (remove input/interactions fields) ☑

---

## Sprint D: Modifier.nativeView Escape Hatch (independent) ☑ (`00cde4f`)

### D-1: Add NativeViewElement ☑
### D-2: Execute configure in ViewModifierApplier ☑
### D-3: Test and Demo ☑

---

## Sprint E: Android Bridge Completion (independent) ☑ (`78295b1`)

### E-1: Typography bridging ☑
### E-2: uiMode auto light/dark ☑

---

## Sprint F: Fine-grained Locals (depends on A) ☑ (`44a51c9`)

### F-1: Add LocalTextStyle and ProvideContentColor ☑

---

## Actual Impact
- **Deleted:** ~700 lines (rebaseComponentStyles, UiComponentStyles, Props/TypedPropKeys, StyleSignature)
- **Added:** ~420 lines (NodeSpec style fields, LocalTextStyle, ProvideContentColor, nativeView, bridge completion)
- **Net reduction:** ~280 lines
