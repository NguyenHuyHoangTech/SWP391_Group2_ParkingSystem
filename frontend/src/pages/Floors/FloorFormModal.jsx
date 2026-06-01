import { useState, useEffect } from 'react';
import floorApi from '../../api/floorApi';
import buildingApi from '../../api/buildingApi';
import vehicleTypeApi from '../../api/vehicleTypeApi';
import '../Zones/ZoneFormModal.css';

function FloorFormModal({ floor, onSaved, onClose }) {
  const isEdit = !!floor;
  const [form, setForm] = useState({
    name: floor?.name || '',
    floorLevel: floor?.floorLevel ?? '',
    capacity: floor?.capacity || '',
    buildingId: floor?.buildingId || '',
    vehicleTypeId: floor?.vehicleTypeId || '',
  });
  const [buildings, setBuildings] = useState([]);
  const [vehicleTypes, setVehicleTypes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    buildingApi.getAll().then((res) => setBuildings(res.data.data || []));
    vehicleTypeApi.getAll().then((res) => setVehicleTypes(res.data.data || []));
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name.trim()) { setError('Tên tầng không được để trống'); return; }
    if (form.floorLevel === '') { setError('Cấp tầng không được để trống'); return; }
    if (!form.capacity || Number(form.capacity) < 1) { setError('Sức chứa phải lớn hơn 0'); return; }
    if (!form.buildingId) { setError('Vui lòng chọn tòa nhà'); return; }
    if (!form.vehicleTypeId) { setError('Vui lòng chọn loại xe cho tầng này'); return; }

    setLoading(true);
    setError('');
    try {
      const payload = {
        name: form.name.trim(),
        floorLevel: Number(form.floorLevel),
        capacity: Number(form.capacity),
        buildingId: Number(form.buildingId),
        vehicleTypeId: Number(form.vehicleTypeId),
      };
      if (isEdit) {
        await floorApi.update(floor.id, payload);
      } else {
        await floorApi.create(payload);
      }
      onSaved();
    } catch (err) {
      setError(err.response?.data?.message || 'Đã xảy ra lỗi. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{isEdit ? 'Cập nhật Tầng' : 'Thêm Tầng mới'}</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>

        <form className="modal-form" onSubmit={handleSubmit}>
          {error && <div className="form-error">{error}</div>}

          <div className="form-group">
            <label htmlFor="floor-name">Tên tầng *</label>
            <input id="floor-name" name="name" type="text" placeholder="VD: Tầng hầm 1, Tầng 1..." value={form.name} onChange={handleChange} />
          </div>

          <div className="form-group">
            <label htmlFor="floor-level">Cấp tầng *</label>
            <input id="floor-level" name="floorLevel" type="number" placeholder="VD: -1 (hầm 1), 1, 2..." value={form.floorLevel} onChange={handleChange} />
          </div>

          <div className="form-group">
            <label htmlFor="floor-capacity">Sức chứa tối đa (ô đỗ) *</label>
            <input id="floor-capacity" name="capacity" type="number" min="1" placeholder="VD: 100" value={form.capacity} onChange={handleChange} />
          </div>

          <div className="form-group">
            <label htmlFor="floor-vehicle">Loại xe *</label>
            <select id="floor-vehicle" name="vehicleTypeId" value={form.vehicleTypeId} onChange={handleChange}>
              <option value="">-- Chọn loại xe --</option>
              {vehicleTypes.map((vt) => (
                <option key={vt.id} value={vt.id}>{vt.name}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="floor-building">Tòa nhà *</label>
            <select id="floor-building" name="buildingId" value={form.buildingId} onChange={handleChange}>
              <option value="">-- Chọn tòa nhà --</option>
              {buildings.map((b) => (
                <option key={b.id} value={b.id}>{b.name}</option>
              ))}
            </select>
          </div>

          <div className="modal-actions">
            <button type="button" className="btn-cancel" onClick={onClose}>Hủy</button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Đang lưu...' : isEdit ? 'Cập nhật' : 'Thêm mới'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default FloorFormModal;
