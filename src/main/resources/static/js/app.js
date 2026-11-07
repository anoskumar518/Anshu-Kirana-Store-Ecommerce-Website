/**
 * Anshu Kirana Store - Homepage Logic
 */

let allCategories = [];
let featuredProducts = [];

document.addEventListener('DOMContentLoaded', async () => {
  initWhatsAppLinks();
  initVillageSelector();
  await loadStoreConfiguration();
  await loadCategories();
  await loadFeaturedProducts();

  // Listen for language change to update product and category names dynamically
  window.addEventListener('languageChanged', () => {
    renderCategories();
    renderFeaturedProducts();
  });
});

function initWhatsAppLinks() {
  const defaultMsg = "Namaste Anshu Kirana Store, I would like to place a grocery order.";
  const link = UI.getWhatsappLink(defaultMsg);

  const heroBtn = document.getElementById('hero-whatsapp-btn');
  if (heroBtn) heroBtn.href = link;

  const floatBtn = document.getElementById('floating-whatsapp-btn');
  if (floatBtn) floatBtn.href = link;

  const footerBtn = document.getElementById('footer-whatsapp-btn');
  if (footerBtn) footerBtn.href = link;
}

function initVillageSelector() {
  const currentVillage = UI.getCurrentVillage();
  document.querySelectorAll('.current-village-name').forEach(el => el.textContent = currentVillage);
}

async function loadStoreConfiguration() {
  try {
    const res = await API.get('/api/store/config');
    if (res.success && res.data) {
      const config = res.data;
      const villagesList = document.getElementById('serviceable-villages-list');
      if (villagesList && config.serviceableVillages) {
        villagesList.innerHTML = config.serviceableVillages.map(v => `
          <button type="button" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center py-3" onclick="selectVillage('${v}')">
            <span class="fw-bold"><i class="bi bi-geo-alt me-2 text-danger"></i>${v}</span>
            <span class="badge bg-success-subtle text-success small">Free over ₹${config.freeDeliveryThreshold}</span>
          </button>
        `).join('');
      }
    }
  } catch (err) {
    console.warn('Could not fetch store config:', err);
  }
}

function selectVillage(villageName) {
  UI.setCurrentVillage(villageName);
  const modalEl = document.getElementById('villageSelectModal');
  if (modalEl) {
    const modal = bootstrap.Modal.getInstance(modalEl);
    if (modal) modal.hide();
  }
}

async function loadCategories() {
  try {
    const res = await API.get('/api/categories');
    if (res.success && res.data) {
      allCategories = res.data;
      renderCategories();
      renderHeaderCategories();
    }
  } catch (err) {
    console.error('Error loading categories:', err);
  }
}

function renderHeaderCategories() {
  const headerLinks = document.getElementById('header-category-links');
  if (!headerLinks) return;

  const html = allCategories.slice(0, 8).map(c => {
    const name = I18N.currentLang === 'te' ? c.nameTe : c.nameEn;
    return `
      <a href="/products.html?category=${c.id}" class="text-decoration-none text-secondary small fw-semibold hover-text-success">
        ${name}
      </a>
    `;
  }).join('');

  headerLinks.innerHTML = `
    <a href="/products.html" class="text-decoration-none text-dark fw-bold small"><i class="bi bi-grid me-1 text-success"></i><span data-i18n="exploreProducts">All Groceries</span></a>
    ${html}
  `;
}

function renderCategories() {
  const container = document.getElementById('home-categories-grid');
  if (!container) return;

  container.innerHTML = allCategories.map(c => {
    const name = I18N.currentLang === 'te' ? c.nameTe : c.nameEn;
    const subName = I18N.currentLang === 'te' ? c.nameEn : c.nameTe;
    return `
      <div class="col">
        <a href="/products.html?category=${c.id}" class="category-card">
          <div class="category-img-wrapper">
            <img src="${c.imageUrl || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=200'}" alt="${name}" loading="lazy">
          </div>
          <span class="category-name-en">${name}</span>
          <span class="category-name-te">${subName}</span>
        </a>
      </div>
    `;
  }).join('');
}

async function loadFeaturedProducts() {
  try {
    const res = await API.get('/api/products/featured');
    if (res.success && res.data) {
      featuredProducts = res.data;
      renderFeaturedProducts();
    }
  } catch (err) {
    console.error('Error loading featured products:', err);
  }
}

