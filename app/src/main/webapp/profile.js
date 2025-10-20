// Apply dark mode globally from localStorage 'theme'
document.addEventListener('DOMContentLoaded', function() {
  const theme = localStorage.getItem('theme') || 'light';
  const isDark = theme === 'dark';
  document.documentElement.classList.toggle('dark-mode', isDark);
});
// profile.js
// Fetch user profile info from backend and display on profile.html

document.addEventListener('DOMContentLoaded', function() {
  fetch('/ProfileServlet', { credentials: 'include' })
    .then(response => response.json())
    .then(data => {
      const container = document.getElementById('profile-info');
      const form = document.getElementById('updateForm');
      if(data && data.success) {
        let editing = false;
        function renderProfile(editMode) {
          if (editMode) {
            container.innerHTML = `
              <div class="profile-details">
                <p><strong>ID:</strong> ${data.id}</p>
                <p><strong>Email:</strong> ${data.email}</p>
              </div>
            `;
          } else {
            container.innerHTML = `
              <div class="profile-details">
                <p><strong>ID:</strong> ${data.id}</p>
                <p><strong>Email:</strong> ${data.email}</p>
                <p><strong>Name:</strong> ${data.firstName} ${data.lastName}</p>
              </div>
            `;
          }
        }
        renderProfile(false);
        form.style.display = 'none';
        const showUpdateBtn = document.getElementById('showUpdateBtn');
        if (showUpdateBtn) {
          showUpdateBtn.onclick = function() {
            editing = true;
            renderProfile(true);
            form.style.display = '';
            document.getElementById('editName').value = `${data.firstName} ${data.lastName}`;
            showUpdateBtn.style.display = 'none';
          };
        }
        // When form is submitted, get name from input
        form.onsubmit = function(e) {
          e.preventDefault();
          let nameInput = document.getElementById('editName');
          let name = nameInput ? nameInput.value.trim() : `${data.firstName} ${data.lastName}`;
          let [firstName, ...lastArr] = name.split(' ');
          let lastName = lastArr.join(' ');
          fetch('/EditUserInfoServlet', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `firstName=${encodeURIComponent(firstName)}&lastName=${encodeURIComponent(lastName)}`
          })
          .then(response => response.json())
          .then(resp => {
            const msg = document.getElementById('updateMsg');
            if(resp.success) {
              msg.textContent = 'Profile updated successfully!';
              msg.style.color = 'green';
              data.firstName = firstName;
              data.lastName = lastName;
              renderProfile(false);
              form.style.display = 'none';
              showUpdateBtn.style.display = '';
              editing = false;
            } else {
              msg.textContent = 'Update failed: ' + (resp.error || 'Unknown error');
              msg.style.color = 'red';
            }
          })
          .catch(() => {
            document.getElementById('updateMsg').textContent = 'Error updating profile.';
            document.getElementById('updateMsg').style.color = 'red';
          });
        };
      } else {
        window.location.href = 'index.html';
      }
    })
    .catch(() => {
      document.getElementById('profile-info').innerHTML = '<p>Error loading profile.</p>';
    });

  // Removed duplicate/old updateForm handler. All logic is handled inside the profile fetch block above.
  // Profile dropdown logic for HTML-based menu (copied from home.js/settings.js)
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
