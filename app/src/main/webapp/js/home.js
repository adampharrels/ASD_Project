// js/home.js

document.addEventListener('DOMContentLoaded', function() {
  const profileBtn = document.getElementById('profileBtn');
  let dropdown = null;

  profileBtn.addEventListener('click', function(e) {
    e.stopPropagation();
    // Remove existing dropdown if present
    if (dropdown) {
      dropdown.remove();
      dropdown = null;
      return;
    }
    // Create dropdown
    dropdown = document.createElement('div');
    dropdown.className = 'profile-dropdown';
    dropdown.innerHTML = `
      <ul>
        <li><a href="profile.html" id="profileLink">Profile</a></li>
        <li><a href="settings.html" id="settingsLink">Settings</a></li>
        <li><a href="#" id="logoutBtn">Log out</a></li>
      </ul>
    `;
    // Position dropdown below the button
    const rect = profileBtn.getBoundingClientRect();
    dropdown.style.position = 'absolute';
    dropdown.style.top = (window.scrollY + rect.bottom + 8) + 'px';
    dropdown.style.left = (window.scrollX + rect.left) + 'px';
    dropdown.style.minWidth = rect.width + 'px';
    dropdown.style.zIndex = 1000;
    document.body.appendChild(dropdown);

    // Close dropdown on outside click
    document.addEventListener('click', closeDropdown, { once: true });

    // Log out handler
    dropdown.querySelector('#logoutBtn').addEventListener('click', function(ev) {
      ev.preventDefault();
      // TODO: Add logout logic here
      alert('Logged out!');
      dropdown.remove();
      dropdown = null;
    });
  });

  function closeDropdown(e) {
    if (dropdown) {
      dropdown.remove();
      dropdown = null;
    }
  }
});
