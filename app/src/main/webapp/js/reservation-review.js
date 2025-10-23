// js/reservation-review.js

document.addEventListener('DOMContentLoaded', function() {
  // Get URL parameters
  const urlParams = new URLSearchParams(window.location.search);
  const roomId = urlParams.get('room');
  const date = urlParams.get('date');
  const time = urlParams.get('time');
  const duration = urlParams.get('duration');
  
  // Calculate start and end times from time and duration
  let startTime = time;
  let endTime = null;
  
  if (time && duration) {
    const [hours, minutes] = time.split(':').map(Number);
    const durationHours = parseInt(duration);
    
    const endDate = new Date();
    endDate.setHours(hours, minutes, 0, 0);
    endDate.setHours(endDate.getHours() + durationHours);
    
    const endHours = String(endDate.getHours()).padStart(2, '0');
    const endMinutes = String(endDate.getMinutes()).padStart(2, '0');
    endTime = `${endHours}:${endMinutes}`;
  }
  
  // Load room data and user session data
  loadRoomData(roomId);
  loadUserData();
  
  // Set booking times if provided
  if (date) document.getElementById('bookingDate').value = date;
  if (startTime) document.getElementById('startTime').value = startTime;
  if (endTime) document.getElementById('endTime').value = endTime;
  
  // Set default values if not provided
  setDefaultBookingTimes();
  
  // Calculate duration when times change
  document.getElementById('startTime').addEventListener('change', calculateDuration);
  document.getElementById('endTime').addEventListener('change', calculateDuration);
  
  // Initial duration calculation
  calculateDuration();
  
  // Setup profile dropdown (same as home page)
  setupProfileDropdown();
});

function loadRoomData(roomId) {
  if (!roomId) {
    console.error('No room ID provided');
    return;
  }
  
  // Fetch room data from the available rooms API
  fetch('/api/available-rooms', {
    credentials: 'include'
  })
    .then(response => response.json())
    .then(rooms => {
      const room = rooms.find(r => r.roomId == roomId);
      if (room) {
        populateRoomData(room);
      } else {
        console.error('Room not found:', roomId);
        document.getElementById('roomName').textContent = 'Room not found';
      }
    })
    .catch(error => {
      console.error('Error loading room data:', error);
      document.getElementById('roomName').textContent = 'Error loading room data';
    });
}

function populateRoomData(room) {
  // Set room image
  document.getElementById('roomImage').src = 'https://images.unsplash.com/photo-1497366216548-37526070297c?w=400&h=250&fit=crop&auto=format';
  
  // Set room details
  document.getElementById('roomName').textContent = room.roomName;
  document.getElementById('roomCapacity').textContent = room.capacity;
  document.getElementById('roomType').textContent = room.roomType;
  document.getElementById('roomLocation').textContent = room.location;
  document.getElementById('roomRating').textContent = room.rating || '4.9';
  
  // Set equipment tags
  const tagsContainer = document.getElementById('roomTags');
  tagsContainer.innerHTML = '';
  
  if (room.equipment && room.equipment.length > 0) {
    room.equipment.forEach(equipment => {
      const tag = document.createElement('span');
      tag.className = 'tag';
      tag.textContent = equipment;
      tagsContainer.appendChild(tag);
    });
  } else {
    const tag = document.createElement('span');
    tag.className = 'tag';
    tag.textContent = 'Basic Equipment';
    tagsContainer.appendChild(tag);
  }
}

