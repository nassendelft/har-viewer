---
name: run-har-viewer
description: Run, start, build, screenshot, or interact with the har-viewer TUI app. Use when asked to launch har-viewer, test a feature visually, capture the app state, navigate requests, or verify a UI change.
---

# run-har-viewer

har-viewer is a Kotlin/Native TUI (terminal UI) for inspecting HAR files. It produces a native binary and runs in a terminal. Agents drive it headlessly via tmux using `.claude/skills/run-har-viewer/driver.sh`.

All paths below are relative to the repo root (`har-viewer/`).

## Prerequisites

- macOS (Apple Silicon) — the built binary targets `macosArm64`
- `tmux` (available at `/opt/homebrew/bin/tmux` on this machine)
- JDK 17+ and a C toolchain (only needed to rebuild)

## Build

A release binary is already present. To rebuild:

```sh
./gradlew :app:linkReleaseExecutableMacosArm64
```

Output: `app/build/bin/macosArm64/releaseExecutable/app.kexe`

## Run (agent path)

Use the driver. Every command is run from the repo root.

```sh
# Launch with a HAR file (test.har lives at the repo root)
.claude/skills/run-har-viewer/driver.sh start test.har

# Capture the current TUI state (text art — strip ANSI if needed)
.claude/skills/run-har-viewer/driver.sh capture

# Save capture to file
.claude/skills/run-har-viewer/driver.sh capture /tmp/screen.txt

# Navigate: move selection n rows (negative = up)
.claude/skills/run-har-viewer/driver.sh nav 2

# Switch detail panel tab (1=Request  2=Resp Headers  3=Body  4=Diagnostics  5=Image)
.claude/skills/run-har-viewer/driver.sh tab 3

# Set regex filter on the request list
.claude/skills/run-har-viewer/driver.sh filter "orders"

# Clear regex filter
.claude/skills/run-har-viewer/driver.sh clear-filter

# Send raw tmux keys (use tmux key names: Up Down Enter Escape, or literal chars)
.claude/skills/run-har-viewer/driver.sh key "j" "j" Down Enter

# Quit and kill the tmux session
.claude/skills/run-har-viewer/driver.sh quit
```

### Reading the capture

`capture` output is box-drawing text art. The left panel is the request list; the right panel is the detail view. The currently selected request is highlighted (color codes stripped by tmux capture-pane, but position is preserved). Look for the content after the `├──` divider row to identify which request is active.

### Typical agent flow

```sh
.claude/skills/run-har-viewer/driver.sh start test.har
.claude/skills/run-har-viewer/driver.sh filter "POST"
.claude/skills/run-har-viewer/driver.sh tab 3          # Body tab
.claude/skills/run-har-viewer/driver.sh capture /tmp/body.txt
cat /tmp/body.txt
.claude/skills/run-har-viewer/driver.sh quit
```

## Run (human path)

```sh
app/build/bin/macosArm64/releaseExecutable/app.kexe test.har
```

A window-filling TUI appears. Press `q` or Ctrl+C to exit. Not useful headless.

## Keyboard reference (for `driver.sh key`)

| Key | Action |
|-----|--------|
| `Up` / `Down` | Move selection in request list |
| `j` / `k` | Same as Up/Down (vim-style) |
| `r` | Focus requests panel |
| `Enter` | Focus detail panel |
| `/` | Open regex filter (while requests panel is focused) |
| `Escape` | Close/clear filter |
| `1`–`5` | Switch detail tab |
| `p` | Toggle JSON pretty-print (Body tab) |
| `q` | Quit |

## Gotchas

- **`j`/`k` keys unreliable when chained fast** — use `nav <n>` (which uses arrow keys with delays) instead of sending multiple `j`/`k` via `key`.
- **Filter requires requests panel focus** — the `filter` command handles this automatically (`r` → Escape → `/` → text). If you call `key "/"` directly, prefix with `key "r"` first.
- **`capture` strips color codes** — selection highlight is invisible in text output, but context (right panel content) shows which row is active.
- **Session name is `har-viewer-driver`** — `start` kills any existing session. Only one app instance at a time.
- **The binary is `app.kexe`**, not `har-view` — the CI renames it, but the build output keeps the `.kexe` extension.

## Troubleshooting

**`session not found` from driver** — run `start` first; the session was killed or never created.

**Keys have no effect** — add a `sleep 0.5` after `start` if startup is slow; the app may still be rendering.

**Build fails: `libstb_image.a` missing** — the `buildStbImage` task runs `cc` to compile the C source. Ensure a C compiler is on `PATH`. On macOS: `xcode-select --install`.
