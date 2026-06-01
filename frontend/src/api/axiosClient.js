import axios from 'axios';

const axiosClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
    'X-Role': 'ADMIN', // Dev mode: luôn gửi ADMIN (sau này sẽ lấy từ JWT)
  },
});

// Cập nhật X-Role từ localStorage nếu có
axiosClient.interceptors.request.use((config) => {
  const role = localStorage.getItem('userRole') || 'ADMIN';
  config.headers['X-Role'] = role;
  return config;
});

export default axiosClient;
