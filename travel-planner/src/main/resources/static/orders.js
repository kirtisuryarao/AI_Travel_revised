const STATUS_FLOW = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED'];

function statusClass(status) {
    return `status-pill status-${status.toLowerCase()}`;
}

function renderOrder(order) {
    const items = order.items.map(item => `
        <li>
            <span>${item.productName}</span>
            <span>×${item.quantity}</span>
            <span>₹${Number(item.lineTotal).toLocaleString('en-IN')}</span>
        </li>
    `).join('');

    const adminControls = isAdmin() ? `
        <div class="order-admin">
            <label>Update status
                <select class="status-select" data-order-id="${order.id}">
                    ${STATUS_FLOW.concat(['CANCELLED']).map(s => `
                        <option value="${s}" ${order.status === s ? 'selected' : ''}>${s}</option>
                    `).join('')}
                </select>
            </label>
            <button type="button" class="secondary-btn save-status" data-order-id="${order.id}">Save</button>
        </div>
    ` : '';

    return `
        <article class="order-card">
            <div class="order-card-head">
                <div>
                    <h3>Order #${order.id}</h3>
                    <p class="muted">${new Date(order.createdAt).toLocaleString()}</p>
                </div>
                <span class="${statusClass(order.status)}">${order.status}</span>
            </div>
            <p class="muted">Customer: ${order.userEmail}</p>
            <ul class="order-items">${items}</ul>
            <p class="order-total">Total: <strong>₹${Number(order.totalAmount).toLocaleString('en-IN')}</strong></p>
            ${adminControls}
        </article>
    `;
}

async function loadOrders() {
    const container = document.getElementById('ordersList');

    if (!isLoggedIn()) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const orders = await apiFetch('/api/orders');
        if (!orders.length) {
            container.innerHTML = '<p class="muted">No orders yet. <a href="/products.html">Browse products</a></p>';
            return;
        }

        container.innerHTML = orders.map(renderOrder).join('');

        container.querySelectorAll('.save-status').forEach(btn => {
            btn.addEventListener('click', async () => {
                const orderId = btn.dataset.orderId;
                const select = container.querySelector(`.status-select[data-order-id="${orderId}"]`);
                try {
                    await apiFetch(`/api/orders/${orderId}/status`, {
                        method: 'PATCH',
                        body: JSON.stringify({ status: select.value })
                    });
                    loadOrders();
                } catch (err) {
                    alert(err.message);
                }
            });
        });
    } catch (err) {
        container.innerHTML = `<p class="muted error-text">${err.message}</p>`;
    }
}

loadOrders();
