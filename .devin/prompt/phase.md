1) Clone the source repository into the root of this project — not a subfolder — and fully detach it from its origin (remove all remote links, git history references, and upstream configuration so it stands alone).

2) Transfer complete ownership of the repository to me. Replace every credential, author attribution, and ownership reference throughout the codebase with my details:
   - Name: Alzimer Ahmed
   - Email: alzimerahmed84@gmail.com
   Perform a full sweep of the repo (READMEs, package manifests, license files, config files, CI workflows, docs, and commit metadata) to locate all ownership info and replace it.

3) Analyze the codebase thoroughly: map the project structure, understand the core functionality, and document the existing feature set before making any changes.

4) Tool selection — after analyzing the codebase (point 3) and understanding the Devin extensibility stack (`.devin/` — 38 skills, 38 sub-agents, 39 rules, 9 orchestrators, prompt engine), curate the subset that is actually relevant to THIS project and write it to `docs/toolset.md` (create it fresh if it doesn't exist) as an **intent-map** (task type → resources). This curated selection becomes the active toolset for all future sessions — instead of scanning all ~120 resources every session, Devin reads `docs/toolset.md` intent-map to know exactly which to invoke for the task at hand. If a resource is not in the intent-map, Devin should not spend time loading or considering it.

   Selection criteria:
   - **Project domain**: match tools to what the project actually does. A music streaming app needs media, real-time, state-management, database, animation, accessibility, performance, security, testing — but not web-scraping, payment, PWA, SEO, email, monorepo, or CSS-architecture. Exclude anything the project will never use.
   - **Tech stack**: match tools to the actual stack (e.g., a Django app needs database, testing, security, i18n — not gradle tooling; an Android app needs Kotlin/Compose tools — not web/CSS resources). Exclude web-only or stack-incompatible resources.
   - **Phase needs**: include tools the current or next phase will need; exclude tools no planned phase will use.
   - **Meta/utility skills**: ALWAYS include ce-work, ce-commit, ce-debug, ce-plan, ce-commit-push-pr, ce-handoff, lfg, caveman, ce-explain, ce-brainstorm, ce-compound, ce-code-review — these are the workflow tools you'll use most. Filtering them out is a bug.
   - **Always-included (every project)**: `code-reviewer` sub-agent (required for every diff), `debugger`, `test-engineer`, and the core rules (05-code-review, 06-debug, 07-testing, 26-git-workflow, 36-anti-vibe-coding). These apply universally regardless of domain.

   `docs/toolset.md` format (use exactly this structure):
   ```markdown
   # Active Toolset — Intent Map

   > Single source of truth for which `.devin/` resources are active for this project.

   ## Project Profile
   - **Domain**: <e.g., Android music streaming app>
   - **Tech stack**: <e.g., Django 5.x + DRF + Celery + PostgreSQL | Kotlin + Compose + Room + Hilt | Next.js + Tailwind>
   - **Current phase**: <e.g., Phase 1 — Foundation>

   ## How to Use This File
   Before starting any non-trivial task:
   1. Identify the task type in the Intent Map below
   2. Invoke every skill listed (via /skill-name)
   3. Invoke every sub-agent listed (by name from .devin/agents/)
   4. Read every rule listed (from .devin/rules/)
   5. At task end: code-reviewer subagent on the final diff (non-negotiable)

   ## Intent Map — Task Type → Resources
   | Task type | Skills (invoke via /name) | Sub-agents | Rules |
   |---|---|---|---|
   | Implement from plan/spec | /ce-work | code-reviewer (end) | 05 |
   | Commit staged changes | /ce-commit | — | 26 |
   | Debug a bug / failure | /ce-debug, /debug | debugger | 06 |
   | ... | ... | ... | ... |

   ## Always-On Rules (from .devin/rules/)
   | Rule | File | Why |
   |------|------|-----|
   | 05 Code Review | 05-code-review.md | Required before every merge |
   ...

   ## Orchestrators (reference docs in .devin/orchestrators/)
   | Orchestrator | File | Sub-agents it coordinates |
   |--------------|------|---------------------------|
   ...

   ## Excluded (with rationale)
   | Resource | Type | Why excluded |
   |----------|------|-------------|
   | web-scraper | sub-agent | No web scraping needed |
   ...

   ## Update Triggers
   Regenerate or update this file when:
   - A new phase introduces a domain not in the current intent map.
   - The tech stack changes (new dependency, framework swap).
   - A previously-excluded domain becomes relevant.
   - New meta/utility skills become available (ce-*, lfg, etc.) — add to intent map immediately.
   ```

   Update `docs/toolset.md` at the start of every new phase — add newly-relevant tools, remove no-longer-needed ones, and keep the excluded list current. This file is the single source of truth for which `.devin/` resources are active for this project.

5) Clean up the repository — remove all unused files, dead code, and redundant folders that add no value to the project.

