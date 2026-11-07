/**
 * Anshu Kirana Store - Product Catalog & Filtering Logic
 */

let currentCategoryId = null;
let currentBrand = null;
let currentSearch = null;
let currentPage = 0;
let currentSortBy = 'id';
let currentDirection = 'desc';

let loadedProducts = [];

document.addEventListener('DOMContentLoaded', async () => {
  const urlParams = new URLSearchParams(window.location.search);
  currentCategoryId = urlParams.get('category') ? Number(urlParams.get('category')) : null;
  currentBrand = urlParams.get('brand') || null;
  currentSearch = urlParams.get('q') || null;

  if (currentSearch) {
    const input = document.getElementById('product-page-search');
    if (input) input.value = currentSearch;
  }

  await loadFilterCategories();
  await loadFilterBrands();
  await fetchProducts();

  window.addEventListener('languageChanged', () => {
    renderFilterCategories();
    renderProducts();
  });
});

let filterCategories = [];
async function loadFilterCategories() {
  try {
    const res = await API.get('/api/categories');
    if (res.success && res.data) {
      filterCategories = res.data;
      renderFilterCategories();
    }
  } catch (err) {
    console.error(err);
  }
}

function renderFilterCategories() {
  const container = document.getElementById('filter-categories-list');
  if (!container) return;

  let html = `
    <button class="btn btn-sm text-start py-1 px-2 rounded ${!currentCategoryId ? 'fw-bold text-success bg-light' : 'text-dark'}" onclick="filterByCategory(null)">
      <i class="bi bi-chevron-right me-1 small"></i> All Categories
    </button>
  `;

  filterCategories.forEach(c => {
    const name = I18N.currentLang === 'te' ? c.nameTe : c.nameEn;
    const isSelected = currentCategoryId === c.id;
    html += `
      <button class="btn btn-sm text-start py-1 px-2 rounded ${isSelected ? 'fw-bold text-success bg-light' : 'text-dark'}" onclick="filterByCategory(${c.id})">
        <i class="bi bi-chevron-right me-1 small"></i> ${name}
      </button>
    `;
  });

  container.innerHTML = html;
}

let filterBrands = [];
async function loadFilterBrands() {
  try {
    const res = await API.get('/api/products/brands');
    if (res.success && res.data) {
      filterBrands = res.data;
      renderFilterBrands();
    }
  } catch (err) {
    console.error(err);
  }
}

function renderFilterBrands() {
  const container = document.getElementById('filter-brands-list');
  if (!container) return;

  let html = `
    <button class="btn btn-sm text-start py-1 px-2 rounded ${!currentBrand ? 'fw-bold text-success bg-light' : 'text-dark'}" onclick="filterByBrand(null)">
      <i class="bi bi-check2 me-1"></i> All Brands
    </button>
  `;

  filterBrands.forEach(b => {
    const isSelected = currentBrand === b;
    html += `
      <button class="btn btn-sm text-start py-1 px-2 rounded ${isSelected ? 'fw-bold text-success bg-light' : 'text-dark'}" onclick="filterByBrand('${b}')">
        <i class="bi bi-check2 me-1"></i> ${b}
      </button>
    `;
  });

  container.innerHTML = html;
}

