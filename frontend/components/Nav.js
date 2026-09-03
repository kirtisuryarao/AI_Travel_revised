'use client';

import Link from 'next/link';
import { useEffect, useState } from 'react';
import { clearAuth, getUser } from '../lib/api';

export default function Nav({ extra }) {
  const [user, setUser] = useState(null);
  const [theme, setTheme] = useState('light');

  useEffect(() => {
    setUser(getUser());
    const saved = localStorage.getItem('theme') || 'light';
    setTheme(saved);
    document.documentElement.classList.toggle('dark-mode', saved === 'dark');
  }, []);

  function toggleTheme() {
    const next = theme === 'dark' ? 'light' : 'dark';
    setTheme(next);
    localStorage.setItem('theme', next);
    document.documentElement.classList.toggle('dark-mode', next === 'dark');
  }

  function logout() {
    clearAuth();
    setUser(null);
    window.location.href = '/login';
  }

  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link href="/" className="nav-brand">
          <span className="logo">✈️</span>
          <span className="brand-text">TravelAI</span>
        </Link>
        <div className="nav-actions">
          {extra}
          {user ? (
            <div id="navAuth">
              <span className="nav-user">{user.fullName}</span>
              <Link href="/trips" className="nav-link">My trips</Link>
              <button type="button" className="nav-link-btn" onClick={logout}>Sign out</button>
            </div>
          ) : (
            <Link href="/login" className="nav-link">Sign in</Link>
          )}
          <button className="theme-toggle" onClick={toggleTheme} aria-label="Toggle theme">
            <span className="theme-icon">{theme === 'dark' ? '☀️' : '🌙'}</span>
          </button>
        </div>
      </div>
    </nav>
  );
}
