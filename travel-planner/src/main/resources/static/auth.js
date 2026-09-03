const AUTH_TOKEN_KEY = 'travelAuthToken';
const AUTH_USER_KEY = 'travelAuthUser';

function getAuthToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY);
}

function getAuthUser() {
    const raw = localStorage.getItem(AUTH_USER_KEY);
    return raw ? JSON.parse(raw) : null;
}

function saveAuth(auth) {
    localStorage.setItem(AUTH_TOKEN_KEY, auth.token);
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify({
        email: auth.email,
        fullName: auth.fullName,
        roles: auth.roles || []
    }));
}

function clearAuth() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_USER_KEY);
}

function isLoggedIn() {
    return Boolean(getAuthToken());
}

function isAdmin() {
    const user = getAuthUser();
    return user?.roles?.includes('ADMIN');
}

async function apiFetch(url, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    const token = getAuthToken();
    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(url, { ...options, headers });
    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
        throw new Error(data.error || 'Request failed');
    }

    return data;
}

function updateNavAuth() {
    const navAuth = document.getElementById('navAuth');
    if (!navAuth) return;

    const user = getAuthUser();
    if (user) {
        navAuth.innerHTML = `
            <span class="nav-user">${user.fullName}</span>
            <a href="/my-trips.html" class="nav-link">My trips</a>
            <a href="/orders.html" class="nav-link">Orders</a>
            <button type="button" class="nav-link-btn" id="logoutBtn">Sign out</button>
        `;
        document.getElementById('logoutBtn')?.addEventListener('click', () => {
            clearAuth();
            window.location.href = '/login.html';
        });
    } else {
        navAuth.innerHTML = `<a href="/login.html" class="nav-link">Sign in</a>`;
    }
}

function initThemeToggle() {
    const themeToggle = document.getElementById('themeToggle');
    const html = document.documentElement;
    if (!themeToggle) return;

    const savedTheme = localStorage.getItem('theme') || 'light';
    if (savedTheme === 'dark') {
        html.classList.add('dark-mode');
        themeToggle.textContent = '☀️';
    }

    themeToggle.addEventListener('click', () => {
        const isDark = html.classList.toggle('dark-mode');
        localStorage.setItem('theme', isDark ? 'dark' : 'light');
        themeToggle.textContent = isDark ? '☀️' : '🌙';
    });
}

document.addEventListener('DOMContentLoaded', () => {
    initThemeToggle();
    updateNavAuth();
});
