// js/home.js

document.addEventListener('DOMContentLoaded', function() {
  const profileBtn = document.getElementById('profileBtn');
  const profileWrapper = profileBtn.closest('.profile-menu-wrapper');
  let dropdown = null;
  let resizeTimeout = null;

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
        <li><a href="#" id="logoutBtn" style="color:#d32f2f">Log out</a></li>
      </ul>
    `;
    
    // Append to the profile wrapper (relative positioning)
    profileWrapper.appendChild(dropdown);

    // Apply initial responsive styling
    handleResize();

    // Close dropdown on outside click
    document.addEventListener('click', closeDropdown, { once: true });
    
    // Add throttled resize listener for real-time responsiveness
    window.addEventListener('resize', throttledResize);

    // Log out handler
    dropdown.querySelector('#logoutBtn').addEventListener('click', function(ev) {
      ev.preventDefault();
      fetch('/api/logout', {
        method: 'POST',
        credentials: 'include'
      })
      .then(res => res.json())
      .then(result => {
        if(result.success){
          window.location.href = 'index.html';
        } else {
          alert('Logout failed.');
        }
      })
      .catch(() => alert('Network error. Please try again.'));
      dropdown.remove();
      dropdown = null;
    });
  });

  function closeDropdown(e) {
    if (dropdown) {
      dropdown.remove();
      dropdown = null;
      // Remove resize listener when dropdown is closed
      window.removeEventListener('resize', throttledResize);
      if (resizeTimeout) {
        clearTimeout(resizeTimeout);
        resizeTimeout = null;
      }
    }
  }

  function handleResize() {
    if (!dropdown) return;
    
    // Apply responsive styles based on current window size
    const windowWidth = window.innerWidth;
    const availableWidth = windowWidth - 32; // Account for margins
    
    // Calculate responsive dimensions
    let minWidth, maxWidth, padding, fontSize;
    
    if (windowWidth <= 320) {
      // Very small screens - ultra compact
      minWidth = Math.max(100, availableWidth * 0.8);
      maxWidth = Math.max(120, availableWidth);
      padding = '0.5rem 0.75rem';
      fontSize = '0.85rem';
    } else if (windowWidth <= 480) {
      // Extra small screens
      minWidth = Math.max(120, availableWidth * 0.7);
      maxWidth = Math.max(140, availableWidth);
      padding = '0.6rem 1rem';
      fontSize = '0.9rem';
    } else if (windowWidth <= 768) {
      // Medium screens
      minWidth = Math.max(140, Math.min(160, availableWidth * 0.6));
      maxWidth = Math.max(160, Math.min(280, availableWidth));
      padding = '0.65rem 1.2rem';
      fontSize = '0.95rem';
    } else {
      // Large screens
      minWidth = 180;
      maxWidth = 280;
      padding = '0.75rem 1.5rem';
      fontSize = '1rem';
    }
    
    // Apply calculated styles
    dropdown.style.minWidth = minWidth + 'px';
    dropdown.style.maxWidth = maxWidth + 'px';
    dropdown.style.right = '8px';
    dropdown.style.left = 'auto';
    
    // Update link styles
    const links = dropdown.querySelectorAll('a');
    links.forEach(link => {
      link.style.padding = padding;
      link.style.fontSize = fontSize;
      
      // Handle text overflow for small screens
      if (windowWidth <= 480) {
        link.style.whiteSpace = 'nowrap';
        link.style.overflow = 'hidden';
        link.style.textOverflow = 'ellipsis';
        link.style.maxWidth = (maxWidth - 24) + 'px'; // Account for padding
      } else {
        link.style.whiteSpace = 'nowrap';
        link.style.overflow = 'visible';
        link.style.textOverflow = 'unset';
        link.style.maxWidth = 'none';
      }
    });
    
    // Ensure dropdown never disappears
    const rect = dropdown.getBoundingClientRect();
    if (rect.width < 80) {
      dropdown.style.minWidth = '80px';
      dropdown.style.maxWidth = '120px';
    }
  }

  // Throttled resize handler for better performance
  function throttledResize() {
    if (resizeTimeout) {
      clearTimeout(resizeTimeout);
    }
    // Apply immediately for more responsive feel
    handleResize();
    resizeTimeout = setTimeout(handleResize, 50); // Additional delay for final adjustment
  }

  // Load available rooms from database
  function loadAvailableRooms() {
    const roomsContainer = document.getElementById('roomsRow');
    if (!roomsContainer) return;

    // Show loading state
    roomsContainer.innerHTML = '<div class="loading">Loading available rooms...</div>';

    fetch('/api/available-rooms')
      .then(response => response.json())
      .then(rooms => {
        if (rooms.length === 0) {
          roomsContainer.innerHTML = `
            <div class="no-rooms">
              <p>No rooms available in the next 30 minutes</p>
              <p><a href="calendar.html" class="btn-outline">View full calendar</a></p>
            </div>
          `;
          return;
        }

        // Clear loading and render rooms
        roomsContainer.innerHTML = '';
        
        rooms.forEach(room => {
          const roomCard = createRoomCard(room);
          roomsContainer.appendChild(roomCard);
        });

        console.log(`✅ Loaded ${rooms.length} available rooms`);
      })
      .catch(error => {
        console.error('❌ Error loading available rooms:', error);
        roomsContainer.innerHTML = `
          <div class="error">
            <p>Unable to load available rooms</p>
            <button onclick="loadAvailableRooms()" class="btn-secondary">Retry</button>
          </div>
        `;
      });
  }

  function createRoomCard(room) {
    const article = document.createElement('article');
    article.className = 'room-card card';
    
    // Create equipment tags
    const equipmentTags = room.equipment ? room.equipment.map(eq => 
      `<span class="tag">${eq}</span>`
    ).join('') : '<span class="tag">Basic Equipment</span>';

    article.innerHTML = `
      <img src="${room.image || 'assets/img/room-default.jpg'}" alt="${room.roomName} room photo" />
      <div class="card-body">
        <h4>${room.roomName}</h4>
        <div class="tags">
          ${equipmentTags}
        </div>
        <ul class="meta">
          <li>Capacity: ${room.capacity}</li>
          <li>${room.roomType}</li>
          <li>${room.location}</li>
          <li>Available: ${room.availableFor}</li>
        </ul>
        <div class="rating">★ ${room.rating || 4.9}</div>
        <button class="btn-primary book-room" data-room-id="${room.roomId}">Book Now</button>
      </div>
    `;

    // Add click handler for book button
    const bookBtn = article.querySelector('.book-room');
    bookBtn.addEventListener('click', () => {
      // Redirect to booking page with room pre-selected
      window.location.href = `booking.html?room=${room.roomId}`;
    });

    return article;
  }

  // Load rooms when page loads
  loadAvailableRooms();

  // Refresh rooms every 5 minutes
  setInterval(loadAvailableRooms, 5 * 60 * 1000);
});