6) Research best-in-class apps in the same domain and compile a competitive analysis into `docs/idea.md` (create it fresh if it doesn't exist). This is the project's FEATURE PIPELINE: research similar apps → identify missing features → list them in `idea.md` → `plan.md` phases implement them. The flow is mandatory and must never degrade into engineering-hygiene-only docs.

`docs/idea.md` MUST contain a dedicated **Feature Gap List** section — a deduplicated, prioritized list of concrete features our app lacks that competitors have. Each entry needs: feature name, which competitor has it, why it matters (user value), and rough implementation effort (S/M/L). This list is the contract: `docs/plan.md` must account for every entry — either assigned to a phase, or explicitly deferred with a reason in `idea.md` (e.g., "deferred: violates FOSS/no-GMS constraint"). No silent drops.

Beyond the Feature Gap List, cover: quality-of-life improvements, missing features, functional enhancements, design systems, UX patterns, and layout/architecture improvements we could adopt. Approach this as a software engineering audit, not just a feature wishlist:
   - **Architecture review**: assess the current app's architecture (layering, separation of concerns, dependency injection, data flow patterns) against the stack's best practices (e.g., MVVM/Clean Architecture on mobile, domain-modular apps in Django, feature folders in SPAs — check `AGENTS.md` for the actual stack). Identify structural weaknesses and refactoring opportunities.
   - **Code quality assessment**: flag tech debt — duplicated logic, god classes, long methods, poor naming, missing abstractions, inconsistent error handling, and hardcoded values that should be resources or constants.
   - **Testing strategy**: evaluate existing test coverage (unit, integration, UI). Identify untested critical paths and propose a testing pyramid plan with the stack's actual tooling (e.g., JUnit/Robolectric on Android, pytest/Django test on Python, Vitest/Playwright on web).
   - **Performance & reliability**: note opportunities for profiling-driven improvements — startup time, memory leaks, main-thread/UI-blocking work, database query efficiency (N+1), and network caching — whatever the stack's equivalents are.
   - **Maintainability & DX**: recommend improvements to module structure, build configuration, lint/static analysis (the stack's linters — e.g., ktlint/detekt, ruff, eslint), CI pipelines, and documentation so the codebase stays maintainable as features grow.
   - **Scalability & extensibility**: identify where feature additions would currently require invasive changes, and propose abstractions or patterns (interfaces, use cases, feature modules) that make future phases cheaper to implement.
   For each competitor analyzed, also note which engineering practices they likely use to deliver their UX quality, so the plan can adopt both features and the engineering discipline behind them.

7) Maintain a living technical research system in `docs/research.md` (create it fresh if it doesn't exist) — this is distinct from the competitive analysis in `idea.md`: `idea.md` captures *what* to build, `research.md` captures *how* to build it well. Structure it as:
   - **Technology & library research**: for every new dependency, API, or platform capability a phase needs, research the current best option — compare alternatives on maintenance status, community health, API stability, performance, license, and compatibility with the project's stack (see `AGENTS.md` — it varies per project). Record the decision and rationale so future phases don't re-litigate it.
   - **Best-practice research**: for each implementation domain (networking, caching, background work, link metadata extraction, security), gather current official documentation and community-accepted patterns before writing code. Cite sources (official docs, ADRs, well-regarded OSS implementations).
   - **Gotchas & pitfalls**: document known issues, version-specific bugs, and breaking changes encountered during implementation so they are never rediscovered the hard way.
   - **Decision records**: for every significant technical choice, append a short entry — context, options considered, decision, consequences (a lightweight ADR format).
   - **Open questions**: track unresolved technical questions and revisit them before the phases that depend on them.
   Update `research.md` continuously during every phase — before implementation (research), during (findings), and after (retrospective notes). It is a required input when planning any new phase.
   If `docs/research.md` does not exist yet, create it fresh using this exact template:
   ```markdown
   # Technical Research

   ## Technology & Library Decisions
   | Date | Decision | Alternatives Considered | Rationale |
   |------|----------|------------------------|-----------|

   ## Best Practices & Sources
   (domain → pattern adopted → source link)

   ## Gotchas & Pitfalls
   (issue → cause → fix/workaround)

   ## Decision Records (ADR)
   ### ADR-001: <title>
   - Context:
   - Options:
   - Decision:
   - Consequences:

   ## Open Questions
   - [ ] <question> (blocking: <phase>)
   ```

8) Based on `docs/idea.md` and `docs/research.md`, produce a comprehensive, phased plan of action into `docs/plan.md` (create it fresh if it doesn't exist) detailing how each improvement and enhancement will be implemented, with clear scope and ordering.

**Feature-gap coverage rule (mandatory):** every entry in `idea.md`'s Feature Gap List must be accounted for in `plan.md` — either implemented by a named phase (feature name → phase number cross-reference) or listed under a `## Deferred` section with the deferral reason taken from `idea.md`. The plan is incomplete if it contains only infrastructure/reliability phases and no feature phases — check the Feature Gap List before declaring `plan.md` done. Feature phases must still be ordered sensibly: prerequisites (e.g., repository refactor before sync) come first.

9) Execute the plan phase by phase. At the start of each phase, review and update `docs/toolset.md` — add any newly-relevant tools the phase requires and confirm the selection is still accurate. After completing each phase, run a code review using the `code-reviewer` subagent from `.devin/agents/` before moving on to the next one. A phase is only considered complete when:
   - The project builds successfully and the full test suite passes — using the project's own build/verify commands as defined in `AGENTS.md` (§Build/Verify) and its CI workflows (e.g., Gradle for Android, `manage.py test` for Django, `npm run build` for web). Never assume a previous project's commands.
   - A short phase-completion report is written to `docs/` summarizing what was implemented, files touched, and any known limitations or follow-ups.
   - A retrospective is written: what went wrong, what was slow, what to avoid next time. Convert durable lessons into new rules in `rules.md` or updates to `map.md` — the system must learn from every phase, not repeat mistakes. Log prompt-system changes in `.devin/prompt/changelog.md`.

10) Design quality gate — every UI-facing change must meet a high design bar, not just "work". The project's concrete design system lives in `docs/design/design-system.md` (created in the Design phase) — these are the universal requirements, translated to the stack's equivalents:
   - **Design system consistency**: all new screens/components must use the project's design tokens (colors, typography, spacing, shapes) — no one-off hardcoded values. Extend the token set deliberately when new styles are needed.
   - **Platform design-language compliance**: follow the stack's design language — Material 3 on Android, Human Interface Guidelines on iOS, the project's component system on web — including elevation/state layers and dynamic color where supported.
   - **Accessibility (a11y)**: platform-minimum touch targets, sufficient contrast ratios (WCAG AA), content descriptions/aria-labels for icons/images, support for dynamic font scaling, and screen-reader-navigable screens (TalkBack/VoiceOver/etc.).
   - **Dark mode & theming**: every new screen must be verified in both light and dark themes; never hardcode colors.
   - **Motion & micro-interactions**: use purposeful animations (state transitions, list item feedback, screen transitions) with sensible durations/easing — follow the platform's motion principles, respect reduced-motion settings.
   - **Responsive & adaptive layouts**: screens must handle different sizes, orientations, and form factors gracefully (no clipped or stretched content).
   - **Empty, loading, and error states**: every screen must define polished states for empty data, loading, errors, and offline — never a blank screen or raw stack trace.
   - **UX polish details**: consistent iconography, sensible keyboard/input handling, platform feedback cues where appropriate, and edge-case handling (very long strings, RTL text, unusual characters).

