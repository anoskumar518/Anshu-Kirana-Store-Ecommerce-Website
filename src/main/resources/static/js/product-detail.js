/**
 * Anshu Kirana Store - Product Details Page Logic
 */

let currentProduct = null;
let selectedVariant = null;
let currentQuantity = 1;

document.addEventListener('DOMContentLoaded', async () => {
  const urlParams = new URLSearchParams(window.location.search);
  const productId = urlParams.get('id');

  if (!productId) {
    window.location.href = '/products.html';
    return;
  }

  await loadProductDetails(productId);
  await loadRelatedProducts();

  window.addEventListener('languageChanged', () => {
    if (currentProduct) {
      renderProductDetails();
    }
  });
});

async function loadProductDetails(id) {
  try {
    const res = await API.get(`/api/products/${id}`);
    if (res.success && res.data) {
      currentProduct = res.data;
      selectedVariant = currentProduct.defaultVariant || currentProduct.variants[0];
      renderProductDetails();

      const breadcrumb = document.getElementById('detail-breadcrumb');
      if (breadcrumb) {
        breadcrumb.textContent = I18N.currentLang === 'te' ? currentProduct.nameTe : currentProduct.nameEn;
      }
    }
  } catch (err) {
    console.error(err);
    const container = document.getElementById('product-detail-container');
    if (container) {
      container.innerHTML = `<div class="alert alert-danger">Could not find requested product. <a href="/products.html">Return to Catalog</a></div>`;
    }
  }
}

function renderProductDetails() {
  const container = document.getElementById('product-detail-container');
  if (!container || !currentProduct) return;

  const name = I18N.currentLang === 'te' ? currentProduct.nameTe : currentProduct.nameEn;
  const subName = I18N.currentLang === 'te' ? currentProduct.nameEn : currentProduct.nameTe;
  const desc = I18N.currentLang === 'te' ? (currentProduct.descriptionTe || currentProduct.descriptionEn) : currentProduct.descriptionEn;

  const inStock = selectedVariant.inStock !== false && selectedVariant.stockQuantity > 0;
  const discount = selectedVariant.discountPercent || 0;

  container.innerHTML = `
    <div class="row g-5 align-items-start">
      <!-- Image Column -->
      <div class="col-lg-5 text-center">
        <div class="p-4 bg-white rounded-4 shadow-sm border position-relative">
          ${discount > 0 ? `<span class="product-badge-discount fs-6">${discount}% OFF</span>` : ''}
          <img src="${currentProduct.imageUrl || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=500'}"
               alt="${name}" class="img-fluid rounded-3" style="max-height: 380px; object-fit: contain;">
          <div class="mt-3 text-muted small"><i class="bi bi-shield-check text-success me-1"></i>100% Guaranteed Fresh Village Quality</div>
        </div>
      </div>

      <!-- Details Column -->
      <div class="col-lg-7">
        <div class="product-brand mb-1 text-success fw-bold">${currentProduct.brand} • ${currentProduct.categoryNameEn || 'Grocery'}</div>
        <h2 class="fw-bold mb-1">${name}</h2>
        <h5 class="text-secondary mb-3">${subName}</h5>

        <!-- Price Section -->
        <div class="p-3 bg-white rounded-3 border mb-4">
          <div class="d-flex align-items-baseline gap-3">
            <span class="fs-2 fw-bold text-dark">₹${selectedVariant.sellingPrice}</span>
            ${selectedVariant.mrp && selectedVariant.mrp > selectedVariant.sellingPrice ? `
              <span class="fs-5 text-muted text-decoration-line-through">₹${selectedVariant.mrp}</span>
              <span class="badge bg-success-subtle text-success fs-6 fw-bold">Save ₹${selectedVariant.mrp - selectedVariant.sellingPrice}</span>
            ` : ''}
          </div>
          <small class="text-muted d-block mt-1">Inclusive of all local taxes. Pay cash upon doorstep delivery.</small>
        </div>

        <!-- Available Pack Size Variants -->
        <div class="mb-4">
          <label class="form-label fw-bold text-uppercase small text-muted">Select Pack Size / Quantity:</label>
          <div class="d-flex flex-wrap gap-2">
            ${currentProduct.variants.map(v => `
              <button type="button" class="btn ${v.id === selectedVariant.id ? 'btn-success fw-bold' : 'btn-outline-secondary'} btn-sm px-3 py-2"
                      onclick="selectDetailVariant(${v.id})">
                ${v.unitName} - ₹${v.sellingPrice}
              </button>
            `).join('')}
          </div>
        </div>

        <!-- Stock Availability -->
        <div class="mb-4">
          <span class="badge ${inStock ? (selectedVariant.lowStock ? 'bg-warning text-dark' : 'bg-success') : 'bg-danger'} px-3 py-2">
            <i class="bi ${inStock ? (selectedVariant.lowStock ? 'bi-exclamation-triangle' : 'bi-check-circle') : 'bi-x-circle'} me-1"></i>
            ${inStock ? (selectedVariant.lowStock ? 'Only few packets left in store!' : 'In Stock - Ready for Village Delivery') : 'Temporarily Out of Stock'}
          </span>
        </div>

        <!-- Quantity & Add to Cart -->
        <div class="d-flex align-items-center gap-3 mb-4">
          <div class="qty-selector">
            <button class="qty-btn" onclick="updateDetailQty(-1)"><i class="bi bi-dash"></i></button>
            <input type="text" class="qty-input" id="detail-qty-input" value="${currentQuantity}" readonly>
            <button class="qty-btn" onclick="updateDetailQty(1)"><i class="bi bi-plus"></i></button>
          </div>

          <button class="btn btn-primary-kirana btn-lg px-4 d-flex align-items-center gap-2 ${!inStock ? 'disabled' : ''}"
                  onclick="handleDetailAddToCart()">
            <i class="bi bi-cart-plus fs-5"></i>
            <span>${inStock ? (I18N.currentLang === 'te' ? 'బుట్టలో వేయి' : 'Add to Cart') : 'Out of Stock'}</span>
          </button>
        </div>

        <!-- Description Box -->
        <div class="card border-0 bg-light p-3 rounded-3 mb-3">
          <h6 class="fw-bold mb-2">Product Description</h6>
          <p class="small text-muted mb-0">${desc || 'High quality staple sourced for your family with love and care.'}</p>
        </div>

        <div class="d-flex align-items-center gap-2 small text-muted">
          <i class="bi bi-geo-alt text-danger"></i>
          <span>Delivering today to <strong>${UI.getCurrentVillage()}</strong></span>
        </div>
      </div>
    </div>
  `;
}

