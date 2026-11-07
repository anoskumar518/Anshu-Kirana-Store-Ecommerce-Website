/**
 * Anshu Kirana Store - Authentication State & Session Manager
 */
const Auth = {
  getUser() {
    try {
      const data = localStorage.getItem('aks_user');
      return data ? JSON.parse(data) : null;
    } catch {
      return null;
    }
  },

  setUser(user) {
    if (user) {
      localStorage.setItem('aks_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('aks_user');
    }
  },

  isLoggedIn() {
    return !!API.getToken() && !!this.getUser();
  },

  isAdmin() {
    const user = this.getUser();
    return user && user.roles && (user.roles.includes('ROLE_ADMIN') || user.roles.includes('ADMIN'));
  },

  async login(identifier, password) {
    const res = await API.post('/api/auth/login', { identifier, password });
    if (res.success && res.data) {
      API.setToken(res.data.token);
      this.setUser(res.data.user);
      return res.data.user;
    }
    throw new Error(res.message || 'Login failed');
  },

  async register(data) {
    const res = await API.post('/api/auth/register', data);
    if (res.success && res.data) {
      API.setToken(res.data.token);
      this.setUser(res.data.user);
      return res.data.user;
    }
    throw new Error(res.message || 'Registration failed');
  },

  logout() {
    API.setToken(null);
    this.setUser(null);
    UI.showToast('You have been logged out.', 'info');
    setTimeout(() => {
      window.location.href = '/index.html';
    }, 400);
  },

  updateNavbarAuthUI() {
    const authContainer = document.getElementById('navbar-auth-section');
    if (!authContainer) return;

    if (this.isLoggedIn()) {
      const user = this.getUser();
      const isAdmin = this.isAdmin();
      authContainer.innerHTML = `
        <div class="dropdown">
          <button class="btn btn-outline-kirana btn-sm dropdown-toggle d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown">
            <i class="bi bi-person-circle fs-5"></i>
            <span class="d-none d-md-inline fw-bold">${user.fullName || 'My Account'}</span>
          </button>
          <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
            <li class="dropdown-header text-muted small">${user.phone}</li>
            <li><a class="dropdown-item py-2" href="/orders.html"><i class="bi bi-bag-check me-2 text-success"></i><span data-i18n="myOrders">My Orders</span></a></li>
            ${isAdmin ? `<li><a class="dropdown-item py-2" href="/admin/index.html"><i class="bi bi-speedometer2 me-2 text-primary"></i><span data-i18n="adminDashboard">Admin Panel</span></a></li>` : ''}
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item py-2 text-danger" href="javascript:void(0)" onclick="Auth.logout()"><i class="bi bi-box-arrow-right me-2"></i><span data-i18n="logout">Logout</span></a></li>
          </ul>
        </div>
      `;
    } else {
      authContainer.innerHTML = `
        <a href="/login.html" class="btn btn-outline-kirana btn-sm d-flex align-items-center gap-1">
          <i class="bi bi-person fs-6"></i>
          <span data-i18n="login">Login</span>
        </a>
      `;
    }

    if (window.I18N) {
      window.I18N.applyTranslations();
    }
  }
};

document.addEventListener('DOMContentLoaded', () => {
  Auth.updateNavbarAuthUI();
});