11) Phase-completion report format — every report in `docs/` uses exactly these sections, nothing more:
   ```markdown
   # Phase N Report
   ## Implemented
   ## Files Touched
   ## Verification (build/tests/review verdict)
   ## Limitations & Follow-ups
   ```

12) Project definition of done — the plan is complete when every phase in `docs/plan.md` is marked done, all phases passed review with an Approved verdict, the build and full test suite are green, all docs (`docs/project.md`, `docs/tools-log.md`, `docs/research.md`, `docs/plan.md`, `docs/toolset.md`, `docs/CONCEPTS.md`) are current, and no open questions in `research.md` remain unresolved. When this is met, stop proposing new phases and deliver a final summary.

13) Portability — this prompt system (`.devin/prompt/`) is project-agnostic and is copied into new projects as-is. Everything in `docs/` is per-project knowledge: `idea.md`, `research.md`, `plan.md`, `project.md`, `tools-log.md`, `CONCEPTS.md`, and `toolset.md` are created fresh for each new project by following points 3–8. Never assume these files exist — create them when a phase calls for them.

14) Gitignore system — maintain a minimal, intentional `.gitignore`:
   - **Always ignore** `.devin/` and the knowledge layer `docs/*.md` (top-level docs knowledge files) — the prompt system and per-project knowledge are private and must never be committed or pushed to GitHub. Repo-source docs like `docs/adr/` or `docs/api/` may stay tracked if they are part of the project source.
   - **Never ignore** `AGENTS.md` — it is Devin's entry point and must be committed to the repo.
   - **Keep it lean**: only ignore files/folders that actually exist or will exist and genuinely shouldn't be tracked (build outputs, IDE files, local config, secrets/keys, OS files). No speculative or copy-pasted entries that have no effect on the project.
   - **Audit it**: when a new ignore-worthy artifact appears (e.g., a new build directory or local config file), add it. When an entry matches nothing in the project, remove it. The `.gitignore` should stay short enough to read at a glance.