function selectDetailVariant(variantId) {
  const v = currentProduct.variants.find(item => item.id === variantId);
  if (v) {
    selectedVariant = v;
    currentQuantity = 1;
    renderProductDetails();
  }
}

function updateDetailQty(delta) {
  const maxStock = selectedVariant.stockQuantity || 1;
  const newQty = currentQuantity + delta;
  if (newQty >= 1 && newQty <= maxStock) {
    currentQuantity = newQty;
    const input = document.getElementById('detail-qty-input');
    if (input) input.value = currentQuantity;
  }
}

async function handleDetailAddToCart() {
  if (!selectedVariant) return;

  const productDetails = {
    productId: currentProduct.id,
    nameEn: currentProduct.nameEn,
    nameTe: currentProduct.nameTe,
    brand: currentProduct.brand,
    unitName: selectedVariant.unitName,
    imageUrl: currentProduct.imageUrl,
    sellingPrice: selectedVariant.sellingPrice,
    mrp: selectedVariant.mrp
  };

  try {
    await Cart.addToCart(selectedVariant.id, currentQuantity, productDetails);
  } catch (err) {
    console.error(err);
  }
}

async function loadRelatedProducts() {
  try {
    const res = await API.get('/api/products/featured');
    if (res.success && res.data) {
      const container = document.getElementById('related-products-grid');
      if (!container) return;

      const items = res.data.filter(p => p.id !== currentProduct?.id).slice(0, 4);
      container.innerHTML = items.map(p => {
        const name = I18N.currentLang === 'te' ? p.nameTe : p.nameEn;
        const v = p.defaultVariant || p.variants[0] || {};
        return `
          <div class="col">
            <div class="product-card h-100">
              <div class="product-img-box">
                <a href="/product-detail.html?id=${p.id}">
                  <img src="${p.imageUrl}" alt="${name}">
                </a>
              </div>
              <div class="product-brand">${p.brand}</div>
              <a href="/product-detail.html?id=${p.id}" class="text-decoration-none">
                <h6 class="product-title">${name}</h6>
              </a>
              <div class="price-box mt-auto">
                <span class="selling-price">₹${v.sellingPrice || 0}</span>
                ${v.mrp ? `<span class="mrp-price">₹${v.mrp}</span>` : ''}
              </div>
              <a href="/product-detail.html?id=${p.id}" class="btn btn-outline-kirana btn-sm w-100 mt-2">View Pack</a>
            </div>
          </div>
        `;
      }).join('');
    }
  } catch (err) {
    console.error(err);
  }
}
