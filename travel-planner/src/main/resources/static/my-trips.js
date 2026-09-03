async function loadTrips() {
    const container = document.getElementById('tripsList');

    if (!isLoggedIn()) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const trips = await apiFetch('/api/itineraries');

        if (!trips.length) {
            container.innerHTML = `
                <div class="empty-trips">
                    <h3>No saved trips yet</h3>
                    <p class="muted">Generate an itinerary, then tap <strong>Save trip</strong> on the itinerary page.</p>
                    <a href="/" class="cta-button">Start planning</a>
                </div>
            `;
            return;
        }

        container.innerHTML = trips.map(trip => {
            const image = trip.heroImageUrl
                ? `style="background-image:url('${trip.heroImageUrl}')"`
                : '';
            const savedDate = new Date(trip.savedAt).toLocaleDateString(undefined, {
                month: 'short', day: 'numeric', year: 'numeric'
            });

            return `
                <article class="trip-card">
                    <a href="/itinerary.html?id=${trip.id}" class="trip-card-media" ${image} aria-label="Open ${trip.destination}"></a>
                    <div class="trip-card-body">
                        <div class="trip-card-top">
                            <h3>${trip.destination.split('(')[0].trim()}</h3>
                            <span class="trip-card-date">${savedDate}</span>
                        </div>
                        <p class="muted">${[trip.duration, trip.budget].filter(Boolean).join(' · ') || 'Trip details'}</p>
                        <div class="trip-card-actions">
                            <a href="/itinerary.html?id=${trip.id}" class="text-link">Open itinerary →</a>
                            <button type="button" class="nav-link-btn delete-trip" data-id="${trip.id}">Delete</button>
                        </div>
                    </div>
                </article>
            `;
        }).join('');

        container.querySelectorAll('.delete-trip').forEach(btn => {
            btn.addEventListener('click', async () => {
                if (!confirm('Delete this saved trip?')) return;
                try {
                    await apiFetch(`/api/itineraries/${btn.dataset.id}`, { method: 'DELETE' });
                    loadTrips();
                } catch (err) {
                    alert(err.message);
                }
            });
        });
    } catch (err) {
        container.innerHTML = `<p class="muted error-text">${err.message}</p>`;
    }
}

loadTrips();