15) README system — every project's `README.md` follows this signature structure (our personal template, based on the LinkNest README plus proven open-source README patterns — centered cover, badges, inline nav, scannable sections, no emojis):
   ```markdown
   # <Project Name> — <one-line what-it-is>

   <div align="center">

   <!-- badges: platform, language, main framework, design system, license — shields.io, with logos -->
   [![Badge 1](...)](...) [![Badge 2](...)](...) ...

   *<tagline — project essence in one sentence>*

   [Download/Quick Start](...) • [Features](#features) • [Building](#building)

   </div>

   ---

   ## Features
   (scannable list or table — never walls of text; screenshots/GIFs where they help)

   ## Screenshots
   (side-by-side device frames or a table, if the project has a UI)

   ## Tech Stack
   (table: layer → technology; for complex projects add a Mermaid architecture diagram)

   ## Project Structure
   (compact ASCII file tree of the important directories — instant architecture overview)

   ## Quick Start / Building
   (≤3 copy-paste steps to get running; advanced setup in <details> collapsibles)

   ## Usage
   (one concrete example of the core workflow — code block or short GIF)

   ## FAQ / Troubleshooting
   (real questions/issues only — add entries as they actually come up; delete this section if empty)

   ## Contributing
   (fork → branch → PR, one short paragraph; omit for personal projects)

   ## Roadmap
   (checklist of planned features — shows the project is alive)

   ## Changelog
   (link to CHANGELOG.md or GitHub Releases — one line, not duplicated content)

   ## License
   (license + author line)
   ```
   Rules: badges must be real (shields.io, matching the actual stack); the tagline answers "what is this, who is it for, why is it different" in the first 50 words; no emojis anywhere in the README; plain section headers; no AI-slop patterns — no marketing fluff ("blazingly fast", "supercharge your workflow"), no filler praise, no empty buzzwords — every sentence states a fact about the project; `---` dividers between major sections; no broken links; every image has descriptive alt text; keep it current — update the README whenever features change.

   Additional README discipline:
   - **GitHub About sync**: the repo's About panel (description, topics, website) must mirror the README tagline and keywords — this is what shows in search and link previews.
   - **Optional sections are earned, not default**: include FAQ, Contributing, Acknowledgments, or a comparison table only when they contain real content. An empty or padded section is worse than no section.
   - **First screen matters most**: badges, tagline, and nav must fully render above the fold with no scrolling — this is the project's landing page.
   - **Screenshots and images are optional**: include logo, screenshots, or GIFs only when the user explicitly requests them. A lean text-and-badges README is the default — no `<img>` tags unless asked.
   - **Long READMEs (>6 sections) get a Table of Contents** after the cover; short ones rely on the inline nav only.

