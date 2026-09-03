'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Nav from '../../../components/Nav';
import { apiFetch, getToken } from '../../../lib/api';

export default function TripDetailPage() {
  const params = useParams();
  const router = useRouter();
  const [trip, setTrip] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!getToken()) {
      router.replace('/login');
      return;
    }
    apiFetch(`/api/trips/${params.id}`)
      .then(setTrip)
      .catch((err) => setError(err.message));
  }, [params.id, router]);

  return (
    <>
      <Nav extra={<a className="back-button" href="/trips">← All trips</a>} />
      <main className="planner-section">
        <div className="planner-container">
          <div className="planner-card">
            {error && <p className="form-error">{error}</p>}
            {trip && (
              <>
                <div className="card-header">
                  <h2>{trip.destination}</h2>
                  <p>{trip.origin} to {trip.destination} · {trip.startDate} – {trip.endDate}</p>
                </div>
                <p>{trip.travelers} travelers · hotel budget ₹{trip.maxHotelBudgetPerNight} / night</p>
                <p>Flight: {trip.selectedFlight?.airline} {trip.selectedFlight?.flightNumber}</p>
                <p>Hotel: {trip.selectedHotel?.name}</p>
                {(Array.isArray(trip.itinerary) ? trip.itinerary : []).map((day) => (
                  <div key={`${day.day}-${day.date}`} className="day-block">
                    <h4>Day {day.day} · {day.date}</h4>
                    {(day.activities || []).map((activity, index) => (
                      <div key={index} className="activity-row">
                        <strong>{activity.time} · {activity.title}</strong>
                        <p>{activity.description}</p>
                      </div>
                    ))}
                  </div>
                ))}
              </>
            )}
          </div>
        </div>
      </main>
    </>
  );
}
