# ScrollScout — smart news feed from your Telegram channels

<div align="center">

[![Platform](https://img.shields.io/badge/platform-Android-green?logo=android)](https://developer.android.com)
[![Language](https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=android)](https://developer.android.com/compose)
[![Engine](https://img.shields.io/badge/engine-TdLib-2AABEE?logo=telegram)](https://github.com/tdlib/td)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)
[![Release](https://img.shields.io/github/v/release/alzimerahmed/ScrollScout?label=release)](https://github.com/alzimerahmed/ScrollScout/releases)
[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF?logo=githubactions)](.github/workflows/ci.yml)

*One relevance-ranked feed of unread posts from every channel you follow — you train it by liking, disliking, and starring.*

[Download](https://github.com/alzimerahmed/ScrollScout/releases) • [Features](#features) • [Building](#building)

</div>

---

## Contents

[Features](#features) · [Tech Stack](#tech-stack) · [Project Structure](#project-structure) · [Building](#building) · [Usage](#usage) · [FAQ](#faq) · [Roadmap](#roadmap) · [Changelog](#changelog) · [License](#license)

---

## Features

- Single feed aggregating unread posts from all your Telegram channels
- Relevance ranking shaped by your signals: like/dislike a post, star a channel, tune weights per channel
- Supports text, photo, video, animation, audio, voice note, video note, sticker, document captions — plus polls (with voting), locations/venues, and contacts
- Swipe triage: right to mark read, left to save for later; mark-all-read per refresh
- Long-press translation into your device language
- Channel management: mute, reorder, group, per-channel notifications
- Full-text search over cached posts; offline reading from the local cache
- Material You dynamic color (Android 12+), dark/light themes
- In-app updates from GitHub Releases
- Built on TdLib — a real Telegram session, not a bot API

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Telegram | TdLib (via `com.github.tdlibx:td`) |
| DI | Hilt |
| Persistence | Room, MMKV |
| Media | Media3 / ExoPlayer |
| Background | WorkManager |
| Build | Gradle 8.3, AGP 7.4.2, multi-module |
| CI/CD | GitHub Actions (CI + tag-triggered signed releases) |

---

## Project Structure

```
app/                  application, DI, splash/auth/profile, navigation
common/               shared theme + resources
feed/                 ranked feed, search, message renderers (domain/data/presentation)
channels/             channel management, post cache + FTS (domain/data/presentation)
settings/             settings screens (theme, dynamic color)
ota-updates/          in-app update checker (domain / data / presentation)
shared-preferences/   MMKV-backed storage
```

---

## Building

1. Get Telegram API credentials at my.telegram.org (`API_ID`, `API_HASH`).
2. Put them in `local.properties` at the repo root:
   ```properties
   API_ID=123456
   API_HASH=abcdef0123456789
   ```
3. Build:
   ```bash
   ./gradlew assembleDebug
   ```

<details>
<summary>Release builds</summary>
Release signing is env-driven (`SCROLLSCOUT_KEYSTORE_PATH`,
`SCROLLSCOUT_KEYSTORE_PASSWORD`, `SCROLLSCOUT_KEY_ALIAS`, `SCROLLSCOUT_KEY_PASSWORD`).
Without them, `assembleRelease` produces unsigned APKs. Signed GitHub Releases are
cut by pushing a `v*` tag — see `.github/workflows/release.yml`. CI expects the
secrets `RELEASE_KEYSTORE` (base64 `.jks`), `RELEASE_STORE_PASSWORD`,
`RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`.
</details>

---

## Usage

1. Install the APK from [Releases](https://github.com/alzimerahmed/ScrollScout/releases) (`universal` works everywhere; `arm64-v8a` for modern devices, `armeabi-v7a` for older ones).
2. Sign in with your Telegram number — the app builds a real TdLib session on-device.
3. Open the feed: unread posts from all your channels arrive ranked by relevance.
4. Train it: like/dislike posts, star channels, or mute/reorder channels in Channel Control. Use swipe-right to mark read, swipe-left to save for later.

---

## FAQ

**Where do I get the APK?**
GitHub Releases — each tag ships signed ABI-split APKs plus a universal APK.

**Is my Telegram account safe?**
The app talks to Telegram through TdLib, the same client library official apps use. Your session stays on-device; there is no ScrollScout server.

**How do updates work?**
The app checks this repository's GitHub Releases and can download and install the new APK in-app.

---

## Roadmap

- [x] Saved posts / read later
- [x] Full-text search across cached posts
- [x] Channel management (mute, reorder, group)
- [x] Relevance tuning screen
- [x] Material You dynamic color
- [x] Offline reading
- [x] Poll, location & contact message types
- [x] Signed GitHub Releases distribution
- [ ] Post summarization
- [ ] Game messages
- [ ] Live poll updates (needs a TdLib update-handler layer)

---

## Changelog

See [GitHub Releases](https://github.com/alzimerahmed/ScrollScout/releases) for versioned release notes.

---

## License

MIT — see [LICENSE](LICENSE). Based on Dirol-Reader by Roman Kuzmych.
Maintained by Alzimer Ahmed.
