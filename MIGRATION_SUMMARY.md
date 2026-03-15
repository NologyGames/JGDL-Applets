# JGDL Applet Game Migration — Summary

A brief summary of the **JGDL applet game migration** agent effort: moving two legacy Java applet games to the modern browser. For engineers considering similar legacy migrations.

---

## Summary of findings

The migration succeeded because the **user had prior know-how of the original application**. They corrected scope (both games, not one), pointed to existing assets and the shared JGDL engine, and described intended behavior (e.g. Hello’s phones on top/bottom for connection). Without that domain knowledge, the agent would have stayed on synthetic assets, ad-hoc logic, and wrong layouts. **Having a stakeholder who knows the legacy system is critical to steer an AI-assisted migration in the right direction.**

---

## Scope

- **Agent:** Single Cursor agent session (“JGDL applet game migration”).
- **User prompts:** ~14 (from initial ask through run fixes, asset integration, JGDL engine port, and game-specific bugs).
- **Games:** **Super Popcorn Machine** (two-pan balance + match-by-click) and **Hello!** (single grid, match-by-click).

---

## Key prompts that set direction

These user prompts are the ones that **determined the correct direction** of the migration (rather than incremental run/debug):

1. **“You will notice you have 2 games: Hello and Popcorn – consider”** — Fixed scope: both games from the start, not a single-game migration.
2. **“Hello looks really odd, does not work. For both games, why did you not consider original sounds and surfaces (images)? They are all in each game folder.”** — Switched from synthetic graphics/sounds to **original assets**; user knew where assets lived.
3. **“Consider JGDL as the underlying game engine shared by both games; make sure you are researching those for complete game logic, loading of assets, etc.”** — Pushed for a **shared engine port** (JGDL → JS) instead of per-game reimplementation; user knew the architecture.
4. **User pasted console error: “Uncaught TypeError: this.AddImage is not a function”** — Unblocked the agent when it could not run a browser; **user’s ability to run and capture logs** was essential.
5. **“Hello! appearance, logic and sounds are off. If I’m not wrong we shall see phones on up and down side of the screen so they can be connected by color. Investigate further.”** — **Domain knowledge**: user knew the intended Hello layout (phones top/bottom for connection by color), steering the agent to the correct design.

Run/deploy prompts (e.g. “run”, “both games not working”, port errors, “Click to start” not working) drove necessary fixes but did not change strategy; the five above **shaped strategy and direction**.

---

## Source and destination technology

| | |
|---|---|
| **Source** | Java applets on **JGDL** (Java Game Development Library). Two games with shared engine; assets: GIFs in game folders, `.au` sounds (8‑bit μ-law). |
| **Destination** | **HTML5**, **Canvas**, **vanilla JavaScript**, **Web Audio API**. No build step. Served over HTTP (e.g. `python3 -m http.server` or `sh run-server.sh` from `migration/`). |
| **Engine** | Java JGDL ported to JS under `migration/js/jgdl/` (JGDLMain, VideoManager, InputManager, TimeHandler, SoundManager, Scene/Layer/Sprite). Original GIFs and `.au` files live in `migration/assets/` and are loaded at runtime; `.au` decoded (μ-law) and played via Web Audio. |

---

## Attempts to run properly (first times)

The games did **not** work on the first run. Roughly **7–8 run/test cycles** were needed:

1. **First run** — Server started; user reported both games not working (screenshot).
2. **After menu click fix** — User reported Hello “really odd,” not working; asked for original sounds/surfaces.
3. **After adding original assets** — Both games still not working; user asked about deploy.
4. **After deploy instructions** — “Address already in use” on 8080, then on another port; agent added `run-server.sh` (tries 3000, 3001, 4000, …).
5. **After server on 3001** — “Click to start” still did nothing for both games.
6. **After overlay/pointer-events and capture-phase click** — Still not working; user asked if the agent could test in a browser and capture logs.
7. **After debug mode + overlay fix** — User shared console error (`this.AddImage is not a function`); agent fixed `addResources` binding. **Hello then worked.** Popcorn score/counts were not updating.
8. **After fixing shared `levelRef` in Popcorn** — User confirmed “SPM looks good enough.” Hello remained under investigation (appearance, logic, sounds, phones on top/bottom).

Takeaway: expect **multiple feedback loops** before “it works” in the browser.

---

## Lessons learned

- **Serve over HTTP** — `file://` breaks asset loading and `fetch()` (e.g. for `.au`). Document “run from a local server” from the start.
- **UI layers and click targets** — The overlay (menu / “Click to start”) must receive clicks when visible and not block the canvas when hidden. Use `pointer-events` and a single overlay listener (e.g. capture phase) so “Click to start” reliably starts the game.
- **Reuse original assets when present** — Surfaces and sounds live in each game folder. Copying them into the migration and loading them improves authenticity and avoids “looks/sounds off” feedback.
- **Legacy audio** — `.au` with 8‑bit μ-law (encoding 1) needs an explicit decoder before feeding into Web Audio.
- **Port the shared engine** — Researching JGDL (loading, loop, scenes, input, video, sound) and porting it to JS gave one consistent model for both games and reduced drift from the original behavior.
- **Binding in callback-based APIs** — When the engine calls `options.addResources(this)`, inside `addResources` `this` was the options object. Use `options.addResources.call(this)` so the engine instance is `this` and `AddImage` / `AddSound` exist.
- **Shared mutable state** — Popcorn used a new `levelRef` per frame in `update()` while boards updated the `levelRef` from `init()`; the HUD never updated. Use one shared `levelRef` for the whole game lifecycle.
- **Debug when you can’t run the app** — Adding `?debug=1` and an in-page log panel, plus asking the user for Console errors, allowed fixing the `AddImage` bug without the agent running a real browser.
- **Port binding and “Address already in use”** — Document alternative ports and a small run script that tries a list of ports so the project runs on different machines.

---

## For engineers doing legacy migrations

Legacy applet → modern web is doable: expect **iterative debugging**, **asset and format mapping** (GIFs, μ-law `.au`), and **operational details** (HTTP, ports, overlay, `this` binding, shared state). Use the original engine and assets as the source of truth; document run steps and failure modes early so each cycle moves the needle.

**Recommendation:** Pair the migration with a **user or stakeholder who knows the original application**—scope, assets, architecture, and intended behavior. Their prompts (e.g. “use the assets in each game folder”, “JGDL is the shared engine”, “phones should be top and bottom”) are what steer the work toward a faithful, correct outcome instead of a superficially working clone.
