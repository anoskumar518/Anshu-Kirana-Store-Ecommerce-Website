/**
 * Anshu Kirana Store - REST API Client Wrapper
 */
const API = {
  // Smart Base URL: points to Spring Boot backend (port 8080) when opened via other dev ports or file protocol
  BASE_URL: (() => {
    if (typeof window !== 'undefined' && window.location) {
      const { protocol, hostname, port } = window.location;
      if (protocol === 'file:' || ((hostname === 'localhost' || hostname === '127.0.0.1') && port && port !== '8080')) {
        return 'http://localhost:8080';
      }
    }
    return '';
  })(),

  getToken() {
    return localStorage.getItem('aks_auth_token') || null;
  },

  setToken(token) {
    if (token) {
      localStorage.setItem('aks_auth_token', token);
    } else {
      localStorage.removeItem('aks_auth_token');
    }
  },

  async request(endpoint, options = {}) {
    const url = `${this.BASE_URL}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(options.headers || {})
    };

    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers
    };

    try {
      const response = await fetch(url, config);
      const isJson = response.headers.get('content-type')?.includes('application/json');
      const data = isJson ? await response.json() : null;

      if (!response.ok) {
        if (response.status === 401) {
          // Token expired or invalid
          console.warn('Session expired or unauthorized.');
        }
        const errorMsg = data?.message || `Request failed with status ${response.status}`;
        throw new Error(errorMsg);
      }

      return data;
    } catch (err) {
      console.error(`API Error [${endpoint}]:`, err);
      throw err;
    }
  },

  get(endpoint, params = {}) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, val]) => {
      if (val !== undefined && val !== null && val !== '') {
        query.append(key, val);
      }
    });
    const queryString = query.toString();
    const finalEndpoint = queryString ? `${endpoint}?${queryString}` : endpoint;
    return this.request(finalEndpoint, { method: 'GET' });
  },

  post(endpoint, body) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body)
    });
  },

  put(endpoint, body) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body)
    });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }
};
