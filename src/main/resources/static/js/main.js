// Smart Campus Management System - Interactive Client Scripts

document.addEventListener('DOMContentLoaded', function () {
    // 1. Auto-dismiss flash alerts after 6 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            try {
                const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
                bsAlert.close();
            } catch (e) {}
        }, 6000);
    });

    // 2. Demo Login Quick-Fill Helper
    const demoButtons = document.querySelectorAll('[data-demo-user]');
    demoButtons.forEach(btn => {
        btn.addEventListener('click', function () {
            const role = this.getAttribute('data-demo-user');
            const usernameInput = document.getElementById('username');
            const passwordInput = document.getElementById('password');

            if (!usernameInput || !passwordInput) return;

            if (role === 'admin') {
                usernameInput.value = 'admin';
                passwordInput.value = 'admin123';
            } else if (role === 'student') {
                usernameInput.value = 'student';
                passwordInput.value = 'student123';
            } else if (role === 'faculty') {
                usernameInput.value = 'faculty';
                passwordInput.value = 'faculty123';
            } else if (role === 'organizer') {
                usernameInput.value = 'organizer';
                passwordInput.value = 'organizer123';
            }

            // Visual feedback
            btn.classList.add('btn-dark');
            setTimeout(() => btn.classList.remove('btn-dark'), 300);
        });
    });

    // 3. Room Booking Time validation helper
    const startTimeInput = document.getElementById('startTime');
    const endTimeInput = document.getElementById('endTime');
    const bookingForm = document.getElementById('roomBookingForm');

    if (startTimeInput && endTimeInput && bookingForm) {
        bookingForm.addEventListener('submit', function (e) {
            const startVal = startTimeInput.value;
            const endVal = endTimeInput.value;

            if (startVal && endVal && startVal >= endVal) {
                e.preventDefault();
                alert('Invalid Time Range: Start time must be earlier than End time.');
                endTimeInput.focus();
            }
        });
    }

    // 4. Interactive Campus Map pin tooltip/highlighter
    const mapPins = document.querySelectorAll('.map-location-pin');
    mapPins.forEach(pin => {
        pin.addEventListener('click', function () {
            const locName = this.getAttribute('data-name');
            const locBlock = this.getAttribute('data-block');
            const locCategory = this.getAttribute('data-category');
            const locHours = this.getAttribute('data-hours');
            const locId = this.getAttribute('data-id');

            const infoBox = document.getElementById('mapSelectedLocation');
            if (infoBox) {
                infoBox.innerHTML = `
                    <div class="card shadow-sm border-primary">
                        <div class="card-body">
                            <span class="badge bg-primary mb-2">${locCategory}</span>
                            <h5 class="card-title text-primary mb-1">${locName}</h5>
                            <p class="card-text text-muted mb-2"><i class="bi bi-geo-alt"></i> ${locBlock}</p>
                            <p class="small text-secondary mb-3"><i class="bi bi-clock"></i> Hours: ${locHours}</p>
                            <a href="/directory/view/${locId}" class="btn btn-sm btn-outline-primary">View Full Details & Contacts &rarr;</a>
                        </div>
                    </div>
                `;
            }
        });
    });
});
