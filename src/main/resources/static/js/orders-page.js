/**
 * Anshu Kirana Store - Customer Orders & Tracking Logic
 */

let myOrders = [];
let pendingCancelOrderId = null;

document.addEventListener('DOMContentLoaded', async () => {
  if (!Auth.isLoggedIn()) {
    const container = document.getElementById('orders-list-container');
    if (container) {
      container.innerHTML = `
        <div class="card border-0 shadow-sm rounded-4 p-5 text-center bg-white">
          <i class="bi bi-person-lock fs-1 text-muted"></i>
          <h4 class="fw-bold mt-3">Please log in to view your orders</h4>
          <p class="text-muted">Enter your registered mobile number to see all your past village deliveries.</p>
          <div>
            <a href="/login.html?redirect=orders" class="btn btn-primary-kirana btn-lg px-4">Login Now</a>
          </div>
        </div>
      `;
    }
    return;
  }

  await loadOrders();

  const confirmCancelBtn = document.getElementById('confirm-cancel-btn');
  if (confirmCancelBtn) {
    confirmCancelBtn.addEventListener('click', executeOrderCancel);
  }

  window.addEventListener('languageChanged', () => {
    if (myOrders.length > 0) renderOrders();
  });
});

async function loadOrders() {
  const container = document.getElementById('orders-list-container');
  try {
    const res = await API.get('/api/orders/my-orders');
    if (res.success && res.data) {
      myOrders = res.data;
      renderOrders();
    }
  } catch (err) {
    console.error(err);
    if (container) {
      container.innerHTML = `<div class="alert alert-danger">Could not load orders: ${err.message}</div>`;
    }
  }
}