function loadUserData() {
  // Fetch user session data
  fetch('/api/user-session', {
    credentials: 'include'
  })
    .then(response => response.json())
    .then(user => {
      console.log('User session data received:', user);
      
      if (user.error || !user.success) {
        console.error('User not logged in:', user.error);
        // Instead of redirecting immediately, show an error message
        document.getElementById('userName').textContent = 'Please log in';
        document.getElementById('userStudentId').textContent = 'N/A';
        document.getElementById('userEmail').textContent = 'Please log in';
        
        // Show alert and redirect after user acknowledges
        alert('You need to be logged in to make a booking. Please log in first.');
        setTimeout(() => {
          window.location.href = 'index.html';
        }, 2000);
        return;
      }
      
      // Populate user information with real data from session
      const displayName = user.fullName || `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.username || user.email?.split('@')[0] || 'Unknown User';
      const studentId = user.studentId || 'No Student ID';
      const email = user.email || 'No email provided';
      
      document.getElementById('userName').textContent = displayName;
      document.getElementById('userStudentId').textContent = studentId;
      document.getElementById('userEmail').textContent = email;
      
      console.log('User data populated:', { 
        displayName, 
        studentId, 
        email,
        firstName: user.firstName,
        lastName: user.lastName,
        fullName: user.fullName
      });
    })
    .catch(error => {
      console.error('Error loading user data:', error);
      document.getElementById('userName').textContent = 'Error loading user data';
      document.getElementById('userStudentId').textContent = 'N/A';
      document.getElementById('userEmail').textContent = 'Error loading user data';
    });
}

function setDefaultBookingTimes() {
  const dateInput = document.getElementById('bookingDate');
  const startTimeInput = document.getElementById('startTime');
  const endTimeInput = document.getElementById('endTime');
  
  console.log('Setting default booking times...');
  console.log('Current values:', {
    date: dateInput.value,
    startTime: startTimeInput.value,
    endTime: endTimeInput.value
  });
  
  // Set default date to today if not set
  if (!dateInput.value) {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    dateInput.value = `${year}-${month}-${day}`;
    console.log('Set default date:', dateInput.value);
  }
  
  // Set default times if not set
  if (!startTimeInput.value) {
    const now = new Date();
    let hours = now.getHours();
    let minutes = Math.ceil(now.getMinutes() / 30) * 30;
    
    // Handle minute overflow
    if (minutes >= 60) {
      hours += 1;
      minutes = 0;
    }
    
    const hoursStr = String(hours).padStart(2, '0');
    const minutesStr = String(minutes).padStart(2, '0');
    startTimeInput.value = `${hoursStr}:${minutesStr}`;
    console.log('Set default start time:', startTimeInput.value);
  }
  
  if (!endTimeInput.value) {
    const start = startTimeInput.value;
    if (start) {
      const [hours, minutes] = start.split(':');
      const endDate = new Date();
      endDate.setHours(parseInt(hours), parseInt(minutes) + 60, 0); // Default 1 hour duration
      const endHours = String(endDate.getHours()).padStart(2, '0');
      const endMinutes = String(endDate.getMinutes()).padStart(2, '0');
      endTimeInput.value = `${endHours}:${endMinutes}`;
      console.log('Set default end time:', endTimeInput.value);
    }
  }
  
  console.log('Final booking times:', {
    date: dateInput.value,
    startTime: startTimeInput.value,
    endTime: endTimeInput.value
  });
}

function calculateDuration() {
  const startTime = document.getElementById('startTime').value;
  const endTime = document.getElementById('endTime').value;
  const durationSpan = document.getElementById('bookingDuration');
  
  if (startTime && endTime) {
    const start = new Date(`2000-01-01T${startTime}`);
    const end = new Date(`2000-01-01T${endTime}`);
    
    if (end > start) {
      const diffMs = end - start;
      const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
      const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
      
      let duration = '';
      if (diffHours > 0) duration += `${diffHours} hour${diffHours > 1 ? 's' : ''}`;
      if (diffMinutes > 0) {
        if (duration) duration += ' ';
        duration += `${diffMinutes} minute${diffMinutes > 1 ? 's' : ''}`;
      }
      
      durationSpan.textContent = duration || '0 minutes';
    } else {
      durationSpan.textContent = 'Invalid time range';
    }
  } else {
    durationSpan.textContent = '-';
  }
}

function confirmBooking() {
  // Validate form
  const termsChecked = document.getElementById('termsAgreement').checked;
  const date = document.getElementById('bookingDate').value;
  const startTime = document.getElementById('startTime').value;
  const endTime = document.getElementById('endTime').value;
  const roomId = new URLSearchParams(window.location.search).get('room');
  
  if (!termsChecked) {
    alert('Please agree to the terms and conditions.');
    return;
  }
  
  if (!date || !startTime || !endTime) {
    alert('Please fill in all booking details.');
    return;
  }
  
  if (!roomId) {
    alert('Room information is missing.');
    return;
  }
  
  // Disable confirm button
  const confirmBtn = document.getElementById('confirmBtn');
  confirmBtn.disabled = true;
  confirmBtn.textContent = 'Confirming...';
  
  // Create booking data
  const bookingData = {
    roomId: roomId,
    date: date,
    startTime: startTime,
    endTime: endTime
  };
  
  // Submit to booking servlet
  fetch('/api/booking', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    credentials: 'include',
    body: JSON.stringify(bookingData)
  })
  .then(response => {
    // Check if response is OK before parsing JSON
    if (!response.ok) {
      return response.json().then(data => {
        throw new Error(data.error || `HTTP error! status: ${response.status}`);
      }).catch(err => {
        // If JSON parsing fails, throw a generic error
        throw new Error(`HTTP error! status: ${response.status}`);
      });
    }
    return response.json();
  })
  .then(data => {
    if (data.success) {
      alert(`Booking confirmed! Your booking reference is: ${data.bookingRefDisplay || data.bookingRef}`);
      window.location.href = 'home.html';
    } else {
      alert(`Booking failed: ${data.error || 'Unknown error'}`);
      confirmBtn.disabled = false;
      confirmBtn.textContent = 'Confirm';
    }
  })
  .catch(error => {
    console.error('Error confirming booking:', error);
    alert(`Error confirming booking: ${error.message}`);
    confirmBtn.disabled = false;
    confirmBtn.textContent = 'Confirm';
  });
}

function goBack() {
  window.history.back();
}

function logout() {
  window.location.href = 'index.html';
}

function setupProfileDropdown() {
  const profileBtn = document.getElementById('profileBtn');
  const profileWrapper = profileBtn.closest('.profile-menu-wrapper');
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
        <li><a href="#" id="logoutBtn" style="color:#d32f2f">Log out</a></li>
      </ul>
    `;
    
    // Append to the profile wrapper (relative positioning)
    profileWrapper.appendChild(dropdown);

    // Add logout handler
    dropdown.querySelector('#logoutBtn').addEventListener('click', logout);

    // Position dropdown
    const rect = profileBtn.getBoundingClientRect();
    const viewportWidth = window.innerWidth;
    
    if (rect.right > viewportWidth - 200) {
      dropdown.style.right = '0';
      dropdown.style.left = 'auto';
    } else {
      dropdown.style.left = '0';
      dropdown.style.right = 'auto';
    }
  });

  // Close dropdown when clicking outside
  document.addEventListener('click', function() {
    if (dropdown) {
      dropdown.remove();
      dropdown = null;
    }
  });
}