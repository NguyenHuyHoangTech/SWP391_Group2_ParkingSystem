import { useState, useEffect } from 'react';
import zoneApi from '../../api/zoneApi';
import floorApi from '../../api/floorApi';
import './ZoneFormModal.css';

function ZoneFormModal({ zone, floors, defaultFloorId, onSaved, onClose }) {
  const isEdit = !!zone;

  const [form, setForm] = useState({
    name: zone?.name || '',
    floorId: zone?.floorId || defaultFloorId || '',
    capacity: zone?.capacity || '',
  });
  const [floorDetail, setFloorDetail] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Khi chọn tầng, load thông tin remaining capacity và loại xe của tầng
  useEffect(() => {
    if (form.floorId) {
      floorApi.getById(form.floorId).then((res) => {
        const floor = res.data.data;
        if (isEdit) {
          // Khi edit: remaining = floor.remainingCapacity + capacity của zone hiện tại
          setFloorDetail({
            ...floor,
            remainingCapacity: (floor.remainingCapacity ?? 0) + Number(zone.capacity),
          });
        } else {
          setFloorDetail(floor);
        }
      }).catch(() => setFloorDetail(null));
    } else {
      setFloorDetail(null);
    }
  }, [form.floorId]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name.trim()) { setError('Tên khu vực không được để trống'); return; }
    if (!form.floorId) { setError('Vui lòng chọn tầng'); return; }
    if (!form.capacity || Number(form.capacity) < 1) { setError('Sức chứa phải lớn hơn 0'); return; }

    if (floorDetail && Number(form.capacity) > floorDetail.remainingCapacity) {
      setError(
        `Sức chứa vượt quá giới hạn! Tầng "${floorDetail.name}" chỉ còn `
        + `${floorDetail.remainingCapacity} chỗ trống (sức chứa tối đa: ${floorDetail.capacity}).`
      );
      return;
    }

    setLoading(true);
    setError('');
    try {
      const payload = {
        name: form.name.trim(),
        floorId: Number(form.floorId),
        capacity: Number(form.capacity),
      };
      if (isEdit) {
        await zoneApi.update(zone.id, payload);
      } else {
        await zoneApi.create(payload);
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
          <h2>{isEdit ? 'Cập nhật Khu vực' : 'Thêm Khu vực mới'}</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>

        <form className="modal-form" onSubmit={handleSubmit}>
          {error && <div className="form-error">{error}</div>}

          <div className="form-group">
            <label htmlFor="zone-name">Tên khu vực *</label>
            <input
              id="zone-name" name="name" type="text"
              placeholder="VD: Zone A, Khu A..." value={form.name} onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label htmlFor="zone-floor">Tầng *</label>
            <select id="zone-floor" name="floorId" value={form.floorId} onChange={handleChange}>
              <option value="">-- Chọn tầng --</option>
              {floors.map((f) => (
                <option key={f.id} value={f.id}>
                  {f.name} {f.buildingName ? `(${f.buildingName})` : ''}
                  {f.vehicleTypeName ? ` — ${f.vehicleTypeName}` : ''}
                </option>
              ))}
            </select>

            {/* Hiển thị thông tin tầng sau khi chọn */}
            {floorDetail && (
              <div className="capacity-hint">
                <span>🚗 Loại xe: <strong>{floorDetail.vehicleTypeName || 'Chưa cấu hình'}</strong></span>
                <span style={{margin: '0 8px'}}>|</span>
                <span>Sức chứa: <strong>{floorDetail.capacity}</strong></span>
                <span style={{margin: '0 8px'}}>|</span>
                <span>Đã dùng: <strong>{floorDetail.usedCapacity ?? 0}</strong></span>
                <span style={{margin: '0 8px'}}>|</span>
                <span>Còn trống: <strong className={floorDetail.remainingCapacity === 0 ? 'text-red' : 'text-green'}>
                  {floorDetail.remainingCapacity}
                </strong></span>
              </div>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="zone-capacity">
              Sức chứa *
              {floorDetail && <span className="capacity-max"> (tối đa {floorDetail.remainingCapacity} chỗ)</span>}
            </label>
            <input
              id="zone-capacity" name="capacity" type="number"
              min="1"
              placeholder="VD: 50" value={form.capacity} onChange={handleChange}
            />
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

export default ZoneFormModal;