function renderFeaturedProducts() {
  const container = document.getElementById('featured-products-grid');
  if (!container) return;

  if (featuredProducts.length === 0) {
    container.innerHTML = `<div class="col-12 text-center py-4 text-muted">No featured items found.</div>`;
    return;
  }

  container.innerHTML = featuredProducts.map(p => {
    const name = I18N.currentLang === 'te' ? p.nameTe : p.nameEn;
    const subName = I18N.currentLang === 'te' ? p.nameEn : p.nameTe;
    const variant = p.defaultVariant || (p.variants && p.variants[0]) || {};

    const inStock = variant.inStock !== false && variant.stockQuantity > 0;
    const discount = variant.discountPercent || 0;

    return `
      <div class="col">
        <div class="product-card" id="product-card-${p.id}">
          ${discount > 0 ? `<span class="product-badge-discount">${discount}% OFF</span>` : ''}
          <span class="product-badge-village"><i class="bi bi-shield-check me-1"></i>Pure Quality</span>
          
          <div class="product-img-box">
            <a href="/product-detail.html?id=${p.id}">
              <img src="${p.imageUrl || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=300'}" alt="${name}" loading="lazy">
            </a>
          </div>

          <div class="product-brand">${p.brand || 'Kirana Select'}</div>
          <a href="/product-detail.html?id=${p.id}" class="text-decoration-none">
            <h6 class="product-title">${name}</h6>
          </a>
          <div class="product-title-te">${subName}</div>

          <!-- Variants selector dropdown -->
          ${p.variants && p.variants.length > 1 ? `
            <select class="form-select form-select-sm variant-selector-select" onchange="onHomeVariantChange(${p.id}, this.value)">
              ${p.variants.map(v => `
                <option value="${v.id}" ${v.id === variant.id ? 'selected' : ''}>
                  ${v.unitName} - ₹${v.sellingPrice}
                </option>
              `).join('')}
            </select>
          ` : `
            <div class="small fw-semibold text-secondary mb-2">${variant.unitName || 'Standard Pack'}</div>
          `}

          <!-- Price & Savings -->
          <div class="price-box">
            <span class="selling-price" id="selling-price-${p.id}">₹${variant.sellingPrice || 0}</span>
            ${variant.mrp && variant.mrp > variant.sellingPrice ? `
              <span class="mrp-price" id="mrp-price-${p.id}">₹${variant.mrp}</span>
              <span class="savings-tag" id="savings-${p.id}">Save ₹${variant.mrp - variant.sellingPrice}</span>
            ` : ''}
          </div>

          <!-- Stock Indicator -->
          <div class="stock-indicator ${inStock ? (variant.lowStock ? 'stock-low' : 'stock-in') : 'stock-out'}" id="stock-badge-${p.id}">
            <i class="bi ${inStock ? (variant.lowStock ? 'bi-exclamation-triangle' : 'bi-check2-circle') : 'bi-x-circle'} me-1"></i>
            ${inStock ? (variant.lowStock ? 'Few packs left' : 'In Stock') : 'Out of Stock'}
          </div>

          <!-- Add to Cart Button -->
          <div class="mt-auto">
            <button class="btn btn-primary-kirana w-100 d-flex align-items-center justify-content-center gap-2 ${!inStock ? 'disabled' : ''}"
                    id="add-btn-${p.id}"
                    onclick="handleHomeAddToCart(${p.id})">
              <i class="bi bi-cart-plus"></i>
              <span data-i18n="addToCart">${inStock ? 'Add to Cart' : 'Out of Stock'}</span>
            </button>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function onHomeVariantChange(productId, variantId) {
  const product = featuredProducts.find(p => p.id === productId);
  if (!product) return;

  const variant = product.variants.find(v => v.id == variantId);
  if (!variant) return;

  // Update card DOM
  const spEl = document.getElementById(`selling-price-${productId}`);
  if (spEl) spEl.textContent = `₹${variant.sellingPrice}`;

  const mrpEl = document.getElementById(`mrp-price-${productId}`);
  if (mrpEl) {
    if (variant.mrp && variant.mrp > variant.sellingPrice) {
      mrpEl.textContent = `₹${variant.mrp}`;
      mrpEl.style.display = 'inline';
    } else {
      mrpEl.style.display = 'none';
    }
  }

  const addBtn = document.getElementById(`add-btn-${productId}`);
  const stockBadge = document.getElementById(`stock-badge-${productId}`);
  const inStock = variant.inStock !== false && variant.stockQuantity > 0;

  if (addBtn) {
    if (inStock) {
      addBtn.classList.remove('disabled');
      addBtn.querySelector('span').textContent = I18N.get('addToCart');
    } else {
      addBtn.classList.add('disabled');
      addBtn.querySelector('span').textContent = I18N.get('outOfStock');
    }
  }

  if (stockBadge) {
    stockBadge.className = `stock-indicator ${inStock ? (variant.lowStock ? 'stock-low' : 'stock-in') : 'stock-out'}`;
    stockBadge.innerHTML = `<i class="bi ${inStock ? (variant.lowStock ? 'bi-exclamation-triangle' : 'bi-check2-circle') : 'bi-x-circle'} me-1"></i>${inStock ? (variant.lowStock ? 'Few packs left' : 'In Stock') : 'Out of Stock'}`;
  }
}

async function handleHomeAddToCart(productId) {
  const product = featuredProducts.find(p => p.id === productId);
  if (!product) return;

  const card = document.getElementById(`product-card-${productId}`);
  const select = card.querySelector('.variant-selector-select');
  const variantId = select ? select.value : (product.defaultVariant?.id || product.variants[0]?.id);

  const selectedVariant = product.variants.find(v => v.id == variantId);

  const productDetails = {
    productId: product.id,
    nameEn: product.nameEn,
    nameTe: product.nameTe,
    brand: product.brand,
    unitName: selectedVariant?.unitName || 'Standard Pack',
    imageUrl: product.imageUrl,
    sellingPrice: selectedVariant?.sellingPrice || 100,
    mrp: selectedVariant?.mrp || 100
  };

  try {
    await Cart.addToCart(variantId, 1, productDetails);
  } catch (err) {
    console.error(err);
  }
}

function handleGlobalSearch(e) {
  e.preventDefault();
  const input = document.getElementById('global-search-input');
  const query = input?.value?.trim() || '';
  if (query) {
    window.location.href = `/products.html?q=${encodeURIComponent(query)}`;
  }
}
