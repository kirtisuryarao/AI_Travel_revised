'use client';

import { useMemo, useState } from 'react';
import { jsPDF } from 'jspdf';
import { apiFetch, getToken } from '../lib/api';

const INTERESTS = ['History', 'Food', 'Adventure', 'Nature', 'Culture', 'Relaxation', 'Nightlife', 'Shopping'];
const FACTS = [
  'France is the most visited country in the world.',
  'Goa has a coastline of more than 100 kilometres.',
  'The first airline ticket was issued in 1914.',
  'Tokyo has more Michelin-starred restaurants than any other city.'
];
const LOADING_MESSAGES = [
  'Analyzing best flight routes...',
  'Matching hotels to your nightly budget...',
  'Asking the travel planner for a personal itinerary...',
  'Adding weather notes and packing ideas...'
];

function formatMoney(value, currency = 'INR') {
  if (value == null) return '—';
  return `${currency} ${Number(value).toLocaleString('en-IN')}`;
}

function formatTime(value) {
  if (!value) return '';
  try {
    return new Date(value).toLocaleString();
  } catch {
    return value;
  }
}

export default function Planner() {
  const [step, setStep] = useState(1);
  const [origin, setOrigin] = useState('DEL');
  const [destination, setDestination] = useState('GOI');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [travelers, setTravelers] = useState(2);
  const [maxTotalBudget, setMaxTotalBudget] = useState('50000');
  const [maxHotelBudgetPerNight, setMaxHotelBudgetPerNight] = useState('5000');
  const [selectedInterests, setSelectedInterests] = useState(['Food', 'Nature']);
  const [customInterest, setCustomInterest] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [loadingMessage, setLoadingMessage] = useState(LOADING_MESSAGES[0]);
  const [flights, setFlights] = useState([]);
  const [hotels, setHotels] = useState([]);
  const [selectedFlight, setSelectedFlight] = useState(null);
  const [selectedHotel, setSelectedHotel] = useState(null);
  const [itinerary, setItinerary] = useState(null);
  const [saveMessage, setSaveMessage] = useState('');

  const durationLabel = useMemo(() => {
    if (!startDate || !endDate) return 'Select dates to calculate duration';
    const start = new Date(startDate);
    const end = new Date(endDate);
    const nights = Math.round((end - start) / 86400000);
    if (Number.isNaN(nights) || nights < 0) return 'End date must be on or after start date';
    return `${nights + 1} days / ${nights} nights`;
  }, [startDate, endDate]);

  function toggleInterest(name) {
    setSelectedInterests((current) =>
      current.includes(name) ? current.filter((item) => item !== name) : [...current, name]
    );
  }

  function addCustomInterest(event) {
    if (event.key !== 'Enter') return;
    event.preventDefault();
    const value = customInterest.trim();
    if (value && !selectedInterests.includes(value)) {
      setSelectedInterests((current) => [...current, value]);
    }
    setCustomInterest('');
  }

  function validateForm() {
    if (!origin.trim() || !destination.trim()) return 'Origin and destination are required.';
    if (!startDate || !endDate) return 'Travel start and end dates are required.';
    if (new Date(endDate) < new Date(startDate)) return 'End date cannot be before start date.';
    if (travelers < 1) return 'Travelers must be at least 1.';
    if (Number(maxTotalBudget) <= 0) return 'Total budget must be positive.';
    if (Number(maxHotelBudgetPerNight) <= 0) return 'Maximum hotel budget per night must be positive.';
    return '';
  }

  async function searchFlights(event) {
    event.preventDefault();
    const message = validateForm();
    if (message) {
      setError(message);
      return;
    }
    setError('');
    setLoading(true);
    setLoadingMessage(LOADING_MESSAGES[0]);
    try {
      const data = await apiFetch('/api/flights/search', {
        method: 'POST',
        body: JSON.stringify({
          origin: origin.trim(),
          destination: destination.trim(),
          departureDate: startDate,
          returnDate: endDate,
          travelers,
          maxTotalBudget: Number(maxTotalBudget),
          currency: 'INR'
        })
      });
      setFlights(data.flights || []);
      setSelectedFlight(null);
      setSelectedHotel(null);
      setItinerary(null);
      setStep(2);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function chooseFlight(flight) {
    setSelectedFlight(flight);
    setError('');
    setLoading(true);
    setLoadingMessage(LOADING_MESSAGES[1]);
    try {
      const data = await apiFetch('/api/hotels/search', {
        method: 'POST',
        body: JSON.stringify({
          destination: destination.trim(),
          checkIn: startDate,
          checkOut: endDate,
          travelers,
          rooms: 1,
          maxHotelBudgetPerNight: Number(maxHotelBudgetPerNight),
          currency: 'INR'
        })
      });
      setHotels(data.hotels || []);
      setStep(3);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function chooseHotel(hotel) {
    setSelectedHotel(hotel);
    setError('');
    setLoading(true);
    let messageIndex = 2;
    setLoadingMessage(LOADING_MESSAGES[messageIndex]);
    const timer = setInterval(() => {
      messageIndex = (messageIndex + 1) % LOADING_MESSAGES.length;
      setLoadingMessage(LOADING_MESSAGES[messageIndex]);
    }, 1800);
    try {
      const data = await apiFetch('/api/ai/itinerary', {
        method: 'POST',
        body: JSON.stringify({
          origin: origin.trim(),
          destination: destination.trim(),
          startDate,
          endDate,
          travelers,
          maxTotalBudget: Number(maxTotalBudget),
          maxHotelBudgetPerNight: Number(maxHotelBudgetPerNight),
          interests: selectedInterests,
          selectedFlight: {
            flightId: selectedFlight.flightId,
            airline: selectedFlight.airline,
            flightNumber: selectedFlight.flightNumber,
            price: selectedFlight.price,
            currency: selectedFlight.currency
          },
          selectedHotel: {
            hotelId: hotel.hotelId,
            name: hotel.name,
            pricePerNight: hotel.pricePerNight,
            currency: hotel.currency
          }
        })
      });
      setItinerary(data);
      setStep(4);
    } catch (err) {
      setError(err.message);
    } finally {
      clearInterval(timer);
      setLoading(false);
    }
  }

  async function saveTrip() {
    if (!getToken()) {
      window.location.href = '/login';
      return;
    }
    setSaveMessage('');
    try {
      await apiFetch('/api/trips', {
        method: 'POST',
        body: JSON.stringify({
          origin: origin.trim(),
          destination: destination.trim(),
          startDate,
          endDate,
          travelers,
          maxTotalBudget: Number(maxTotalBudget),
          maxHotelBudgetPerNight: Number(maxHotelBudgetPerNight),
          interests: selectedInterests,
          selectedFlight,
          selectedHotel,
          itinerary: itinerary?.itinerary || [],
          budgetEstimate: itinerary?.budgetEstimate || {}
        })
      });
      setSaveMessage('Trip saved. You can view it under My trips.');
    } catch (err) {
      setError(err.message);
    }
  }

  function exportPdf() {
    if (!itinerary) return;
    const doc = new jsPDF();
    doc.setFontSize(16);
    doc.text(`TravelAI itinerary — ${itinerary.tripSummary.destination}`, 14, 18);
    doc.setFontSize(11);
    let y = 28;
    const lines = [
      `${itinerary.tripSummary.origin} to ${itinerary.tripSummary.destination}`,
      `${itinerary.tripSummary.startDate} – ${itinerary.tripSummary.endDate}`,
      `Flight: ${itinerary.selectedFlight.airline} ${itinerary.selectedFlight.flightNumber}`,
      `Hotel: ${itinerary.selectedHotel.name}`,
      itinerary.weather || ''
    ];
    lines.forEach((line) => {
      doc.text(line, 14, y);
      y += 8;
    });
    (itinerary.itinerary || []).forEach((day) => {
      if (y > 270) {
        doc.addPage();
        y = 20;
      }
      doc.text(`Day ${day.day} (${day.date})`, 14, y);
      y += 7;
      (day.activities || []).forEach((activity) => {
        const text = `${activity.time} ${activity.title}: ${activity.description}`;
        const wrapped = doc.splitTextToSize(text, 180);
        doc.text(wrapped, 18, y);
        y += wrapped.length * 6 + 2;
      });
    });
    doc.save(`travelai-${itinerary.tripSummary.destination}.pdf`);
  }

  return (
    <section id="planner-section" className="planner-section">
      <div className="planner-container">
        <div className="planner-card" id="plannerCard">
          <div className="card-header">
            <h2>Start Your Adventure</h2>
            <p>Tell us about your dream trip</p>
          </div>

          <div className="step-pills">
            <span className={`step-pill ${step === 1 ? 'active' : ''}`}>1. Trip details</span>
            <span className={`step-pill ${step === 2 ? 'active' : ''}`}>2. Flights</span>
            <span className={`step-pill ${step === 3 ? 'active' : ''}`}>3. Hotels</span>
            <span className={`step-pill ${step === 4 ? 'active' : ''}`}>4. Itinerary</span>
          </div>

          {error && <p className="form-error">{error}</p>}

          {loading && (
            <div className="premium-loading">
              <div className="loading-airplane-container">
                <div className="loading-airplane">✈️</div>
                <div className="flight-path"></div>
              </div>
              <h3 className="loading-title">Crafting Your Journey...</h3>
              <div className="loading-messages">{loadingMessage}</div>
              <div className="progress-bar-container">
                <div className="progress-bar"></div>
              </div>
              <p className="estimated-time">Estimated completion: <span>15s</span></p>
              <div className="travel-fact-card">
                <div className="fact-icon">💡</div>
                <div className="fact-content">
                  <h4>Did you know?</h4>
                  <p>{FACTS[Math.floor(Math.random() * FACTS.length)]}</p>
                </div>
              </div>
            </div>
          )}

          {!loading && step === 1 && (
            <form className="travel-form" onSubmit={searchFlights}>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="flyingFrom">Flying From</label>
                  <input id="flyingFrom" value={origin} onChange={(e) => setOrigin(e.target.value)} placeholder="e.g. DEL or Delhi" required />
                </div>
                <div className="form-group">
                  <label htmlFor="destination">Flying To</label>
                  <input id="destination" value={destination} onChange={(e) => setDestination(e.target.value)} placeholder="e.g. GOI or Goa" required />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Travel Dates</label>
                  <div className="date-picker-group">
                    <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} required />
                    <span>to</span>
                    <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} required />
                  </div>
                  <span className="form-help">{durationLabel}</span>
                </div>
                <div className="form-group">
                  <label>Travelers</label>
                  <div className="traveler-counter">
                    <button type="button" className="counter-btn" onClick={() => setTravelers((n) => Math.max(1, n - 1))}>-</button>
                    <span>{travelers}</span>
                    <button type="button" className="counter-btn" onClick={() => setTravelers((n) => n + 1)}>+</button>
                  </div>
                  <span className="form-help">Total people</span>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="budgetInput">Maximum Total Trip Budget (₹)</label>
                  <input id="budgetInput" type="number" min="1" value={maxTotalBudget} onChange={(e) => setMaxTotalBudget(e.target.value)} required />
                </div>
                <div className="form-group">
                  <label htmlFor="hotelBudget">Maximum Hotel Budget Per Night (₹)</label>
                  <input id="hotelBudget" type="number" min="1" value={maxHotelBudgetPerNight} onChange={(e) => setMaxHotelBudgetPerNight(e.target.value)} required />
                </div>
              </div>

              <div className="form-group">
                <label>Your Interests</label>
                <div className="interests-container">
                  {INTERESTS.map((name) => (
                    <button
                      type="button"
                      key={name}
                      className={`chip ${selectedInterests.includes(name) ? 'active' : ''}`}
                      onClick={() => toggleInterest(name)}
                    >
                      {name}
                    </button>
                  ))}
                  {selectedInterests.filter((name) => !INTERESTS.includes(name)).map((name) => (
                    <button type="button" key={name} className="chip active" onClick={() => toggleInterest(name)}>
                      {name}
                    </button>
                  ))}
                </div>
                <input
                  value={customInterest}
                  onChange={(e) => setCustomInterest(e.target.value)}
                  onKeyDown={addCustomInterest}
                  placeholder="Add custom interest + Enter"
                />
              </div>

              <button type="submit" className="plan-button">
                <span className="button-text">Find Flights</span>
                <span className="button-icon">✈️</span>
              </button>
            </form>
          )}

          {!loading && step === 2 && (
            <div className="option-grid">
              <button type="button" className="back-button" onClick={() => setStep(1)}>← Edit trip details</button>
              {flights.length === 0 && <p>No flights matched this search. Try a different route or budget.</p>}
              {flights.map((flight) => (
                <article key={flight.flightId} className="option-card">
                  <h3>{flight.airline} {flight.flightNumber}</h3>
                  <div className="option-meta">
                    <span>{flight.departure?.airport} {formatTime(flight.departure?.time)}</span>
                    <span>→ {flight.arrival?.airport} {formatTime(flight.arrival?.time)}</span>
                    <span>{flight.durationMinutes} min</span>
                    <span>{flight.stops} stop{flight.stops === 1 ? '' : 's'}</span>
                    <strong>{formatMoney(flight.price, flight.currency)}</strong>
                  </div>
                  <button type="button" className="plan-button" onClick={() => chooseFlight(flight)}>Select Flight</button>
                </article>
              ))}
            </div>
          )}

          {!loading && step === 3 && (
            <div className="option-grid">
              <button type="button" className="back-button" onClick={() => setStep(2)}>← Choose another flight</button>
              {hotels.length === 0 && <p>No hotels are at or under ₹{maxHotelBudgetPerNight} per night for this destination.</p>}
              {hotels.map((hotel) => (
                <article key={hotel.hotelId} className="option-card">
                  {hotel.imageUrl && <img src={hotel.imageUrl} alt={hotel.name} />}
                  <h3>{hotel.name}</h3>
                  <div className="option-meta">
                    <span>★ {hotel.rating}</span>
                    <span>{hotel.location}</span>
                    <span>{hotel.roomType}</span>
                    <strong>{formatMoney(hotel.pricePerNight, hotel.currency)} / night</strong>
                    <span>Stay total {formatMoney(hotel.totalPrice, hotel.currency)}</span>
                  </div>
                  <div className="amenity-row">
                    {(hotel.amenities || []).map((item) => <span key={item} className="amenity">{item}</span>)}
                  </div>
                  <button type="button" className="plan-button" onClick={() => chooseHotel(hotel)}>Select Hotel</button>
                </article>
              ))}
            </div>
          )}

          {!loading && step === 4 && itinerary && (
            <div className="results-content">
              <button type="button" className="back-button" onClick={() => setStep(3)}>← Choose another hotel</button>
              <div className="weather-card-result">{itinerary.weather}</div>
              <div className="itinerary-card">
                <div className="itinerary-header">
                  <h3>📋 Your Trip Details</h3>
                </div>
                <p>{itinerary.tripSummary.origin} → {itinerary.tripSummary.destination} · {itinerary.tripSummary.travelers} travelers</p>
                <p>{itinerary.tripSummary.startDate} to {itinerary.tripSummary.endDate}</p>
                <p>Flight: {itinerary.selectedFlight.airline} {itinerary.selectedFlight.flightNumber} · {formatMoney(itinerary.selectedFlight.price, itinerary.selectedFlight.currency)}</p>
                <p>Hotel: {itinerary.selectedHotel.name} · {formatMoney(itinerary.selectedHotel.pricePerNight, itinerary.selectedHotel.currency)} / night</p>
                {itinerary.budgetEstimate && (
                  <p>
                    Estimated total {formatMoney(itinerary.budgetEstimate.total, itinerary.budgetEstimate.currency)}
                    {itinerary.budgetEstimate.flight != null && ` (flights ${formatMoney(itinerary.budgetEstimate.flight, itinerary.budgetEstimate.currency)}, hotels ${formatMoney(itinerary.budgetEstimate.hotel, itinerary.budgetEstimate.currency)})`}
                  </p>
                )}
                {(itinerary.itinerary || []).map((day) => (
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
                {itinerary.packingSuggestions?.length > 0 && (
                  <div className="day-block">
                    <h4>Packing</h4>
                    <ul>{itinerary.packingSuggestions.map((item) => <li key={item}>{item}</li>)}</ul>
                  </div>
                )}
                {itinerary.travelTips?.length > 0 && (
                  <div className="day-block">
                    <h4>Travel notes</h4>
                    <ul>{itinerary.travelTips.map((item) => <li key={item}>{item}</li>)}</ul>
                  </div>
                )}
              </div>
              <div className="export-section">
                <button type="button" className="plan-button" onClick={saveTrip}>Save Trip</button>
                <button type="button" className="export-button" onClick={exportPdf}>📄 Export as PDF</button>
              </div>
              {saveMessage && <p className="success-note">{saveMessage}</p>}
            </div>
          )}
        </div>
      </div>
    </section>
  );
}
