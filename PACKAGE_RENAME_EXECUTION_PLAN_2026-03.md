# Package Rename Execution Plan (2026-03)

## Baseline
- Goal: migrate source package root from `com.gzq.uiframework` to `com.viewcompose`.
- Scope:
  - Rename Gradle module IDs to `:viewcompose-*` (keep `:app` unchanged).
  - Keep `applicationId = "com.gzq.uiframework"` unchanged.
  - No compatibility alias layer.
  - Rename brand text to `ViewCompose`.
  - Update root docs and `docs/archive/*`.
- Baseline scan (2026-03-07):
  - `com.gzq.uiframework` references exist across source, tests, build scripts, and docs.
  - Module IDs still `:ui-*` and `:benchmark`.

## Mapping
- Module IDs:
  - `:ui-runtime` -> `:viewcompose-runtime`
  - `:ui-renderer` -> `:viewcompose-renderer`
  - `:ui-widget-core` -> `:viewcompose-widget-core`
  - `:ui-overlay-android` -> `:viewcompose-overlay-android`
  - `:ui-image-coil` -> `:viewcompose-image-coil`
  - `:benchmark` -> `:viewcompose-benchmark`
- Package root:
  - `com.gzq.uiframework.*` -> `com.viewcompose.*`

## Done Criteria
- All source packages and Gradle namespaces migrated to `com.viewcompose.*`.
- `applicationId` remains `com.gzq.uiframework`.
- Reflection-based overlay host contract points to new package.
- Docs (including archive) no longer describe obsolete package/module names.
- `qaQuick` passes for each step.
- `qaFull` passes at step 4 and final close, or blocker is recorded.

## Checklist
- [x] Step 1. Add execution plan doc.
- [x] Step 2. Rename Gradle module IDs and dependency paths.
- [x] Step 3. Migrate namespaces, package declarations, imports, and directories.
- [x] Step 4. Keep install package boundaries stable.
- [ ] Step 5. Update reflection contracts and brand naming.
- [ ] Step 6. Sync docs including archive.
- [ ] Step 7. Run final gates and archive this plan.

## Commit Log
- Step 1: `a21be23` (`docs: add package rename execution plan for com.viewcompose`)
- Step 2: `9a1e293` (`refactor: rename gradle module ids to viewcompose-*`)
- Step 3: `0cbf3bf` (`refactor: migrate package root to com.viewcompose across modules`)
- Step 4: pending
- Step 5: pending
- Step 6: pending
- Step 7: pending

## Blocker Log
- None.
