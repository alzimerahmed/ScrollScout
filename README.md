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
- Relevance ranking shaped by your signals: like/dislike a post, star a channel
- Supports text, photo, video, animation, audio, voice note, video note, sticker, and document captions
- Built on TdLib — a real Telegram session, not a bot API

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Telegram | TdLib (JNI) |
| DI | Hilt |
| Persistence | Room, MMKV |
| Media | Media3 / ExoPlayer |
| Build | Gradle 8.3, AGP 7.4.2, multi-module |

## Project Structure

```
app/                  application, DI, UI screens, TdLib client
common/               shared theme + resources
ota-updates/          in-app update checker (domain / data / presentation)
shared-preferences/   MMKV-backed storage
libtd/                TdLib Android wrapper (built separately)
```

## Building

1. Get Telegram API credentials at my.telegram.org (`API_ID`, `API_HASH`).
2. Put them in `local.properties` at the repo root:
   ```properties
   API_ID=123456
   API_HASH=abcdef0123456789
   ```
3. Provide the `libtd` module (TdLib Android build) and run:
   ```bash
   ./gradlew assembleDebug
   ```

<details>
<summary>Firebase (optional)</summary>
The app applies the google-services plugin. Without `app/google-services.json` the build fails;
a CI placeholder is generated automatically in GitHub Actions.
</details>

## Roadmap

- [ ] Saved posts / read later
- [ ] Full-text search across cached posts
- [ ] Channel management (mute, reorder, group)
- [ ] Relevance tuning screen
- [ ] Material You dynamic color
- [ ] Offline reading

## License

MIT — see [LICENSE](LICENSE). Based on Dirol-Reader by Roman Kuzmych.
Maintained by Alzimer Ahmed.