function renderOrders() {
  const container = document.getElementById('orders-list-container');
  if (!container) return;

  if (myOrders.length === 0) {
    container.innerHTML = `
      <div class="card border-0 shadow-sm rounded-4 p-5 text-center bg-white">
        <i class="bi bi-bag-x fs-1 text-muted"></i>
        <h4 class="fw-bold mt-3">No orders placed yet</h4>
        <p class="text-muted">You haven't ordered any groceries yet. Start shopping to get fresh village delivery.</p>
        <div>
          <a href="/products.html" class="btn btn-primary-kirana btn-lg px-4" data-i18n="startShopping">Start Shopping</a>
        </div>
      </div>
    `;
    return;
  }

  container.innerHTML = myOrders.map(order => {
    const status = order.orderStatus;
    const canCancel = (status === 'PLACED' || status === 'CONFIRMED');

    return `
      <div class="card border-0 shadow-sm rounded-4 bg-white overflow-hidden" id="order-card-${order.id}">
        <!-- Order Header -->
        <div class="card-header bg-light border-bottom p-3 d-flex flex-wrap justify-content-between align-items-center gap-2">
          <div>
            <span class="text-muted small">Order #: </span>
            <strong class="font-monospace fs-6 text-dark">${order.orderNumber}</strong>
            <span class="mx-2 text-muted">•</span>
            <span class="text-muted small">${UI.formatDate(order.createdAt)}</span>
          </div>
          <div class="d-flex align-items-center gap-2">
            <span class="badge-status badge-${status}">${formatStatusLabel(status)}</span>
            <span class="fw-bold fs-6 text-success">₹${order.totalAmount} (COD)</span>
          </div>
        </div>

        <div class="card-body p-4">
          <!-- Visual Status Timeline Milestone Tracker -->
          ${renderTimelineTrack(status)}

          <!-- Order Items Breakdown -->
          <div class="p-3 bg-light rounded-3 my-3">
            <h6 class="fw-bold small text-muted text-uppercase mb-2">Items in this Delivery</h6>
            <div class="d-flex flex-column gap-2">
              ${order.items.map(item => `
                <div class="d-flex justify-content-between align-items-center small py-1 border-bottom">
                  <div>
                    <span class="fw-bold">${item.productName}</span>
                    <span class="text-muted">(${item.unitName}) x ${item.quantity}</span>
                  </div>
                  <span class="fw-semibold">₹${item.subtotal}</span>
                </div>
              `).join('')}
            </div>

            <div class="d-flex justify-content-between pt-2 mt-2 border-top small">
              <span class="text-muted">Delivery Address:</span>
              <span class="fw-semibold text-end">${order.deliveryVillage}, ${order.deliveryAddress}</span>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="d-flex flex-wrap justify-content-end gap-2 mt-3">
            ${canCancel ? `
              <button class="btn btn-outline-danger btn-sm" onclick="openCancelModal(${order.id})">
                <i class="bi bi-x-circle me-1"></i> <span data-i18n="cancelOrder">Cancel Order</span>
              </button>
            ` : ''}

            <button class="btn btn-primary-kirana btn-sm" onclick="handleReorder(${order.id})">
              <i class="bi bi-arrow-repeat me-1"></i> <span data-i18n="reorder">Order Again</span>
            </button>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function renderTimelineTrack(status) {
  if (status === 'CANCELLED') {
    return `
      <div class="alert alert-danger py-2 px-3 small rounded-3 my-2 d-flex align-items-center gap-2">
        <i class="bi bi-x-octagon-fill fs-5"></i>
        <span>This order was <strong>CANCELLED</strong>. Stock has been returned to store.</span>
      </div>
    `;
  }

  const steps = [
    { key: 'PLACED', title: 'Placed', icon: 'bi-receipt' },
    { key: 'CONFIRMED', title: 'Confirmed', icon: 'bi-check2-circle' },
    { key: 'PACKING', title: 'Packing', icon: 'bi-box-seam' },
    { key: 'OUT_FOR_DELIVERY', title: 'On Route', icon: 'bi-bicycle' },
    { key: 'DELIVERED', title: 'Delivered', icon: 'bi-house-check' }
  ];

  const orderHierarchy = ['PLACED', 'CONFIRMED', 'PACKING', 'OUT_FOR_DELIVERY', 'DELIVERED'];
  const currentIndex = orderHierarchy.indexOf(status);

  return `
    <div class="timeline-track">
      ${steps.map((st, idx) => {
        const isDone = idx <= currentIndex;
        const isActive = idx === currentIndex;
        const cls = isDone ? (isActive ? 'active' : 'completed') : '';
        return `
          <div class="timeline-step ${cls}">
            <div class="timeline-icon">
              <i class="bi ${st.icon}"></i>
            </div>
            <div class="timeline-title">${st.title}</div>
          </div>
        `;
      }).join('')}
    </div>
  `;
}

function formatStatusLabel(st) {
  switch (st) {
    case 'PLACED': return I18N.get('placed');
    case 'CONFIRMED': return I18N.get('confirmed');
    case 'PACKING': return I18N.get('packing');
    case 'OUT_FOR_DELIVERY': return I18N.get('outForDelivery');
    case 'DELIVERED': return I18N.get('delivered');
    case 'CANCELLED': return I18N.get('cancelled');
    default: return st;
  }
}

function openCancelModal(orderId) {
  pendingCancelOrderId = orderId;
  const modal = new bootstrap.Modal(document.getElementById('cancelOrderModal'));
  modal.show();
}

async function executeOrderCancel() {
  if (!pendingCancelOrderId) return;
  const reason = document.getElementById('cancel-reason-select').value;

  try {
    const res = await API.put(`/api/orders/${pendingCancelOrderId}/cancel`, { reason });
    if (res.success) {
      UI.showToast('Order cancelled successfully', 'info');
      const modalEl = document.getElementById('cancelOrderModal');
      const modal = bootstrap.Modal.getInstance(modalEl);
      if (modal) modal.hide();
      await loadOrders();
    }
  } catch (err) {
    UI.showToast(err.message, 'error');
  }
}

async function handleReorder(orderId) {
  const order = myOrders.find(o => o.id === orderId);
  if (!order || !order.items) return;

  UI.showToast('Re-adding items to your basket...', 'info');

  for (let item of order.items) {
    if (item.variantId) {
      try {
        await Cart.addToCart(item.variantId, item.quantity);
      } catch (e) {
        console.warn('Reorder item warning:', e);
      }
    }
  }

  UI.showToast('Items added to basket! Opening cart...', 'success');
  setTimeout(() => {
    window.location.href = '/cart.html';
  }, 600);
}
