// Apply theme from localStorage (run once at top)
document.addEventListener('DOMContentLoaded', function() {
  const isDark = localStorage.getItem('theme') === 'dark';
  document.body.classList.toggle('dark-mode', isDark);
});
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
// Profile dropdown logic for HTML-based menu
document.addEventListener('DOMContentLoaded', function () {
  const trigger = document.getElementById('profileBtn');
  const dropdown = document.getElementById('profileDropdown');
  if (!trigger || !dropdown) return;

  function openDropdown() {
    dropdown.classList.remove('hidden');
    trigger.setAttribute('aria-expanded', 'true');
    dropdown.querySelector('[role="menuitem"]')?.focus();
  }
  function closeDropdown() {
    dropdown.classList.add('hidden');
    trigger.setAttribute('aria-expanded', 'false');
  }
  trigger.addEventListener('click', function (e) {
    e.stopPropagation();
    dropdown.classList.contains('hidden') ? openDropdown() : closeDropdown();
  });
  document.addEventListener('click', function (e) {
    if (!dropdown.classList.contains('hidden') && !dropdown.contains(e.target) && e.target !== trigger) {
      closeDropdown();
    }
  });
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') closeDropdown();
  });
});
