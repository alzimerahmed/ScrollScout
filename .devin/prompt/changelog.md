# Prompt System Changelog

Every change to the files in `.devin/prompt/` is logged here: date, file, what changed, and why. This lets us see how the system evolved and roll back bad rules.

## 2026-09-21 — Mandatory feature pipeline (idea.md → plan.md)

**Problem (ScribbleX project):** The Phase 1→2 docs run produced a `docs/plan.md` containing only engineering/reliability phases (testing, CI, backups, migrations) — zero user-facing feature phases — despite `docs/idea.md` identifying real competitive gaps (sync, repository refactor, search-first UX). The "research competitors → find missing features → implement" loop was implicit in §6/§8 and silently degraded into hygiene-only planning.

**Changes (phase.md):**
- **§6 (competitive analysis → idea.md):** added explicit mandatory FEATURE PIPELINE: research similar apps → identify missing features → list them in `idea.md` → `plan.md` implements them. `idea.md` must now contain a **Feature Gap List** section — deduplicated, prioritized entries with competitor source, user value, and effort estimate (S/M/L). No silent drops: every entry must be implemented by a phase or explicitly deferred with reason.
- **§8 (phased plan → plan.md):** added the feature-gap coverage rule — every Feature Gap List entry must map to a named phase (feature → phase cross-reference) or appear under a `## Deferred` section with its reason. A plan with only infrastructure/reliability phases is declared incomplete. Prerequisites still order first (e.g., repository refactor before sync).

**Files changed:** `.devin/prompt/phase.md` (§6, §8), this changelog.

## 2026-09-17 — Remote-first verification policy

**Problem:** the verify rules (`rules.md` §4, map.md lifecycle) demanded a full local `assembleDebug` + test run before any task could be declared done — on a laptop, that's sustained CPU/heat/8 GB daemon heap per task.

**Changes:**
- **rules.md** — §4 rewritten as tiered verification: targeted compile/scoped tests while iterating, scoped lint before commit, full gate delegated to CI (`.github/workflows/checks.yml`), full local build only for build-file/native/dependency changes or on-device APKs.
- **map.md** — lifecycle verify step + Stop-hook table updated to match (correct tier; CI green counts).
- **AGENTS.md (project root)** — added Remote-first verification policy paragraph; fixed stale `CONTEXT.md` reference (lives in `docs/CONCEPTS.md`).
- **Global policy** — same verification policy added to `%APPDATA%\devin\AGENTS.md` so it applies to every project; user-level hooks softened (`posttool_edit.js` lint reminder notes CI counts; `stop.js` asks for correct tier instead of implying local reruns).
- **Repo** — new `checks.yml` workflow (ktlint + detekt + unit tests on PR/push, all branches except `nightly`); `org.gradle.caching=true` in gradle.properties.

## 2026-09-08
- **phase.md** — added SWE audit focus to competitive analysis (§5); added `research.md` technical research system with template (§6); added phase-completion criteria (§8), design quality gate (§9), report format (§10), project definition of done (§11).
- **rules.md** — created: scoping, pre-edit, implementation, verification, documentation, and escalation rules for quick tasks.
- **quick.md** — added references to `rules.md` (§5) and `map.md` (§8); clarified agent usage (§1) and `research.md` usage (§6).
- **map.md** — created: system map with visual overview, document map, task lifecycle, agent delegation, reading order, and write-back order; integrated `research.md` throughout.

## 2026-09-08 (later)
- **Portability restructure** — `docs/` contents (`idea.md`, `research.md`, `plan.md`, `project.md`, `tools-log.md`) are now defined as per-project artifacts, created fresh for each new project; `.devin/prompt/` is the portable, project-agnostic engine.
  - **phase.md** — new §12 (portability); §5–7 and §11 now reference `docs/` paths with "create fresh if absent" instructions; research.md template serves as the bootstrap for new projects.
  - **map.md** — file tree marks `docs/` as fresh-per-project with a portability note; reading order entries made conditional ("if absent, it will be created").
  - **rules.md** — §2 doc-reading made conditional; agents must not block on missing per-project docs.
  - **quick.md** — point 6 updated: create `project.md`/`tools-log.md` fresh if absent.
