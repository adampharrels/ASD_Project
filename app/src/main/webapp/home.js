/* Attach simple scrolling to carousels with Prev/Next buttons */
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

  // keyboard support when section is focused
  section.tabIndex = 0;
  section.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowLeft') row.scrollBy({ left: -step(), behavior: 'smooth' });
    if (e.key === 'ArrowRight') row.scrollBy({ left:  step(), behavior: 'smooth' });
  });
}

/* init */
document.querySelectorAll('.carousel').forEach(attachCarousel);

/* (Optional) demo handlers for quick buttons – replace with real actions later */
document.querySelectorAll('.qa .btn-secondary').forEach(btn => {
  btn.addEventListener('click', () => {
    alert('This is a demo action. Hook this button to real API later.');
  });
});
