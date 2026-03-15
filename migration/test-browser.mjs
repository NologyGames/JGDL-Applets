import { chromium } from 'playwright';

async function main() {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const logs = [];
  const errors = [];
  page.on('console', (msg) => logs.push({ type: msg.type(), text: msg.text() }));
  page.on('pageerror', (err) => errors.push(err.toString()));

  const port = process.env.PORT || 3001;
  const base = `http://127.0.0.1:${port}`;
  console.log('Loading', base + '/hello.html');
  await page.goto(base + '/hello.html', { waitUntil: 'domcontentloaded', timeout: 8000 });
  await page.waitForTimeout(2000);

  const overlay = await page.$('#ui-overlay');
  const menu = await page.$('#main-menu');
  const target = overlay || menu;
  if (!target) {
    console.log('ERROR: #ui-overlay and #main-menu not found');
    await browser.close();
    return;
  }
  console.log('Clicking', overlay ? 'overlay' : 'menu');
  await target.click();
  await page.waitForTimeout(500);

  const result = await page.evaluate(() => {
    const menu = document.getElementById('main-menu');
    const hud = document.getElementById('hud');
    const overlay = document.getElementById('ui-overlay');
    return {
      menuHasHidden: menu ? menu.classList.contains('hidden') : 'no-menu',
      hudHasHidden: hud ? hud.classList.contains('hidden') : 'no-hud',
      overlayPointerEvents: overlay ? overlay.style.pointerEvents : 'no-overlay',
    };
  });
  console.log('After click:', JSON.stringify(result, null, 2));
  console.log('Page errors:', errors);
  console.log('Last 10 console:', logs.slice(-10).map((l) => l.type + ': ' + l.text));
  await browser.close();
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});