- **phase.md** — new §13 (gitignore system): always ignore `.devin/` and `docs/`; keep `.gitignore` minimal — only real artifacts, no dead entries.
- **phase.md** — new §14 (README system): signature personal template — centered cover (logo, shields.io badges, tagline, inline nav), plain section headers, scannable Features/Screenshots/Tech Stack tables, ≤3-step Quick Start with collapsible advanced setup, Roadmap checklist, License; tagline must answer what/who/why in the first 50 words; explicit anti-slop rule: no emojis, no marketing fluff or buzzwords — facts only; README kept current with feature changes.
- **phase.md** — README system upgraded: added Project Structure (ASCII tree), Usage example, FAQ/Troubleshooting (real content only), Contributing, Changelog link sections; Mermaid architecture diagram option for complex projects; alt-text requirement; discipline rules — GitHub About panel sync, optional sections earned not defaulted, above-the-fold first screen, mandatory real screenshots for UI projects, Table of Contents for READMEs >6 sections.

## 2026-09-12 — Count reconciliation & loose-end cleanup

**Problem:** Counts drifted across files after rules 37–39 (caveman, playwright-design-clone, pixel-perfect-image-analysis) were added without updating all index/mapping files. The empty `.devin/workflows/` directory was undocumented. `skill prompt.md` had no archive marker. Portability claim was undercut by undocumented global-path dependencies.

