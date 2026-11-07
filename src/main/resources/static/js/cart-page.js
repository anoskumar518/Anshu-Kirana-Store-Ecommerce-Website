/**
 * Anshu Kirana Store - Cart Page Presentation Logic
 */

let currentCart = null;

document.addEventListener('DOMContentLoaded', async () => {
  await loadAndRenderCart();

  window.addEventListener('languageChanged', () => {
    if (currentCart) renderCart();
  });
});

async function loadAndRenderCart() {
  currentCart = await Cart.getCart();
  renderCart();
}

function renderCart() {
  const meterBox = document.getElementById('free-delivery-meter-box');
  const layout = document.getElementById('cart-layout-container');
  if (!layout) return;

  if (!currentCart || !currentCart.items || currentCart.items.length === 0) {
    if (meterBox) meterBox.style.display = 'none';
    layout.innerHTML = `
      <div class="col-12 text-center py-5">
        <div class="p-5 bg-white rounded-4 shadow-sm border">
          <i class="bi bi-cart-x fs-1 text-muted"></i>
          <h4 class="fw-bold mt-3" data-i18n="cartEmpty">Your grocery cart is empty</h4>
          <p class="text-muted" data-i18n="cartEmptyDesc">Browse our fresh catalog and add essentials to your basket.</p>
          <a href="/products.html" class="btn btn-primary-kirana btn-lg px-4 mt-2" data-i18n="startShopping">
            <i class="bi bi-shop me-2"></i>Start Shopping
          </a>
        </div>
      </div>
    `;
    return;
  }

  // Render Delivery Meter
  if (meterBox) {
    meterBox.style.display = 'block';
    const subtotal = currentCart.subtotal || 0;
    const threshold = 299;
    const pct = Math.min(100, Math.round((subtotal / threshold) * 100));

    if (subtotal >= threshold) {
      meterBox.innerHTML = `
        <div class="d-flex align-items-center gap-2 text-success fw-bold">
          <i class="bi bi-patch-check-fill fs-5"></i>
          <span>Congratulations! You have unlocked FREE Village Doorstep Delivery!</span>
        </div>
      `;
    } else {
      const diff = threshold - subtotal;
      meterBox.innerHTML = `
        <div class="d-flex justify-content-between small fw-bold mb-1">
          <span><i class="bi bi-truck text-warning me-1"></i>Add ₹${diff} more for FREE Village Delivery</span>
          <span>${pct}%</span>
        </div>
        <div class="progress" style="height: 8px;">
          <div class="progress-bar bg-success" role="progressbar" style="width: ${pct}%"></div>
        </div>
      `;
    }
  }

  // Render Items List & Summary Sidebar
  layout.innerHTML = `
    <!-- Items List -->
    <div class="col-lg-8">
      <div class="card border-0 shadow-sm rounded-4 p-3 bg-white mb-3">
        <div class="table-responsive">
          <table class="table align-middle mb-0">
            <thead class="text-secondary small text-uppercase">
              <tr>
                <th scope="col">Product</th>
                <th scope="col">Price</th>
                <th scope="col" class="text-center">Quantity</th>
                <th scope="col" class="text-end">Total</th>
                <th scope="col"></th>
              </tr>
            </thead>
            <tbody>
              ${currentCart.items.map(item => {
                const name = I18N.currentLang === 'te' ? (item.productNameTe || item.productNameEn) : item.productNameEn;
                return `
                  <tr>
                    <td>
                      <div class="d-flex align-items-center gap-3">
                        <img src="${item.imageUrl || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=100'}"
                             alt="${name}" class="rounded-2" style="width: 54px; height: 54px; object-fit: contain; background: #f8fafc;">
                        <div>
                          <a href="/product-detail.html?id=${item.productId}" class="fw-bold text-dark text-decoration-none small d-block">
                            ${name}
                          </a>
                          <span class="badge bg-light text-secondary border small">${item.unitName}</span>
                        </div>
                      </div>
                    </td>
                    <td class="fw-semibold">₹${item.unitPrice}</td>
                    <td class="text-center">
                      <div class="qty-selector">
                        <button class="qty-btn" onclick="modifyCartQty(${item.id}, ${item.variantId}, ${item.quantity - 1})"><i class="bi bi-dash"></i></button>
                        <input type="text" class="qty-input" value="${item.quantity}" readonly>
                        <button class="qty-btn" onclick="modifyCartQty(${item.id}, ${item.variantId}, ${item.quantity + 1})"><i class="bi bi-plus"></i></button>
                      </div>
                    </td>
                    <td class="text-end fw-bold fs-6">₹${item.itemTotal || (item.unitPrice * item.quantity)}</td>
                    <td class="text-end">
                      <button class="btn btn-sm btn-link text-danger p-0" onclick="removeCartItem(${item.id}, ${item.variantId})" title="Remove">
                        <i class="bi bi-trash3 fs-6"></i>
                      </button>
                    </td>
                  </tr>
                `;
              }).join('')}
            </tbody>
          </table>
        </div>
      </div>
      <a href="/products.html" class="text-success fw-bold text-decoration-none small">
        <i class="bi bi-arrow-left me-1"></i> Add More Groceries
      </a>
    </div>

    <!-- Order Summary Card -->
    <div class="col-lg-4">
      <div class="card border-0 shadow-sm rounded-4 p-4 bg-white sticky-top" style="top: 85px;">
        <h5 class="fw-bold mb-3 border-bottom pb-2">Order Bill Summary</h5>

        <div class="d-flex justify-content-between mb-2">
          <span class="text-muted" data-i18n="subtotal">Subtotal</span>
          <span class="fw-bold">₹${currentCart.subtotal || 0}</span>
        </div>

        <div class="d-flex justify-content-between mb-2">
          <span class="text-muted" data-i18n="deliveryFee">Village Delivery Charge</span>
          <span class="fw-bold ${currentCart.deliveryCharge === 0 ? 'text-success' : ''}">
            ${currentCart.deliveryCharge === 0 ? '<span class="badge bg-success-subtle text-success">FREE</span>' : '₹' + currentCart.deliveryCharge}
          </span>
        </div>

        ${currentCart.discountAmount && currentCart.discountAmount > 0 ? `
          <div class="d-flex justify-content-between mb-2 text-success">
            <span data-i18n="discount">Discount</span>
            <span class="fw-bold">-₹${currentCart.discountAmount}</span>
          </div>
        ` : ''}

        <hr class="my-3">

        <div class="d-flex justify-content-between fs-5 fw-bold mb-4">
          <span data-i18n="grandTotal">Final Payable Amount</span>
          <span class="text-success">₹${currentCart.grandTotal || currentCart.subtotal}</span>
        </div>

        <a href="/checkout.html" class="btn btn-primary-kirana btn-lg w-100 py-3 fw-bold d-flex align-items-center justify-content-center gap-2" data-i18n="checkout">
          <span>Proceed to Checkout</span> <i class="bi bi-arrow-right"></i>
        </a>

        <div class="mt-3 text-center small text-muted">
          <i class="bi bi-cash text-success me-1"></i> Cash on Delivery available at your doorstep
        </div>
      </div>
    </div>
  `;
}

async function modifyCartQty(cartItemId, variantId, newQty) {
  try {
    currentCart = await Cart.updateQuantity(cartItemId, variantId, newQty);
    renderCart();
  } catch (err) {
    console.error(err);
  }
}

async function removeCartItem(cartItemId, variantId) {
  try {
    currentCart = await Cart.removeItem(cartItemId, variantId);
    renderCart();
  } catch (err) {
    console.error(err);
  }
}
