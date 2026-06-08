import axiosClient from './axiosClient';

const slotApi = {
  getByZone:  (zoneId) => axiosClient.get('/slots', { params: { zoneId } }),
  lock:       (id)     => axiosClient.patch(`/slots/${id}/lock`),
  unlock:     (id)     => axiosClient.patch(`/slots/${id}/unlock`),
  occupy:     (id)     => axiosClient.patch(`/slots/${id}/occupy`),
  vacate:     (id)     => axiosClient.patch(`/slots/${id}/vacate`),
  delete:     (id)     => axiosClient.delete(`/slots/${id}`),
  bulkDelete: (ids)    => axiosClient.delete('/slots/bulk', { data: ids }),
};

export default slotApi;
