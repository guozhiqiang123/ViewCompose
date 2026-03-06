# Re-Audit Execution Plan (2026-03)

## 1. Baseline

- Plan source: `docs/archive/PROJECT_REAUDIT_2026-03-06.md`
- Execution scope in this round: `F1/F2/F3/F5`
- Out of scope in this round: `F4` (`ui-widget-core` / Android bridge module split)

## 2. Done Criteria

1. F1 completed with API + lifecycle disposal coverage + migration docs.
2. F2 completed with delayed-container unit + instrumentation coverage for grid/pagers.
3. F3 completed with ModalBottomSheet UI regression coverage.
4. F5 completed with large demo file split (behavior unchanged, stable test tags).
5. `qaQuick` passes; `qaFull` executed when device is available (or waiver documented).
6. Plan doc archived after all steps finish.

## 3. Checklist

- [x] Step 1: Add this execution plan doc in project root and commit.
- [x] Step 2: F1 implement Fragment official host API with auto-dispose and commit.
- [x] Step 3: F1 deprecate low-level Fragment API and update docs with migration guidance, then commit.
- [x] Step 4: F2 add delayed-container unit tests (`LazyVerticalGrid/HorizontalPager/VerticalPager`) and commit.
- [x] Step 5: F2 add instrumentation coverage for grid/pager visible refresh paths and commit.
- [x] Step 6: F2 sync `SESSION_CONTAINER_CHECKLIST.md` + `ROADMAP.md` status and commit.
- [x] Step 7: F3 add bottom-sheet test tags + visual UI test coverage and commit.
- [x] Step 8: F3 sync `ROADMAP.md` overlay UI status and commit.
- [x] Step 9: F5 split `DemoFeedbackPage` into section files and commit.
- [x] Step 10: F5 split `DemoModifiersPage` into section files and commit.
- [x] Step 11: F5 split `DemoWidgetShowcaseDetails` into section files and commit.
- [x] Step 12: Run `qaQuick` (and `qaFull` if available), record result and commit any doc waiver if needed.
- [x] Step 13: Archive this plan doc into `docs/archive/` and update archive index.
- [x] Step 14: Final root docs state alignment for remaining `In Progress/Next` markers, then commit.

## 4. Execution Log

| Date | Step | Commit | Notes |
| --- | --- | --- | --- |
| 2026-03-06 | Step 1 | `ee4ffb8` | Plan file created in root. |
| 2026-03-06 | Step 2 | `407ca88` | Added `Fragment.setUiContent(...)` and internal auto-dispose registry. |
| 2026-03-06 | Step 3 | `798d14e` | Deprecated low-level Fragment API, added migration note and lifecycle unit tests. |
| 2026-03-06 | Step 4 | `6f6f4c2` | Added delayed-container unit tests for grid/horizontal pager/vertical pager. |
| 2026-03-06 | Step 5 | `34e063f` | Added instrumentation coverage for grid span refresh and horizontal pager repeated refresh. |
| 2026-03-06 | Step 6 | `fd4971a` | Synced delayed-container checklist and roadmap progress status. |
| 2026-03-06 | Step 7 | `0a49ecf` | Added bottom-sheet test tags and visual regression flow coverage. |
| 2026-03-06 | Step 8 | `e3dd944` | Synced overlay milestone UI status after bottom-sheet instrumentation coverage. |
| 2026-03-06 | Step 9 | `8cb8417` | Split `DemoFeedbackPage` into main/model/overlay/sections files. |
| 2026-03-06 | Step 10 | `30f041a` | Split `DemoModifiersPage` into main + visual/core/layering section files. |
| 2026-03-06 | Step 11 | `a2aad69` | Split `DemoWidgetShowcaseDetails` into seven section files (<300 LOC each) with behavior unchanged. |
| 2026-03-06 | Step 12 | `c6c58bf` | `qaQuick` + `qaFull` executed on Pixel 4 XL API 33；中途修复 BottomSheet instrumentation flake（`9d8e78f` / `8c3eb03`）。 |
| 2026-03-06 | Step 13 | `85c2efe` | Moved this plan from root into `docs/archive/` and updated archive index. |
| 2026-03-06 | Step 14 | pending | Root docs re-aligned with latest remediation state; added VerticalPager instrumentation closure (`fe1b092`) and refreshed `qaFull` baseline to 20/20. |

## 5. Blockers

- 2026-03-06: `qaFull` initially failed at `feedbackPage_modalBottomSheet_showAndDismissFlow` due overlay-window scope mismatch for `testTag` query; resolved by device-layer assertion path and re-run passed.
