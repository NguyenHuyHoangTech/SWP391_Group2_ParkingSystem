import { useState } from 'react';
import floorApi from '../../api/floorApi';
import './ZoneFormModal.css'; // reuse CSS

function FloorEditModal({ floor, onSaved, onClose }) {
  const [form, setForm] = useState({
    name: floor.name || '',
    floorLevel: floor.floorLevel ?? '',
    capacity: floor.capacity || '',
    buildingId: floor.buildingId || '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name.trim()) { setError('Tên tầng không được để trống'); return; }
    if (form.capacity === '' || Number(form.capacity) < 1) {
      setError('Sức chứa phải lớn hơn 0'); return;
    }

    setLoading(true);
    setError('');
    try {
      await floorApi.update(floor.id, {
        name: form.name.trim(),
        floorLevel: Number(form.floorLevel),
        capacity: Number(form.capacity),
        buildingId: Number(form.buildingId),
      });
      onSaved();
    } catch (err) {
      const msg = err.response?.data?.message || 'Đã xảy ra lỗi. Vui lòng thử lại.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Cấu hình Tầng</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>

        <form className="modal-form" onSubmit={handleSubmit}>
          {error && <div className="form-error">{error}</div>}

          <div className="form-group">
            <label htmlFor="floor-name">Tên tầng *</label>
            <input
              id="floor-name"
              name="name"
              type="text"
              value={form.name}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label htmlFor="floor-level">Cấp tầng *</label>
            <input
              id="floor-level"
              name="floorLevel"
              type="number"
              placeholder="VD: -1 (hầm), 1, 2..."
              value={form.floorLevel}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label htmlFor="floor-capacity">Sức chứa tối đa (ô đỗ) *</label>
            <input
              id="floor-capacity"
              name="capacity"
              type="number"
              min="1"
              value={form.capacity}
              onChange={handleChange}
            />
          </div>

          <div className="modal-actions">
            <button type="button" className="btn-cancel" onClick={onClose}>Hủy</button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Đang lưu...' : 'Cập nhật'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default FloorEditModal;
