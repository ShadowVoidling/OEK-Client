# OEK Client v0.1 — Original Fabric Utility Client

Minecraft Java **1.21.11** · Fabric Loader **0.20.1** · Fabric API **0.141.6+1.21.11** · Java **21**

This is the **original source project**, not Wurst or a modified Wurst jar. The code is intentionally structured to add modules later. No CurseForge required.

## Easiest method: GitHub builds the .jar (no Java/Gradle installs required on your PC)

1. Sign in at [GitHub](https://github.com/) and create a **new empty repository** named `OEK-Client`. Choose **Public** or **Private**. Do **not** initialize it with a README.
2. Extract this source ZIP to a normal folder. Open that folder and select **its contents**, including the `.github` and `src` folders, `build.gradle`, `gradle.properties`, `settings.gradle`, and other files. Do **not** upload the ZIP itself or put the project inside an extra folder.
3. In the new GitHub repository, select **uploading an existing file** (or **Add file → Upload files**), drag the extracted *contents* into the upload area, then **Commit changes**. The workflow must end up at `.github/workflows/build.yml` in the repository root.
4. Open the repository's **Actions** tab. The workflow **Build OEK Client JAR** starts on a push to `main`. If it does not start, select the workflow and choose **Run workflow**.
5. When the run finishes with a **green check**, open it, find **Artifacts**, and download `OEK-Client-0.1.0-Fabric-1.21.11` (GitHub downloads an artifact ZIP).
6. **Extract the artifact ZIP**. Copy `oek-client-0.1.0.jar` (NOT the ZIP) to `%appdata%\.minecraft\mods`, alongside the **Fabric API** jar for Minecraft 1.21.11.
7. Launch **Minecraft Java 1.21.11** with Fabric Loader in the Minecraft Launcher. Use **Right Shift** to open the OEK menu.

**Important:** The GitHub workflow has been added, but the mod source is **not yet compiled or tested**. The first build may fail on Java/Minecraft API mismatches; if the Actions run turns red, open **Build the Fabric JAR** and send the first meaningful `error:` line from the log. A green build checks compilation; actual in-game behavior still needs testing.

**Keep the repository private** if you do not want your source publicly visible. You are downloading the artifact for your own testing, not an official ready-to-play release.

---

## What is implemented

| Module | Status | Notes |
|---|---|---|
| OEK menu | Source implemented | Right Shift opens a toggle menu, saved to JSON |
| Ore Radar | Source implemented | Top-down radar displays ore blocks present in **client-loaded block data**; not terrain transparency |
| Ore filters | Source implemented | Enable individual ore types in menu |
| Coordinates HUD | Source implemented | Block X, Y, Z |
| Compass and FPS | Source implemented | Heading and FPS count |
| Zoom | Source implemented | Hold Z for 30-degree FOV, returns to previous FOV |
| Waypoint | Source implemented | B saves a waypoint or clears it in same dimension |
| Tool durability alert | Source implemented | Warning below 15% durability for main-hand item |
| Brightness Boost | Source implemented | Switches normal gamma setting to 1.0, **not** true fullbright |
| True X-ray / terrain transparency | **Not yet implemented** | Needs renderer compatibility work + testing |
| Wurst-style combat / automation | **Not yet implemented** | Deferred future modules |

**IMPORTANT:** The source has **not been compiled or runtime-tested here** because Gradle dependencies cannot be downloaded in the current environment. Treat this as an initial development build, not a ready-to-play release jar. The first build on your Windows machine may surface API issues that we can fix together.

## Windows build instructions

1. Install Java **21 JDK** (not only JRE): e.g., [Adoptium Temurin 21](https://adoptium.net/temurin/releases/?version=21).
2. Install **Gradle 9.6.0** or another version compatible with the configured Fabric Loom plugin (or open this project in IntelliJ IDEA with a configured compatible Gradle installation). [Gradle installation guide](https://docs.gradle.org/current/userguide/installation.html).
3. Unzip the project, open PowerShell in the folder with `build.gradle`, and run:

   ```powershell
   gradle build
   ```

4. Find the built mod in `build/libs/oek-client-0.1.0.jar` (choose the jar without `-sources`).
5. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft **1.21.11** via the official Minecraft Launcher.
6. Download the **Fabric API for Minecraft 1.21.11** and put the API jar and OEK jar into `%appdata%\.minecraft\mods`.
7. Start the **1.21.11 Fabric** installation in your regular Minecraft Launcher.

Your first build requires internet access to download Gradle dependencies and Minecraft libraries. **No Gradle wrapper jar is bundled**, so use installed `gradle build` rather than `gradlew.bat`.

## Controls

- **Right Shift** — open OEK menu
- **X** — toggle Ore Radar
- **Z** — hold to zoom (when enabled)
- **B** — save/remove one waypoint
- Change keybinds in Minecraft **Options → Controls → Key Binds → OEK Client**.

## Limitations and performance

Ore Radar scans a bounded area (default +/-16 blocks horizontally and vertically) every 30 client ticks. It does **not** access server-only hidden ore data and can only show blocks sent to your client. Some servers replace ore blocks in chunk data as anti-X-ray; these cannot be detected by this mod. Rendering every ore as a 3D outline and seeing through stone is future work.

The menu cycles scan radius 16 → 24 → 32 → 8 → 16. Higher radii have a significant CPU cost; keep radius at 16 while testing. Gamma Boost only uses Minecraft's supported brightness range.

Multiplayer servers may ban X-ray, utility overlays, or automation. Verify server rules before joining.

## File layout

- `OekClient.java`: client lifecycle, keybinds, HUD registration
- `OekConfig.java`: persistent config JSON
- `Module.java`: feature catalog
- `OreType.java`: supported ore types and colors
- `OreScanner.java`: loaded-chunk search
- `OekHud.java`: informational HUD + top-down ore radar
- `OekScreen.java`: configurable in-game GUI

## Roadmap

1. Test and compile v0.1 on Windows, resolve mapping/API issues if any.
2. Add a renderer-based ore outline module (genuinely visible through walls), tested without Sodium first.
3. Add draggable HUD widgets, categories, search, saved profiles, and adjustable colors.
4. Add performance controls, exploration tools, and user-selected additional utilities.
5. Port to Minecraft 26.3 / Java 25 after validating current Fabric APIs.

## License

Original OEK Client project scaffold is MIT licensed. It does **not** reuse or include Wurst source code. Wurst was used only as a feature reference. Official Fabric documentation is linked below.

Sources: https://docs.fabricmc.net/1.21.11/develop/key-mappings · https://docs.fabricmc.net/1.21.11/develop/rendering/hud · https://docs.fabricmc.net/1.21.11/develop/rendering/gui/custom-screens
