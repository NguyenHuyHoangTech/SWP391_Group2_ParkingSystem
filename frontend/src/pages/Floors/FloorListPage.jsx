import { useState, useEffect } from 'react';
import floorApi from '../../api/floorApi';
import FloorFormModal from './FloorFormModal';
import './FloorListPage.css';

function FloorListPage() {
  const [floors, setFloors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingFloor, setEditingFloor] = useState(null);

  const isAdmin = localStorage.getItem('userRole') === 'ADMIN';

  const fetchFloors = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await floorApi.getAll();
      setFloors(res.data.data || []);
    } catch {
      setError('Không thể tải danh sách tầng. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchFloors(); }, []);

  const handleAdd = () => { setEditingFloor(null); setShowModal(true); };
  const handleEdit = (floor) => { setEditingFloor(floor); setShowModal(true); };
  const handleSaved = () => { setShowModal(false); fetchFloors(); };

  const handleDelete = async (floor) => {
    if (!window.confirm(`Xóa tầng "${floor.name}"? Thao tác này không thể hoàn tác.`)) return;
    try {
      await floorApi.delete(floor.id);
      fetchFloors();
    } catch (err) {
      alert(err.response?.data?.message || 'Xóa thất bại!');
    }
  };

  const getCapacityPercent = (floor) => {
    if (!floor.capacity) return 0;
    return Math.round((floor.usedCapacity / floor.capacity) * 100);
  };

  return (
    <div className="floor-page">
      <div className="floor-header">
        <div>
          <h1>Quản lý Tầng</h1>
          <p className="floor-subtitle">Cấu hình các tầng và sức chứa trong tòa nhà</p>
        </div>
        {isAdmin && (
          <button className="btn-primary" onClick={handleAdd}>
            + Thêm Tầng
          </button>
        )}
      </div>

      {error && <div className="floor-error">{error}</div>}

      {loading ? (
        <div className="floor-loading">Đang tải...</div>
      ) : (
        <div className="floor-table-wrapper">
          <table className="floor-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Tên tầng</th>
                <th>Cấp tầng</th>
                <th>Loại xe</th>
                <th>Tòa nhà</th>
                <th>Sức chứa</th>
                <th>Đã dùng</th>
                <th>Còn trống</th>
                <th>Tình trạng</th>
                {isAdmin && <th>Thao tác</th>}
              </tr>
            </thead>
            <tbody>
              {floors.length === 0 ? (
                <tr>
                  <td colSpan={isAdmin ? 10 : 9} className="floor-empty">
                    Chưa có tầng nào
                  </td>
                </tr>
              ) : (
                floors.map((floor, idx) => {
                  const pct = getCapacityPercent(floor);
                  return (
                    <tr key={floor.id}>
                      <td>{idx + 1}</td>
                      <td><strong>{floor.name}</strong></td>
                      <td>
                        <span className="level-badge">
                          {floor.floorLevel < 0 ? `Hầm ${Math.abs(floor.floorLevel)}` : `Tầng ${floor.floorLevel}`}
                        </span>
                      </td>
                      <td>
                        {floor.vehicleTypeName
                          ? <span className="vehicle-type-badge">{floor.vehicleTypeName}</span>
                          : <span style={{color:'#999'}}>Chưa cấu hình</span>}
                      </td>
                      <td>{floor.buildingName}</td>
                      <td>{floor.capacity}</td>
                      <td>{floor.usedCapacity ?? 0}</td>
                      <td>
                        <span className={`remaining ${(floor.remainingCapacity ?? floor.capacity) === 0 ? 'full' : ''}`}>
                          {floor.remainingCapacity ?? floor.capacity}
                        </span>
                      </td>
                      <td>
                        <div className="progress-bar-wrap">
                          <div
                            className="progress-bar-fill"
                            style={{ width: `${pct}%`, background: pct >= 100 ? '#e53935' : pct >= 80 ? '#fb8c00' : '#43a047' }}
                          />
                          <span className="progress-label">{pct}%</span>
                        </div>
                      </td>
                      {isAdmin && (
                        <td className="action-cell">
                          <button className="btn-edit" onClick={() => handleEdit(floor)}>Sửa</button>
                          <button className="btn-delete" onClick={() => handleDelete(floor)}>Xóa</button>
                        </td>
                      )}
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      )}

      {showModal && (
        <FloorFormModal
          floor={editingFloor}
          onSaved={handleSaved}
          onClose={() => setShowModal(false)}
        />
      )}
    </div>
  );
}

export default FloorListPage;
