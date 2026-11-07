/**
 * Anshu Kirana Store - Shared Admin Dashboard Logic
 */
const AdminApp = {
  checkAdminAuth() {
    if (!Auth.isLoggedIn() || !Auth.isAdmin()) {
      UI.showToast('Admin login required. Redirecting to login...', 'warning');
      setTimeout(() => {
        window.location.href = '/login.html?redirect=admin';
      }, 700);
      return false;
    }
    return true;
  },

  renderSidebar(activePage = 'dashboard') {
    const sidebar = document.getElementById('admin-sidebar-container');
    if (!sidebar) return;

    sidebar.innerHTML = `
      <div class="sidebar-brand">
        <i class="bi bi-shop text-success fs-4"></i>
        <div>
          <div class="lh-1">Anshu Kirana</div>
          <small class="text-muted fw-normal" style="font-size: 0.72rem;">Admin Control Center</small>
        </div>
      </div>

      <ul class="sidebar-menu">
        <li class="nav-item">
          <a class="nav-link ${activePage === 'dashboard' ? 'active' : ''}" href="/admin/index.html">
            <i class="bi bi-speedometer2"></i> Overview
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link ${activePage === 'orders' ? 'active' : ''}" href="/admin/orders.html">
            <i class="bi bi-receipt"></i> Orders Management
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link ${activePage === 'products' ? 'active' : ''}" href="/admin/products.html">
            <i class="bi bi-box-seam"></i> Products Catalog
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link ${activePage === 'categories' ? 'active' : ''}" href="/admin/categories.html">
            <i class="bi bi-tags"></i> Categories
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link ${activePage === 'inventory' ? 'active' : ''}" href="/admin/inventory.html">
            <i class="bi bi-boxes"></i> Inventory & Stock
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link ${activePage === 'reports' ? 'active' : ''}" href="/admin/reports.html">
            <i class="bi bi-graph-up-arrow"></i> Sales Reports
          </a>
        </li>
        <li class="nav-item mt-4 pt-3 border-top border-secondary">
          <a class="nav-link text-info" href="/index.html" target="_blank">
            <i class="bi bi-box-arrow-up-right"></i> View Live Store
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link text-danger" href="javascript:void(0)" onclick="Auth.logout()">
            <i class="bi bi-box-arrow-right"></i> Logout
          </a>
        </li>
      </ul>
    `;
  },

  renderTopbar(title = 'Dashboard') {
    const topbar = document.getElementById('admin-topbar-container');
    if (!topbar) return;

    const user = Auth.getUser() || { fullName: 'Administrator' };

    topbar.innerHTML = `
      <div class="d-flex align-items-center gap-3">
        <button class="btn btn-outline-secondary btn-sm d-lg-none" onclick="document.querySelector('.admin-sidebar').classList.toggle('show')">
          <i class="bi bi-list fs-5"></i>
        </button>
        <h4 class="fw-bold mb-0">${title}</h4>
      </div>

      <div class="d-flex align-items-center gap-3">
        <div class="d-flex align-items-center gap-2">
          <span class="d-inline-flex align-items-center justify-content-center bg-success text-white rounded-circle fw-bold" style="width: 36px; height: 36px;">
            ${user.fullName ? user.fullName[0].toUpperCase() : 'A'}
          </span>
          <div class="d-none d-sm-block text-end lh-sm">
            <div class="fw-bold small">${user.fullName || 'Admin'}</div>
            <div class="text-muted" style="font-size: 0.72rem;">Store Owner</div>
          </div>
        </div>
      </div>
    `;
  }
};
