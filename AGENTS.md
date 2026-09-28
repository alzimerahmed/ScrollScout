# ScrollScout — Rules for AI Agents

## Project

ScrollScout = fork of therxmv/Dirol-Reader (upstream: https://github.com/therxmv/Dirol-Reader). Android news reader that authenticates a real Telegram session via TdLib and aggregates unread posts from the user's channels into one relevance-ranked feed; like/dislike on posts and star on channels tune the ranking. Fully independent from upstream (history detached 2026-09-28; no code sync). Distribution: GitHub Releases (APKs).

**License:** MIT (inherited). The original copyright notice (Roman Kuzmych, 2023) MUST stay in LICENSE alongside the ScrollScout maintainer line — stripping it violates MIT. See `docs/research.md` ADR-002.

**Scope:** Native Android app only. No backend of our own — TdLib talks to Telegram directly. No web frontend.

## Tech Stack & Conventions (inherited from upstream — do not fight it)

### Language & Tooling
- **Language**: Kotlin 1.9.0, JVM target 1.8, coroutines + Flow.
- **UI**: Jetpack Compose (BOM 2022.10.00, Material 2/3 mix — Material 3 is the direction, see `docs/design/design-system.md`). Navigation Compose. No XML layouts except splash/theme.
- **DI**: Hilt 2.51.1 with kapt (KSP is the migration target when Kotlin upgrades).
- **Persistence**: Room 2.5.2 (feed cache), MMKV via `shared-preferences` module.
- **Telegram**: TdLib through the `:libtd` module — **not in the repo** (upstream gitignored it); it must be built from tdlib source or vendored. Never assume it exists.
- **Media**: Media3/ExoPlayer. **Background**: WorkManager. **Telemetry**: Firebase Analytics/Crashlytics (plugins applied; `google-services.json` absent — CI generates a placeholder).
- **Build**: Gradle 8.3, AGP 7.4.2, Groovy DSL. Version catalog = `gradle/libs.versions.toml` (Phase 4 migration); `global.gradle` holds only `appConfig`. Add new deps via catalog aliases. Lint: ktlint + detekt with per-module baselines — new code must not add violations.
- **Tests**: JUnit 4, MockK, Kotest (existing); Turbine/Robolectric when added.

### Architecture & Conventions
- **Modules**: `app` (auth/profile/splash/nav/DI), `common` (theme/resources/shared composables), `ota-updates` (presentation/data/domain), `channels` (domain+data), `feed` (domain+data+presentation), `settings` (presentation), `shared-preferences`, `libtd` (external, via JitPack).
- **App package layout**: `data/`, `domain/`, `di/`, `ui/`, `utils/` under `com.therxmv.dirolreader`. Package namespace is KEPT as-is (ADR-001 in `docs/research.md`) — do not rename without a dedicated phase.
- **TdLib discipline**: all TdLib calls go through the client wrapper; handle updates on the event stream, never block main thread; TdLib objects are not stable across TDLib versions — pin and match `libtd`.
- **Compose**: state down / events up; StateFlow + `collectAsStateWithLifecycle`; Paging 3 for feed lists. Design tokens per `docs/design/design-system.md` — no hardcoded colors/dp where a token exists.

### Build Inputs (secrets/config — never commit)
- `local.properties`: `API_ID` (Integer) + `API_HASH` (String) from my.telegram.org; configure-time read — build fails without them.
- `app/google-services.json`: absent locally; CI generates a placeholder.
- `libtd/`: must exist before `:app` compiles.

## Build / Verify

```bash
./gradlew assembleDebug          # debug build
./gradlew testDebugUnitTest      # unit tests
./gradlew lint                   # Android lint
```

**Remote-first verification (mandatory):** we do NOT build or test locally — all builds/tests run in GitHub Actions (`.github/workflows/ci.yml`). Local work is edit-only: IDE typecheck / targeted static review while iterating. Push a branch and let CI verify. A CI green check counts as the gate; never re-run it locally for ceremony. Local builds only when debugging the build system itself (requires JDK 17 + Android SDK + the build inputs above).

**IMPORTANT:** `./gradlew` first run downloads Gradle 8.3 + all deps — slow; if ever run locally, background + poll.

## Gotchas

- **libtd is missing from the repo** — the #1 build blocker. Resolution tracked in `docs/research.md` open questions (Phase 3).
- **google-services plugin fails the build** without `google-services.json` — make it conditional (Phase 3) or generate a placeholder.
- **Release buildType signs with the debug key** (upstream shortcut) — must be replaced with proper signing before any release (Phase 8 / release protocol in `.devin/prompt/phase.md` §18).
- **API_ID/API_HASH are configure-time reads** from `local.properties` — CI writes them from secrets; never hardcode.
- **Compose BOM 2022.10 + Kotlin 1.9.0 are pinned together** (compiler ext 1.5.2) — do not bump one without the other.
- **ABI splits enabled** (armeabi-v7a, arm64-v8a + universal) — APK naming/output implications for releases.
- **MIT attribution** — keep upstream copyright in LICENSE (see License above).

## Agent Guidelines & Constraints

### Do's
- **Follow the module boundaries**; new features get their own package (or module, per Phase 4 refactor) following the `ota-updates` pattern.
- **Route UI through the design system** (`docs/design/design-system.md`): tokens, dark/light parity, empty/loading/error states, a11y minimums.
- **Write tests** for new use cases/repositories (JUnit + MockK; behavior, not implementation).
- **Keep TdLib usage behind wrappers** — no direct client calls from composables.
- **Conventional Commits**: `type(scope): description`.

### Don'ts
- **NO local builds or test runs** — verification is CI-only (user directive).
- **NO package/applicationId renames** without a dedicated planned phase (ADR-001).
- **NO new heavy dependencies** without a decision record in `docs/research.md` (license must stay MIT-compatible).
- **NO secrets in code** — API_ID/API_HASH/Firebase/keystore material via env/secrets only.
- **NO deleting resources** without grepping all reference types (manifest, `R.*`, `@drawable/...`) — Android R-reference rule (`.devin/prompt/phase.md` §19).

## Agent Guidelines & Workflow (this repo's .devin system)

### Resource Discipline (mandatory, non-trivial tasks)
Before any non-trivial task:
1. Read `docs/toolset.md` intent-map (task type → resources)
2. Invoke every skill + sub-agent in that row
3. Read every rule for that task type (`.devin/rules/`)
4. At task end: `code-reviewer` sub-agent on final diff (non-negotiable)
5. Append learnings via `/ce-compound` if durable lesson

Phase implementations (task completes a docs/plan.md row): follow `.devin/prompt/phase.md`.

Skip all this for single-line edits, pure Q&A, reading files.

### Project-Type Filter (Android news reader)
Per `docs/toolset.md` intent-map:
- **Skip web-only:** pwa-engineer, seo-specialist, css-architect, playwright-design-clone, web-scraper, payment-integrator, email-engineer, monorepo-manager.
- **Keep universal:** code-reviewer, debugger, test-engineer, security-auditor (Telegram session credentials = security-critical), performance-engineer (startup/jank), git-master, migration-specialist (AGP/Kotlin upgrades), docs-writer, i18n-specialist (RTL/translations), build-optimizer (Gradle), caveman-compressor, vibe-coding-auditor, type-safety-engineer, state-manager (Flow/TdLib events), search-architect (feed search), realtime-engineer (TdLib event stream), database-engineer (Room), media-optimizer (ExoPlayer/images), animation-engineer, frontend-designer (Compose taste).
- **Quality gates:** CI-only — `assembleDebug`, `testDebugUnitTest`, `lint` on GitHub Actions. No local builds.

## Communication Style

Default **caveman-lite** (lightly compressed, readable, technically accurate). `/caveman` skill for full/ultra/wenyan modes.

## Quick Task Flow

Quick tasks: `.devin/prompt/quick.md` (commandments) + `.devin/prompt/rules.md` (scoping, verification, escalation). Phased work: `.devin/prompt/phase.md`.

## Key References

- `docs/toolset.md` — intent map (task type → skills, sub-agents, rules)
- `docs/plan.md` — phased plan + status
- `docs/project.md` — project state/structure
- `docs/tools-log.md` — .devin resources invoked per session
- `docs/CONCEPTS.md` — project vocabulary
- `docs/research.md` — research, ADRs, gotchas, open questions
- `docs/idea.md` — competitive analysis + Feature Gap List
- `docs/design/design-system.md` — UI tokens + rules
- Upstream reference — https://github.com/therxmv/Dirol-Reader (no code sync)
