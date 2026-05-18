// js/api.js

const baseUrl = '/api';

// Helper to extract JWT token from local storage
const getToken = () => {
  const userStr = localStorage.getItem('user');
  if (!userStr) return null;
  try {
    const user = JSON.parse(userStr);
    return user.token || user.jwt || user.accessToken || null;
  } catch (e) {
    console.error('Error reading JWT from local storage', e);
    return null;
  }
};

const getHeaders = () => {
  const headers = {
    'Content-Type': 'application/json'
  };
  const token = getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
};

const handleResponse = async (response) => {
  if (!response.ok) {
    let errorMessage = response.statusText;
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || errorData.error || errorMessage;
    } catch (e) {
      try {
        const text = await response.text();
        if (text) errorMessage = text;
      } catch (inner) {}
    }

    // JWT Expire handling: If unauthorized, redirect to authentication portal
    if (response.status === 401 || response.status === 403) {
      console.warn('Session expired or unauthorized. Redirecting to login.');
      localStorage.removeItem('user');
      if (!window.location.pathname.endsWith('/index.html') && window.location.pathname !== '/') {
        window.location.href = '/index.html';
      }
    }

    throw new Error(errorMessage);
  }

  if (response.status === 204) return null;
  return response.json();
};

export const apiRequest = async (method, url, body = null) => {
  const options = {
    method,
    headers: getHeaders()
  };
  if (body !== null) {
    options.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(`${baseUrl}${url}`, options);
    return await handleResponse(response);
  } catch (error) {
    console.error(`API request failed [${method} ${url}]:`, error);
    throw error;
  }
};

// Unified named request() supporting both options object and raw GET signature
export const request = async (url, options = {}) => {
  const method = options.method || 'GET';
  const body = options.body || null;
  return apiRequest(method, url, body);
};

// Named api wrapper export for Subjects/Courses modules compatibility
export const api = {
  get: (url) => apiRequest('GET', url),
  post: (url, data) => apiRequest('POST', url, data),
  put: (url, data) => apiRequest('PUT', url, data),
  delete: (url) => apiRequest('DELETE', url)
};
