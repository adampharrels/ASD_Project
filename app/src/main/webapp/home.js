/* ========= Carousel ========= */
function attachCarousel(section) {
  const row = section.querySelector('.cards-row');
  const prev = section.querySelector('.prev');
  const next = section.querySelector('.next');
  if (!row) return;

  const step = () => Math.max(280, section.clientWidth * 0.6);

  prev?.addEventListener('click', () => {
    row.scrollBy({ left: -step(), behavior: 'smooth' });
  });

  next?.addEventListener('click', () => {
    row.scrollBy({ left:  step(), behavior: 'smooth' });
  });

  section.tabIndex = 0;
  section.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowLeft') row.scrollBy({ left: -step(), behavior: 'smooth' });
    if (e.key === 'ArrowRight') row.scrollBy({ left:  step(), behavior: 'smooth' });
  });
}
document.querySelectorAll('.carousel').forEach(attachCarousel);

document.querySelectorAll('.qa .btn-secondary').forEach(btn => {
  btn.addEventListener('click', () => {
    alert('This is a demo action. Hook this button to real API later.');
  });
});

/* ========= Profile dropdown + blurred backdrop ========= */
(function () {
  const trigger = document.getElementById('profileBtn');
  if (!trigger) {
    console.warn('profileBtn not found');
    return;
  }

  // Create backdrop + dropdown once
  const backdrop = document.createElement('div');
  backdrop.className = 'backdrop hidden';
  backdrop.setAttribute('aria-hidden', 'true');

  const panel = document.createElement('nav');
  panel.className = 'dropdown-panel hidden';
  panel.setAttribute('role', 'menu');
  panel.innerHTML = `
    <a role="menuitem" href="/profile">Profile</a>
    <a role="menuitem" href="/settings">Settings</a>
    <a role="menuitem" href="/logout">Log out</a>
  `;

  document.body.append(backdrop, panel);

  // Position panel right under the profile button
  function positionPanel() {
    const r = trigger.getBoundingClientRect();
    const top = Math.round(r.bottom + 8 + window.scrollY);             // 8px below
    const right = Math.round(window.innerWidth - r.right);             // align right edge
    panel.style.top = `${top}px`;
    panel.style.right = `${right}px`;
  }

  function open() {
    positionPanel();
    panel.classList.remove('hidden');
    backdrop.classList.remove('hidden');
    trigger.setAttribute('aria-expanded', 'true');
    document.body.classList.add('no-scroll');
    panel.querySelector('[role="menuitem"]')?.focus();
  }

  function close() {
    panel.classList.add('hidden');
    backdrop.classList.add('hidden');
    trigger.setAttribute('aria-expanded', 'false');
    document.body.classList.remove('no-scroll');
  }

  trigger.addEventListener('click', (e) => {
    e.stopPropagation();
    panel.classList.contains('hidden') ? open() : close();
  });

  // Close on outside click / backdrop / Esc
  backdrop.addEventListener('click', close);
  document.addEventListener('click', (e) => {
    if (!panel.classList.contains('hidden') && !panel.contains(e.target) && e.target !== trigger) close();
  });
  document.addEventListener('keydown', (e) => { if (e.key === 'Escape') close(); });

  // Reposition if window changes while open
  window.addEventListener('resize', () => { if (!panel.classList.contains('hidden')) positionPanel(); });
  window.addEventListener('scroll',  () => { if (!panel.classList.contains('hidden')) positionPanel(); });
})();
