import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
});

// Endpoints GET públicos (no requieren autenticación)
const publicGetEndpoints = ['/productos', '/productos/buscar', '/productos/'];

api.interceptors.request.use((config) => {
  let isPublicGet = false;
  if (config.method?.toLowerCase() === 'get') {
    isPublicGet = publicGetEndpoints.some(endpoint => config.url?.startsWith(endpoint));
  }
  if (!isPublicGet) {
    const auth = localStorage.getItem('auth');
    if (auth) {
      const { username, password } = JSON.parse(auth);
      config.auth = { username, password };
    }
  }
  return config;
});

export default api;