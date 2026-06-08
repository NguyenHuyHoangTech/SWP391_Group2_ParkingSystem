import { useState, useEffect, useCallback } from 'react';
import './StaffList.css';

// Staff Management API base path used by the list, create modal, and status update actions.
const API_URL = 'http://localhost:8080/api/staff';
// Client-side email validation mirrors backend validation before sending create requests.
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
// Phone must contain only digits and be 10 or 11 characters long.
const PHONE_PATTERN = /^\d{10,11}$/;
// Defensive filter prevents raw SQL or framework exception text from appearing in the modal.
const SQL_ERROR_PATTERN = /(sql|hibernate|constraint|duplicate key|stack trace|exception|violation of unique key)/i;

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

  const fetchStaff = useCallback(async () => {
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
  }, []);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchStaff();
  }, [fetchStaff]);

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

  // Validates Add Staff form fields before the API call so malformed data is rejected in the modal.
  const validateStaffForm = () => {
    const username = formData.username.trim();
    const password = formData.password.trim();
    const email = formData.email.trim();
    const phone = formData.phone.trim();
    const role = formData.role.trim();
    const buildingId = formData.buildingId.trim();

    if (!username) {
      return 'Username is required.';
    }

    if (!password) {
      return 'Password is required.';
    }

    if (!email) {
      return 'Email is required.';
    }

    if (!EMAIL_PATTERN.test(email)) {
      return 'Invalid email format.';
    }

    if (!phone) {
      return 'Phone is required.';
    }

    if (!PHONE_PATTERN.test(phone)) {
      return 'Invalid phone number format.';
    }

    if (!['STAFF', 'MANAGER'].includes(role)) {
      return 'Role must be STAFF or MANAGER.';
    }

    if (!buildingId) {
      return 'Building ID is required.';
    }

    if (!Number.isInteger(Number(buildingId)) || Number(buildingId) < 1) {
      return 'Building ID is required.';
    }

    return '';
  };

  // Keeps backend errors concise and safe for display inside the red validation box.
  const getFriendlyErrorMessage = (message) => {
    if (!message || SQL_ERROR_PATTERN.test(message)) {
      return 'Invalid staff account details.';
    }

    return message.length > 120 ? 'Invalid staff account details.' : message;
  };

  const handleAddStaff = async (event) => {
    event.preventDefault();
    setFormError('');
    setStatusMessage('');
    setStatusError('');

    const validationError = validateStaffForm();
    if (validationError) {
      // Stop submission early so invalid form data never reaches the backend from the UI.
      setFormError(validationError);
      return;
    }

    setSubmitting(true);

    try {
      const payload = {
        username: formData.username.trim(),
        password: formData.password.trim(),
        email: formData.email.trim(),
        phone: formData.phone.trim(),
        role: formData.role,
        buildingId: Number(formData.buildingId),
      };

      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        // Backend validation messages are shown directly when they are already safe and concise.
        let message = 'Failed to create staff account.';

        try {
          const errorData = await response.json();
          message = getFriendlyErrorMessage(errorData.message || message);
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

  const isAddSubmitDisabled = submitting
    || !formData.username.trim()
    || !formData.password.trim()
    || !formData.email.trim()
    || !formData.phone.trim()
    || !formData.buildingId.trim();

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
          <p className="staff-subtitle">Manage staff accounts, roles, and building assignments ({staffList.length} members)</p>
        </div>
        <button type="button" className="add-staff-btn" onClick={openAddModal}>
          + Add Staff
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
                      {staff.status === 'ACTIVE' ? (
                        <button
                          type="button"
                          className="action-btn deactivate-btn"
                          onClick={() => handleStatusUpdate(staff.id, 'INACTIVE')}
                          disabled={processingStatusId !== null}
                        >
                          Deactivate
                        </button>
                      ) : (
                        <button
                          type="button"
                          className="action-btn activate-btn"
                          onClick={() => handleStatusUpdate(staff.id, 'ACTIVE')}
                          disabled={processingStatusId !== null}
                        >
                          Activate
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
                ✕
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
                  required
                />
              </label>

              <label>
                Phone
                <input
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleInputChange}
                  required
                  inputMode="numeric"
                  pattern="[0-9]{10,11}"
                />
              </label>

              <label>
                Role
                <select name="role" value={formData.role} onChange={handleInputChange} required>
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
                  required
                />
              </label>

              <div className="form-actions">
                <button type="button" className="cancel-btn" onClick={closeAddModal} disabled={submitting}>
                  Cancel
                </button>
                <button type="submit" className="submit-btn" disabled={isAddSubmitDisabled}>
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
