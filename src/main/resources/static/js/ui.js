/**
 * Anshu Kirana Store - Common UI Utilities & Notification Toasts
 */
const UI = {
  formatCurrency(amount) {
    if (amount === undefined || amount === null) return '₹0';
    const num = Number(amount);
    return '₹' + num.toLocaleString('en-IN', { maximumFractionDigits: 2, minimumFractionDigits: 0 });
  },

  formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  },

  showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    const bgClass = type === 'success' ? 'bg-success text-white' :
                    type === 'error' ? 'bg-danger text-white' :
                    type === 'warning' ? 'bg-warning text-dark' : 'bg-primary text-white';

    const icon = type === 'success' ? 'bi-check-circle-fill' :
                 type === 'error' ? 'bi-exclamation-triangle-fill' :
                 type === 'warning' ? 'bi-exclamation-circle-fill' : 'bi-info-circle-fill';

    toast.className = `toast align-items-center ${bgClass} border-0 show shadow-lg mb-2`;
    toast.setAttribute('role', 'alert');
    toast.innerHTML = `
      <div class="d-flex">
        <div class="toast-body d-flex align-items-center gap-2 fw-semibold">
          <i class="bi ${icon} fs-5"></i>
          <span>${message}</span>
        </div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" onclick="this.parentElement.parentElement.remove()"></button>
      </div>
    `;

    container.appendChild(toast);
    setTimeout(() => {
      toast.remove();
    }, 3800);
  },

  getCurrentVillage() {
    return localStorage.getItem('aks_village') || 'Repudi Village';
  },

  setCurrentVillage(village) {
    localStorage.setItem('aks_village', village);
    document.querySelectorAll('.current-village-name').forEach(el => el.textContent = village);
    this.showToast(`Delivery village updated to ${village}`, 'info');
  },

  getWhatsappLink(message = "Namaste Anshu Kirana Store, I would like to order groceries.") {
    const phone = "917993542873";
    return `https://wa.me/${phone}?text=${encodeURIComponent(message)}`;
  }
};
