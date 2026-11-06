# YBOW Browser

A modern, minimalist and Chromium-based Android web browser.

YBOW is a real Android browser built with Kotlin, Jetpack Compose and the Android
WebView engine (Chromium). No ads, no trackers, no analytics, no remote accounts.
Everything the browser remembers stays in a local database on your phone.

<p align="center">
  <b>Deep ink surfaces · signal teal accent · typography first</b>
</p>

---

## Download the APK

Every tagged version is published on the [Releases page](../../releases):

1. Open the [Releases page](../../releases) of this repository.
2. Pick the newest release and download
   `YBOW-Browser-<version>-install.apk` (or `-signed.apk` when the build used your
   own release keystore).
3. Open the downloaded file on your Android device and confirm the install
   (Android will ask you to allow "install unknown apps" for your file manager).

These builds are signed with the standard Android debug key, which is enough for
installing and testing, but that key is regenerated on every build machine, so
uninstall a previous version before installing a build signed with a different key.
See [Signing](#signing) for release builds with your own keystore.

### Alternative: CI artifacts

1. Open the **Actions** tab of this repository.
2. Select the latest successful **Android CI** run.
3. Download the **ybow-browser-debug-apk** artifact and unzip it.

## Features

### Real browser engine
- Android WebView (Chromium) rendering, so modern sites work as expected.
- HTTPS with strict SSL handling: certificate errors cancel the connection, they are
  never silently ignored.
- JavaScript, DOM storage, cookies, zoom and media playback support.
- Chromium **Safe Browsing** enabled through AndroidX WebKit.
- Mixed content blocked, file access from web pages disabled, and no JavaScript bridge
  is exposed to pages.

### Tabs
- Multi-tab browsing where every tab keeps its own real WebView instance, so back
  history, scroll position and forms survive tab switching.
- Tab grid with favicons, titles and URLs; close or switch with one tap.
- `target="_blank"` popups open as new tabs.
- Incognito tabs never write to the history database.

### Navigation
- Combined address and search bar: type a URL, a host name or search words.
- Search engines: **Google** and **DuckDuckGo** (switchable in settings).
- Back, forward, reload, stop and home actions.
- Live page progress bar, page title and favicon.
- Share and copy the current URL.
- Safe handling of external schemes: only `mailto`, `tel`, `sms`, `geo` and `market`
  links are handed to other apps.

### Local library
- Bookmarks with one-tap add/remove from the page menu.
- Browsing history with day grouping, local search and per-entry delete.
- Real file downloads through the platform DownloadManager, with a download manager
  screen that shows live progress and opens finished files.
- Cache, cookie and history clearing.
- Text upload support for `<input type="file">` on web pages.

### Design
- Original "deep ink + signal teal" palette, Material 3 components, custom type scale.
- Light, dark and system themes.
- Fluid Compose animations, edge-to-edge layout and a bottom toolbar built for phones.

## Privacy and security

| Topic | Behaviour |
| --- | --- |
| Ads / trackers / analytics | None. No third-party SDK is bundled. |
| Data leaving the device | None. No telemetry endpoint exists. |
| History | Stored only in a local Room database; can be disabled or cleared. |
| JavaScript bridge | No `@JavascriptInterface` bridge is added to any page. |
| SSL errors | Cancelled, never ignored. |
| Permissions | Only `INTERNET` and `ACCESS_NETWORK_STATE`. No storage permission is requested; downloads use scoped storage. |
| Third-party cookies | Off by default. |

### Incognito mode: what it really does

Incognito tabs in YBOW:

- never write to the history database,
- bypass the WebView cache,
- are excluded from bookmark quick access and history suggestions,
- are destroyed when closed.

Android's WebView does not expose per-profile cookie jars to normal apps, so cookies and
site storage are shared with normal tabs. YBOW does not pretend otherwise: clear cookies
from Settings when you want a clean session. Full storage isolation would require a
custom Chromium build, which is out of scope for this project.

### Known limitations

- Open tabs are not restored after the system kills the app process.
- Downloads use the system DownloadManager; pause/resume UI beyond the system
  notification is not implemented.
- JavaScript-disabled mode is available but many sites will not render correctly.

## Tech stack

| Layer | Library |
| --- | --- |
| Language | Kotlin 2.0 |
| UI | Jetpack Compose, Material 3 |
| Engine | Android WebView + AndroidX WebKit |
| Persistence | Room (history, bookmarks, downloads) |
| Preferences | DataStore Preferences |
| Async | Kotlin Coroutines and Flow |
| Build | Gradle Kotlin DSL, version catalog |

Minimum Android version: **8.0 (API 26)**. Target and compile SDK: **35**.

## Project structure

```
YBOW-Browser/
├── .github/workflows/        # CI (debug APK) and release pipeline
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/ybow/browser/
│       │   │   ├── browser/   # WebView engine, tabs, clients, ViewModel
│       │   │   ├── data/      # Room database, repositories, settings
│       │   │   ├── ui/        # Compose screens, theme, components
│       │   │   └── util/      # URL parsing helpers
│       │   └── res/           # strings, themes, launcher icon
│       └── test/              # JVM unit tests for the address bar logic
├── gradle/libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── LICENSE
└── README.md
```

### Where things live

- `browser/BrowserViewModel.kt` — tab lifecycle, WebView configuration, navigation,
  history recording and downloads.
- `browser/YbowWebViewClient.kt` — navigation, SSL and error handling.
- `browser/YbowWebChromeClient.kt` — progress, title, favicon, file chooser, popups.
- `data/` — Room entities/DAOs, `BrowserRepository`, `DownloadRepository`,
  `SettingsRepository`.
- `ui/browser/` — address bar, bottom toolbar, menu sheet, WebView host.
- `ui/home|tabs|library|downloads|settings/` — the remaining screens.

## Building locally

Requirements: JDK 17 and Android SDK with platform 35 and build-tools 35.0.0.

```bash
# Point Gradle at your SDK
echo "sdk.dir=/path/to/android-sdk" > local.properties

# Unit tests
gradle :app:testDebugUnitTest

# Debug APK -> app/build/outputs/apk/debug/app-debug.apk
gradle :app:assembleDebug
```

The Gradle wrapper JAR is not committed. Run `gradle wrapper --gradle-version 8.9` once to
generate the `gradlew` scripts and the wrapper JAR for your machine, or open the project in
Android Studio, which creates the wrapper during the first sync. CI installs Gradle 8.9
directly and does not need the wrapper.

## Continuous integration

`.github/workflows/android.yml` runs on every push and pull request to `main`:

1. checks out the repository,
2. installs JDK 17 and the Android SDK,
3. runs the unit tests,
4. builds the debug APK,
5. uploads the APK and the test report as artifacts.

`.github/workflows/release.yml` runs for `v*` tags, builds the release variant and
publishes a GitHub Release with an installable APK attached.

To cut a release, create and push a tag:

```bash
git tag v1.0.0
git push origin v1.0.0
```

### Signing

No keystore or password is committed to this repository. To sign release builds, add
these repository secrets and the workflow will use them:

- `SIGNING_KEYSTORE_BASE64` — your keystore, base64 encoded
  (`base64 -w0 release.jks`),
- `SIGNING_STORE_PASSWORD`
- `SIGNING_KEY_ALIAS`
- `SIGNING_KEY_PASSWORD`

Without them the release workflow publishes a debug-signed, installable APK alongside the
unsigned release build.

## Roadmap

- Tab session restore across process death.
- Download pause/resume UI.
- Custom home shortcuts.
- Per-site JavaScript and cookie rules.

## License

MIT — see [LICENSE](LICENSE).

---

## Türkçe özet

YBOW, Kotlin + Jetpack Compose + Android WebView (Chromium) ile yazılmış gerçek bir
Android tarayıcısıdır. Reklam, takip SDK'sı ve analitik içermez; hiçbir veri cihazdan
çıkmaz.

- **APK indirme:** GitHub'da `Releases` sayfası → en yeni sürüm →
  `YBOW-Browser-...-install.apk` (alternatif: `Actions` → son başarılı `Android CI`
  çalışması → `ybow-browser-debug-apk` artifact'i).
- **Özellikler:** gerçek çoklu sekme, adres/arama çubuğu, Google & DuckDuckGo,
  yer imleri, geçmiş, gerçek dosya indirme yöneticisi, açık/koyu tema, gizli sekmeler,
  önbellek ve çerez temizleme.
- **Gizlilik:** yalnızca `INTERNET` ve `ACCESS_NETWORK_STATE` izinleri; geçmiş yalnızca
  cihazdaki yerel veritabanında tutulur, istenirse kapatılabilir.
- **Gizli mod sınırı:** Android WebView uygulamalara ayrı çerez deposu vermediği için
  çerezler normal sekmelerle ortaktır; gizli sekmeler geçmişe kaydedilmez ve kapanınca
  yok edilir.
