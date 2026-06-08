import { useState, useEffect, useCallback } from 'react';
import './StaffList.css'; 

const API_URL = 'http://localhost:8080/api/customer/bookings';

function CustomerBooking() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [accountId, setAccountId] = useState(20); // Default account ID for testing, manager1 is 20
  const [showModal, setShowModal] = useState(false);
  
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

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleAddBooking = async (event) => {
    event.preventDefault();
    try {
      const payload = {
        ...formData,
        accountId: Number(formData.accountId),
        buildingId: Number(formData.buildingId),
        vehicleTypeId: Number(formData.vehicleTypeId),
        // Ensure format is ISO string without Z for LocalDateTime if needed, or backend might handle ISO
        // React datetime-local returns YYYY-MM-DDTHH:mm
        startTime: formData.startTime + ':00', 
        endTime: formData.endTime + ':00'
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
        alert('Booking created successfully!');
      } else {
        let msg = 'Failed to create booking.';
        try {
          const errData = await response.json();
          msg = errData.message || msg;
        } catch(e) {}
        alert(msg);
      }
    } catch (e) {
      console.error('Error creating booking:', e);
      alert('An error occurred while creating the booking.');
    }
  };

  const handleCancelBooking = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this booking?')) return;
    try {
      const response = await fetch(`${API_URL}/${id}/cancel`, {
        method: 'PATCH',
      });
      if (response.ok) {
        await fetchBookings();
      } else {
        alert('Failed to cancel booking.');
      }
    } catch (e) {
      console.error('Error canceling booking:', e);
      alert('An error occurred while canceling the booking.');
    }
  };

  const handleExpireOverdue = async () => {
    try {
      const response = await fetch(`${API_URL}/expire-overdue`, {
        method: 'POST',
      });
      if (response.ok) {
        alert('Successfully expired overdue bookings.');
        await fetchBookings();
      } else {
        alert('Failed to expire overdue bookings.');
      }
    } catch (e) {
      console.error('Error expiring bookings:', e);
      alert('An error occurred while expiring bookings.');
    }
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
                  <td>{new Date(booking.createdAt).toLocaleString()}</td>
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
                  required
                  placeholder="e.g. 29A-12345"
                />
              </div>
              <div className="form-group">
                <label>Building ID</label>
                <input
                  type="number"
                  name="buildingId"
                  value={formData.buildingId}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Vehicle Type ID</label>
                <input
                  type="number"
                  name="vehicleTypeId"
                  value={formData.vehicleTypeId}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Start Time</label>
                <input
                  type="datetime-local"
                  name="startTime"
                  value={formData.startTime}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>End Time</label>
                <input
                  type="datetime-local"
                  name="endTime"
                  value={formData.endTime}
                  onChange={handleInputChange}
                  required
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
