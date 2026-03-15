# JGDL games – run locally

Play **Hello!** and **Super Popcorn Machine** in your browser from this folder.

---

## Running the games

1. **Open a terminal** and go into the `migration` folder:
   ```bash
   cd migration
   ```

2. **Start the server** (pick one):
   ```bash
   npm run deploy
   ```
   or:
   ```bash
   sh run-server.sh
   ```
   The script will pick a free port and print something like:
   ```text
   Starting server at http://localhost:3000/
   Open in browser: http://localhost:3000/
   ```

3. **Open that URL in your browser** (e.g. `http://localhost:3000/`).

4. **Click “Click to start”** on the launcher, then choose a game:
   - **Hello!** — Match phones by color (connect two same-color phones through empty cells).
   - **Super Popcorn Machine** — Balance two pans and match popcorn by color.

**Direct links** (replace `PORT` with the port the server printed):
- Launcher: `http://localhost:PORT/`
- Hello!: `http://localhost:PORT/hello.html`
- Popcorn: `http://localhost:PORT/popcorn.html`

---

## Why a local server?

The games load images and sounds over HTTP. Opening the HTML files with `file://` can break loading and audio, so they need to be served (e.g. from `migration/` with the commands above).

---

## Option 2: Start the server yourself

```bash
cd migration
python3 -m http.server 3000
```

Then open **http://localhost:3000/** in your browser.

If you see **“Address already in use”**, use another port (e.g. 3001, 4000, 9090).

---

## Free a port that’s in use

To free a port (example: 8080):

```bash
kill $(lsof -t -i :8080)
```

Then start the server again on that port.

---

## Debug

Add `?debug=1` to a game URL (e.g. `http://localhost:3000/hello.html?debug=1`) to show an on-page log. Use the browser’s Developer Tools (F12 → Console) for messages and errors.