async function fetchProducts() {
  const grid = document.getElementById('products-catalog-grid');
  if (grid) {
    grid.innerHTML = `<div class="col-12 text-center py-5"><div class="spinner-border text-success" role="status"></div><div class="mt-2 text-muted small">Loading fresh groceries...</div></div>`;
  }

  try {
    const params = {
      page: currentPage,
      size: 15,
      sortBy: currentSortBy,
      direction: currentDirection
    };

    if (currentCategoryId) params.categoryId = currentCategoryId;
    if (currentBrand) params.brand = currentBrand;
    if (currentSearch) params.keyword = currentSearch;

    const res = await API.get('/api/products', params);
    if (res.success && res.data) {
      loadedProducts = res.data.content || [];
      renderProducts();
      renderPagination(res.data);
      updateFilterSummary(res.data.totalElements);
    }
  } catch (err) {
    console.error('Error fetching products:', err);
    if (grid) {
      grid.innerHTML = `
        <div class="col-12 text-center py-5 text-danger">
          <i class="bi bi-exclamation-triangle fs-3"></i>
          <p class="mt-2 fw-semibold">Could not load products. Please check server.</p>
          <div class="small text-muted mb-3">${err.message || ''}</div>
          <button class="btn btn-outline-success btn-sm" onclick="fetchProducts()">
            <i class="bi bi-arrow-clockwise me-1"></i> Retry
          </button>
        </div>
      `;
    }
  }
}

