// profile.js
// Fetch user profile info from backend and display on profile.html

document.addEventListener('DOMContentLoaded', function() {
  fetch('/ProfileServlet')
    .then(response => response.json())
    .then(data => {
      const container = document.getElementById('profile-info');
      if (data && data.success) {
        container.innerHTML = `
          <div class="profile-details">
            <p><strong>ID:</strong> ${data.id}</p>
            <p><strong>Email:</strong> ${data.email}</p>
            <p><strong>First Name:</strong> ${data.firstName}</p>
            <p><strong>Last Name:</strong> ${data.lastName}</p>
            <p><strong>Student Number:</strong> ${data.studentNumber}</p>
          </div>
        `;
      } else {
        container.innerHTML = '<p>Unable to load profile information.</p>';
      }
    })
    .catch(() => {
      document.getElementById('profile-info').innerHTML = '<p>Error loading profile.</p>';
    });
});
