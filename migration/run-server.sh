#!/bin/sh
# Start a local HTTP server on the first available port.
# Uses Python if available, otherwise Node (server.js).
cd "$(dirname "$0")"
for port in 3000 3001 4000 4001 9090 9091 7070 6060; do
  if ! lsof -i :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "Starting server at http://localhost:$port/"
    echo "Open in browser: http://localhost:$port/"
    if command -v python3 >/dev/null 2>&1; then
      exec python3 -m http.server "$port"
    fi
    if command -v node >/dev/null 2>&1; then
      exec node server.js "$port"
    fi
    echo "Need python3 or node to run the server."
    exit 1
  fi
done
echo "No free port found. Try: kill \$(lsof -t -i :3000) then run again."
exit 1
