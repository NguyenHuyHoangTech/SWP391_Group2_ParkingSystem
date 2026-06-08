import { useState, useEffect, useCallback } from 'react';
import buildingApi from '../../api/buildingApi';
import floorApi from '../../api/floorApi';
import zoneApi from '../../api/zoneApi';
import slotApi from '../../api/slotApi';

const page    = { padding: '24px', maxWidth: 1100, margin: '0 auto', fontFamily: 'Segoe UI,sans-serif' };
const header  = { display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 };
const select  = { padding: '8px 12px', borderRadius: 7, border: '1.5px solid #ddd', fontSize: '0.9rem', outline: 'none', minWidth: 180 };
const badge   = (color, bg) => ({ background: bg, color, padding: '3px 10px', borderRadius: 12, fontSize: '0.82rem', fontWeight: 600 });

const STATUS_CONFIG = {
  EMPTY:       { label: '🟢 Trống',    color: '#2e7d32', bg: '#e8f5e9' },
  OCCUPIED:    { label: '🔴 Có xe',    color: '#c62828', bg: '#fdecea' },
  MAINTENANCE: { label: '🔒 Bảo trì', color: '#e65100', bg: '#fff3e0' },
};

function SlotListPage() {
  const [buildings, setBuildings] = useState([]);
  const [floors,    setFloors]    = useState([]);
  const [zones,     setZones]     = useState([]);
  const [slots,     setSlots]     = useState([]);

  const [selBuilding, setSelBuilding] = useState('');
  const [selFloor,    setSelFloor]    = useState('');
  const [selZone,     setSelZone]     = useState('');

  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState('');
  const [actionId, setActionId] = useState(null);
  const [selected, setSelected] = useState(new Set()); // IDs được chọn để xóa

  // Load tòa nhà lúc khởi động
  useEffect(() => {
    buildingApi.getAll()
      .then(r => setBuildings(r.data.data || []))
      .catch(() => {});
  }, []);

  // Khi chọn tòa nhà → load tầng theo tòa
  useEffect(() => {
    setFloors([]); setZones([]); setSlots([]);
    setSelFloor(''); setSelZone('');
    if (!selBuilding) return;
    floorApi.getAll()
      .then(r => {
        const all = r.data.data || [];
        setFloors(all.filter(f => String(f.buildingId) === String(selBuilding)));
      })
      .catch(() => {});
  }, [selBuilding]);

  // Khi chọn tầng → load zone theo tầng
  useEffect(() => {
    setZones([]); setSlots([]);
    setSelZone('');
    if (!selFloor) return;
    zoneApi.getAll(selFloor)
      .then(r => setZones(r.data.data || []))
      .catch(() => {});
  }, [selFloor]);

  // Khi chọn zone → load slots
  useEffect(() => {
    setSlots([]);
    if (!selZone) return;
    fetchSlots(selZone);
  }, [selZone]);

  const fetchSlots = useCallback(async (zoneId) => {
    if (!zoneId) return;
    setLoading(true); setError('');
    setSelected(new Set()); // reset selection khi load lại
    try {
      const r = await slotApi.getByZone(zoneId);
      setSlots(r.data.data || []);
    } catch {
      setError('Không thể tải danh sách ô đỗ.');
    } finally {
      setLoading(false);
    }
  }, []);

  const handleLock = async (slot) => {
    if (!window.confirm(`Khóa ô "${slot.name}" để bảo trì?`)) return;
    setActionId(slot.id);
    try {
      await slotApi.lock(slot.id);
      await fetchSlots(selZone);
    } catch (err) {
      alert(err.response?.data?.message || 'Khóa thất bại!');
    } finally {
      setActionId(null);
    }
  };

  const handleUnlock = async (slot) => {
    if (!window.confirm(`Mở ô "${slot.name}"?`)) return;
    setActionId(slot.id);
    try {
      await slotApi.unlock(slot.id);
      await fetchSlots(selZone);
    } catch (err) {
      alert(err.response?.data?.message || 'Mở thất bại!');
    } finally {
      setActionId(null);
    }
  };

  const handleOccupy = async (slot) => {
    if (!window.confirm(`Đánh dấu ô "${slot.name}" có xe đậu?`)) return;
    setActionId(slot.id);
    try {
      await slotApi.occupy(slot.id);
      await fetchSlots(selZone);
    } catch (err) {
      alert(err.response?.data?.message || 'Thao tác thất bại!');
    } finally {
      setActionId(null);
    }
  };

  const handleVacate = async (slot) => {
    if (!window.confirm(`Đánh dấu ô "${slot.name}" xe đã ra?`)) return;
    setActionId(slot.id);
    try {
      await slotApi.vacate(slot.id);
      await fetchSlots(selZone);
    } catch (err) {
      alert(err.response?.data?.message || 'Thao tác thất bại!');
    } finally {
      setActionId(null);
    }
  };

  // Xóa 1 ô đỗ
  const handleDelete = async (slot) => {
    if (!window.confirm(`Xóa ô đỗ "${slot.name}"?`)) return;
    setActionId(slot.id);
    try {
      await slotApi.delete(slot.id);
      setSlots(prev => prev.filter(s => s.id !== slot.id));
      setSelected(prev => { const n = new Set(prev); n.delete(slot.id); return n; });
    } catch(err) {
      alert(err.response?.data?.message || 'Xóa thất bại!');
    } finally { setActionId(null); }
  };

  // Xóa nhiều ô đỗ đã chọn
  const handleBulkDelete = async () => {
    if (selected.size === 0) return;
    if (!window.confirm(`Xóa ${selected.size} ô đỗ đã chọn?`)) return;
    try {
      await slotApi.bulkDelete([...selected]);
      setSlots(prev => prev.filter(s => !selected.has(s.id)));
      setSelected(new Set());
    } catch(err) {
      alert(err.response?.data?.message || 'Xóa thất bại!');
    }
  };

  // Toggle chọn 1 ô
  const toggleSelect = (id) => setSelected(prev => {
    const n = new Set(prev);
    n.has(id) ? n.delete(id) : n.add(id);
    return n;
  });

  // Chọn / bỏ chọn tất cả
  const allSelected = slots.length > 0 && selected.size === slots.length;
  const toggleAll = () => setSelected(allSelected ? new Set() : new Set(slots.map(s => s.id)));

  // Tính thống kê
  const stats = {
    total:       slots.length,
    empty:       slots.filter(s => s.status === 'EMPTY').length,
    occupied:    slots.filter(s => s.status === 'OCCUPIED').length,
    maintenance: slots.filter(s => s.status === 'MAINTENANCE').length,
  };

  return (
    <div style={page}>
      {/* Header */}
      <div style={header}>
        <div>
          <h1 style={{ margin: '0 0 4px', fontSize: '1.6rem', fontWeight: 700, color: '#1a1a2e' }}>Quản lý Ô đỗ xe</h1>
          <p style={{ margin: 0, color: '#666', fontSize: '0.9rem' }}>Xem trạng thái và khóa/mở các ô đỗ để bảo trì</p>
        </div>
      </div>

      {/* Filter 3 cấp */}
      <div style={{ display: 'flex', gap: 12, alignItems: 'center', flexWrap: 'wrap', marginBottom: 16 }}>
        {/* Tòa nhà */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <label style={{ fontWeight: 600, color: '#444', fontSize: '0.9rem', whiteSpace: 'nowrap' }}>🏢 Tòa nhà:</label>
          <select value={selBuilding} onChange={e => setSelBuilding(e.target.value)} style={select}>
            <option value="">-- Chọn tòa nhà --</option>
            {buildings.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
          </select>
        </div>

        {/* Tầng */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <label style={{ fontWeight: 600, color: '#444', fontSize: '0.9rem', whiteSpace: 'nowrap' }}>🏗️ Tầng:</label>
          <select value={selFloor} onChange={e => setSelFloor(e.target.value)} style={select} disabled={!selBuilding}>
            <option value="">-- Chọn tầng --</option>
            {floors.map(f => <option key={f.id} value={f.id}>{f.name}</option>)}
          </select>
        </div>

        {/* Khu vực */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <label style={{ fontWeight: 600, color: '#444', fontSize: '0.9rem', whiteSpace: 'nowrap' }}>🅿️ Khu vực:</label>
          <select value={selZone} onChange={e => setSelZone(e.target.value)} style={select} disabled={!selFloor}>
            <option value="">-- Chọn khu vực --</option>
            {zones.map(z => <option key={z.id} value={z.id}>{z.name}</option>)}
          </select>
        </div>
      </div>

      {/* Thanh thống kê — chỉ hiện khi đã chọn zone */}
      {selZone && slots.length > 0 && (
        <div style={{ display: 'flex', gap: 10, marginBottom: 16, flexWrap: 'wrap' }}>
          {[
            { label: 'Tổng',     value: stats.total,       color: '#1a73e8', bg: '#e8f0fe' },
            { label: 'Trống',    value: stats.empty,       color: '#2e7d32', bg: '#e8f5e9' },
            { label: 'Có xe',    value: stats.occupied,    color: '#c62828', bg: '#fdecea' },
            { label: 'Bảo trì', value: stats.maintenance, color: '#e65100', bg: '#fff3e0' },
          ].map(s => (
            <span key={s.label} style={{ background: s.bg, color: s.color, padding: '6px 16px', borderRadius: 20, fontSize: '0.85rem', fontWeight: 600 }}>
              {s.label}: {s.value}
            </span>
          ))}
        </div>
      )}

      {/* Toolbar xóa nhiều */}
      {selected.size > 0 && (
        <div style={{ display:'flex', alignItems:'center', gap:12, marginBottom:12, padding:'10px 16px', background:'#fdecea', borderRadius:8, border:'1px solid #ef9a9a' }}>
          <span style={{ fontWeight:600, color:'#c62828' }}>Đã chọn {selected.size} ô đỗ</span>
          <button onClick={handleBulkDelete}
            style={{ padding:'6px 18px', borderRadius:6, border:'none', background:'#e53935', color:'#fff', cursor:'pointer', fontWeight:700, fontSize:'0.88rem' }}>
            🗑️ Xóa {selected.size} ô đã chọn
          </button>
          <button onClick={() => setSelected(new Set())}
            style={{ padding:'6px 14px', borderRadius:6, border:'1px solid #ccc', background:'#fff', cursor:'pointer', fontSize:'0.85rem' }}>
            Bỏ chọn
          </button>
        </div>
      )}

      {/* Error */}
      {error && <div style={{ background: '#fdecea', color: '#c62828', padding: '10px 14px', borderRadius: 7, marginBottom: 14 }}>{error}</div>}

      {/* Trạng thái chưa chọn */}
      {!selZone && !loading && (
        <div style={{ textAlign: 'center', padding: '60px 0', color: '#999' }}>
          <div style={{ fontSize: '3rem', marginBottom: 12 }}>🅿️</div>
          <p style={{ fontSize: '1rem' }}>Vui lòng chọn Tòa nhà → Tầng → Khu vực để xem danh sách ô đỗ</p>
        </div>
      )}

      {/* Loading */}
      {loading && <p style={{ textAlign: 'center', color: '#888' }}>Đang tải...</p>}

      {/* Bảng slot */}
      {!loading && selZone && (
        <div style={{ borderRadius: 10, overflow: 'hidden', boxShadow: '0 2px 12px rgba(0,0,0,0.08)' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', background: '#fff' }}>
            <thead>
              <tr style={{ background: '#1a1a2e', color: '#fff' }}>
                <th style={{ padding:'12px 14px',width:36 }}>
                  <input type="checkbox" checked={allSelected} onChange={toggleAll}
                    style={{ cursor:'pointer', width:15, height:15 }} />
                </th>
                {['#', 'Tên ô', 'Khu vực', 'Tầng', 'Trạng thái', 'Thao tác'].map(h => (
                  <th key={h} style={{ padding: '12px 14px', textAlign: 'left', fontSize: '0.82rem', fontWeight: 600, letterSpacing: '0.3px' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {slots.length === 0 ? (
                <tr><td colSpan={7} style={{ textAlign: 'center', padding: 40, color: '#999' }}>Khu vực này chưa có ô đỗ nào</td></tr>
              ) : slots.map((slot, i) => {
                const cfg = STATUS_CONFIG[slot.status] || { label: slot.status, color: '#333', bg: '#eee' };
                const isProcessing = actionId === slot.id;
                const isSelected = selected.has(slot.id);
                return (
                  <tr key={slot.id} style={{ borderBottom: '1px solid #f0f0f0', background: isSelected ? '#e8f0fe' : slot.status === 'MAINTENANCE' ? '#fffbf5' : 'white' }}>
                    <td style={{ padding:'8px 14px' }}>
                      <input type="checkbox" checked={isSelected} onChange={() => toggleSelect(slot.id)}
                        style={{ cursor:'pointer', width:15, height:15 }} />
                    </td>
                    <td style={{ padding: '12px 14px', fontSize: '0.9rem', color: '#888' }}>{i + 1}</td>
                    <td style={{ padding: '12px 14px', fontWeight: 700, fontSize: '1rem' }}>{slot.name}</td>
                    <td style={{ padding: '12px 14px', color: '#555' }}>{slot.zoneName}</td>
                    <td style={{ padding: '12px 14px', color: '#555' }}>{slot.floorName}</td>
                    <td style={{ padding: '12px 14px' }}>
                      <span style={badge(cfg.color, cfg.bg)}>{cfg.label}</span>
                    </td>
                    <td style={{ padding: '12px 14px' }}>
                      <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                        {slot.status === 'EMPTY' && (<>
                          <button disabled={isProcessing} onClick={() => handleOccupy(slot)}
                            style={{ background: '#fdecea', color: '#c62828', border: '1px solid #ef9a9a', padding: '5px 12px', borderRadius: 6, cursor: 'pointer', fontSize: '0.82rem', fontWeight: 600, opacity: isProcessing ? 0.6 : 1 }}>
                            {isProcessing ? '...' : '🚗 Xe vào'}
                          </button>
                          <button disabled={isProcessing} onClick={() => handleLock(slot)}
                            style={{ background: '#fff3e0', color: '#e65100', border: '1px solid #ffcc80', padding: '5px 12px', borderRadius: 6, cursor: 'pointer', fontSize: '0.82rem', fontWeight: 600, opacity: isProcessing ? 0.6 : 1 }}>
                            {isProcessing ? '...' : '🔒 Khóa'}
                          </button>
                        </>)}
                        {slot.status === 'OCCUPIED' && (
                          <button disabled={isProcessing} onClick={() => handleVacate(slot)}
                            style={{ background: '#e8f5e9', color: '#2e7d32', border: '1px solid #a5d6a7', padding: '5px 12px', borderRadius: 6, cursor: 'pointer', fontSize: '0.82rem', fontWeight: 600, opacity: isProcessing ? 0.6 : 1 }}>
                            {isProcessing ? '...' : '🚗 Xe ra'}
                          </button>
                        )}
                        {slot.status === 'MAINTENANCE' && (
                          <button disabled={isProcessing} onClick={() => handleUnlock(slot)}
                            style={{ background: '#e8f5e9', color: '#2e7d32', border: '1px solid #a5d6a7', padding: '5px 12px', borderRadius: 6, cursor: 'pointer', fontSize: '0.82rem', fontWeight: 600, opacity: isProcessing ? 0.6 : 1 }}>
                            {isProcessing ? '...' : '🔓 Mở'}
                          </button>
                        )}
                        <button disabled={isProcessing} onClick={() => handleDelete(slot)}
                          style={{ background: '#fff', color: '#e53935', border: '1px solid #ef9a9a', padding: '5px 10px', borderRadius: 6, cursor: 'pointer', fontSize: '0.82rem', fontWeight: 600, opacity: isProcessing ? 0.6 : 1 }}>
                          🗑️
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default SlotListPage;