**Canonical counts established (single source of truth):**
- Main agents: 9 (7 domain + 1 full-stack orchestrator + 1 vibe coding guardian)
- Sub-agents: 38 (one per workflow)
- Workflows: 38 (live globally at `~/.codeium/windsurf/windsurf/workflows/`; `.devin/workflows/` is empty for project-local overrides)
- Rules: 39 (38 domain rules + 1 orchestration meta-rule #35 which has no workflow/sub-agent)
- Skills: 38 domains (each has a deep-methodology `.md` + an invokable `SKILL.md`)

**Files changed:**
- **rules/35-agent-system.md** — header 37→38 workflows; sub-agents 37→38; Design Engineer sub-agents 8→9 (added pixel-analyst); Quality Engineer sub-agents 6→7 (added pixel-analyst); added pixel-analyst row to mapping table (rule 39); workflows clarification (global vs. project-local override); "37-workflow" → "38-workflow" in applicability.
- **agents/INDEX.md** — Total Agent Files 48→49 (9 agents + 2 index + 38 sub-agents); workflows file tree note clarified; Design Engineer 8→9 sub-agents; Quality Engineer 6→7 sub-agents.
- **agents/README.md** — "37 workflows" → "38 workflows"; file tree updated (added full-stack-orchestrator.md, sub-agents 37→38, workflows note); Full Stack Orchestrator added to the 9 Main Agents table; pixel-analyst added to Design Engineer and Quality Engineer sub-agent lists.
- **prompt/map.md** — file tree rewritten: sub-agents 37→38, skills dual-file structure documented (flat `.md` + `SKILL.md`), workflows dir clarified (empty for overrides), rules 39 with breakdown (38 domain + 1 orchestration), `skill prompt.md` marked as archive; agent delegation map updated (pixel-analyst added to Quality and Design, caveman-compressor added to Infra); portability note expanded (`.devin/` orchestration layer is environment-specific, not copied per project).
- **prompt/skill prompt.md** — added archive header comment (reference only, not an active prompt).
- **workflows/README.md** — created: documents that the directory is intentionally empty, workflows live globally, this dir is for project-local overrides; includes the full 39-row workflow/rule/sub-agent mapping table with explanation of why 39 rules but 38 workflows.
- **~/.codeium/windsurf/memories/global_rules.md** (global) — sub-agents 35→38; rules 36→39; skills 35→38; workflows 35→38; orchestration description 35→38 sub-agents; added rules 37–39 to the rule tables (new "Infrastructure Utilities & Visual Tools" section); lifecycle summary updated (rule 37→Infra, 38→Design, 39→Quality); stats section fully reconciled.

## 2026-09-12 — Devin CLI native migration (max potential optimization)

**Problem:** The prompt system was built for Windsurf/Codeium and had critical incompatibilities with Devin CLI's native extensibility model. Large parts of the system were invisible to Devin.

**Changes made:**
- **AGENTS.md** (project root) — created: Devin auto-loads this at every session start as always-on rules. It is the entry point that references the entire `.devin/prompt/` system. Un-gitignored (was previously blocked).
- **.gitignore** — removed `AGENTS.md` and `**/AGENTS.md` (was blocking Devin's recommended rules approach); added `.devin/` and `docs/` (per phase.md §13 — private, never pushed).
- **.devin/agents/** — moved 38 sub-agent files from `.devin/sub-agents/` (invisible to Devin) to `.devin/agents/` (Devin's native scan location). Rewrote all frontmatter to use Devin-native fields (`name`, `description`) instead of Windsurf fields (`agent: true`, `type: sub`, `parent:`, `workflow:`).
- **.devin/orchestrators/** — moved 9 main orchestrator files out of `.devin/agents/` to prevent Devin from loading them as empty subagent profiles. These are reference docs, not spawnable profiles.
- **.devin/agents-INDEX.md** and **.devin/agents-README.md** — moved INDEX.md and README.md out of `.devin/agents/` to prevent loading as empty profiles.
- **.devin/sub-agents/** — deleted (all files safely moved to `.devin/agents/`).
- **.devin/config.json** — created: project-level permissions for Android/Gradle development (allow: gradle, git reads, adb; ask: release builds, git push, gradle config edits; deny: sudo, rm -rf /, secret file writes).
- **~/.config/devin/mcp_config.json** — enabled Playwright MCP (was disabled); added GitHub MCP server.
- **docs/** — bootstrapped: created `project.md`, `tools-log.md`, `plan.md`, `research.md` per phase.md §3–7.
- **.devin/workflows/*.md** — set `auto_execution_mode: 3` (turbo) on all 38 workflow files (was 0 or missing).

**Prompt system files updated:**
- **quick.md** — rewritten: added AGENTS.md as Devin entry point; added MCP/hooks/permissions awareness (points 8–9); updated subagent references to use `.devin/agents/` by name; clarified orchestrators are reference docs.
- **map.md** — rewritten: new visual overview with AGENTS.md as entry point; new file tree reflecting Devin-native structure (agents/ = 38 subagents, orchestrators/ = 9 reference docs, config.json, mcp_config, hooks); new §6 Devin Extensibility Stack table; updated Mermaid diagrams; updated reading order; updated agent delegation map.
- **rules.md** — updated: added project permissions check (§2); added subagent invocation by name (§3); added MCP tools usage (§3); added hook respect (§3); updated subagent verification to use `.devin/agents/` (§4); added Stop hook checkpoint (§4); updated gitignore rule to clarify AGENTS.md must NOT be gitignored (§5).
- **phase.md** — updated: §8 now references `code-reviewer` subagent from `.devin/agents/`; §13 clarifies AGENTS.md must NOT be gitignored; new §15 Devin extensibility stack (AGENTS.md, subagents, skills, workflows, rules, config, MCP, hooks, user config); new §16 subagent invocation rules (invoke by name, orchestrators are reference docs, priority order).
- **changelog.md** — this entry.

**Canonical structure (single source of truth):**
- AGENTS.md (project root, committed to git) — Devin entry point
- .devin/agents/ (38 files, gitignored) — spawnable subagent profiles
- .devin/orchestrators/ (9 files, gitignored) — reference docs for delegation guidance
- .devin/skills/ (38 dirs, gitignored) — invokable via /skill-name
- .devin/workflows/ (38 files, gitignored) — execution plans, auto_execution_mode: 3
- .devin/rules/ (39 files, gitignored) — always-on domain rules
- .devin/prompt/ (6 files, gitignored) — portable prompt engine
- .devin/config.json (gitignored) — project permissions
- docs/ (5 files, gitignored) — per-project knowledge

## 2026-09-12 — Release engineering, resource deletion, README image policy (Phase 7 + v1.0.0 release)

**Trigger:** First successful end-to-end Android release (keystore creation → GitHub secrets → tag push → CI release build → signed APK publication). Several hard-won learnings needed to be captured for future releases.

**Changes made:**
- **phase.md §14 (README template)** — removed `<img>` logo line from the template; changed "Screenshots are mandatory for UI projects" to "Screenshots and images are optional — include only when the user explicitly requests them. A lean text-and-badges README is the default — no `<img>` tags unless asked." Updated "First screen matters most" to drop the "logo" reference (badges + tagline + nav only).
- **phase.md §17 (Release engineering)** — new section: keystore creation (keytool, RSA-4096, 30y, stored at `C:\Users\shadd\keystores\`), GitHub secrets pattern (4 secrets: RELEASE_KEYSTORE base64 + alias + 2 passwords), pre-release validation (run release build before tagging — R8 catches what debug skips), versionName vs tag alignment, tag-triggered release workflow, `gh run watch` monitoring, force-tag re-triggering after fix, `gh release view` verification.
- **phase.md §18 (Android resource deletion protocol)** — new section: before deleting any resource, grep ALL reference types — AndroidManifest.xml, Kotlin/Java source (`R.*.*`), XML layouts/styles (`@*/*`). Only delete after zero remaining references.
- **phase.md §19 (Windows Git Bash gotchas)** — new section: `printf` mangles `\U`/`\C` in Windows backslash paths (use forward slashes or heredocs); `subst X:` for ndk-build with space-containing paths; Python may not be installed (use `py -3` or Node fallback); ImageMagick may not be installed (use PowerShell `System.Drawing`).

**Why:** The v1.0.0 release hit a latent proguard bug (spaces in package name from pre-rebrand era) that only surfaced in R8 full mode — debug builds were green. The icon replacement required grepping manifest + Kotlin + XML to catch all references. The keystore creation hit a `printf \U` mangling bug. All three are reusable across future Android projects, so they went into the portable prompt system rather than per-project docs.

**Per-project learnings (docs/research.md):**
- ADR-008: Release engineering setup — keystore, secrets, proguard fix.
- ADR-009: Icon unification — single TypeCraft.png source, deleted adaptive icon set.

## 2026-09-12 — Tool selection phase (docs/toolset.md)

**Problem:** Every session, Devin had to scan all 162 `.devin/` resources (38 skills, 38 workflows, 38 sub-agents, 39 rules, 9 orchestrators) to figure out which were relevant to the current task. This is wasteful — a music streaming app will never need web-scraping, payment, PWA, SEO, email, monorepo, or CSS-architecture resources. The system needed a curated selection layer so Devin knows exactly which tools to use without scanning everything.

**Solution:** Added a new "tool selection" phase (phase.md §4) that runs after codebase analysis (§3). Devin curates the subset of `.devin/` resources relevant to THIS project and writes them to `docs/toolset.md`. This file becomes the single source of truth for which tools are active — future sessions read it instead of scanning all 162 resources.

**Changes made:**
- **phase.md** — new §4 (tool selection): after analyzing the codebase, curate the relevant skills/workflows/sub-agents/rules/orchestrators and write to `docs/toolset.md`. Includes selection criteria (domain, tech stack, phase needs, always-included core tools), the exact `docs/toolset.md` format template (project profile, always-on rules, selected skills/workflows/sub-agents/orchestrators, excluded-with-rationale, update triggers), and the rule that resources not in toolset.md should not be loaded. All subsequent points renumbered (old 4–19 → new 5–20). §9 (execute) updated to review/update toolset.md at the start of each phase. §12 (definition of done) now includes `docs/toolset.md` in the docs list. §13 (portability) lists `toolset.md` as a per-project file and updates the creation range to "points 3–8". §16 (extensibility stack) adds `docs/toolset.md` as the first item — the curated entry point. §17 (subagent invocation) adds "check toolset.md first" rule.
- **map.md** — `docs/toolset.md` added to: ASCII visual overview (knowledge layer column), file tree (with "created per phase.md §4" note), Mermaid knowledge layer diagram (TOOLSET node + PHASE→TOOLSET arrow), task lifecycle (added to the Understand step reading chain), agent delegation map (check toolset.md first), reading order (new item 5 — read before scanning `.devin/`), write-back order (new item 3 — update if tools changed). All phase.md section references updated: §5→§6, §6→§7, §7→§8, §9→§10, §13→§14. phase.md file tree description updated to include "tool selection (§4)".
- **quick.md** — point 1 (subagents): added "check docs/toolset.md first" rule. Point 2 (skills/workflows): added "check docs/toolset.md first" and "don't scan all 38 blindly" guidance. Point 4 (study project): added `docs/toolset.md` to the reading list. Point 5 (rules): changed "follow the 39 domain rules" to "follow the domain rules listed in docs/toolset.md". Point 6 (docs): added reading and updating `docs/toolset.md`.
- **rules.md** — §2 (before editing): added `docs/toolset.md` to the doc-reading list; updated phase.md reference from "§3–7" to "§3–8". §3 (during implementation): subagent invocation now says "check docs/toolset.md first"; design gate reference updated from "§9" to "§10". §5 (documentation): added "update docs/toolset.md if new tools became relevant". Research reference updated from "§6" to "§7". Gitignore reference updated from "§13" to "§14".
- **changelog.md** — this entry.

**Section renumbering reference (phase.md):**
| Old | New | Title |
|-----|-----|-------|
| 4 | 5 | Clean up the repository |
| 5 | 6 | Competitive analysis → docs/idea.md |
| 6 | 7 | Technical research → docs/research.md |
| 7 | 8 | Phased plan → docs/plan.md |
| 8 | 9 | Execute the plan phase by phase |
| 9 | 10 | Design quality gate |
| 10 | 11 | Phase-completion report format |
| 11 | 12 | Project definition of done |
| 12 | 13 | Portability |
| 13 | 14 | Gitignore system |
| 14 | 15 | README system |
| 15 | 16 | Devin extensibility stack |
| 16 | 17 | Subagent invocation |
| 17 | 18 | Release engineering |
| 18 | 19 | Android resource deletion protocol |
| 19 | 20 | Windows Git Bash gotchas |

## 2026-09-13 — Intent-map restructure + meta-skill inclusion + caveman default-on

**Problem:** `docs/toolset.md` was organized by resource type (skills / workflows / sub-agents / rules) with separate tables the agent had to cross-reference per task. It also silently omitted all meta/utility skills (ce-work, ce-commit, ce-debug, ce-plan, ce-commit-push-pr, lfg, caveman, ce-explain, ce-brainstorm, ce-compound, ce-code-review, etc.) — filtering them out as if they were domain skills. During Phase 2 ("implement the phase 2"), `ce-work` (whose description literally says "Use when implementing from a plan document") never fired because it wasn't in the selected list. Caveman never fired because the user didn't explicitly ask for compression, even though it would have saved tokens. The agent defaulted to doing work inline.

**Root cause:** `toolset.md` was curated with a blind spot — it treated "project-relevant" as "domain-relevant" and forgot that workflow/utility skills are also project-relevant. The per-type table structure also required the agent to mentally join 4 tables per task, which it won't do reliably.

**Solution:** Three structural changes.

**1. `docs/toolset.md` restructured as intent-map** — one table keyed by task type, each row maps directly to skills + sub-agents + rules. One read tells the agent exactly what to invoke. All meta/utility skills (ce-work, ce-commit, ce-debug, ce-plan, ce-commit-push-pr, lfg, caveman, ce-handoff, ce-explain, ce-brainstorm, ce-ideate, ce-pov, ce-doc-review, ce-simplify-code, ce-compound, ce-strategy, ce-worktree, ce-babysit-pr, ce-resolve-pr-feedback, ce-test-browser, ce-optimize, ce-code-review) are now included. Excluded table kept (web-only resources genuinely N/A for a Kotlin music app).

**2. `AGENTS.md` (project root) created** — Devin auto-loads this at every session start. Contains the Resource Discipline rule: before any non-trivial task, read `docs/toolset.md` intent-map, find the task type row, invoke the listed resources. Forces the per-task scan that was previously optional. Also contains the caveman-lite default-on rule (delegates to the global `%APPDATA%\devin\AGENTS.md` for cross-project consistency).

**3. Caveman made globally available + default-on** — copied `.devin/skills/caveman/` to `%APPDATA%\devin\skills\caveman\` so `/caveman` works in every project. Added caveman-lite default to `%APPDATA%\devin\AGENTS.md` (global rules) so every session starts in lightly-compressed mode without requiring explicit invocation.

**Files changed:**
- **docs/toolset.md** — rewritten as intent-map (task type → skills/sub-agents/rules). All meta/utility skills included. Excluded table kept.
- **AGENTS.md** (project root) — created: Resource Discipline rule (mandatory intent-map scan per task), caveman-lite default, key references.
- **%APPDATA%\devin\skills\caveman\SKILL.md** — created (copy of project-local skill) for global availability.
- **%APPDATA%\devin\AGENTS.md** — created: global caveman-lite default-on rule.
- **.devin/prompt/quick.md** — commandments 1, 2, 4, 5, 6 updated: "check docs/toolset.md" → "check docs/toolset.md intent-map"; "find your task type row" language added.
- **.devin/prompt/map.md** — §3 (agent delegation): "check docs/toolset.md first" → "check docs/toolset.md intent-map first"; reading order item 5 rewritten to describe intent-map; write-back order item 3 updated.
- **.devin/prompt/phase.md** — §4 (tool selection): format template rewritten as intent-map; added "Meta/utility skills: ALWAYS include ce-work, ce-commit, ce-debug, ce-plan, ce-commit-push-pr, ce-handoff, lfg, caveman, ce-explain, ce-brainstorm, ce-compound, ce-code-review" to selection criteria. §16 (extensibility stack): "curated selection" → "intent-map". §17 (subagent invocation): "check docs/toolset.md first" → "check docs/toolset.md intent-map first".
- **.devin/prompt/rules.md** — §2 (before editing): doc-reading updated to reference intent-map. §3 (during implementation): subagent invocation updated to reference intent-map. §5 (documentation): toolset update language updated.
- **.devin/rules/35-agent-system.md** — "When this rule does NOT apply" section rewritten: removed the "non-website projects" exclusion (was wrong — many sub-agents are universally useful). Added "Project-type awareness" section explaining that `docs/toolset.md` intent-map is the authoritative filter for which sub-agents apply to THIS project.
- **changelog.md** — this entry.

**Why this matters:** The system was built but not used. 38 sub-agents, 38 skills, 39 rules, 9 orchestrators — and Phase 2 shipped using maybe 5 of them. The intent-map + Resource Discipline rule fixes the awareness bottleneck: one cheap read per task, agent discovers ce-work on "implement phase 2", ce-commit on "commit this", ce-debug on "fix this bug" — without the user naming the skill. Caveman now saves tokens by default instead of sitting idle.

## 2026-09-13 — All 39 rules set to always_on + agent exclusions fixed

**Problem:** Only 1 of 39 rules (35-agent-system) had `trigger: always_on` frontmatter. The other 38 had no frontmatter at all, so Devin loaded them as lazy-loaded (only when explicitly relevant). User wanted all 39 always-on. Also, `devops-engineer` and `dx-optimizer` were incorrectly excluded from the intent-map — CI/CD and developer experience are relevant to this project.

**Changes:**
- **.devin/rules/01–34, 36–39** (38 files) — added `trigger: always_on` YAML frontmatter to each. Now all 39 rules inject at session start.
- **docs/toolset.md** — added `devops-engineer` (CI/CD work row) and `dx-optimizer` (developer experience row) to intent-map. Removed them from Excluded table. Removed `deploy` and `dx` skills from Excluded (now included). Updated Always-On Rules section to note all 39 are now always-on.
- **changelog.md** — this entry.

**Cost:** ~75KB of rules now injected into every session. This is significant context overhead but ensures no rule is missed.

**Agent count in intent-map:** 18 of 38 (was 16). Excluded 20 are genuinely web-only (web-scraper, payment-integrator, pwa-engineer, seo-specialist, email-engineer, monorepo-manager, css-architect, i18n-specialist, design-cloner, analytics-engineer, realtime-engineer, content-writer, search-architect, file-handler, type-safety-engineer, frontend-designer, design-system-builder, researcher, backend-architect, animation-engineer).

## 2026-09-13 — Hooks rewritten in Node.js + intent-map enforcement hooks

**Critical discovery:** Python is NOT installed on this machine. The `python.exe` in WindowsApps is a Microsoft Store stub. All 5 existing hooks (session_start.py, pretool_exec.py, pretool_edit.py, posttool_exec.py, stop.py) have been silently failing since they were created — Devin loaded them, Python failed to execute, and the hooks produced no output.

**Fix:** Rewrote all 5 hooks in Node.js (v22.16.0 confirmed working). Updated `%APPDATA%\devin\config.json` to use `node` instead of `python` and `.js` instead of `.py`.

**New enforcement hooks:**
- **stop.js** — completely rewritten: enforces intent-map compliance before stopping. Asks agent to confirm: (1) which intent-map row was used, (2) were all listed skills invoked, (3) were all listed sub-agents invoked, (4) was code-reviewer run on the diff, (5) were rules read, (6) were docs updated. Uses `stop_hook_active` to prevent infinite loops (blocks once, allows on second attempt).
- **pretool_edit.js** — updated: now includes both the secret-leak guard (original) AND a new intent-map reminder that fires on every edit, nudging the agent to read `docs/toolset.md` before editing source files.

**Also fixed:**
- **session_start.js** — rewritten in Node.js, now also checks for `docs/toolset.md` and reminds agent to read the intent-map.
- **pretool_exec.js** — rewritten in Node.js, same destructive-command blocking behavior.
- **posttool_exec.js** — rewritten in Node.js, same audit-log behavior.
- **map.md** — new §2 "Full System Flow" added: shows the complete automated cycle from user request to completion, including hook enforcement. Documents what the hooks do and don't enforce. Section numbers updated (2→3, 3→4, 4→5, 5→6, 6→7).

**Windows stdin fix:** `fs.readFileSync('/dev/stdin')` does NOT work on Windows (resolves to `C:\dev\stdin`). All hooks use `fs.readFileSync(0, 'utf8')` (file descriptor 0 = stdin) which works cross-platform.

**Verification:** All 5 hooks tested with piped JSON input — all produce correct output. Catastrophic commands block with exit code 2. Dangerous commands produce warnings. Stop hook produces compliance check. Edit hook produces intent-map reminder.

## 2026-09-13 — Revert 35 rules to lazy-loaded (token limit fix)

**Problem:** Setting all 39 rules to `trigger: always_on` pushed ~75KB into every session's context. Devin hit its token limit and dropped 22 rules — including critical ones (06-debug, 07-testing, 16-database, 36-anti-vibe-coding, 37-caveman). The rules that survived were mostly web-only ones (CSS, payment, email, PWA) that are excluded in toolset.md — they wasted the budget that applicable rules needed.

**Fix:** Reverted 35 rules to lazy-loaded (removed `trigger: always_on` frontmatter). Kept only 4 always-on:
- 05-code-review (required every task)
- 07-testing (required every task)
- 35-agent-system (orchestration meta-rule)
- 37-caveman (compression default)

The other 35 rules load on-demand via the intent-map. When a task type row lists a rule, the agent reads it then. This is what the system was designed for — the 39 always-on was a mistake.

**Also fixed:**
- **docs/toolset.md** — "Implement from plan/spec" row now explicitly says "read docs/plan.md first to find phase scope". "How to Use" section step 1 now says "If implementing a phase: read docs/plan.md first". Always-On Rules section rewritten to reflect 4 rules, not 39.

**Why:** The intent-map + lazy-loading is the correct architecture. Always-on for everything broke it. The 4 always-on rules cover universal requirements (review, test, orchestrate, compress). Everything else is domain-specific and should load when the domain is relevant.

## 2026-09-17 — Phase 1 (Optique fork)
- Created docs/tools-log.md (was referenced by quick.md/rules.md but never existed).
- Learned: ownership sweeps need a distinct "monetization endpoints" category (PayPal/Revolut/crypto wallets/FUNDING.yml) — author-name grep alone missed them; code-reviewer caught it.
- Learned: repo-name consistency check belongs in the ownership sweep (Gallery→ReFra→Optique collisions surfaced at review).
- Note: user-level hooks (session_start, pretool_*, posttool_*, stop) are wired via ~/.config/devin/config.json — project-level hooks.v1.json does not exist; enforcement currently lives at user level.

## 2026-09-17 — System audit pass (post-Phase 1)
- **Renamed** `docs/agent.md` → `docs/tools-log.md`. Purpose narrowed: logs ONLY which .devin resources (skills, sub-agents, rules) were invoked per session — not implementations/decisions (those live in project.md/research.md). All references updated in AGENTS.md, quick.md, rules.md, map.md, phase.md.
- **Removed** `.devin/workflows/` (38 files). They were Windsurf-format docs (`auto_execution_mode` is not executable by Devin); the real execution layer is `.devin/skills/<name>/SKILL.md` slash commands + user-level ce-* skills. All references updated: rules (each rule's Workflow line now points to `.devin/skills/<name>/SKILL.md`), agents/, agents-INDEX.md, agents-README.md, map.md, phase.md, quick.md, rule 35.
- **Added** `scripts/verify-env.ps1` — Windows build-env check (JDK 17/21, SDK 37, NDK pinned to `refra.ndkVersion`, CMake 3.31.x, no CMake 4.x). Wired into quick.md commandment #10 and AGENTS.md build section. Verified: this machine passes all checks.
- **Added** `docs/CONCEPTS.md` — project vocabulary from CONTEXT.md ubiquitous language (Cutout Engine, Mask Refinement, etc.) + build/distribution/cloud terms. Update via /ce-compound.
- **Added** toolset.md rows: fork bootstrap, rebrand sweep, doc-only changes, env/toolchain verification.
- **Skipped** `.devin/hooks.v1.json` (owner decision — user-level hooks suffice).

## 2026-09-17 — Hooks upgrade (stack-agnostic, all projects)
- **New** `posttool_edit.js` (PostToolUse edit|write): lint reminder — detects linter by file extension + project marker files (Gradle→ktlint/detekt, Swift→swiftlint, ESLint configs→eslint, Stylelint, Ruff, Go, Cargo, dotnet). Silent when no linter configured. Registered in user config PostToolUse.
- **pretool_exec.js**: added RELEASE_PUBLISH warning category — gradlew *Release, fastlane, npm/yarn/pnpm publish, cargo publish, pod trunk push, flutter build appbundle/ipa/apk, eas build/submit, gh release create, App Store upload. Warn mode (not block).
- **stop.js**: item 7 added — requires docs/tools-log.md session entry before stopping.
- **session_start.js**: workflow references removed (aligns with .devin/workflows removal).
- All hooks verified with node --check + live stdin tests.

## 2026-09-26 — Stack sweep + stack-agnostic sanitization (FitGains/wger fork)

**Problem (FitGains project):** First non-Android project to use the system. Audit found the "project-agnostic" prompt engine was polluted with the previous Android project's stack — §9 phase-completion demanded `./gradlew assembleDebug`; rules.md §4 literally said "For THIS project (LanSlide, Flutter + Rust)" and prescribed `fvm flutter`/`cargo clippy`; map.md's file tree was labeled "TypeCraft - Keyboard app"; quick.md §10 commanded a `scripts/verify-env.ps1` JDK/Android-SDK/NDK/CMake check that doesn't exist here; .devin/config.json allowed gradle/adb/bundletool but nothing Python. Root cause: no boundary between the portable engine and per-project values — concrete commands, tool names, and project names were inlined in "portable" files.

**Changes:**
- **phase.md** — NEW §21 "Stack sweep": mandatory at every Inception — grep `.devin/` for prior-stack tokens (build tools, language, project names), replace/genericize, fix config.json, delete or recreate referenced scripts, log the sweep. Rule of thumb codified: process text is portable; commands, tool names, project names, and paths are per-project → parameterize or move to AGENTS.md/docs/.
- **phase.md** — §6 engineering-audit genericized (JUnit/Robolectric/Turbine → stack-appropriate equivalents); §9 phase-completion criterion → "project's build/verify commands per AGENTS.md §Build/Verify"; §10 design gate → platform-agnostic (points at `docs/design/design-system.md`, Material 3/HIG/component-system conditionals, TalkBack→screen-reader); §16 config.json description → generic with instruction to update at Inception; §18 release engineering → marked stack-conditional (signed-binary projects); §19 → marked Android/R-resource conditional; §20 subst/ImageMagick notes → stack-conditional.
- **quick.md** — §9 permissions description → generic (reads config.json, warns not to assume prior project); §10 → generic env verification via AGENTS.md §Build/Verify instead of a hardcoded Android script.
- **rules.md** — §1 architecture conventions → generic (examples across stacks); §3 → "language/framework idioms"; §4 verification tiers → commands sourced from AGENTS.md + CI workflows, removed "LanSlide, Flutter + Rust" reference and fvm/cargo commands.
- **map.md** — file tree root "TypeCraft - Keyboard app/" → `<Project>/`; removed verify-env.ps1 + gradle/fastlane source dirs lines.
- **.devin/config.json** — rewritten for Python/Django stack: allow uv/ruff/manage.py/docker-compose/git reads; ask on push/tag/docker build/pyproject+settings writes; deny unchanged.
- **docs/tools-log.md** + **docs/CONCEPTS.md** — created (were in reading order but missing).
- **docs/idea.md** — added Engineering Hygiene & QoL section (H1–H7) — the §6 audit contract was previously fulfilled only by the Feature Gap List; hygiene items now share the same plan-coverage contract.
- **changelog.md** — this entry.

**Recurring pattern (third occurrence):** Windsurf→Devin migration, rule count drift, and now stack pollution — all caused by portable files containing per-project concrete values. §21 makes the sweep self-repairing on every future project instead of rediscovered.
## 2026-09-26 — Phase 3 retrospective lesson
- map.md: added rule of thumb — verify Feature Gap List items against the codebase before assigning them to a build phase (G4/G8 were already implemented upstream).
## 2026-09-26 — Phase 6 retrospective lesson
- map.md: never pre-write a verification/review verdict in a phase report — run the reviewer first, then record the actual verdict.
- map.md: when touching fixed/sticky UI, read the full parent template (footer, modals live outside #main) — local gates can't see layout overlap.
## 2026-09-26 — Phase 7 retrospective lessons
- rules.md §4: added permission-preserving queryset rule (custom prefetch querysets must chain onto get_queryset / re-run check_object_permissions — bare .get(pk) is a review blocker).
- rules.md §4: added baseline-first debugging rule (stash-baseline failing suites before treating them as regressions).

## 2026-09-28 — Stack sweep (Dirol-Reader import)
- Swept prior-stack tokens: `.devin/config.json` permissions (uv/Django/docker → gradle/gh/Android secrets), `.gitignore` therxmv dictionary entry genericized.
- No prior project names remain in `.devin/` or `docs/`.

## 2026-09-28 — Phase 3 (Build Reliability)
- libtd -> JitPack com.github.tdlibx:td:1.8.56; Firebase plugins/deps removed (ADR-004).
- CI green after 4 fixes: SDK licenses-only setup, gradlew exec bit, R8 8.3.37 classpath, branch rename master->main.
- Lesson: android-actions/setup-android@v3 unreliable on ubuntu-latest (obsolete tools package) — use preinstalled SDK + license accept.

## 2026-09-28 — Phase 4 (Codebase Refactor)
- Module split: :channels (domain+data), :feed (domain+data+presentation), :settings; packages kept (ADR-005).
- Version catalog gradle/libs.versions.toml; global.gradle reduced to appConfig.
- ktlint+detekt with baselines; CI gate. Turbine pinned 0.12.1 (coroutines 1.6.4).
- Lesson: ktlint plugin resolves baseline paths per-module — generate per-module baselines, never one root file.
- Lesson: cross-module smart casts fail when same-package classes split across modules — bind to local val.
