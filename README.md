# Doctor Knepper's Pocket Dimension

Craft a Dimensional Key to visit your own private pocket realm: a 10x10 bedrock platform floating in the void, with a sky you choose.

![Dimensional Key](src/main/resources/assets/dimensionalkey/icon.png)

## How it works

- **Use** the key to travel to your pocket realm. Use it again to go back to exactly where you left from.
- Every player gets their **own platform**, far away from everyone else's. Build whatever you like on it, and the realm remembers where you last stood.
- **Sneak + Use** to change your realm's sky: Natural (day and night), Eternal Day, Starry Night, Golden Sunset, Nebula, The End, or Void.
- Fall off the edge and the realm pulls you back onto your platform.
- The area around your platform stays loaded, so farms and machines keep running while you're away.
- The key is fire-resistant and **stays with you when you die**, so you can't lose your way home.

## Recipe

| | | |
|:-:|:-:|:-:|
| Obsidian | Obsidian | Obsidian |
| Obsidian | Diamond | Obsidian |
| Obsidian | Obsidian | Obsidian |

The recipe unlocks when you pick up obsidian.

## Requirements

- Minecraft **26.3** (Java Edition)
- [Fabric Loader](https://fabricmc.net/use/) 0.20.1 or newer
- [Fabric API](https://modrinth.com/mod/fabric-api)

Install it on both the client and the server.

## Installing

Download `dimensional-key-<version>.jar` from the [releases page](https://github.com/DoctorKnepper/Doctor-Kneppers-Pocket-Dimension/releases), Modrinth or CurseForge, and put it in your `mods` folder along with Fabric API.

## Building from source

You need a Java 25 JDK.

```sh
./gradlew build    # the mod jar ends up in build/libs/
```

## Releasing

Publishing a GitHub release (Releases, then "Draft a new release", with a tag like `v1.0.0`) runs
`.github/workflows/release.yml`. It builds the jar, attaches it to the release and uploads it to Modrinth
and CurseForge. It needs these repository settings (Settings, then "Secrets and variables", then "Actions"):

| Name | Kind | Value |
|---|---|---|
| `MODRINTH_TOKEN` | secret | a Modrinth personal access token with the "Create versions" scope |
| `CURSEFORGE_TOKEN` | secret | a CurseForge API token (from your CurseForge account's API tokens page) |
| `MODRINTH_ID` | variable | the Modrinth project ID |
| `CURSEFORGE_ID` | variable | the CurseForge project ID |

If one platform's values aren't set, the workflow skips that platform. Update `mod_version` in
`gradle.properties` before tagging a new release.

## License

CC0 1.0 (public domain). See [LICENSE](LICENSE).
