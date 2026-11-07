/**
 * Anshu Kirana Store - Shopping Cart Client Manager
 * Supports backend sync for authenticated users and local fallback for guest browsing.
 */
const Cart = {
  getGuestCart() {
    try {
      return JSON.parse(localStorage.getItem('aks_guest_cart')) || { items: [], totalItemsCount: 0, subtotal: 0 };
    } catch {
      return { items: [], totalItemsCount: 0, subtotal: 0 };
    }
  },

  setGuestCart(cart) {
    localStorage.setItem('aks_guest_cart', JSON.stringify(cart));
    this.updateCartBadges(cart.totalItemsCount || 0);
  },

  async getCart() {
    if (Auth.isLoggedIn()) {
      try {
        const res = await API.get('/api/cart');
        if (res.success && res.data) {
          this.updateCartBadges(res.data.totalItemsCount);
          return res.data;
        }
      } catch (err) {
        console.warn('Could not fetch backend cart, falling back to guest cart:', err);
      }
    }
    const guestCart = this.getGuestCart();
    this.updateCartBadges(guestCart.totalItemsCount);
    return guestCart;
  },

  async addToCart(variantId, quantity = 1, productDetails = null) {
    if (Auth.isLoggedIn()) {
      try {
        const res = await API.post('/api/cart/items', { variantId, quantity });
        if (res.success && res.data) {
          this.updateCartBadges(res.data.totalItemsCount);
          UI.showToast(I18N.currentLang === 'te' ? 'సరుకు బుట్టలో చేర్చబడింది!' : 'Item added to cart!');
          return res.data;
        }
      } catch (err) {
        UI.showToast(err.message || 'Could not add to cart', 'error');
        throw err;
      }
    } else {
      // Guest mode
      const guestCart = this.getGuestCart();
      const existing = guestCart.items.find(i => i.variantId === variantId);

      if (existing) {
        existing.quantity += quantity;
        existing.itemTotal = existing.quantity * (existing.unitPrice || 0);
      } else if (productDetails) {
        guestCart.items.push({
          id: Date.now(),
          variantId,
          productId: productDetails.productId,
          productNameEn: productDetails.nameEn,
          productNameTe: productDetails.nameTe,
          brand: productDetails.brand,
          unitName: productDetails.unitName,
          imageUrl: productDetails.imageUrl,
          unitPrice: productDetails.sellingPrice,
          mrp: productDetails.mrp,
          quantity: quantity,
          itemTotal: quantity * productDetails.sellingPrice,
          inStock: true
        });
      }

      this.recalculateGuestCart(guestCart);
      this.setGuestCart(guestCart);
      UI.showToast(I18N.currentLang === 'te' ? 'సరుకు బుట్టలో చేర్చబడింది!' : 'Item added to cart!');
      return guestCart;
    }
  },

  async updateQuantity(cartItemId, variantId, quantity) {
    if (Auth.isLoggedIn()) {
      try {
        const res = await API.put(`/api/cart/items/${cartItemId}`, { quantity });
        if (res.success && res.data) {
          this.updateCartBadges(res.data.totalItemsCount);
          return res.data;
        }
      } catch (err) {
        UI.showToast(err.message || 'Could not update cart', 'error');
        throw err;
      }
    } else {
      const guestCart = this.getGuestCart();
      const index = guestCart.items.findIndex(i => i.id === cartItemId || i.variantId === variantId);
      if (index !== -1) {
        if (quantity <= 0) {
          guestCart.items.splice(index, 1);
        } else {
          guestCart.items[index].quantity = quantity;
          guestCart.items[index].itemTotal = quantity * guestCart.items[index].unitPrice;
        }
      }
      this.recalculateGuestCart(guestCart);
      this.setGuestCart(guestCart);
      return guestCart;
    }
  },

  async removeItem(cartItemId, variantId) {
    if (Auth.isLoggedIn()) {
      try {
        const res = await API.delete(`/api/cart/items/${cartItemId}`);
        if (res.success && res.data) {
          this.updateCartBadges(res.data.totalItemsCount);
          UI.showToast('Item removed from cart', 'info');
          return res.data;
        }
      } catch (err) {
        UI.showToast(err.message, 'error');
      }
    } else {
      return this.updateQuantity(cartItemId, variantId, 0);
    }
  },

  recalculateGuestCart(cart) {
    let totalCount = 0;
    let subtotal = 0;
    cart.items.forEach(item => {
      totalCount += item.quantity;
      subtotal += item.itemTotal;
    });

    cart.totalItemsCount = totalCount;
    cart.subtotal = subtotal;
    cart.freeDeliveryThreshold = 299;

    if (subtotal === 0) {
      cart.deliveryCharge = 0;
      cart.grandTotal = 0;
      cart.isFreeDelivery = false;
      cart.amountNeededForFreeDelivery = 299;
    } else if (subtotal >= 299) {
      cart.deliveryCharge = 0;
      cart.grandTotal = subtotal;
      cart.isFreeDelivery = true;
      cart.amountNeededForFreeDelivery = 0;
    } else {
      cart.deliveryCharge = 20;
      cart.grandTotal = subtotal + 20;
      cart.isFreeDelivery = false;
      cart.amountNeededForFreeDelivery = 299 - subtotal;
    }
  },

  updateCartBadges(count = 0) {
    document.querySelectorAll('.cart-count-badge').forEach(el => {
      el.textContent = count;
      el.style.display = count > 0 ? 'inline-block' : 'none';
    });
  }
};

document.addEventListener('DOMContentLoaded', () => {
  Cart.getCart();
});
