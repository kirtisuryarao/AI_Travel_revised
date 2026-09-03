'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import Nav from '../../components/Nav';
import { apiFetch, getToken } from '../../lib/api';

export default function TripsPage() {
  const router = useRouter();
  const [trips, setTrips] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!getToken()) {
      router.replace('/login');
      return;
    }
    apiFetch('/api/trips')
      .then(setTrips)
      .catch((err) => setError(err.message));
  }, [router]);

  async function removeTrip(id) {
    try {
      await apiFetch(`/api/trips/${id}`, { method: 'DELETE' });
      setTrips((current) => current.filter((trip) => trip.id !== id));
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <>
      <Nav />
      <main className="planner-section">
        <div className="planner-container">
          <div className="planner-card">
            <div className="card-header">
              <h2>My trips</h2>
              <p>Saved itineraries for the signed-in traveler</p>
            </div>
            {error && <p className="form-error">{error}</p>}
            <div className="option-grid">
              {trips.length === 0 && <p>No saved trips yet.</p>}
              {trips.map((trip) => (
                <article key={trip.id} className="option-card">
                  <h3>{trip.destination}</h3>
                  <div className="option-meta">
                    <span>{trip.startDate} to {trip.endDate}</span>
                    <span>{trip.travelers} travelers</span>
                    <span>{trip.status}</span>
                  </div>
                  <Link className="nav-link" href={`/trips/${trip.id}`}>View</Link>
                  <button type="button" className="back-button" onClick={() => removeTrip(trip.id)}>Delete</button>
                </article>
              ))}
            </div>
          </div>
        </div>
      </main>
    </>
  );
}