16) Devin extensibility stack — this project uses Devin CLI's full extensibility system. Understand and use each layer:
   - **`docs/toolset.md`** (intent-map): The single source of truth for which `.devin/` resources are active for this project, structured as task type → skills/sub-agents/rules. Created in point 4 after codebase analysis. Read this FIRST to find your task type row and invoke the listed resources — do not scan all ~120 resources blindly. Update it at the start of each new phase.
   - **AGENTS.md** (project root): Devin auto-loads this at every session start as always-on rules. It is the entry point that references the entire `.devin/prompt/` system. Must be committed to git.
   - **Subagent profiles** (`.devin/agents/*.md`, 38 files): Devin scans this directory for custom subagent profiles. Each file uses Devin-native frontmatter (`name`, `description`, optionally `model`, `allowed-tools`, `max-nesting`). Invoke subagents by name (e.g., "review this using the code-reviewer subagent"). The 9 orchestrators in `.devin/orchestrators/` are reference docs, not spawnable profiles.
   - **Skills** (`.devin/skills/<name>/SKILL.md`, 38 dirs): Invokable via `/skill-name` slash commands. Each skill uses frontmatter (`name`, `description`, optionally `model`, `subagent`, `agent`, `allowed-tools`, `permissions`, `triggers`). Skills run inline by default; set `subagent: true` to run in an isolated context window.
   - **Domain rules** (`.devin/rules/*.md`, 39 files): 4 always-on (05, 07, 35, 37); the rest are lazy-loaded via the `docs/toolset.md` intent-map. Each rule mandates when to use a specific skill/subagent.
   - **Project config** (`.devin/config.json`): Project-level permissions (allow/ask/deny). Contents are per-project — the allow list should pre-approve the current stack's common commands (e.g., `uv`/`manage.py`/`ruff` for Python, gradle/adb for Android, npm for web), ask before risky ones (git push, tags, release builds, build-config edits), and deny destructive operations and secret file writes. Update it at Inception if it still references a previous project's toolchain.
   - **MCP servers** (`~/.config/devin/mcp_config.json`): Playwright MCP (browser testing, design cloning) and GitHub MCP (issues, PRs, repo operations). Use MCP tools instead of shell commands when they provide better integration.
   - **Hooks** (`~/.config/devin/hooks/*.py` + `~/.config/devin/config.json` `hooks` key): SessionStart injects workflow context; PreToolUse exec blocks destructive commands; PreToolUse edit flags sensitive file writes; PostToolUse exec logs all commands to audit log; Stop reminds to complete quality checks. Respect hook decisions — do not bypass.
   - **User config** (`~/.config/devin/config.json`): User-level settings (model, theme, global permissions, global hooks). Applies to all projects.

17) Subagent invocation — when delegating to subagents, follow these rules:
   - **Check `docs/toolset.md` intent-map first**: it maps task types to sub-agents. Find your task type row and invoke the sub-agents listed there; if a task seems to need a sub-agent not in the intent map, update `docs/toolset.md` first (add it with rationale), then invoke.
   - **Invoke by name**: Use the subagent's profile name (e.g., "review this using the code-reviewer subagent", "audit security using the security-auditor subagent").
   - **Required**: `code-reviewer` subagent on the final diff before declaring any task done.
   - **Optional but recommended**: domain specialists (security-auditor, performance-engineer, a11y-specialist, test-engineer, debugger) when the task touches their domain.
   - **Orchestrators are reference docs**: The 9 files in `.devin/orchestrators/` describe delegation patterns — read them to understand which subagents to invoke for each domain, but invoke the subagents directly.
   - **Priority when subagents disagree**: security > performance > design > DX.

