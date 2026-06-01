import { useState, useEffect } from 'react';
import zoneApi from '../../api/zoneApi';
import floorApi from '../../api/floorApi';
import ZoneFormModal from './ZoneFormModal';
import './ZoneListPage.css';

function ZoneListPage() {
  const [zones, setZones] = useState([]);
  const [floors, setFloors] = useState([]);
  const [selectedFloorId, setSelectedFloorId] = useState('');
  const [selectedFloor, setSelectedFloor] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingZone, setEditingZone] = useState(null);

  const isAdmin = localStorage.getItem('userRole') === 'ADMIN';

  const fetchZones = async (floorId) => {
    setLoading(true);
    setError('');
    try {
      const res = await zoneApi.getAll(floorId || undefined);
      setZones(res.data.data || []);
    } catch {
      setError('Không thể tải danh sách khu vực. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  };

  const fetchFloors = async () => {
    try {
      const res = await floorApi.getAll();
      setFloors(res.data.data || []);
    } catch { /* ignore */ }
  };

  useEffect(() => {
    fetchFloors();
    fetchZones('');
  }, []);

  const handleFloorFilter = (e) => {
    const fid = e.target.value;
    setSelectedFloorId(fid);
    setSelectedFloor(floors.find((f) => String(f.id) === fid) || null);
    fetchZones(fid);
  };

  const handleAdd = () => { setEditingZone(null); setShowModal(true); };
  const handleEdit = (zone) => { setEditingZone(zone); setShowModal(true); };
  const handleSaved = () => {
    setShowModal(false);
    fetchZones(selectedFloorId);
    // Refresh floor info để cập nhật remaining capacity
    fetchFloors();
  };

  const handleDelete = async (zone) => {
    if (!window.confirm(`Xóa khu vực "${zone.name}"?`)) return;
    try {
      await zoneApi.delete(zone.id);
      fetchZones(selectedFloorId);
      fetchFloors();
    } catch (err) {
      alert(err.response?.data?.message || 'Xóa thất bại!');
    }
  };

  // Tính remaining capacity từ floor đang chọn (đã được cập nhật từ API)
  const currentFloor = selectedFloorId
    ? floors.find((f) => String(f.id) === selectedFloorId)
    : null;

  return (
    <div className="zone-page">
      <div className="zone-header">
        <div>
          <h1>Quản lý Khu vực</h1>
          <p className="zone-subtitle">Cấu hình các khu vực đỗ xe trong từng tầng</p>
        </div>
        {isAdmin && (
          <button className="btn-primary" onClick={handleAdd}>
            + Thêm Khu vực
          </button>
        )}
      </div>

      <div className="zone-toolbar">
        <label htmlFor="floor-filter">Lọc theo tầng:</label>
        <select id="floor-filter" value={selectedFloorId} onChange={handleFloorFilter}>
          <option value="">-- Tất cả tầng --</option>
          {floors.map((f) => (
            <option key={f.id} value={f.id}>
              {f.name} {f.buildingName ? `(${f.buildingName})` : ''}
            </option>
          ))}
        </select>
      </div>

      {/* Info bar hiện khi chọn 1 tầng */}
      {currentFloor && (
        <div className="floor-info-bar">
          <span className="info-item">
            📐 <strong>{currentFloor.name}</strong>
          </span>
          <span className="info-sep">|</span>
          <span className="info-item">
            Sức chứa: <strong>{currentFloor.capacity}</strong>
          </span>
          <span className="info-sep">|</span>
          <span className="info-item">
            Đã dùng: <strong>{currentFloor.usedCapacity ?? 0}</strong>
          </span>
          <span className="info-sep">|</span>
          <span className={`info-item ${(currentFloor.remainingCapacity ?? currentFloor.capacity) === 0 ? 'info-full' : 'info-ok'}`}>
            Còn trống: <strong>{currentFloor.remainingCapacity ?? currentFloor.capacity}</strong>
          </span>
        </div>
      )}

      {error && <div className="zone-error">{error}</div>}

      {loading ? (
        <div className="zone-loading">Đang tải...</div>
      ) : (
        <div className="zone-table-wrapper">
          <table className="zone-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Tên khu vực</th>
                <th>Tầng</th>
                <th>Tòa nhà</th>
                <th>Loại xe</th>
                <th>Sức chứa</th>
                {isAdmin && <th>Thao tác</th>}
              </tr>
            </thead>
            <tbody>
              {zones.length === 0 ? (
                <tr>
                  <td colSpan={isAdmin ? 7 : 6} className="zone-empty">
                    Chưa có khu vực nào
                  </td>
                </tr>
              ) : (
                zones.map((zone, idx) => (
                  <tr key={zone.id}>
                    <td>{idx + 1}</td>
                    <td><strong>{zone.name}</strong></td>
                    <td>{zone.floorName}</td>
                    <td>{zone.buildingName}</td>
                    <td><span className="badge">{zone.vehicleTypeName}</span></td>
                    <td>{zone.capacity}</td>
                    {isAdmin && (
                      <td className="action-cell">
                        <button className="btn-edit" onClick={() => handleEdit(zone)}>Sửa</button>
                        <button className="btn-delete" onClick={() => handleDelete(zone)}>Xóa</button>
                      </td>
                    )}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {showModal && (
        <ZoneFormModal
          zone={editingZone}
          floors={floors}
          defaultFloorId={selectedFloorId}
          onSaved={handleSaved}
          onClose={() => setShowModal(false)}
        />
      )}
    </div>
  );
}

export default ZoneListPage;
