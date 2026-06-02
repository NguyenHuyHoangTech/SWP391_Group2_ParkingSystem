import axiosClient from './axiosClient';
const floorApi = {
  getAll: () => axiosClient.get('/floors'),
  getById: (id) => axiosClient.get(`/floors/${id}`),
  create: (data) => axiosClient.post('/floors', data),
  update: (id, data) => axiosClient.put(`/floors/${id}`, data),
  delete: (id) => axiosClient.delete(`/floors/${id}`),
};
export default floorApi;
