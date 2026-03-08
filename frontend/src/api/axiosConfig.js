import axios from "axios";

const api = axios.create({
  baseURL: "/api", // Proxied to http://localhost:8080/api via Vite proxy
  withCredentials: false,
});

// Interceptor: attach JWT token to every request if present
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("jwt_token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
