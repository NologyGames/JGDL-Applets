# JGDL – JavaScript Game Development Library

This folder is a direct port of the **JGDL** (Java Game Development Library) engine used by both **Popcorn** and **Hello**. The original Java engine lives in the project root under `JGDL/`.

## Engine layout (matches Java JGDL)

| Java (JGDL/)        | JS (jgdl/)        | Role |
|---------------------|-------------------|------|
| JGDLMain            | jgdl-main.js      | Entry: AddImage/AddSound, LoadResources(), Loop(), SetCurrentScene(), TimeHandler, Input, Video, Sound |
| JGDLVideoManager    | jgdl-video.js     | LoadImage (cache), BackBuffer (canvas), DrawImage(position, image, frameIndex), DrawLoading(%) |
| JGDLInputManager    | jgdl-input.js     | GetMousePos(), MouBtnPressed(btn), Read() at start of frame |
| JGDLTimeHandler     | jgdl-time.js      | Update(), iFrameTime (ms), fFrameTime (s), GetFPS() |
| JGDLTimeAccumulator | jgdl-time.js      | Init(main, limitMs), Update(), Ended(), Restart() |
| JGDLSoundManager    | jgdl-sound.js     | LoadSound/RegisterSound, Play(path), bEnableSounds |
| JGDLScene           | jgdl-scene.js     | CreateLayer(size), Draw() / Update() over layers, Initialize(), Execute() |
| JGDLLayer           | jgdl-layer.js     | CreateSprite(fileName, frameSize), Draw() / Update() over sprites |
| JGDLSprite          | jgdl-sprite.js    | position, pr_Image, Animations[], Draw(), Update(), MoveTo(), IsMouseOver(), Clicked() |
| JGDLAnimation       | jgdl-animation.js | Frames[], iFramesPerSecond, Update(), GetCurrentFrame(), Ended() |
| JGDLVector          | jgdl-vector.js    | fx, fy, atrib(), Floor() |

## Flow (same as Java)

1. **AddResources**  
   Game calls `AddImage(filename)` and `AddSound(filename)` (paths relative to `ImagesDir` / `SoundsDir`).

2. **LoadResources**  
   Engine loads every listed image and sound (with loading % on the back buffer), then continues.

3. **InitGame**  
   Game calls `SetCurrentScene(scene)`. Engine sets `scene.pr_Main = this`, then `scene.Initialize()` and `TimeHandler.Reset()`.

4. **Loop** (each frame)  
   `InputManager.Read()` → `TimeHandler.Update()` → `BackBuffer.Clear()` → `scene.Draw()` → if not paused: `scene.Update()`, `scene.Execute()` → optional pause overlay → next frame.

## Usage

Games register resources and a scene, then start the engine:

- Set `ImagesDir` / `SoundsDir` (e.g. `assets/popcorn/surfaces/`, `assets/popcorn/sounds/`).
- In `addResources`, call `AddImage` / `AddSound` for every asset.
- In `initGame`, create your scene (with `Initialize`, `Draw`, `Update`, `Execute`) and call `SetCurrentScene(scene)`.
- Call `Start(baseUrl)` so the engine runs `LoadResources` then the main loop.

Images are cached by full path (`ImagesDir + filename`). Sounds are decoded (e.g. .au) and stored by path; `SoundManager.Play(path)` plays them.
