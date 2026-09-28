# ScrollScout — smart news feed from your Telegram channels

<div align="center">

[![Platform](https://img.shields.io/badge/platform-Android-green?logo=android)](https://developer.android.com)
[![Language](https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=android)](https://developer.android.com/compose)
[![Engine](https://img.shields.io/badge/engine-TdLib-2AABEE?logo=telegram)](https://github.com/tdlib/td)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)
[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF?logo=githubactions)](.github/workflows/ci.yml)

*One relevance-ranked feed of unread posts from every channel you follow — you train it by liking, disliking, and starring.*

[Features](#features) • [Tech Stack](#tech-stack) • [Building](#building)

</div>

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

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Telegram | TdLib (via `com.github.tdlibx:td`) |
| DI | Hilt |
| Persistence | Room, MMKV |
| Media | Media3 / ExoPlayer |
| Build | Gradle 8.3, AGP 7.4.2, multi-module |

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
Release signing is optional and env-driven (`SCROLLSCOUT_KEYSTORE_PATH`,
`SCROLLSCOUT_KEYSTORE_PASSWORD`, `SCROLLSCOUT_KEY_ALIAS`, `SCROLLSCOUT_KEY_PASSWORD`).
Without them, `assembleRelease` produces unsigned APKs. GitHub Releases are cut by
pushing a `v*` tag — see `.github/workflows/release.yml`.
</details>

## Roadmap

- [x] Saved posts / read later
- [x] Full-text search across cached posts
- [x] Channel management (mute, reorder, group)
- [x] Relevance tuning screen
- [x] Material You dynamic color
- [x] Offline reading
- [x] Poll, location & contact message types
- [ ] Post summarization
- [ ] Game messages
- [ ] Live poll updates (needs a TdLib update-handler layer)

## License

MIT — see [LICENSE](LICENSE). Based on Dirol-Reader by Roman Kuzmych.
Maintained by Alzimer Ahmed.
