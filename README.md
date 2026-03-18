# JGDL Games

This repository contains **JGDL** (Java Game Development Library) games: two legacy Java applets that were migrated to run in the modern browser.

**Origins:** The games and JGDL were originally developed in **2000** by [Nology](https://sites.google.com/site/nologygames/about-nology-games), a former game development company from Brazil.

## What’s in this repo

- **`original/`** — Unchanged legacy material: Java source and assets for **Hello!** and **Super Popcorn Machine**, plus the original JGDL engine (`.jar`, `.java`, build/config files). Kept for reference only; Java applets are no longer supported in browsers.

- **`migration/`** — Browser version of the same two games. HTML5, Canvas, and vanilla JavaScript; no build step. Use this folder to **run and play** the games. See **`migration/README.md`** for how to run them.

- **`MIGRATION_SUMMARY.md`** — Notes on how the migration was done and what was learned.

## The games

1. **Hello!** — Single grid: match phones by color by connecting two same-color phones through empty cells. Clear enough before the board fills.
2. **Super Popcorn Machine** — Two pans: balance and match popcorn by color.

Both use the same JGDL engine (ported to JavaScript in `migration/js/jgdl/`) and original art and sound assets under `migration/assets/`.

## Runtime

Click to launch: **https://nologygames.github.io/JGDL-Applets/**.
