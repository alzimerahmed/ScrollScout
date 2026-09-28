# Quick Task — Entry Point

This file is the entry point for quick tasks. The full system is loaded via `AGENTS.md` (project root), which Devin reads automatically at every session start. Read `map.md` (same folder) before starting any task — it is the system map.

## Commandments

1) **Use subagents from `.devin/agents/` where they add value — not continuously.** Devin loads 38 subagent profiles from `.devin/agents/`. **Check `docs/toolset.md` intent-map first** — it maps task types to sub-agents, skills, and rules. Find your task type row, invoke the sub-agents listed there. If a task needs a sub-agent not in the intent map, update `docs/toolset.md` first. Invoke them by name (e.g., "review this code using the code-reviewer subagent"). Required: the `code-reviewer` subagent on the final diff before declaring the task done. Orchestrator reference docs live in `.devin/orchestrators/` — read them for delegation guidance, but they are not spawnable subagents.

2) **Leverage all relevant skills.** **Check `docs/toolset.md` intent-map first** — it maps task types to skills. Find your task type row, invoke the skills listed via `/skill-name` slash commands. Skills live in `.devin/skills/` (38 domain skills, `SKILL.md` inside each dir) and globally in `%APPDATA%\devin\skills\` (meta/utility skills like ce-work, ce-commit, ce-debug, lfg, caveman). Do not scan all skills blindly — use the intent-map row for your task type.

3) **First, understand our workflow and prompt system.** Read `map.md` (same folder) — it shows how every document, agent, rule, workflow, MCP server, hook, and config file connects, including the required reading order, task lifecycle, agent delegation, and write-back order. Follow it exactly.

4) **Study the project structure, codebase, and design system before making any changes.** Read `README.md`, `docs/project.md`, `docs/tools-log.md`, `docs/toolset.md`, and `docs/plan.md` to understand the current state. `docs/toolset.md` intent-map tells you which `.devin/` resources (skills, sub-agents, rules) to invoke for your task type — read it before scanning `.devin/`.

5) **Strictly follow all rules and instructions** — those given in the prompt, derived from research, and issued by subagents. Additionally, comply with every rule in `rules.md` (same folder), which defines scoping, verification, design, and escalation rules for all quick tasks. Follow the domain rules listed in the intent-map row for your task type from `.devin/rules/` when the task touches their domain.

6) **Read `docs/project.md` and `docs/tools-log.md`** to understand past implementations; update both at the end of the implementation. Read `docs/toolset.md` intent-map to know which tools to invoke; update it if new tools became relevant during the task. These are per-project files — if absent, create them fresh. Consult `docs/research.md` before adding any dependency or unfamiliar pattern, and append new tech decisions/findings to it.

7) **Place all documentation (`.md`) files inside the `docs` folder.** Never scatter `.md` files in the repo root or source folders.

8) **Use MCP servers and hooks where they add value.** Playwright MCP is enabled for browser-based testing and design cloning. GitHub MCP is configured for issue/PR/repo operations. Hooks (SessionStart, PreToolUse, PostToolUse, Stop) enforce policies automatically — respect their decisions.

9) **Respect project-level permissions.** `.devin/config.json` defines allow/ask/deny rules for this project — read it to see which commands are pre-approved, which require confirmation (typically git push, release builds, build-config edits), and which are denied (destructive operations, secret file writes). Do not assume the permissions of a previous project apply.

10) **Verify the build environment before any build-dependent task.** Run the project's build/verify commands defined in `AGENTS.md` (§Build/Verify) or `docs/toolset.md` — never assume a toolchain from memory; the stack varies per project. If a project-specific env-check script exists, run it; if not, verify the toolchain the project's docs name (package manager, language runtime, DB) before building.
