# FitWell

An Android fitness and nutrition tracker built in Kotlin. FitWell brings workout logging, calorie and nutrition tracking, step counting, BMI analysis, personalised diet and workout recommendations, and progress charts into one app. All data is stored on the device, so no account server is needed.

## Features

- **Onboarding and accounts:** local sign-up and login with SHA-256 password hashing, and detailed input validation for name, email, password, age, height and weight (metric or imperial units).
- **Calorie tracking:** log meals by type from a built-in food database of 60+ items, with calories, macros (protein, carbs, fat, fibre, sugar) and micronutrients. A daily summary shows progress against your calorie goal.
- **Nutrition label scanner:** point the camera at a packaged-food label and the app reads it with on-device OCR (ML Kit), then fills in calories and macros.
- **Workout logging:** log exercises with sets, reps, weight and duration, chosen from a library of 35+ exercises grouped by muscle group. Calories burned are estimated from MET values.
- **Step counter:** a foreground service counts steps using the device's step sensor, and converts them to distance and calories burned. A boot receiver restarts it after a reboot.
- **BMI and goals:** calculates BMI, BMR and TDEE from your profile, and sets a daily calorie goal for weight loss, maintenance or muscle gain. Weight-loss goals can include a target date, which adjusts the daily calorie target.
- **Personalised plans:** diet and weekly workout suggestions based on your goal and diet preference (vegetarian, vegan or non-vegetarian).
- **Progress charts:** calorie, step and workout charts with selectable date ranges, plus a weight trend line built from logged weigh-ins.
- **Hydration reminders:** scheduled notifications that can be switched on or off in the profile.
- **Personalisation:** light and dark themes, and a language setting (English, Hindi, Spanish, French, German) for recommendation text.

## Tech Stack

| Area | Tools |
| --- | --- |
| Language | Kotlin |
| UI | XML layouts with View Binding, Material Components, Navigation Component, ViewPager2 |
| Local database | Room (with KSP) |
| Async | Kotlin Coroutines and Flow |
| Charts | MPAndroidChart |
| Camera and OCR | CameraX, ML Kit Text Recognition |
| Background work | Foreground service, AlarmManager, boot receiver |
| Images | Glide |
| Build | Gradle 8.6, Android Gradle Plugin, KSP |

## Requirements

- Android Studio (recent stable version)
- JDK 17
- Android device or emulator running **Android 10 (API 29) or higher**. The project compiles against API 34.

## Getting Started

```bash
git clone https://github.com/aditya26mishra/FitWell.git
```

1. Open the project folder in Android Studio.
2. Let Gradle sync finish. Dependencies are downloaded automatically, including MPAndroidChart from JitPack.
3. Select a device or emulator and click **Run**.

To build a debug APK from the command line:

```bash
./gradlew assembleDebug
```

### Permissions

The app asks for these at runtime or install time:

| Permission | Used for |
| --- | --- |
| Activity recognition | Counting steps |
| Camera | Scanning nutrition labels |
| Notifications | Step service and hydration reminders |
| Foreground service (health) | Keeping step tracking alive in the background |
| Receive boot completed | Restarting step tracking after a reboot |
| Schedule exact alarm | Hydration reminders |

The camera and step sensor are optional hardware, so the app still installs on devices without them.

## Project Structure

```
app/src/main/java/com/fitwell/app/
├── activities/     # Splash, Auth, Main, Scan (camera + OCR)
├── fragments/      # Home, Nutrition, Progress, Profile and their tab fragments
├── adapters/       # RecyclerView adapters
├── database/       # Room database, entities and DAOs
├── services/       # StepService (step counting)
├── receivers/      # Boot and hydration reminder receivers
└── utils/          # Calculators, food and exercise data, validation, session handling
```

### Data Model

Room stores five tables, all linked to the user and deleted with them:

- `users`: profile, goals, diet preference, language and notification settings
- `food_log`: meals with macros and micronutrients
- `workout_log`: exercises with sets, reps, weight, duration and calories burned
- `step_log`: daily steps, distance and calories burned
- `weight_log`: weigh-ins over time

## Notes

- Everything is stored locally on the device. Uninstalling the app clears the data.
- Calorie, BMR and calorie-burn figures are estimates meant for general guidance, not medical advice.
- Internet access is only needed for food and exercise images, and for translating recommendation text into other languages.

## Author

**Aditya Mishra**
[GitHub](https://github.com/aditya26mishra) · [LinkedIn](https://linkedin.com/in/aditya26mishra)