function renderProducts() {
  const grid = document.getElementById('products-catalog-grid');
  if (!grid) return;

  if (loadedProducts.length === 0) {
    grid.innerHTML = `
      <div class="col-12 text-center py-5">
        <div class="p-4 bg-white rounded shadow-sm">
          <i class="bi bi-search fs-1 text-muted"></i>
          <h5 class="fw-bold mt-2">No groceries found</h5>
          <p class="text-muted small">Try checking your spelling or clearing filters.</p>
          <button class="btn btn-primary-kirana btn-sm" onclick="resetFilters()">View All Products</button>
        </div>
      </div>
    `;
    return;
  }

  grid.innerHTML = loadedProducts.map(p => {
    const name = I18N.currentLang === 'te' ? p.nameTe : p.nameEn;
    const subName = I18N.currentLang === 'te' ? p.nameEn : p.nameTe;
    const variant = p.defaultVariant || (p.variants && p.variants[0]) || {};

    const inStock = variant.inStock !== false && variant.stockQuantity > 0;
    const discount = variant.discountPercent || 0;

    return `
      <div class="col">
        <div class="product-card" id="catalog-card-${p.id}">
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

          <!-- Variants selector -->
          ${p.variants && p.variants.length > 1 ? `
            <select class="form-select form-select-sm variant-selector-select" onchange="onCatalogVariantChange(${p.id}, this.value)">
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
            <span class="selling-price" id="cat-selling-${p.id}">₹${variant.sellingPrice || 0}</span>
            ${variant.mrp && variant.mrp > variant.sellingPrice ? `
              <span class="mrp-price" id="cat-mrp-${p.id}">₹${variant.mrp}</span>
              <span class="savings-tag" id="cat-savings-${p.id}">Save ₹${variant.mrp - variant.sellingPrice}</span>
            ` : ''}
          </div>

          <!-- Stock Indicator -->
          <div class="stock-indicator ${inStock ? (variant.lowStock ? 'stock-low' : 'stock-in') : 'stock-out'}" id="cat-stock-${p.id}">
            <i class="bi ${inStock ? (variant.lowStock ? 'bi-exclamation-triangle' : 'bi-check2-circle') : 'bi-x-circle'} me-1"></i>
            ${inStock ? (variant.lowStock ? 'Few packs left' : 'In Stock') : 'Out of Stock'}
          </div>

          <!-- Add to Cart Button -->
          <div class="mt-auto">
            <button class="btn btn-primary-kirana w-100 d-flex align-items-center justify-content-center gap-2 ${!inStock ? 'disabled' : ''}"
                    id="cat-add-btn-${p.id}"
                    onclick="handleCatalogAddToCart(${p.id})">
              <i class="bi bi-cart-plus"></i>
              <span>${inStock ? (I18N.currentLang === 'te' ? 'బుట్టలో వేయి' : 'Add to Cart') : (I18N.currentLang === 'te' ? 'స్టాక్ లేదు' : 'Out of Stock')}</span>
            </button>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function onCatalogVariantChange(productId, variantId) {
  const product = loadedProducts.find(p => p.id === productId);
  if (!product) return;

  const variant = product.variants.find(v => v.id == variantId);
  if (!variant) return;

  const spEl = document.getElementById(`cat-selling-${productId}`);
  if (spEl) spEl.textContent = `₹${variant.sellingPrice}`;

  const mrpEl = document.getElementById(`cat-mrp-${productId}`);
  if (mrpEl) {
    if (variant.mrp && variant.mrp > variant.sellingPrice) {
      mrpEl.textContent = `₹${variant.mrp}`;
      mrpEl.style.display = 'inline';
    } else {
      mrpEl.style.display = 'none';
    }
  }

  const addBtn = document.getElementById(`cat-add-btn-${productId}`);
  const stockBadge = document.getElementById(`cat-stock-${productId}`);
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

async function handleCatalogAddToCart(productId) {
  const product = loadedProducts.find(p => p.id === productId);
  if (!product) return;

  const card = document.getElementById(`catalog-card-${productId}`);
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

function renderPagination(pageData) {
  const container = document.getElementById('pagination-container');
  if (!container) return;

  if (pageData.totalPages <= 1) {
    container.innerHTML = '';
    return;
  }

  let html = `<ul class="pagination pagination-sm shadow-sm">`;

  // Previous
  html += `
    <li class="page-item ${pageData.pageNumber === 0 ? 'disabled' : ''}">
      <a class="page-link" href="javascript:void(0)" onclick="goToPage(${pageData.pageNumber - 1})"><i class="bi bi-chevron-left"></i></a>
    </li>
  `;

  for (let i = 0; i < pageData.totalPages; i++) {
    html += `
      <li class="page-item ${i === pageData.pageNumber ? 'active' : ''}">
        <a class="page-link" href="javascript:void(0)" onclick="goToPage(${i})">${i + 1}</a>
      </li>
    `;
  }

  // Next
  html += `
    <li class="page-item ${pageData.last ? 'disabled' : ''}">
      <a class="page-link" href="javascript:void(0)" onclick="goToPage(${pageData.pageNumber + 1})"><i class="bi bi-chevron-right"></i></a>
    </li>
  `;

  html += `</ul>`;
  container.innerHTML = html;
}

function goToPage(page) {
  currentPage = page;
  fetchProducts();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function updateFilterSummary(total) {
  const countEl = document.getElementById('products-count-text');
  const badgeEl = document.getElementById('active-filter-badge');

  if (countEl) {
    countEl.textContent = `${total} Grocery Products`;
  }

  if (badgeEl) {
    let parts = [];
    if (currentCategoryId) {
      const cat = filterCategories.find(c => c.id === currentCategoryId);
      if (cat) parts.push(`Category: ${cat.nameEn}`);
    }
    if (currentBrand) parts.push(`Brand: ${currentBrand}`);
    if (currentSearch) parts.push(`Search: "${currentSearch}"`);

    badgeEl.textContent = parts.length > 0 ? parts.join(' | ') : 'Showing all products';
  }
}

function filterByCategory(catId) {
  currentCategoryId = catId;
  currentPage = 0;
  renderFilterCategories();
  fetchProducts();
}

function filterByBrand(brand) {
  currentBrand = brand;
  currentPage = 0;
  renderFilterBrands();
  fetchProducts();
}

function onSortChange(sortVal) {
  const [field, dir] = sortVal.split('-');
  currentSortBy = field;
  currentDirection = dir;
  currentPage = 0;
  fetchProducts();
}

function onProductPageSearch(e) {
  e.preventDefault();
  const input = document.getElementById('product-page-search');
  currentSearch = input?.value?.trim() || null;
  currentPage = 0;
  fetchProducts();
}

function resetFilters() {
  currentCategoryId = null;
  currentBrand = null;
  currentSearch = null;
  currentPage = 0;
  const input = document.getElementById('product-page-search');
  if (input) input.value = '';
  renderFilterCategories();
  renderFilterBrands();
  fetchProducts();
}
