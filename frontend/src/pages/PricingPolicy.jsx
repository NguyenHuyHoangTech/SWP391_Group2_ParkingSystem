import { useState, useEffect, useCallback } from 'react';
import './StaffList.css'; // Reusing staff list css for layout consistency

const API_URL = 'http://localhost:8080/api/pricing-policies';

function PricingPolicy() {
  const [policies, setPolicies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    status: 'ACTIVE',
    vehicleTypeId: 1
  });

  const fetchPolicies = useCallback(async () => {
    try {
      setLoading(true);
      const response = await fetch(API_URL);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setPolicies(data);
      setError(null);
    } catch (e) {
      console.error('Error fetching policies:', e);
      setError('Failed to load pricing policies. Please check your backend connection.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPolicies();
  }, [fetchPolicies]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleAddPolicy = async (event) => {
    event.preventDefault();
    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setShowModal(false);
        setFormData({ name: '', status: 'ACTIVE', vehicleTypeId: 1 });
        await fetchPolicies();
      } else {
        alert('Failed to create pricing policy.');
      }
    } catch (e) {
      console.error('Error creating policy:', e);
      alert('An error occurred while creating the pricing policy.');
    }
  };

  const handleDeletePolicy = async (id) => {
    if (!window.confirm('Are you sure you want to delete this pricing policy?')) return;
    try {
      const response = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        await fetchPolicies();
      } else {
        alert('Failed to delete pricing policy.');
      }
    } catch (e) {
      console.error('Error deleting policy:', e);
      alert('An error occurred while deleting the pricing policy.');
    }
  };

  if (loading) return <div className="staff-loading">Loading pricing policies...</div>;
  if (error) return <div className="staff-error"><h3>Error</h3><p>{error}</p></div>;

  return (
    <div className="staff-container">
      <div className="staff-header">
        <div>
          <h1>Pricing Policies</h1>
          <span className="staff-count">Total Policies: {policies.length}</span>
        </div>
        <button className="add-staff-btn" onClick={() => setShowModal(true)}>
          + Add New Policy
        </button>
      </div>

      {policies.length === 0 ? (
        <div className="staff-empty">
          <p>No pricing policies found. Please add a new policy.</p>
        </div>
      ) : (
        <div className="table-responsive">
          <table className="staff-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Status</th>
                <th>Vehicle Type ID</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {policies.map((policy) => (
                <tr key={policy.id}>
                  <td>#{policy.id}</td>
                  <td><strong>{policy.name}</strong></td>
                  <td>
                    <span className={`role-badge role-${policy.status.toLowerCase()}`}>
                      {policy.status}
                    </span>
                  </td>
                  <td>{policy.vehicleType?.id || policy.vehicleTypeId || 'N/A'}</td>
                  <td>
                    <button className="action-btn delete-btn" onClick={() => handleDeletePolicy(policy.id)}>
                      Delete
                    </button>
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
            <h2>Add New Pricing Policy</h2>
            <form onSubmit={handleAddPolicy} className="staff-form">
              <div className="form-group">
                <label htmlFor="name">Policy Name</label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  value={formData.name}
                  onChange={handleInputChange}
                  required
                  placeholder="e.g. Daily Pass"
                />
              </div>
              <div className="form-group">
                <label htmlFor="status">Status</label>
                <select id="status" name="status" value={formData.status} onChange={handleInputChange}>
                  <option value="ACTIVE">Active</option>
                  <option value="INACTIVE">Inactive</option>
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="vehicleTypeId">Vehicle Type ID</label>
                <input
                  type="number"
                  id="vehicleTypeId"
                  name="vehicleTypeId"
                  value={formData.vehicleTypeId}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-actions">
                <button type="button" className="cancel-btn" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="submit-btn">Create Policy</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default PricingPolicy;
