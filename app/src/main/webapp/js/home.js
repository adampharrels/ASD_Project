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

  // Global variable to store all rooms
  let allRooms = [];

  // Load available rooms from database
  function loadAvailableRooms() {
    const roomsContainer = document.getElementById('roomsRow');
    if (!roomsContainer) return;

    // Show loading state
    roomsContainer.innerHTML = '<div class="loading">Loading available rooms...</div>';

    fetch('/api/available-rooms', {
      credentials: 'include'
    })
      .then(response => response.json())
      .then(rooms => {
        allRooms = rooms; // Store all rooms globally
        
        if (rooms.length === 0) {
          roomsContainer.innerHTML = `
            <div class="no-rooms">
              <p>No rooms available right now</p>
              <p><a href="calendar.html" class="btn-outline">View full calendar</a></p>
            </div>
          `;
          updateRoomCount(0);
          return;
        }

        displayRooms(allRooms);
        setupFilters();
        console.log(`✅ Loaded ${rooms.length} available rooms`);
      })
      .catch(error => {
        console.error('❌ Error loading available rooms:', error);
        roomsContainer.innerHTML = `
          <div class="room-error">
            <p>Unable to load available rooms</p>
            <button onclick="loadAvailableRooms()" class="btn-secondary">Retry</button>
          </div>
        `;
      });
  }

  // Display filtered rooms
  function displayRooms(rooms) {
    const roomsContainer = document.getElementById('roomsRow');
    if (!roomsContainer) return;

    // Clear container and render rooms
    roomsContainer.innerHTML = '';
    
    rooms.forEach(room => {
      const roomCard = createRoomCard(room);
      roomsContainer.appendChild(roomCard);
    });

    updateRoomCount(rooms.length);
  }

  // Update room count display
  function updateRoomCount(count) {
    const roomsCount = document.getElementById('roomsCount');
    if (roomsCount) {
      roomsCount.textContent = `Showing ${count} available room${count !== 1 ? 's' : ''}`;
    }
  }

  // Setup filter event listeners
  function setupFilters() {
    // Equipment filters
    const equipmentFilters = ['filterSpeaker', 'filterWhiteboard', 'filterMonitor', 'filterHDMI'];
    equipmentFilters.forEach(filterId => {
      const filter = document.getElementById(filterId);
      if (filter) {
        filter.addEventListener('change', applyFilters);
      }
    });

    // Building filter
    const buildingFilter = document.getElementById('filterBuilding');
    if (buildingFilter) {
      buildingFilter.addEventListener('change', applyFilters);
    }

    // Date, time, and duration filters
    const dateTimeFilters = ['filterDate', 'filterTime', 'filterDuration'];
    dateTimeFilters.forEach(filterId => {
      const filter = document.getElementById(filterId);
      if (filter) {
        filter.addEventListener('change', applyFilters);
      }
    });

    // Set default date to today
    const dateFilter = document.getElementById('filterDate');
    if (dateFilter && !dateFilter.value) {
      const today = new Date();
      dateFilter.value = today.toISOString().split('T')[0];
      dateFilter.min = today.toISOString().split('T')[0]; // Prevent past dates
    }

    // Clear filters button
    const clearFilters = document.getElementById('clearFilters');
    if (clearFilters) {
      clearFilters.addEventListener('click', () => {
        equipmentFilters.forEach(filterId => {
          const filter = document.getElementById(filterId);
          if (filter) filter.checked = false;
        });
        if (buildingFilter) buildingFilter.value = '';
        
        // Clear date/time filters
        dateTimeFilters.forEach(filterId => {
          const filter = document.getElementById(filterId);
          if (filter) {
            if (filterId === 'filterDate') {
              // Reset to today's date
              const today = new Date();
              filter.value = today.toISOString().split('T')[0];
            } else {
              filter.value = '';
            }
          }
        });
        
        applyFilters();
      });
    }
  }

  // Apply filters to room list
  function applyFilters() {
    let filteredRooms = [...allRooms];

    // Equipment filters
    const speakerFilter = document.getElementById('filterSpeaker')?.checked;
    const whiteboardFilter = document.getElementById('filterWhiteboard')?.checked;
    const monitorFilter = document.getElementById('filterMonitor')?.checked;
    const hdmiFilter = document.getElementById('filterHDMI')?.checked;

    if (speakerFilter) filteredRooms = filteredRooms.filter(room => room.speaker);
    if (whiteboardFilter) filteredRooms = filteredRooms.filter(room => room.whiteboard);
    if (monitorFilter) filteredRooms = filteredRooms.filter(room => room.monitor);
    if (hdmiFilter) filteredRooms = filteredRooms.filter(room => room.hdmiCable);

    // Building filter
    const buildingValue = document.getElementById('filterBuilding')?.value;
    if (buildingValue) {
      filteredRooms = filteredRooms.filter(room => 
        room.roomName && room.roomName.startsWith(buildingValue)
      );
    }

    // Date, Time, and Duration filters
    const selectedDate = document.getElementById('filterDate')?.value;
    const selectedTime = document.getElementById('filterTime')?.value;
    const selectedDuration = document.getElementById('filterDuration')?.value;

    if (selectedDate || selectedTime || selectedDuration) {
      filteredRooms = filteredRooms.filter(room => {
        return isRoomAvailableForDateTime(room, selectedDate, selectedTime, selectedDuration);
      });
    }

    displayRooms(filteredRooms);
  }

  // Check if room is available for specific date/time/duration
  function isRoomAvailableForDateTime(room, date, time, duration) {
    // If no date/time filters are set, show all rooms
    if (!date && !time && !duration) {
      return true;
    }

    // For now, we'll do a basic simulation since we don't have a booking database
    // In a real app, this would check against actual bookings
    
    // If date is specified, check if it's valid
    if (date) {
      const selectedDate = new Date(date);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      
      // Don't show rooms for past dates
      if (selectedDate < today) {
        return false;
      }
    }

    // If time is specified, check basic availability
    if (time) {
      const [hours, minutes] = time.split(':').map(Number);
      const currentTime = new Date();
      const selectedDateTime = new Date();
      selectedDateTime.setHours(hours, minutes, 0, 0);
      
      // For same-day bookings, don't show past times
      if (date) {
        const selectedDate = new Date(date);
        const today = new Date();
        if (selectedDate.toDateString() === today.toDateString() && selectedDateTime <= currentTime) {
          return false;
        }
      }
      
      // Basic business hours check (8 AM to 10 PM)
      if (hours < 8 || hours >= 22) {
        return false;
      }
    }

    // Simulate some rooms being booked (for demo purposes)
    // In reality, this would check against a booking database
    if (date && time && duration) {
      const [hours] = time.split(':').map(Number);
      const durationNum = parseInt(duration);
      
      // Simulate some popular times being unavailable for certain rooms
      const roomHash = room.roomId ? room.roomId.toString() : room.roomName;
      const dateTimeHash = `${date}-${hours}`;
      
      // Create some deterministic "unavailable" periods for demo
      const unavailableSlots = [
        'CB05.101-9', 'CB05.102-10', 'CB05.103-14', 'CB05.104-15',
        'CB06.201-11', 'CB06.202-13', 'CB07.301-9', 'CB07.302-16'
      ];
      
      if (unavailableSlots.includes(`${roomHash}-${hours}`)) {
        return false;
      }
      
      // Check if requested duration would conflict (simplified)
      if (durationNum >= 3 && hours >= 16) { // Long bookings in evening
        return Math.random() > 0.3; // 70% chance unavailable
      }
    }

    return true; // Room is available
  }

  function createRoomCard(room) {
    const article = document.createElement('article');
    article.className = 'room-card card';
    
    // Create equipment tags
    const equipmentTags = room.equipment ? room.equipment.map(eq => 
      `<span class="tag">${eq}</span>`
    ).join('') : '<span class="tag">Basic Equipment</span>';

    // Get current filter values to show contextual availability
    const selectedDate = document.getElementById('filterDate')?.value;
    const selectedTime = document.getElementById('filterTime')?.value;
    const selectedDuration = document.getElementById('filterDuration')?.value;
    
    // Create availability message based on filters
    let availabilityMsg = room.availableFor || 'Available now';
    if (selectedDate || selectedTime || selectedDuration) {
      const parts = [];
      if (selectedDate) {
        const date = new Date(selectedDate);
        parts.push(date.toLocaleDateString());
      }
      if (selectedTime) {
        parts.push(`at ${selectedTime}`);
      }
      if (selectedDuration) {
        parts.push(`for ${selectedDuration} hour${selectedDuration !== '1' ? 's' : ''}`);
      }
      availabilityMsg = `Available ${parts.join(' ')}`;
    }

    article.innerHTML = `
      <img src="https://images.unsplash.com/photo-1497366216548-37526070297c?w=400&h=250&fit=crop&auto=format" alt="${room.roomName} room photo" />
      <div class="card-body">
        <h4>${room.roomName}</h4>
        <div class="tags">
          ${equipmentTags}
        </div>
        <ul class="meta">
          <li>Capacity: ${room.capacity}</li>
          <li>${room.roomType}</li>
          <li>${room.location}</li>
          <li class="availability">${availabilityMsg}</li>
        </ul>
        <div class="rating">★ ${room.rating || 4.9}</div>
        <button class="btn-primary book-room" data-room-id="${room.roomId}">Book Now</button>
      </div>
    `;

    // Add click handler for book button
    const bookBtn = article.querySelector('.book-room');
    bookBtn.addEventListener('click', () => {
      // Build reservation URL with pre-selected filters
      let reservationUrl = `reservation-review.html?room=${room.roomId}`;
      
      // Add date/time parameters if they're set
      const params = new URLSearchParams();
      if (selectedDate) params.append('date', selectedDate);
      if (selectedTime) params.append('time', selectedTime);
      if (selectedDuration) params.append('duration', selectedDuration);
      
      if (params.toString()) {
        reservationUrl += '&' + params.toString();
      }
      
      window.location.href = reservationUrl;
    });

    return article;
  }

  // Load rooms when page loads
  loadAvailableRooms();

  // Refresh rooms every 5 minutes
  setInterval(loadAvailableRooms, 5 * 60 * 1000);
});
