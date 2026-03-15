# Shared game engine (JGDL)

Both **Super Popcorn Machine** and **Hello!** run on a shared engine ported from the original Java **JGDL** (Java Game Development Library). The Java sources live in the project root under `JGDL/`; the JavaScript port is in `js/jgdl/`.

## Flow

1. **Resource registration** – Each game calls `AddImage(filename)` and `AddSound(filename)` (relative to `ImagesDir` / `SoundsDir`) in `addResources`.
2. **LoadResources** – The engine loads every listed image and sound (with a loading % on the canvas), decoding `.au` sounds via `AssetLoader.decodeAu`.
3. **InitGame** – The game provides a **scene** with `Initialize()`, `Draw()`, `Update()`, `Execute()` and the engine calls `SetCurrentScene(scene)`.
4. **Loop** – Each frame: `InputManager.Read()` → `TimeHandler.Update()` → clear back buffer → `scene.Draw()` → (if not paused) `scene.Update()`, `scene.Execute()` → `afterExecute(main)` (UI/popups) → optional pause overlay.

## Usage

- **Popcorn** – `popcorn.html` loads the JGDL scripts and `main.js` creates `JGDLMain` with `imagesDir`/`soundsDir` from `popcorn-assets.js`, `addResources` (all `POPCORN_IMAGE_NAMES` and `POPCORN_SOUND_NAMES`), and a scene that delegates to `createPopcornGame`. Drawing uses `VideoManager.LoadImage(path)` and `getImg(name)`; sound uses `SoundManager.Play(path)` or `AudioManager` with engine main as assets.
- **Hello** – Same pattern in `hello.html` / `hello-main.js` with `HELLO_IMAGE_NAMES` and no sounds. `hello-game.js` uses `getImg(name)` so it can use either the preloaded asset list or the engine’s `VideoManager`.

If the JGDL scripts are not loaded, both games fall back to the previous flow: `loadPopcornAssets()` / `loadHelloAssets()` and a local `requestAnimationFrame` loop.

See `js/jgdl/README.md` for the Java ↔ JS class mapping and API details.
