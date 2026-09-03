let products = [];
const cart = new Map();

async function loadProducts() {
    const grid = document.getElementById('productGrid');
    try {
        products = await apiFetch('/api/products');
        if (!products.length) {
            grid.innerHTML = '<p class="muted">No products available yet.</p>';
            return;
        }

        grid.innerHTML = products.map(product => `
            <article class="product-card" data-id="${product.id}">
                <div class="product-card-top">
                    <span class="product-category">${product.category}</span>
                    <h3>${product.name}</h3>
                    <p>${product.description || ''}</p>
                </div>
                <div class="product-card-bottom">
                    <div>
                        <strong>₹${Number(product.price).toLocaleString('en-IN')}</strong>
                        <span class="muted">${product.stockQuantity} in stock</span>
                    </div>
                    <div class="product-actions">
                        <input type="number" min="1" max="${product.stockQuantity}" value="1" class="qty-input" aria-label="Quantity">
                        <button type="button" class="secondary-btn add-btn">Add</button>
                    </div>
                </div>
            </article>
        `).join('');

        grid.querySelectorAll('.add-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const card = btn.closest('.product-card');
                const id = Number(card.dataset.id);
                const qty = Number(card.querySelector('.qty-input').value) || 1;
                const current = cart.get(id) || 0;
                cart.set(id, current + qty);
                updateCheckoutButton();
                btn.textContent = 'Added';
                setTimeout(() => { btn.textContent = 'Add'; }, 900);
            });
        });
    } catch (err) {
        grid.innerHTML = `<p class="muted error-text">${err.message}</p>`;
    }
}

function updateCheckoutButton() {
    const btn = document.getElementById('checkoutBtn');
    const count = [...cart.values()].reduce((sum, qty) => sum + qty, 0);
    btn.textContent = `Place order (${count})`;
    btn.disabled = count === 0;
}

document.getElementById('checkoutBtn').addEventListener('click', async () => {
    if (!isLoggedIn()) {
        window.location.href = '/login.html';
        return;
    }

    const items = [...cart.entries()].map(([productId, quantity]) => ({ productId, quantity }));
    try {
        await apiFetch('/api/orders', {
            method: 'POST',
            body: JSON.stringify({ items })
        });
        cart.clear();
        updateCheckoutButton();
        window.location.href = '/orders.html';
    } catch (err) {
        alert(err.message);
    }
});

loadProducts();
updateCheckoutButton();
