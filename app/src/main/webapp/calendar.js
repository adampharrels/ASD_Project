// Apply dark mode globally from localStorage 'theme'
document.addEventListener('DOMContentLoaded', function() {
    const theme = localStorage.getItem('theme') || 'light';
    const isDark = theme === 'dark';
        document.documentElement.classList.toggle('dark-mode', isDark);
});
let bookings = [];
let rooms = [];

// Update status indicator
function updateStatus(message, isError = false) {
    const statusEl = document.getElementById('statusIndicator');
    if (statusEl) {
        statusEl.textContent = message;
        statusEl.style.color = isError ? '#ef4444' : '#10b981';
    }
}

// Initialize timeline when page loads
document.addEventListener('DOMContentLoaded', async function() {
    console.log('DOM loaded, initializing timeline...');
    
    // Clear any initial status text
    updateStatus('', false);
    
    updateCurrentTime();
    setInterval(updateCurrentTime, 60000); // Update every minute
    
    // Generate a basic timeline first, then load data sequentially
    generateBasicTimeline();
    
    try {
        // Load rooms first (required for timeline structure)
        await loadRooms();
        
        // Only load bookings after rooms are loaded
        await loadBookings();
        
        console.log('Initialization complete');
    } catch (error) {
        console.error('Initialization failed:', error);
        updateStatus('Initialization failed', true);
    }
});

// Generate a basic timeline structure even without rooms data
function generateBasicTimeline() {
    const timeline = document.getElementById('timeline');
    timeline.innerHTML = '';
    
    // Create header with time slots
    const header = document.createElement('div');
    header.className = 'timeline-header';
    
    // Room column header
    const roomHeader = document.createElement('div');
    roomHeader.textContent = 'Rooms';
    roomHeader.style.fontWeight = '700';
    header.appendChild(roomHeader);
    
    // Time slot headers (8:00 AM to 6:00 PM in 1-hour intervals for simplicity)
    for (let hour = 8; hour <= 18; hour++) {
        const timeHeader = document.createElement('div');
        const timeStr = `${hour.toString().padStart(2, '0')}:00`;
        timeHeader.textContent = timeStr;
        timeHeader.dataset.time = hour;
        header.appendChild(timeHeader);
    }
    
    timeline.appendChild(header);
    
    // Don't create room rows here - wait for rooms to be loaded
}

