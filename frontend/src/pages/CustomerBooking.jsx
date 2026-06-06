import { useState, useEffect, useCallback } from 'react';
import './StaffList.css';
import buildingApi from '../api/buildingApi';
import vehicleTypeApi from '../api/vehicleTypeApi';

const API_URL = 'http://localhost:8080/api/customer/bookings';

function CustomerBooking() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [accountId, setAccountId] = useState(20); // Default account ID for testing, manager1 is 20
  const [showModal, setShowModal] = useState(false);
  const [buildings, setBuildings] = useState([]);
  const [vehicleTypes, setVehicleTypes] = useState([]);

  const [formData, setFormData] = useState({
    accountId: 20,
    buildingId: 1,
    vehicleTypeId: 1,
    licensePlate: '',
    startTime: '',
    endTime: ''
  });

  const fetchBookings = useCallback(async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_URL}?accountId=${accountId}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setBookings(data);
      setError(null);
    } catch (e) {
      console.error('Error fetching bookings:', e);
      setError('Failed to load bookings. Please check your backend connection.');
    } finally {
      setLoading(false);
    }
  }, [accountId]);

  useEffect(() => {
    fetchBookings();
    setFormData(prev => ({ ...prev, accountId: accountId }));
  }, [fetchBookings, accountId]);

  useEffect(() => {
    buildingApi.getAll().then(r => setBuildings(r.data.data || [])).catch(console.error);
    vehicleTypeApi.getAll().then(r => setVehicleTypes(r.data.data || [])).catch(console.error);
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const [customAlert, setCustomAlert] = useState(null);
  const [confirmDialog, setConfirmDialog] = useState(null);

  const showAlert = (msg) => setCustomAlert(msg);

  const handleAddBooking = async (event) => {
    event.preventDefault();

    if (!formData.licensePlate) { showAlert("Vui lòng nhập Biển số xe (License Plate)!"); return; }
    if (!formData.buildingId) { showAlert("Vui lòng chọn Tòa nhà (Building)!"); return; }
    if (!formData.vehicleTypeId) { showAlert("Vui lòng chọn Loại xe (Vehicle Type)!"); return; }
    if (!formData.startTime) { showAlert("Vui lòng chọn Thời gian bắt đầu (Start Time)!"); return; }
    if (!formData.endTime) { showAlert("Vui lòng chọn Thời gian kết thúc (End Time)!"); return; }

    try {
      const payload = {
        ...formData,
        accountId: Number(formData.accountId),
        buildingId: Number(formData.buildingId),
        vehicleTypeId: Number(formData.vehicleTypeId),
        startTime: formData.startTime.length === 16 ? formData.startTime + ':00' : formData.startTime,
        endTime: formData.endTime.length === 16 ? formData.endTime + ':00' : formData.endTime
      };
      
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        setShowModal(false);
        setFormData({ ...formData, licensePlate: '', startTime: '', endTime: '' });
        await fetchBookings();
        showAlert('Booking created successfully!');
      } else {
        let msg = 'Failed to create booking.';
        try {
          const errData = await response.json();
          msg = errData.message || msg;
        } catch (e) { }
        showAlert(msg);
      }
    } catch (e) {
      console.error('Error creating booking:', e);
      showAlert('An error occurred while creating the booking.');
    }
  };

  const executeCancel = async (id) => {
    try {
      const response = await fetch(`${API_URL}/${id}/cancel`, {
        method: 'PATCH',
      });
      if (response.ok) {
        await fetchBookings();
        showAlert('Booking cancelled successfully!');
      } else {
        showAlert('Failed to cancel booking.');
      }
    } catch (e) {
      console.error('Error canceling booking:', e);
      showAlert('An error occurred while canceling the booking.');
    }
  };

  const handleCancelBooking = (id) => {
    setConfirmDialog({
      message: 'Are you sure you want to cancel this booking?',
      onConfirm: () => {
        setConfirmDialog(null);
        executeCancel(id);
      },
      onCancel: () => setConfirmDialog(null)
    });
  };

  const handleExpireOverdue = async () => {
    try {
      const response = await fetch(`${API_URL}/expire-overdue`, {
        method: 'POST',
      });
      if (response.ok) {
        showAlert('Successfully expired overdue bookings.');
        await fetchBookings();
      } else {
        showAlert('Failed to expire overdue bookings.');
      }
    } catch (e) {
      console.error('Error expiring bookings:', e);
      showAlert('An error occurred while expiring bookings.');
    }
  };

  const formatDate = (dateInput) => {
    if (!dateInput) return 'N/A';
    if (Array.isArray(dateInput)) {
      const [year, month, day, hour, minute, second] = dateInput;
      return new Date(year, month - 1, day, hour || 0, minute || 0, second || 0).toLocaleString();
    }
    return new Date(dateInput).toLocaleString();
  };

  return (
    <div className="staff-container">
      <div className="staff-header">
        <div>
          <h1>Customer Bookings</h1>
          <span className="staff-count">Total Bookings: {bookings.length}</span>
        </div>
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
          <label style={{ fontWeight: 'bold' }}>Test with Account ID: </label>
          <input
            type="number"
            value={accountId}
            onChange={(e) => setAccountId(Number(e.target.value))}
            style={{ padding: '8px', width: '80px', borderRadius: '4px', border: '1px solid #ccc' }}
          />
          <button className="add-staff-btn" onClick={() => setShowModal(true)}>
            + Add New Booking
          </button>
          <button className="add-staff-btn" style={{ backgroundColor: '#dc3545' }} onClick={handleExpireOverdue}>
            Trigger Expire Overdue
          </button>
        </div>
      </div>

      {customAlert && (
        <div className="staff-modal" style={{ zIndex: 10000 }}>
          <div className="modal-content" style={{ maxWidth: '400px', textAlign: 'center' }}>
            <h3 style={{ marginTop: 0 }}>Notification</h3>
            <p>{customAlert}</p>
            <button className="submit-btn" onClick={() => setCustomAlert(null)} style={{ marginTop: '1rem' }}>OK</button>
          </div>
        </div>
      )}

      {confirmDialog && (
        <div className="staff-modal" style={{ zIndex: 10000 }}>
          <div className="modal-content" style={{ maxWidth: '400px', textAlign: 'center' }}>
            <h3 style={{ marginTop: 0 }}>Confirmation</h3>
            <p>{confirmDialog.message}</p>
            <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', marginTop: '1.5rem' }}>
              <button className="cancel-btn" onClick={confirmDialog.onCancel}>Cancel</button>
              <button className="submit-btn" style={{ backgroundColor: '#dc3545' }} onClick={confirmDialog.onConfirm}>Confirm</button>
            </div>
          </div>
        </div>
      )}

      {loading ? (
        <div className="staff-loading">Loading bookings...</div>
      ) : error ? (
        <div className="staff-error"><h3>Error</h3><p>{error}</p></div>
      ) : bookings.length === 0 ? (
        <div className="staff-empty">
          <p>No bookings found for Account ID {accountId}.</p>
        </div>
      ) : (
        <div className="table-responsive">
          <table className="staff-table">
            <thead>
              <tr>
                <th>Booking ID</th>
                <th>Status</th>
                <th>License Plate</th>
                <th>Creation Time</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {bookings.map((booking) => (
                <tr key={booking.id}>
                  <td>#{booking.id}</td>
                  <td>
                    <span className={`role-badge role-${booking.status?.toLowerCase() || 'default'}`}>
                      {booking.status}
                    </span>
                  </td>
                  <td>{booking.licensePlate}</td>
                  <td>{formatDate(booking.createdAt)}</td>
                  <td>
                    {booking.status === 'CONFIRMED' && (
                       <button className="action-btn delete-btn" onClick={() => handleCancelBooking(booking.id)}>
                         Cancel Booking
                       </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showModal && (
        <div className="staff-modal">
          <div className="modal-content">
            <span className="close-btn" onClick={() => setShowModal(false)}>&times;</span>
            <h2>Add New Booking</h2>
            <form onSubmit={handleAddBooking} className="staff-form">
              <div className="form-group">
                <label>Account ID</label>
                <input type="number" name="accountId" value={formData.accountId} disabled />
              </div>
              <div className="form-group">
                <label>License Plate</label>
                <input
                  type="text"
                  name="licensePlate"
                  value={formData.licensePlate}
                  onChange={handleInputChange}
                  placeholder="e.g. 29A-12345"
                />
              </div>
              <div className="form-group">
                <label>Building</label>
                <select
                  name="buildingId"
                  value={formData.buildingId}
                  onChange={handleInputChange}
                >
                  <option value="">-- Select Building --</option>
                  {buildings.map(b => (
                    <option key={b.id} value={b.id}>{b.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Vehicle Type</label>
                <select
                  name="vehicleTypeId"
                  value={formData.vehicleTypeId}
                  onChange={handleInputChange}
                >
                  <option value="">-- Select Vehicle Type --</option>
                  {vehicleTypes.map(v => (
                    <option key={v.id} value={v.id}>{v.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Start Time</label>
                <input
                  type="datetime-local"
                  name="startTime"
                  value={formData.startTime}
                  onChange={handleInputChange}
                />
              </div>
              <div className="form-group">
                <label>End Time</label>
                <input
                  type="datetime-local"
                  name="endTime"
                  value={formData.endTime}
                  onChange={handleInputChange}
                />
              </div>
              <div className="form-actions">
                <button type="button" className="cancel-btn" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="submit-btn">Create Booking</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default CustomerBooking;
