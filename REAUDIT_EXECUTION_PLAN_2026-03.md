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
- [ ] Step 2: F1 implement Fragment official host API with auto-dispose and commit.
- [ ] Step 3: F1 deprecate low-level Fragment API and update docs with migration guidance, then commit.
- [ ] Step 4: F2 add delayed-container unit tests (`LazyVerticalGrid/HorizontalPager/VerticalPager`) and commit.
- [ ] Step 5: F2 add instrumentation coverage for grid/pager visible refresh paths and commit.
- [ ] Step 6: F2 sync `SESSION_CONTAINER_CHECKLIST.md` + `ROADMAP.md` status and commit.
- [ ] Step 7: F3 add bottom-sheet test tags + visual UI test coverage and commit.
- [ ] Step 8: F3 sync `ROADMAP.md` overlay UI status and commit.
- [ ] Step 9: F5 split `DemoFeedbackPage` into section files and commit.
- [ ] Step 10: F5 split `DemoModifiersPage` into section files and commit.
- [ ] Step 11: F5 split `DemoWidgetShowcaseDetails` into section files and commit.
- [ ] Step 12: Run `qaQuick` (and `qaFull` if available), record result and commit any doc waiver if needed.
- [ ] Step 13: Archive this plan doc into `docs/archive/` and update archive index.
- [ ] Step 14: Final root docs state alignment for remaining `In Progress/Next` markers, then commit.

## 4. Execution Log

| Date | Step | Commit | Notes |
| --- | --- | --- | --- |
| 2026-03-06 | Step 1 | TBD | Plan file created in root. |

## 5. Blockers

- None currently.
