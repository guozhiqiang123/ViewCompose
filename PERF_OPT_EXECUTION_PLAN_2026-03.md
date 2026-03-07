# PERF Optimization Execution Plan (2026-03)

## 1. Baseline

1. Current scope is limited to internal performance improvements (no public DSL API expansion).
2. Existing renderer already supports node-level skip/patch/rebind accounting.
3. Existing lazy/pager containers already use keyed updates but still rely on non-DiffUtil O(n^2) keyed lookup.

## 2. Scope

In scope for this execution:

1. Lazy/Pager diff engine upgrade (DiffUtil-based internal path).
2. Payload-aware change dispatch for lazy/pager adapters.
3. Framework-managed RecyclerView default tuning (internal only).
4. Subtree skip path in renderer patch pipeline.
5. Unit/instrumentation coverage and doc synchronization.

Out of scope for this execution:

1. Public DSL/API expansion for RecyclerView tuning flags.
2. Module split/restructure work.

## 3. Completion Criteria

1. All plan steps are completed with independent commits.
2. Gate policy:
   - Each step runs `./gradlew qaQuick`.
   - Milestones run `./gradlew qaFull` (Step 3, Step 5, final closure).
3. Root docs reflect final status (`PERFORMANCE.md`, `ROADMAP.md`, `ARCHITECTURE.md`, `SESSION_CONTAINER_CHECKLIST.md`).
4. Plan doc is archived under `docs/archive/` after closure.

## 4. Step Checklist

- [x] Step 1: Add this execution plan doc and commit.
- [x] Step 2: Replace lazy keyed diff engine with DiffUtil-backed internal engine; sync `PERFORMANCE.md`.
- [x] Step 3: Add payload-aware lazy/pager update path; sync `SESSION_CONTAINER_CHECKLIST.md`.
- [x] Step 4: Apply framework-managed RecyclerView defaults tuning; sync `ARCHITECTURE.md` + `PERFORMANCE.md`.
- [ ] Step 5: Add subtree skip plan/pipeline/stats path; sync `PERFORMANCE.md` + `ROADMAP.md`.
- [ ] Step 6: Add/refresh tests (unit + instrumentation) and pass gates.
- [ ] Step 7: Close, archive plan doc, and align final doc statuses.

## 5. Commit Log

| Date | Step | Commit | Notes |
| --- | --- | --- | --- |
| 2026-03-07 | Step 1 | `bf0760d` | Added execution plan doc. |
| 2026-03-07 | Step 2 | `14d34f5` | Switched lazy keyed diff engine to DiffUtil and updated performance doc. |
| 2026-03-07 | Step 3 | `b976da2` | Added payload-aware lazy/pager update path and updated container checklist doc. |
| 2026-03-07 | Step 4 | _pending_ | Tuned framework-managed RecyclerView defaults and synced architecture/performance docs. |

## 6. Blocker Log

When blocked by missing confirmation or unavailable device for `qaFull`, append an entry to `PERF_OPT_BLOCKER_CONTEXT_2026-03.md` with:

1. Timestamp.
2. Completed step and latest commit hash.
3. Current branch and workspace status summary.
4. Blocking reason.
5. Verified facts.
6. Exact next command to resume.
