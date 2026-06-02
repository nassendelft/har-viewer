#!/usr/bin/env bash
# Driver for har-viewer TUI — wraps tmux so agents can drive the app headlessly.
# Usage: driver.sh <command> [args...]
set -euo pipefail

SESSION="har-viewer-driver"
BINARY="app/build/bin/macosArm64/releaseExecutable/app.kexe"

cmd="${1:-help}"
shift || true

case "$cmd" in

  start)
    # start <file.har>  — launch the app in a background tmux session.
    # Kills any existing session first.
    file="${1:-test.har}"
    tmux kill-session -t "$SESSION" 2>/dev/null || true
    tmux new-session -d -s "$SESSION" -x 220 -y 50
    tmux send-keys -t "$SESSION" "$BINARY $file" Enter
    sleep 1.5
    echo "Started. Session: $SESSION"
    ;;

  capture)
    # capture [outfile]  — dump current pane state to stdout or a file.
    out="${1:-}"
    if [[ -n "$out" ]]; then
      tmux capture-pane -t "$SESSION" -p > "$out"
      echo "Captured to $out"
    else
      tmux capture-pane -t "$SESSION" -p
    fi
    ;;

  key)
    # key <key> [key...]  — send one or more tmux keys with small delays.
    # Use tmux key names: Up, Down, Enter, Escape, or literal chars like "j".
    for k in "$@"; do
      tmux send-keys -t "$SESSION" "$k"
      sleep 0.15
    done
    sleep 0.3
    ;;

  filter)
    # filter <regex>  — type a regex into the search box.
    # Focuses the requests panel first, clears any existing filter, then types.
    tmux send-keys -t "$SESSION" "r"  ; sleep 0.2  # focus requests
    tmux send-keys -t "$SESSION" Escape; sleep 0.2  # clear old filter
    tmux send-keys -t "$SESSION" "/"  ; sleep 0.3  # open search
    tmux send-keys -t "$SESSION" "$1" ; sleep 0.4
    ;;

  clear-filter)
    # clear-filter — clear the active regex filter.
    tmux send-keys -t "$SESSION" "r"    ; sleep 0.2
    tmux send-keys -t "$SESSION" Escape ; sleep 0.3
    ;;

  nav)
    # nav <n>  — navigate n rows (positive = down, negative = up).
    n="${1:-1}"
    if (( n > 0 )); then
      for (( i=0; i<n; i++ )); do
        tmux send-keys -t "$SESSION" Down; sleep 0.12
      done
    else
      for (( i=0; i>n; i-- )); do
        tmux send-keys -t "$SESSION" Up; sleep 0.12
      done
    fi
    sleep 0.3
    ;;

  tab)
    # tab <1-5>  — switch detail panel tab.
    tmux send-keys -t "$SESSION" "$1"
    sleep 0.3
    ;;

  quit)
    # quit — send q and kill the session.
    tmux send-keys -t "$SESSION" "q" 2>/dev/null || true
    sleep 0.3
    tmux kill-session -t "$SESSION" 2>/dev/null || true
    echo "Stopped."
    ;;

  help|*)
    cat <<'EOF'
har-viewer driver — drive the TUI via tmux

Commands:
  start [file.har]      Launch app (default: test.har)
  capture [outfile]     Dump current screen to stdout or file
  key <key> [key...]    Send tmux key(s) e.g. Up Down Enter Escape "j"
  filter <regex>        Set regex filter on request list
  clear-filter          Clear the regex filter
  nav <n>               Move selection n rows (negative = up)
  tab <1-5>             Switch detail panel tab
  quit                  Quit and kill session

Tab numbers: 1=Request  2=Resp Headers  3=Body  4=Diagnostics  5=Image
EOF
    ;;

esac
