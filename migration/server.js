#!/usr/bin/env node
/**
 * Minimal static HTTP server for local development.
 * Usage: node server.js [port]
 * Default port: 3000. Serves current working directory.
 */
const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = parseInt(process.env.PORT || process.argv[2] || '3000', 10);
const MIMES = {
  '.html': 'text/html',
  '.js': 'application/javascript',
  '.css': 'text/css',
  '.json': 'application/json',
  '.gif': 'image/gif',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.ico': 'image/x-icon',
  '.au': 'audio/basic',
  '.wav': 'audio/wav',
};

const server = http.createServer((req, res) => {
  let p = path.join(process.cwd(), req.url === '/' ? 'index.html' : req.url.replace(/^\//, '').split('?')[0]);
  fs.readFile(p, (err, data) => {
    if (err) {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('Not Found');
      return;
    }
    const ext = path.extname(p);
    res.setHeader('Content-Type', MIMES[ext] || 'application/octet-stream');
    res.end(data);
  });
});

server.listen(PORT, () => {
  console.log('Starting server at http://localhost:' + PORT + '/');
  console.log('Open in browser: http://localhost:' + PORT + '/');
});

server.on('error', (e) => {
  if (e.code === 'EADDRINUSE') {
    process.exit(1);
  }
  throw e;
});
