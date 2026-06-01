import axiosClient from './axiosClient';

const zoneApi = {
  getAll: (floorId) => {
    const params = floorId ? { floorId } : {};
    return axiosClient.get('/zones', { params });
  },
  getById: (id) => axiosClient.get(`/zones/${id}`),
  create: (data) => axiosClient.post('/zones', data),
  update: (id, data) => axiosClient.put(`/zones/${id}`, data),
  delete: (id) => axiosClient.delete(`/zones/${id}`),
};

export default zoneApi;
