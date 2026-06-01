import axiosClient from './axiosClient';

const buildingApi = {
  getAll: () => axiosClient.get('/buildings'),
};

export default buildingApi;
