<<<<<<< HEAD
# Kreeda-Prerana Scout 🏏

**Digital Scout for Rural Athletic Talent** — Supporting Khelo India

An Android application designed to serve as a grassroots sports talent tracking tool for PE teachers in rural schools across India. Kreeda-Prerana enables systematic recording of athletic performance, talent progression visualization, and automated badge-based recognition.

## Features

- 🏃 **Athlete Profile Management** — Create and manage comprehensive student-athlete profiles
- ⏱ **High-Precision Chronometer** — Timing accurate to 0.01 seconds using `SystemClock.elapsedRealtime()`
- 📏 **Distance Logger** — Record field event measurements (long jump, high jump, shot put)
- 📈 **Talent Curve Visualization** — Performance progression graphs with benchmark overlays
- 🏅 **Milestone Badge System** — Automated badges for District, State, and National level readiness
- 🏆 **School Leaderboard** — Ranked performance lists with sport/event/gender filters
- 📝 **Batch Entry Mode** — Efficient data entry for entire classes (30+ students)
- 📴 **Fully Offline** — All data stored locally via Room Database
- 🇮🇳 **Hindi Support** — Bilingual English/Hindi interface

## Tech Stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Database | Room Persistence Library |
| DI | Hilt |
| Charts | Vico |
| Architecture | MVVM + Clean Architecture |
| Min SDK | API 24 (Android 7.0) |

## Building

1. Open in Android Studio (Hedgehog or later)
2. Sync Gradle dependencies
3. Run on device or emulator (API 24+)

```bash
./gradlew assembleDebug
```

## Project Structure

```
app/src/main/java/com/kreeda/prerana/
├── data/           # Room entities, DAOs, repositories
├── domain/         # Use cases, domain models
├── di/             # Hilt dependency injection modules
├── ui/             # Compose screens, components, theme
│   ├── theme/      # Material 3 color, typography, theme
│   ├── components/ # Reusable composables
│   ├── screen/     # Feature screens + ViewModels
│   └── navigation/ # Nav graph + routes
└── res/            # Strings (EN/HI), themes, colors
```

## License

Built for India's young athletes 🇮🇳
=======
# Kreeda---prerana-Scout
>>>>>>> 5d32220f42b30938382e36834c5cb7754e65dd0a
