# Heart Badges (Fabric mod)

Lets players wear one of 14 heart badges next to their name (tab list +
above-head nametag): 7 virtues (solid heart) and their 7 corrupted
counterparts (struck-through heart), matching:

Determination / Irresolution, Bravery / Cowardice, Justice / Corruption,
Kindness / Animosity, Patience / Agitation, Integrity / Dishonesty,
Perseverance / Apathy.

## Why this isn't already a .jar

Compiling a Fabric mod requires Gradle to download the Minecraft game
files, Fabric Loader, Fabric API, and Yarn mappings from the internet.
I don't have internet access in this sandbox, so I can't run that build
myself — but the project below is complete and builds in one step on
any machine with internet + Java 21.

## Build it

1. Install a JDK 21 (Temurin/Adoptium works well).
2. Open a terminal in this folder.
3. Run:
   - macOS/Linux: `./gradlew build`
   - Windows: `gradlew.bat build`
4. The very first run has no Gradle wrapper jar bundled (see note
   below) — Gradle will fetch everything it needs automatically the
   first time, which takes a few minutes.
5. Your compiled mod appears at `build/libs/heart-mod-1.0.0.jar`.

### Note on the Gradle wrapper

This project doesn't include the binary `gradle-wrapper.jar` (I can't
ship binaries I haven't built/verified). If `./gradlew` complains it's
missing, just run once, in this folder, with any local Gradle 8.x install:

```
gradle wrapper --gradle-version 8.8
```

That generates the wrapper jar, then `./gradlew build` works from then on.
If you don't have Gradle installed either, grab it from
https://gradle.org/install/ (one-time, unrelated to Minecraft).

## Install on your Fabric server

1. Make sure your server is running **Fabric Loader** for Minecraft
   1.21.1, with **Fabric API** dropped into `mods/`.
2. Copy `heart-mod-1.0.0.jar` into your server's `mods/` folder.
3. Restart the server.

## Using it in-game

- `/heart determination` (or any of the 14 names below, lowercase)
- `/heart clear` — removes your badge

Names: `determination`, `irresolution`, `bravery`, `cowardice`, `justice`,
`corruption`, `kindness`, `animosity`, `patience`, `agitation`,
`integrity`, `dishonesty`, `perseverance`, `apathy`.

By default `/heart` requires no special permission — any player can set
their own badge. If you want to restrict it (e.g. only badges players
earn), that's a quick change to `HeartMod.java` — happy to add a
permission check if you're using a permissions mod like LuckPerms.

## If the build fails

Fabric bumps its version numbers often, and I built this offline so I
couldn't pin exact-latest numbers. If Gradle errors on a missing
version, open `gradle.properties` and update `minecraft_version`,
`yarn_mappings`, `loader_version`, and `fabric_version` to match what's
listed at https://fabricmc.net/develop/ for your target Minecraft
version, then rebuild.
