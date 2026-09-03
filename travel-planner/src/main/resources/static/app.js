// Theme Toggle
const themeToggle = document.getElementById('themeToggle');
const html = document.documentElement;

// Initialize theme from localStorage
function initTheme() {
    const savedTheme = localStorage.getItem('theme') || 'light';
    if (savedTheme === 'dark') {
        html.classList.add('dark-mode');
        themeToggle.textContent = '☀️';
    } else {
        html.classList.remove('dark-mode');
        themeToggle.textContent = '🌙';
    }
}

// Toggle theme
function toggleTheme() {
    const isDark = html.classList.toggle('dark-mode');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    themeToggle.textContent = isDark ? '☀️' : '🌙';
}

themeToggle.addEventListener('click', toggleTheme);

// Initialize theme on page load
initTheme();

// Form Logic

// Travelers
let travelers = 2;
function updateTravelers(change) {
    travelers += change;
    if (travelers < 1) travelers = 1;
    if (travelers > 15) travelers = 15;
    document.getElementById('travelerCount').textContent = travelers;
}

// Budget Input Logic
function checkBudgetHeuristic() {
    const input = document.getElementById('budgetInput');
    const warning = document.getElementById('budgetWarning');
    const destination = document.getElementById('destination').value.trim().toLowerCase();
    const budgetVal = parseInt(input.value);

    // Sync hidden input
    document.getElementById('budget').value = `₹${budgetVal}`;

    if (!budgetVal || isNaN(budgetVal) || !destination) {
        warning.style.display = 'none';
        return;
    }

    // Simple heuristic map (Baseline Minimums)
    const heuristics = {
        'goa': 8000,
        'paris': 80000,
        'london': 90000,
        'tokyo': 75000,
        'new york': 100000,
        'bali': 25000,
        'dubai': 50000,
        'mumbai': 10000,
        'delhi': 8000
    };

    let minExpected = 0;
    for (const [key, value] of Object.entries(heuristics)) {
        if (destination.includes(key)) {
            minExpected = value;
            break;
        }
    }

    // Default minimum if destination not in list
    if (minExpected === 0) {
        minExpected = 5000;
    }

    if (budgetVal < minExpected) {
        const destDisplay = destination.charAt(0).toUpperCase() + destination.slice(1);
        warning.innerHTML = `💡 <strong>AI Tip:</strong> Trips to ${destDisplay} generally start around ₹${minExpected.toLocaleString()}. You can continue, but increasing your budget may improve accommodation and activity options.`;
        warning.style.display = 'block';
    } else {
        warning.style.display = 'none';
    }
}

// Interest Chips
let selectedInterests = new Set();
function toggleChip(btn) {
    btn.classList.toggle('active');
    const interest = btn.textContent;
    if (btn.classList.contains('active')) {
        selectedInterests.add(interest);
    } else {
        selectedInterests.delete(interest);
    }
    updateInterestsInput();
}

function addCustomChip(e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        const input = document.getElementById('customInterest');
        const val = input.value.trim();
        if (val) {
            const container = document.getElementById('interestsContainer');
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'chip active';
            btn.textContent = val;
            btn.onclick = function() { toggleChip(this); };
            container.appendChild(btn);
            selectedInterests.add(val);
            updateInterestsInput();
            input.value = '';
        }
    }
}

function updateInterestsInput() {
    document.getElementById('interests').value = Array.from(selectedInterests).join(', ');
}

// Date Range
function updateDuration() {
    const start = document.getElementById('startDate').value;
    const end = document.getElementById('endDate').value;
    if (start && end) {
        const d1 = new Date(start);
        const d2 = new Date(end);
        if (d2 >= d1) {
            const diffTime = Math.abs(d2 - d1);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // +1 to include both start and end days
            document.getElementById('durationDisplay').textContent = `${diffDays} days total`;
            document.getElementById('duration').value = `${diffDays} days`;
        } else {
            document.getElementById('durationDisplay').textContent = 'End date must be after start date';
            document.getElementById('duration').value = '';
        }
    }
}
document.getElementById('startDate').addEventListener('change', updateDuration);
document.getElementById('endDate').addEventListener('change', updateDuration);

// Loading Animation
const loadingMessages = [
    "Analyzing best flight routes...",
    "Finding hidden gems...",
    "Curating local dining spots...",
    "Checking weather patterns...",
    "Optimizing your budget...",
    "Finalizing your itinerary..."
];

