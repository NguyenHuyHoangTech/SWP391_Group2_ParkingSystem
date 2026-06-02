import axiosClient from './axiosClient';
const vehicleTypeApi = { getAll: () => axiosClient.get('/vehicle-types') };
export default vehicleTypeApi;
