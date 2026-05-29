import { useState, useEffect } from 'react';
import './StaffList.css';

const API_URL = 'http://localhost:8080/api/staff';

function StaffList() {
  const [staffList, setStaffList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    email: '',
    phone: '',
    role: 'STAFF',
    buildingId: '',
  });
  const [formError, setFormError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [statusMessage, setStatusMessage] = useState('');
  const [statusError, setStatusError] = useState('');
  const [processingStatusId, setProcessingStatusId] = useState(null);

  useEffect(() => {
    fetchStaff();
  }, []);

  const fetchStaff = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await fetch(API_URL);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setStaffList(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData((currentData) => ({
      ...currentData,
      [name]: value,
    }));
  };

  const resetForm = () => {
    setFormData({
      username: '',
      password: '',
      email: '',
      phone: '',
      role: 'STAFF',
      buildingId: '',
    });
    setFormError('');
  };

  const openAddModal = () => {
    resetForm();
    setShowAddModal(true);
  };

  const closeAddModal = () => {
    if (submitting) {
      return;
    }

    setShowAddModal(false);
    resetForm();
  };

  const handleAddStaff = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setFormError('');
    setStatusMessage('');
    setStatusError('');

    try {
      const payload = {
        username: formData.username.trim(),
        password: formData.password,
        email: formData.email.trim() || null,
        phone: formData.phone.trim() || null,
        role: formData.role,
        buildingId: formData.buildingId === '' ? null : Number(formData.buildingId),
      };

      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        let message = 'Failed to create staff account.';

        try {
          const errorData = await response.json();
          message = errorData.message || message;
        } catch {
          if (response.status === 400) {
            message = 'Please check the staff account details.';
          }
        }

        throw new Error(message);
      }

      await fetchStaff();
      setShowAddModal(false);
      resetForm();
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleStatusUpdate = async (staffId, status) => {
    setProcessingStatusId(staffId);
    setStatusMessage('');
    setStatusError('');

    try {
      const response = await fetch(`${API_URL}/${staffId}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status }),
      });

      if (!response.ok) {
        let message = 'Failed to update staff status.';

        try {
          const errorData = await response.json();
          message = errorData.message || message;
        } catch {
          if (response.status === 400) {
            message = 'Please select a valid status.';
          }
        }

        throw new Error(message);
      }

      await fetchStaff();
      setStatusMessage('Status updated successfully');
    } catch (err) {
      setStatusError(err.message);
    } finally {
      setProcessingStatusId(null);
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case 'ACTIVE': return 'status-active';
      case 'INACTIVE': return 'status-inactive';
      case 'BANNED': return 'status-banned';
      default: return '';
    }
  };

  const getRoleBadgeClass = (role) => {
    switch (role) {
      case 'MANAGER': return 'role-manager';
      case 'STAFF': return 'role-staff';
      default: return '';
    }
  };

  if (loading) {
    return (
      <div className="staff-container">
        <div className="staff-loading">
          <div className="spinner"></div>
          <p>Loading staff list...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="staff-container">
        <div className="staff-error">
          <span className="error-icon">!</span>
          <h3>Failed to load staff</h3>
          <p>{error}</p>
          <button onClick={fetchStaff} className="retry-btn">Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="staff-container">
      <div className="staff-header">
        <div>
          <h1>Staff List</h1>
          <span className="staff-count">{staffList.length} members</span>
        </div>
        <button type="button" className="add-staff-btn" onClick={openAddModal}>
          Add Staff
        </button>
      </div>

      {statusMessage && <div className="status-message success-message">{statusMessage}</div>}
      {statusError && <div className="status-message error-message">{statusError}</div>}

      {staffList.length === 0 ? (
        <div className="staff-empty">
          <p>No staff members found.</p>
        </div>
      ) : (
        <div className="table-wrapper">
          <table className="staff-table" id="staff-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Role</th>
                <th>Building</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {staffList.map((staff) => (
                <tr key={staff.id}>
                  <td className="cell-id">{staff.id}</td>
                  <td className="cell-username">{staff.username}</td>
                  <td className="cell-email">{staff.email || '-'}</td>
                  <td className="cell-phone">{staff.phone || '-'}</td>
                  <td>
                    <span className={`role-badge ${getRoleBadgeClass(staff.role)}`}>
                      {staff.role}
                    </span>
                  </td>
                  <td className="cell-id">{staff.buildingId || '-'}</td>
                  <td>
                    <span className={`status-badge ${getStatusClass(staff.status)}`}>
                      {staff.status}
                    </span>
                  </td>
                  <td>
                    <div className="status-actions">
                      {staff.status !== 'ACTIVE' && (
                        <button
                          type="button"
                          className="action-btn activate-btn"
                          onClick={() => handleStatusUpdate(staff.id, 'ACTIVE')}
                          disabled={processingStatusId !== null}
                        >
                          Activate
                        </button>
                      )}
                      {staff.status !== 'INACTIVE' && (
                        <button
                          type="button"
                          className="action-btn deactivate-btn"
                          onClick={() => handleStatusUpdate(staff.id, 'INACTIVE')}
                          disabled={processingStatusId !== null}
                        >
                          Deactivate
                        </button>
                      )}
                      {staff.status !== 'BANNED' && (
                        <button
                          type="button"
                          className="action-btn ban-btn"
                          onClick={() => handleStatusUpdate(staff.id, 'BANNED')}
                          disabled={processingStatusId !== null}
                        >
                          Ban
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showAddModal && (
        <div className="modal-backdrop" role="presentation">
          <div className="staff-modal" role="dialog" aria-modal="true" aria-labelledby="add-staff-title">
            <div className="modal-header">
              <h2 id="add-staff-title">Add Staff Account</h2>
              <button type="button" className="modal-close-btn" onClick={closeAddModal} disabled={submitting}>
                x
              </button>
            </div>

            <form className="staff-form" onSubmit={handleAddStaff}>
              {formError && <div className="form-error">{formError}</div>}

              <label>
                Username
                <input
                  type="text"
                  name="username"
                  value={formData.username}
                  onChange={handleInputChange}
                  required
                  autoFocus
                />
              </label>

              <label>
                Password
                <input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  required
                />
              </label>

              <label>
                Email
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                />
              </label>

              <label>
                Phone
                <input
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleInputChange}
                />
              </label>

              <label>
                Role
                <select name="role" value={formData.role} onChange={handleInputChange}>
                  <option value="STAFF">STAFF</option>
                  <option value="MANAGER">MANAGER</option>
                </select>
              </label>

              <label>
                Building ID
                <input
                  type="number"
                  name="buildingId"
                  value={formData.buildingId}
                  onChange={handleInputChange}
                  min="1"
                />
              </label>

              <div className="form-actions">
                <button type="button" className="cancel-btn" onClick={closeAddModal} disabled={submitting}>
                  Cancel
                </button>
                <button type="submit" className="submit-btn" disabled={submitting}>
                  {submitting ? 'Creating...' : 'Create Staff'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default StaffList;
