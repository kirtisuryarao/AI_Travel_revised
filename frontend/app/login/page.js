'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Nav from '../../components/Nav';
import { apiFetch, saveAuth } from '../../lib/api';

export default function LoginPage() {
  const router = useRouter();
  const [tab, setTab] = useState('login');
  const [error, setError] = useState('');

  async function onLogin(event) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    setError('');
    try {
      const auth = await apiFetch('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({
          email: form.get('email'),
          password: form.get('password')
        })
      });
      saveAuth(auth);
      router.push('/');
    } catch (err) {
      setError(err.message);
    }
  }

  async function onRegister(event) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    setError('');
    try {
      const auth = await apiFetch('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify({
          fullName: form.get('fullName'),
          email: form.get('email'),
          password: form.get('password')
        })
      });
      saveAuth(auth);
      router.push('/');
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <>
      <Nav />
      <main className="auth-shell">
        <section className="auth-card">
          <p className="auth-eyebrow">Account</p>
          <h1>Welcome back</h1>
          <p className="auth-subtitle">Sign in to save trips and view your itineraries.</p>
          <div className="auth-tabs">
            <button type="button" className={`auth-tab ${tab === 'login' ? 'active' : ''}`} onClick={() => setTab('login')}>Sign in</button>
            <button type="button" className={`auth-tab ${tab === 'register' ? 'active' : ''}`} onClick={() => setTab('register')}>Create account</button>
          </div>

          {tab === 'login' ? (
            <form className="auth-form" onSubmit={onLogin}>
              <label>Email
                <input type="email" name="email" required placeholder="you@example.com" />
              </label>
              <label>Password
                <input type="password" name="password" required placeholder="••••••••" />
              </label>
              <button type="submit" className="cta-button full-width">Sign in</button>
            </form>
          ) : (
            <form className="auth-form" onSubmit={onRegister}>
              <label>Full name
                <input type="text" name="fullName" required placeholder="Alex Rivera" />
              </label>
              <label>Email
                <input type="email" name="email" required placeholder="you@example.com" />
              </label>
              <label>Password
                <input type="password" name="password" required minLength={6} placeholder="At least 6 characters" />
              </label>
              <button type="submit" className="cta-button full-width">Create account</button>
            </form>
          )}

          <p className="auth-message" role="status">{error}</p>
          <div className="auth-demo">
            <p>Demo accounts</p>
            <code>demo@travelai.com / demo123</code>
            <code>admin@travelai.com / admin123</code>
          </div>
        </section>
      </main>
    </>
  );
}