18) Release engineering — **stack-conditional: applies to signed-binary projects (Android/iOS/desktop apps)**. Skip for libraries, Docker-deployed services, or web apps — their release flow is the stack's own (tag + CI build, npm publish, image push). For signed-app projects, when the user asks to push, tag, build a release, or ship to GitHub, follow this protocol:
   - **Keystore creation** (first release only): generate a release keystore with `keytool -genkeypair -keystore <App>-release.jks -alias <alias> -keyalg RSA -keysize 4096 -validity 10950 -dname "CN=<name>, EMAILADDRESS=<email>, O=<App>, C=<country>"`. Store at `C:\Users\shadd\keystores\<App>-release.jks`. Save credentials in `keystore-info.txt` alongside (never commit). Back up the keystore — losing it means you can never update-sign the app again.
   - **GitHub secrets**: set 4 repository secrets — `RELEASE_KEYSTORE` (base64-encoded .jks via `base64 -w0 <file>`), `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`, `RELEASE_STORE_PASSWORD`. The CI workflow decodes the keystore and writes `keystore.properties` at repo root.
   - **Pre-release validation**: ALWAYS run a release build (`assembleStandardfullRelease` or equivalent) before tagging. R8 full mode (release minify) catches proguard/keep-rule issues that debug builds skip entirely. Debug green does NOT guarantee release green.
   - **versionName vs tag alignment**: ensure `versionName` in `app/build.gradle.kts` matches the git tag version before pushing. A mismatch (e.g. tag `v1.0.0` but `versionName = "4.2.2"`) produces correctly-built APKs with a confusing version label.
   - **Tag-triggered release**: `git tag -a v<X.Y.Z> -m "<message>"` then `git push origin v<X.Y.Z>`. The release workflow triggers on tag push.
   - **Monitoring**: `gh run watch <run-id> --repo <owner/repo> --exit-status --interval 60` — blocks until the run succeeds or fails. Get the run ID with `gh run list --workflow <workflow-file> --limit 1 --json databaseId --jq '.[0].databaseId'`.
   - **Re-triggering after a fix**: if the release build fails, fix the code, commit, then force-move the tag: `git tag -f v<X.Y.Z> -m "<message>"` and `git push origin v<X.Y.Z> --force`. This re-triggers the workflow without creating a new tag.
   - **Verify the release**: `gh release view v<X.Y.Z> --repo <owner/repo> --json name,assets --jq '{name, assets: [.assets[] | {name, size}]}'` — confirms the release published with the expected binary assets.

19) Resource deletion protocol — **stack-conditional: Android/R-resource projects** (the principle applies everywhere — grep every reference type before deleting — but the specific greps below are Android-only):
   - Before deleting any Android resource (icon, drawable, layout, string, color), grep ALL reference types:
   - `AndroidManifest.xml` (icon, roundIcon, theme references)
   - Kotlin/Java source (`R.drawable.*`, `R.mipmap.*`, `R.string.*`, `R.color.*`)
   - XML layouts and styles (`@drawable/*`, `@mipmap/*`, `@string/*`, `@color/*`)
   - Only delete after confirming zero remaining references. A missed reference causes a compile error at best, a runtime crash at worst.

20) Windows Git Bash gotchas:
   - **`printf` with Windows backslash paths**: `printf` interprets `\U` and `\C` in paths like `C:\Users\shadd` as Unicode escapes, mangling the output. Use forward slashes (`C:/Users/shadd/...`) or heredocs (`cat << 'EOF'`) when writing credential or config files containing Windows paths.
   - **`subst` for space-containing paths (Android/NDK projects)**: `ndk-build` fails on paths with spaces. Workaround: `subst X: "C:\path with spaces\project"` and build from `X:\`. The `subst` mapping persists for the session but is lost on reboot.
   - **Python may not be installed**: `python` and `python3` may not resolve. Try `py -3` or use Node.js as a fallback for quick script validation.
   - **ImageMagick may not be installed**: use PowerShell `System.Drawing` to generate image derivatives — `Add-Type -AssemblyName System.Drawing` then `System.Drawing.Bitmap` + `Graphics.DrawImage`.

21) **Stack sweep — mandatory at Inception for every new project.** The `.devin/prompt/` files and `.devin/config.json` are copied from the previous project and often carry over that project's toolchain (commands, permissions, gate criteria, env-check scripts). Before starting any phase work, sweep and fix:
   - Grep `.devin/` for prior-stack tokens — e.g., `gradle|Gradle|Kotlin|Compose|Flutter|Dart|adb|fastlane|pnpm|npm|Django|manage.py|ruff` — wherever they appear in prompt/rules/map/config files, replace with the current stack's equivalents or genericize them (prefer referencing `AGENTS.md` §Build/Verify over inlining commands).
   - Update `.devin/config.json` allow/ask lists to the current stack's toolchain.
   - Verify no referenced script/tool actually missing (e.g., a `scripts/verify-env.ps1` that doesn't exist must be removed or recreated for the new stack).
   - Log the sweep in `.devin/prompt/changelog.md` — which stale tokens were found and replaced. If a project name from a PREVIOUS project appears anywhere in `.devin/` or `docs/`, that is a bug — fix it, do not inherit it.
   - Rule of thumb for what stays portable: process/methodology text is universal; concrete commands, tool names, project names, and file paths are per-project — either parameterize them ("the project's build command") or move them to `AGENTS.md`/`docs/` where per-project values belong.