// Update current time display
function updateCurrentTime() {
    const now = new Date();
    const timeStr = now.toLocaleString('en-AU', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
    const timeDisplay = document.getElementById('currentTime');
    if (timeDisplay) {
        timeDisplay.textContent = timeStr;
    }
}

// Load rooms from API
async function loadRooms() {
    try {
        console.log('Loading rooms...');
        
        const response = await fetch('/api/rooms');
        console.log('Rooms response status:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const roomsData = await response.json();
        console.log('Raw rooms data:', roomsData);
        
        rooms = roomsData || [];
        console.log('Loaded rooms:', rooms);
        
        populateRoomFilter();
        generateTimeline();
    } catch (error) {
        console.error('Error loading rooms:', error);
        updateStatus(`Error loading rooms: ${error.message}`, true);
        
        // Clear all data when database connection fails
        rooms = [];
        bookings = [];
        
        // Clear the timeline completely
        const timeline = document.getElementById('timeline');
        timeline.innerHTML = '';
        
        // Show error message only
        const errorRow = document.createElement('div');
        errorRow.className = 'timeline-row';
        errorRow.innerHTML = `
            <div class="room-label" style="color: red; padding: 20px; text-align: center;">
                ❌ Database Connection Failed<br>
                <small>${error.message}</small><br>
                <small style="color: #666;">No rooms or bookings can be displayed</small>
            </div>
        `;
        timeline.appendChild(errorRow);
        
        // Clear room filter
        const roomFilter = document.getElementById('roomFilter');
        if (roomFilter) {
            roomFilter.innerHTML = '<option value="">No Rooms Available</option>';
        }
    }
}

// Load bookings from API
async function loadBookings() {
    try {
        console.log('Loading bookings...');
        
        const response = await fetch('/api/bookings');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        bookings = await response.json();
        console.log('Loaded bookings:', bookings);
        
        updateTimelineWithBookings();
    } catch (error) {
        console.error('Error loading bookings:', error);
        updateStatus(`Error loading bookings: ${error.message}`, true);
        
        // Clear all booking data when API fails
        bookings = [];
        
        // Clear any existing booking blocks from timeline
        const existingBlocks = document.querySelectorAll('.booking-block');
        existingBlocks.forEach(block => block.remove());
    }
}

// Populate room filter dropdown
function populateRoomFilter() {
    const select = document.getElementById('roomFilter');
    select.innerHTML = '<option value="">All Rooms</option>';
    
    rooms.forEach(room => {
        const option = document.createElement('option');
        option.value = room.roomId;
        option.textContent = `${room.roomName} (${room.roomType})`;
        select.appendChild(option);
    });
    
    // Add change event listener
    select.addEventListener('change', updateTimelineWithBookings);
}

// Generate timeline structure (simplified version)
function generateTimeline() {
    console.log('Generating timeline with rooms:', rooms);
    const timeline = document.getElementById('timeline');
    timeline.innerHTML = '';
    
    // Create header with time slots
    const header = document.createElement('div');
    header.className = 'timeline-header';
    
    // Room column header
    const roomHeader = document.createElement('div');
    roomHeader.textContent = 'Rooms';
    roomHeader.style.fontWeight = '700';
    header.appendChild(roomHeader);
    
    // Time slot headers (8:00 AM to 6:00 PM in 1-hour intervals)
    for (let hour = 8; hour <= 18; hour++) {
        const timeHeader = document.createElement('div');
        const timeStr = `${hour.toString().padStart(2, '0')}:00`;
        timeHeader.textContent = timeStr;
        timeHeader.dataset.time = hour;
        header.appendChild(timeHeader);
    }
    
    timeline.appendChild(header);
    
    // Filter rooms based on selection
    const selectedRoomId = document.getElementById('roomFilter')?.value;
    const filteredRooms = selectedRoomId ? 
        rooms.filter(r => r.roomId == selectedRoomId) : rooms;
    
    console.log('Filtered rooms:', filteredRooms);
    
    // Create rows for each room
    if (filteredRooms && filteredRooms.length > 0) {
        filteredRooms.forEach(room => {
            const row = document.createElement('div');
            row.className = 'timeline-row';
            row.dataset.roomId = room.roomId;
            
            // Room label
            const roomLabel = document.createElement('div');
            roomLabel.className = 'room-label';
            roomLabel.innerHTML = `
                <div style="font-size: 13px; font-weight: 700; margin-bottom: 2px;">${room.roomName}</div>
                <div class="room-type">${room.roomType}</div>
            `;
            
            // Add click handler for room details modal
            roomLabel.style.cursor = 'pointer';
            roomLabel.addEventListener('click', () => showRoomDetails(room));
            
            row.appendChild(roomLabel);
            
            // Time cells (1 hour intervals)
            for (let hour = 8; hour <= 18; hour++) {
                const cell = document.createElement('div');
                cell.className = 'time-cell';
                cell.dataset.roomId = room.roomId;
                cell.dataset.hour = hour;
                cell.id = `cell-${room.roomId}-${hour}`;
                row.appendChild(cell);
            }
            
            timeline.appendChild(row);
        });
    } else {
        // Show message if no rooms
        const noRoomsRow = document.createElement('div');
        noRoomsRow.className = 'timeline-row';
        noRoomsRow.innerHTML = `
            <div class="room-label">
                No rooms available<br>
                <small>Check database connection</small>
            </div>
        `;
        timeline.appendChild(noRoomsRow);
    }
}

// Add current time indicator line
function addCurrentTimeIndicator() {
    const now = new Date();
    const currentHour = now.getHours();
    const currentMinute = now.getMinutes();
    
    // Only show if within business hours
    if (currentHour >= 8 && currentHour <= 18) {
        // Calculate position (30-minute intervals)
        const timeSlot = Math.floor(currentMinute / 30) * 30;
        const cellsBeforeCurrent = ((currentHour - 8) * 2) + (timeSlot / 30);
        
        // Position the line (rough calculation)
        const linePosition = 150 + (cellsBeforeCurrent * 60) + ((currentMinute % 30) / 30 * 60);
        
        const timeLine = document.createElement('div');
        timeLine.className = 'current-time-line';
        timeLine.style.left = `${linePosition}px`;
        
        const timeline = document.getElementById('timeline');
        timeline.style.position = 'relative';
        timeline.appendChild(timeLine);
    }
}

// Update timeline with booking data
function updateTimelineWithBookings() {
    console.log('updateTimelineWithBookings called');
    console.log('Rooms loaded:', rooms.length);
    console.log('Bookings loaded:', bookings.length);
    
    // Safety check: ensure rooms are loaded first
    if (!rooms || rooms.length === 0) {
        console.warn('No rooms loaded yet, cannot display bookings');
        return;
    }
    
    // Safety check: ensure timeline structure exists
    const timeline = document.getElementById('timeline');
    const timelineRows = timeline.querySelectorAll('.timeline-row');
    if (timelineRows.length === 0) {
        console.warn('No timeline structure found, regenerating...');
        generateTimeline();
        // Wait a moment for DOM to update, then try again
        setTimeout(() => updateTimelineWithBookings(), 100);
        return;
    }
    
    const selectedRoomId = document.getElementById('roomFilter').value;
    
    // Clear existing bookings
    document.querySelectorAll('.booking-block').forEach(el => el.remove());
    
    console.log('All bookings:', bookings); // Debug log
    
    // Filter bookings based on selected room
    const filteredBookings = selectedRoomId ? 
        bookings.filter(b => b.roomId == selectedRoomId) : bookings;
    
    console.log('Filtered bookings:', filteredBookings); // Debug log
    
    // Regenerate timeline if room filter changed
    generateTimeline();
    
    filteredBookings.forEach(booking => {
        // Skip invalid bookings (like the placeholder booking with timeID 0)
        if (booking.startTime && 
            booking.startTime !== '0000-00-00 00:00:00' && 
            booking.timeID !== 0) {
            displayBookingOnTimeline(booking);
        }
    });
}

// Display individual booking on timeline (precise positioning)
function displayBookingOnTimeline(booking) {
    try {
        console.log('Processing booking:', booking);
        
        // Handle the datetime format from database (YYYY-MM-DD HH:MM:SS)
        const startTime = new Date(booking.startTime.replace(' ', 'T'));
        const endTime = new Date(booking.endTime.replace(' ', 'T'));
        
        console.log('Parsed start time:', startTime);
        console.log('Parsed end time:', endTime);
        
        const startHour = startTime.getHours();
        const startMinute = startTime.getMinutes();
        const endHour = endTime.getHours();
        const endMinute = endTime.getMinutes();
        
        // Only display if within business hours (8 AM to 6 PM)
        if (startHour >= 8 && startHour <= 18) {
            // Find the room row
            const roomRow = document.querySelector(`.timeline-row[data-room-id="${booking.roomId}"]`);
            if (!roomRow) {
                console.log('Room row not found for roomId:', booking.roomId);
                return;
            }
            
            // Calculate precise positioning
            const totalMinutesFromStart = (startHour - 8) * 60 + startMinute;
            const durationMinutes = (endTime - startTime) / (1000 * 60);
            
            // Each hour column is 100px wide, so each minute is 100/60 = 1.667px
            const pixelsPerMinute = 100 / 60;
            
            // Calculate position and width
            const leftPosition = 180 + (totalMinutesFromStart * pixelsPerMinute); // 180px for room column
            const blockWidth = durationMinutes * pixelsPerMinute;
            
            console.log(`Positioning: ${totalMinutesFromStart} minutes from start, ${durationMinutes} minutes duration`);
            console.log(`Left: ${leftPosition}px, Width: ${blockWidth}px`);
            
            // Create booking block with absolute positioning
            const bookingBlock = document.createElement('div');
            bookingBlock.className = 'booking-block';
            bookingBlock.style.position = 'absolute';
            bookingBlock.style.left = `${leftPosition}px`;
            bookingBlock.style.width = `${blockWidth}px`;
            bookingBlock.style.top = '5px';
            bookingBlock.style.bottom = '5px';
            bookingBlock.style.zIndex = '10';
            
            // Content
            const startTimeStr = startTime.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
            const endTimeStr = endTime.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
            
            bookingBlock.innerHTML = `
                <div style="font-size: 10px; color: #ffffff;">${startTimeStr} - ${endTimeStr}</div>
            `;
            
            bookingBlock.title = `${booking.roomName} (${booking.roomType})\n${startTimeStr} - ${endTimeStr}`;
            
            // Add click handler
            bookingBlock.addEventListener('click', () => {
                const duration = Math.round(durationMinutes);
                alert(`Booking Details:\nRoom: ${booking.roomName} (${booking.roomType})\nTime: ${startTimeStr} - ${endTimeStr}\nDuration: ${duration} minutes`);
            });
            
            // Add to the room row (make it relative positioned)
            roomRow.style.position = 'relative';
            roomRow.appendChild(bookingBlock);
        }
        
    } catch (error) {
        console.error('Error displaying booking on timeline:', error, booking);
    }
}

// Refresh function for the button
async function refreshCalendar() {
    console.log('Refreshing calendar...');
    
    // Force clear all cached data
    bookings = [];
    rooms = [];
    
    // Clear all existing UI elements
    const timeline = document.getElementById('timeline');
    timeline.innerHTML = '';
    
    // Clear any existing booking blocks
    const existingBlocks = document.querySelectorAll('.booking-block');
    existingBlocks.forEach(block => block.remove());
    
    // Reload data sequentially to ensure proper loading order
    try {
        generateBasicTimeline();
        await loadRooms();
        await loadBookings();
        console.log('Refresh complete');
    } catch (error) {
        console.error('Refresh failed:', error);
        updateStatus('Refresh failed', true);
    }
}

// Function to show room details in a modal popup
function showRoomDetails(room) {
    const modal = document.getElementById('roomModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalBody = document.getElementById('modalBody');
    
    const imageUrl = room.image ? `/images/${room.image}.jpg` : '/images/default.jpg';
    
    modalTitle.textContent = `Room Details - ${room.roomName}`;
    
    modalBody.innerHTML = `
        <img src="${imageUrl}" alt="${room.roomName}" class="room-image" 
             onerror="this.style.display='none'">
        <div class="room-type-modal">${room.roomType}</div>
        <div class="detail-item">
            <span class="detail-label">Room ID:</span> ${room.roomId}
        </div>
        <div class="detail-item">
            <span class="detail-label">Capacity:</span> ${room.capacity || 'Not specified'}
        </div>
        <div class="detail-item">
            <span class="detail-label">Location:</span> ${room.location || 'Not specified'}
        </div>
        <div class="detail-item">
            <span class="detail-label">Equipment:</span> ${room.equipment || 'Standard facilities'}
        </div>
    `;
    
    modal.style.display = 'block';
}

// Function to close the room details modal
function closeRoomModal() {
    const modal = document.getElementById('roomModal');
    modal.style.display = 'none';
}

// Close modal when clicking outside of it
window.onclick = function(event) {
    const modal = document.getElementById('roomModal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
}

// Function to show room details in a modal popup
function showRoomDetails(room) {
    const modal = document.getElementById('roomModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalBody = document.getElementById('modalBody');
    
    const imageUrl = room.image ? `/images/${room.image}.jpg` : '/images/default.jpg';
    
    modalTitle.textContent = `Room Details - ${room.roomName}`;
    
    modalBody.innerHTML = `
        <img src="${imageUrl}" alt="${room.roomName}" class="room-image" 
             onerror="this.style.display='none'">
        <div class="room-type-modal">${room.roomType}</div>
        <div class="detail-item">
            <span class="detail-label">Room ID:</span> ${room.roomId}
        </div>
        <div class="detail-item">
            <span class="detail-label">Capacity:</span> ${room.capacity || 'Not specified'}
        </div>
        <div class="detail-item">
            <span class="detail-label">Location:</span> ${room.location || 'Not specified'}
        </div>
        <div class="detail-item">
            <span class="detail-label">Equipment:</span> ${room.equipment || 'Standard facilities'}
        </div>
    `;
    
    modal.style.display = 'block';
}

// Function to close the room details modal
function closeRoomModal() {
    const modal = document.getElementById('roomModal');
    modal.style.display = 'none';
}

// Close modal when clicking outside of it
window.onclick = function(event) {
    const modal = document.getElementById('roomModal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
}

