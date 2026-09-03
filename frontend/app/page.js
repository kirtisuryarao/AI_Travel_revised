'use client';

import Nav from '../components/Nav';
import Planner from '../components/Planner';

export default function HomePage() {
  return (
    <>
      <Nav />
      <section className="hero">
        <div className="hero-content">
          <div className="hero-badge">✨ AI-Powered Travel Planning</div>
          <h1 className="hero-title">Plan Your Perfect Trip with AI</h1>
          <p className="hero-subtitle">Get personalized itineraries, weather insights, and budget planning powered by artificial intelligence</p>
          <button className="cta-button" onClick={() => document.getElementById('planner-section')?.scrollIntoView({ behavior: 'smooth' })}>
            Start Planning
          </button>
        </div>
        <div className="hero-background"></div>
      </section>

      <section className="features-section">
        <div className="features-header">
          <h2>Why Choose TravelAI?</h2>
          <p>Everything you need for the perfect trip</p>
        </div>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">🤖</div>
            <h3>AI Itinerary</h3>
            <p>Get detailed day-by-day plans tailored to your interests</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🌤️</div>
            <h3>Weather Intel</h3>
            <p>Real-time weather data for your destination</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">💰</div>
            <h3>Budget Planning</h3>
            <p>Smart budget allocation for all travel expenses</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">📄</div>
            <h3>PDF Export</h3>
            <p>Save and share your itinerary in one click</p>
          </div>
        </div>
      </section>

      <Planner />

      <footer className="footer">
        <p>© 2026 TravelAI. Plan smarter, travel better.</p>
      </footer>
    </>
  );
}
