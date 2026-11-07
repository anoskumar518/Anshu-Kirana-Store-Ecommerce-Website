/**
 * Anshu Kirana Store - Checkout Logic
 */

let checkoutCart = null;

document.addEventListener('DOMContentLoaded', async () => {
  checkoutCart = await Cart.getCart();

  if (!checkoutCart || !checkoutCart.items || checkoutCart.items.length === 0) {
    UI.showToast('Your cart is empty. Please add items to checkout.', 'warning');
    setTimeout(() => {
      window.location.href = '/products.html';
    }, 1200);
    return;
  }

  // Pre-fill user data if logged in
  if (Auth.isLoggedIn()) {
    const user = Auth.getUser();
    if (user) {
      const nameInput = document.getElementById('cust-name');
      const phoneInput = document.getElementById('cust-phone');
      if (nameInput && user.fullName) nameInput.value = user.fullName;
      if (phoneInput && user.phone) phoneInput.value = user.phone;
    }
  }

  // Set default village
  const villageSelect = document.getElementById('cust-village');
  const storedVillage = UI.getCurrentVillage();
  if (villageSelect && storedVillage) {
    let matched = false;
    for (let opt of villageSelect.options) {
      if (opt.value === storedVillage) {
        opt.selected = true;
        matched = true;
        break;
      }
    }
    if (!matched) {
      villageSelect.value = 'OTHER';
      onVillageDropdownChange('OTHER');
      const customInput = document.getElementById('cust-custom-village');
      if (customInput) customInput.value = storedVillage;
    }
  }

  renderCheckoutSummary();

  window.addEventListener('languageChanged', () => {
    renderCheckoutSummary();
  });
});

function onVillageDropdownChange(val) {
  const customInput = document.getElementById('cust-custom-village');
  if (customInput) {
    customInput.style.display = val === 'OTHER' ? 'block' : 'none';
    if (val === 'OTHER') customInput.focus();
  }
}

function renderCheckoutSummary() {
  const itemsContainer = document.getElementById('checkout-items-preview');
  const subtotalEl = document.getElementById('checkout-subtotal');
  const deliveryEl = document.getElementById('checkout-delivery');
  const totalEl = document.getElementById('checkout-total');

  if (!checkoutCart || !itemsContainer) return;

  itemsContainer.innerHTML = checkoutCart.items.map(i => {
    const name = I18N.currentLang === 'te' ? (i.productNameTe || i.productNameEn) : i.productNameEn;
    return `
      <div class="d-flex justify-content-between align-items-center small py-1 border-bottom">
        <div>
          <span class="fw-bold">${name}</span>
          <span class="text-muted">(${i.unitName}) x ${i.quantity}</span>
        </div>
        <span class="fw-semibold">₹${i.itemTotal || (i.unitPrice * i.quantity)}</span>
      </div>
    `;
  }).join('');

  if (subtotalEl) subtotalEl.textContent = `₹${checkoutCart.subtotal || 0}`;
  if (deliveryEl) {
    deliveryEl.textContent = checkoutCart.deliveryCharge === 0 ? 'FREE' : `₹${checkoutCart.deliveryCharge}`;
  }
  if (totalEl) totalEl.textContent = `₹${checkoutCart.grandTotal || checkoutCart.subtotal}`;
}

async function handlePlaceOrder(e) {
  e.preventDefault();

  const submitBtn = document.getElementById('place-order-submit-btn');
  const origBtnText = submitBtn.innerHTML;

  const name = document.getElementById('cust-name').value.trim();
  const phone = document.getElementById('cust-phone').value.trim();
  const villageSelect = document.getElementById('cust-village').value;
  let village = villageSelect;

  if (villageSelect === 'OTHER') {
    village = document.getElementById('cust-custom-village').value.trim();
    if (!village) {
      UI.showToast('Please type your village name', 'error');
      return;
    }
  }

  const street = document.getElementById('cust-street').value.trim();
  const landmark = document.getElementById('cust-landmark').value.trim();
  const instructions = document.getElementById('cust-instructions').value.trim();
  const pincode = document.getElementById('cust-pincode').value.trim() || '518001';
  const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked')?.value || 'COD';

  if (!phone || phone.length !== 10) {
    UI.showToast('Please enter a valid 10-digit mobile number', 'error');
    return;
  }

  submitBtn.disabled = true;
  submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>Placing your order...`;

  try {
    // If not logged in, auto-login or register seamlessly for the village customer
    if (!Auth.isLoggedIn()) {
      try {
        await Auth.register({
          fullName: name,
          phone: phone,
          password: 'village' + phone.substring(6),
          preferredLanguage: I18N.currentLang
        });
      } catch (regErr) {
        // If user already exists, try logging in
        try {
          await Auth.login(phone, 'village' + phone.substring(6));
        } catch {
          // If custom password was set previously, prompt to login
          UI.showToast('Mobile number registered. Please log in with your password to continue.', 'warning');
          setTimeout(() => { window.location.href = '/login.html?redirect=checkout'; }, 1500);
          return;
        }
      }

      // Sync any guest cart items to user's backend cart
      const guestCart = Cart.getGuestCart();
      if (guestCart.items && guestCart.items.length > 0) {
        for (let item of guestCart.items) {
          try {
            await API.post('/api/cart/items', { variantId: item.variantId, quantity: item.quantity });
          } catch (e) {
            console.warn('Item sync warning:', e);
          }
        }
        localStorage.removeItem('aks_guest_cart');
      }
    }

    // Place the order
    const orderPayload = {
      customerName: name,
      customerPhone: phone,
      deliveryVillage: village,
      streetArea: street,
      landmark: landmark,
      pincode: pincode,
      deliveryInstructions: instructions,
      paymentMethod: paymentMethod
    };

    const res = await API.post('/api/orders', orderPayload);
    if (res.success && res.data) {
      // Clear client cart
      Cart.setGuestCart({ items: [], totalItemsCount: 0, subtotal: 0 });
      Cart.updateCartBadges(0);

      UI.showToast('Order confirmed successfully!', 'success');
      setTimeout(() => {
        window.location.href = `/order-confirmation.html?orderNumber=${res.data.orderNumber}`;
      }, 500);
    }
  } catch (err) {
    console.error(err);
    UI.showToast(err.message || 'Failed to place order. Please try again.', 'error');
    submitBtn.disabled = false;
    submitBtn.innerHTML = origBtnText;
  }
}