function startLoadingAnimation() {
    const loading = document.getElementById('loading');
    const form = document.getElementById('travelForm');
    const messagesEl = document.getElementById('loadingMessages');
    const progressBar = document.getElementById('progressBar');
    const estTime = document.getElementById('estTime');
    
    // Hide form, show loading
    form.style.display = 'none';
    loading.style.display = 'block';
    
    // Reset
    progressBar.style.width = '0%';
    let progress = 0;
    let messageIdx = 0;
    let timeRemaining = 15;
    
    // Interval for progress & time
    const interval = setInterval(() => {
        progress += (100 / 150); // 15 seconds * 10 ticks/sec
        if (progress > 95) progress = 95; // cap at 95% until complete
        progressBar.style.width = `${progress}%`;
        
        if (progress % 10 < 1) { // roughly every second
            timeRemaining--;
            if (timeRemaining < 1) timeRemaining = 1;
            estTime.textContent = `${timeRemaining}s`;
        }
        
        // Cycle messages
        if (progress % 20 < 1) { // every ~3 seconds
            messageIdx = (messageIdx + 1) % loadingMessages.length;
            messagesEl.style.opacity = 0;
            setTimeout(() => {
                messagesEl.textContent = loadingMessages[messageIdx];
                messagesEl.style.opacity = 1;
            }, 300);
        }
        
    }, 100);
    
    return interval;
}

// Generate Plan Function
async function generatePlan() {
    const flyingFrom = document.getElementById('flyingFrom').value.trim();
    let destination = document.getElementById('destination').value.trim();
    let duration = document.getElementById('duration').value.trim();
    const budget = document.getElementById('budget').value;
    const interests = document.getElementById('interests').value.trim();
    const planBtn = document.getElementById('planBtn');

    if (!destination || !duration || !budget || !interests) {
        showNotification('Please fill in all fields (Dates, Interests)', 'error');
        return;
    }

    // Enhance prompt data without breaking backend API format
    if (flyingFrom) {
        destination = `${destination} (Flying from: ${flyingFrom})`;
    }
    duration = `${duration} (for ${travelers} travelers)`;

    planBtn.disabled = true;
    
    const loaderInterval = startLoadingAnimation();

    try {
        const response = await fetch('/api/plan', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ destination, duration, budget, interests })
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const data = await response.json();
        
        // Finish progress bar
        clearInterval(loaderInterval);
        document.getElementById('progressBar').style.width = '100%';
        document.getElementById('loadingMessages').textContent = "Journey Ready!";
        document.getElementById('estTime').textContent = "Complete";
        
        // Brief pause for effect
        await new Promise(resolve => setTimeout(resolve, 800));
        
        // Store data in sessionStorage for itinerary page
        const travelData = {
            destination: destination,
            duration: duration,
            budget: budget,
            interests: interests,
            weather: data.weather,
            itinerary: data.itinerary
        };
        
        sessionStorage.setItem('travelData', JSON.stringify(travelData));
        
        // Navigate to itinerary dashboard
        window.location.href = '/itinerary.html';
        
    } catch (error) {
        console.error('Error:', error);
        clearInterval(loaderInterval);
        document.getElementById('loading').style.display = 'none';
        document.getElementById('travelForm').style.display = 'flex';
        showNotification('Failed to generate travel plan. Please try again.', 'error');
    } finally {
        planBtn.disabled = false;
    }
}

// Export to PDF
function exportPDF() {
    const destination = document.getElementById('destination').value.trim();
    const itineraryText = document.getElementById('itineraryOutput').innerText.trim();

    if (!itineraryText) {
        showNotification('No itinerary to export', 'error');
        return;
    }

    try {
        const doc = new window.jspdf.jsPDF();
        const title = destination ? `${destination} Travel Itinerary` : 'Travel Itinerary';

        // Add title
        doc.setFontSize(18);
        doc.setTextColor(51, 102, 255);
        doc.text(title, 15, 15);

        // Add metadata
        doc.setFontSize(10);
        doc.setTextColor(100, 116, 139);
        doc.text(`Generated on: ${new Date().toLocaleDateString()}`, 15, 25);

        // Add content
        doc.setFontSize(11);
        doc.setTextColor(15, 23, 42);
        const lines = doc.splitTextToSize(itineraryText, 180);
        doc.text(lines, 15, 35);

        doc.save('travel-itinerary.pdf');
        showNotification('PDF exported successfully!', 'success');
    } catch (error) {
        console.error('PDF export error:', error);
        showNotification('Failed to export PDF', 'error');
    }
}

// Notification System
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background: ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#3366ff'};
        color: white;
        border-radius: 12px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
        z-index: 1000;
        animation: slideIn 0.3s ease-out;
        font-weight: 500;
        max-width: 300px;
        word-wrap: break-word;
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Add animation styles for notifications
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            opacity: 0;
            transform: translateX(100%);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }

    @keyframes slideOut {
        from {
            opacity: 1;
            transform: translateX(0);
        }
        to {
            opacity: 0;
            transform: translateX(100%);
        }
    }
`;
document.head.appendChild(style);

// Allow Enter key to submit form
document.getElementById('travelForm').addEventListener('keypress', (e) => {
    if (e.key === 'Enter' && e.target.tagName !== 'TEXTAREA') {
        generatePlan();
    }
});


