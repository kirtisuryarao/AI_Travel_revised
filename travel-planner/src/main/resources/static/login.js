const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const authMessage = document.getElementById('authMessage');
const tabs = document.querySelectorAll('.auth-tab');

tabs.forEach(tab => {
    tab.addEventListener('click', () => {
        tabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        const isLogin = tab.dataset.tab === 'login';
        loginForm.classList.toggle('hidden', !isLogin);
        registerForm.classList.toggle('hidden', isLogin);
        authMessage.textContent = '';
    });
});

function showMessage(text, isError = false) {
    authMessage.textContent = text;
    authMessage.className = `auth-message ${isError ? 'error' : 'success'}`;
}

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = new FormData(loginForm);
    try {
        const auth = await apiFetch('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({
                email: form.get('email'),
                password: form.get('password')
            })
        });
        saveAuth(auth);
        window.location.href = sessionStorage.getItem('travelData') ? '/itinerary.html' : '/products.html';
    } catch (err) {
        showMessage(err.message, true);
    }
});

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = new FormData(registerForm);
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
        window.location.href = sessionStorage.getItem('travelData') ? '/itinerary.html' : '/products.html';
    } catch (err) {
        showMessage(err.message, true);
    }
});

if (isLoggedIn()) {
    window.location.href = '/products.html';
}
