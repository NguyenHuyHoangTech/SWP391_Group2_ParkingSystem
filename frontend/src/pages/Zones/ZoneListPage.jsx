import { useState, useEffect } from 'react';
import zoneApi from '../../api/zoneApi';
import floorApi from '../../api/floorApi';
import buildingApi from '../../api/buildingApi';
import ZoneFormModal from './ZoneFormModal';
import GenerateSlotsModal from './GenerateSlotsModal';

const page = { padding:'24px',maxWidth:1100,margin:'0 auto',fontFamily:'Segoe UI,sans-serif' };
const header = { display:'flex',alignItems:'center',justifyContent:'space-between',marginBottom:20 };
const btnPrimary = { background:'#1a73e8',color:'#fff',border:'none',padding:'10px 20px',borderRadius:8,fontWeight:600,cursor:'pointer',fontSize:'0.9rem' };
const btnEdit = { background:'#fff3e0',color:'#e65100',border:'1px solid #ffcc80',padding:'5px 14px',borderRadius:6,cursor:'pointer',fontSize:'0.82rem',fontWeight:500 };
const btnDel = { background:'#fdecea',color:'#c62828',border:'1px solid #ef9a9a',padding:'5px 14px',borderRadius:6,cursor:'pointer',fontSize:'0.82rem',fontWeight:500 };
const badge = (color, bg) => ({ background:bg,color,padding:'3px 10px',borderRadius:12,fontSize:'0.82rem',fontWeight:500 });

function ZoneListPage() {
  const [zones, setZones] = useState([]);
  const [buildings, setBuildings] = useState([]);
  const [floors, setFloors] = useState([]);
  const [allFloors, setAllFloors] = useState([]);
  const [selectedBuilding, setSelectedBuilding] = useState('');
  const [selectedFloor, setSelectedFloor] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showModal,    setShowModal]    = useState(false);
  const [editing,      setEditing]      = useState(null);
  const [showGenerate, setShowGenerate] = useState(false);
  const [generating,   setGenerating]   = useState(null); // zone được chọn để generate

  const fetchFloors = async () => {
    try {
      const r = await floorApi.getAll();
      setAllFloors(r.data.data || []);
    } catch {}
  };

  // Khi chọn tòa nhà → lọc tầng
  useEffect(() => {
    buildingApi.getAll().then(r => setBuildings(r.data.data || [])).catch(() => {});
    fetchFloors();
  }, []);

  useEffect(() => {
    setSelectedFloor('');
    if (!selectedBuilding) {
      setFloors(allFloors);
    } else {
      setFloors(allFloors.filter(f => String(f.buildingId) === String(selectedBuilding)));
    }
  }, [selectedBuilding, allFloors]);

  const fetchZones = async () => {
    setLoading(true); setError('');
    try { const r = await zoneApi.getAll(selectedFloor || null); setZones(r.data.data || []); }
    catch { setError('Không thể tải danh sách khu vực.'); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchZones(); }, [selectedFloor]);

  const selectedFloorData = floors.find(f => String(f.id) === String(selectedFloor));

  const handleDelete = async (zone) => {
    const slotCount = zone.totalSlots || 0;
    const occupiedCount = zone.occupiedSlots || 0;
    if (occupiedCount > 0) {
      alert(`Không thể xóa khu vực "${zone.name}" vì đang có ${occupiedCount} xe đang đỗ!\nHãy cho xe ra trước.`);
      return;
    }
    const msg = slotCount > 0
      ? `Xóa khu vực "${zone.name}"?\n⚠️ Khu vực này có ${slotCount} ô đỗ — tất cả sẽ bị xóa theo!`
      : `Xóa khu vực "${zone.name}"?`;
    if (!window.confirm(msg)) return;
    try { await zoneApi.delete(zone.id); fetchZones(); fetchFloors(); }
    catch(err) { alert(err.response?.data?.message || 'Xóa thất bại!'); }
  };

  return (
    <div style={page}>
      <div style={header}>
        <div>
          <h1 style={{ margin:'0 0 4px',fontSize:'1.6rem',fontWeight:700,color:'#1a1a2e' }}>Quản lý Khu vực</h1>
          <p style={{ margin:0,color:'#666',fontSize:'0.9rem' }}>Quản lý các khu vực đỗ xe trong từng tầng</p>
        </div>
        <button style={btnPrimary} onClick={() => { setEditing(null); setShowModal(true); }}>+ Thêm Khu vực</button>
      </div>

      {/* Filter by building + floor */}
      <div style={{ marginBottom:16, display:'flex', alignItems:'center', gap:12, flexWrap:'wrap' }}>
        <label style={{ fontWeight:600, color:'#444' }}>🏢 Tòa nhà:</label>
        <select value={selectedBuilding} onChange={e => setSelectedBuilding(e.target.value)}
          style={{ padding:'8px 12px', borderRadius:7, border:'1.5px solid #ddd', fontSize:'0.9rem', outline:'none', minWidth:160 }}>
          <option value="">Tất cả tòa nhà</option>
          {buildings.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
        </select>

        <label style={{ fontWeight:600, color:'#444' }}>🏗️ Tầng:</label>
        <select value={selectedFloor} onChange={e => setSelectedFloor(e.target.value)}
          style={{ padding:'8px 12px', borderRadius:7, border:'1.5px solid #ddd', fontSize:'0.9rem', outline:'none', minWidth:160 }}>
          <option value="">Tất cả tầng</option>
          {floors.map(f => <option key={f.id} value={f.id}>{f.name} {f.vehicleTypeName ? `— ${f.vehicleTypeName}` : ''}</option>)}
        </select>

        {selectedFloorData && (
          <span style={{ background:'#e8f0fe', color:'#1a73e8', padding:'6px 14px', borderRadius:20, fontSize:'0.83rem', fontWeight:500 }}>
            📊 Sức chứa: {selectedFloorData.capacity} | Đã dùng: {selectedFloorData.usedCapacity} | Còn: {selectedFloorData.remainingCapacity}
          </span>
        )}
      </div>

      {error && <div style={{ background:'#fdecea',color:'#c62828',padding:'10px 14px',borderRadius:7,marginBottom:14 }}>{error}</div>}

      {loading ? <p style={{ textAlign:'center',color:'#888' }}>Đang tải...</p> : (
        <div style={{ borderRadius:10,overflow:'hidden',boxShadow:'0 2px 12px rgba(0,0,0,0.08)' }}>
          <table style={{ width:'100%',borderCollapse:'collapse',background:'#fff' }}>
            <thead>
              <tr style={{ background:'#1a1a2e',color:'#fff' }}>
                {['#','Tên khu vực','Tầng','Tòa nhà','Loại xe','Sức chứa','🟢 Trống','🔴 Có xe','🔒 Bảo trì','Thao tác'].map(h => (
                  <th key={h} style={{ padding:'12px 14px',textAlign:'left',fontSize:'0.82rem',fontWeight:600 }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {zones.length === 0 ? (
                <tr><td colSpan={10} style={{ textAlign:'center',padding:40,color:'#999' }}>Chưa có khu vực nào</td></tr>
              ) : zones.map((z, i) => (
                <tr key={z.id} style={{ borderBottom:'1px solid #f0f0f0' }}>
                  <td style={{ padding:'12px 14px' }}>{i+1}</td>
                  <td style={{ padding:'12px 14px',fontWeight:600 }}>{z.name}</td>
                  <td style={{ padding:'12px 14px' }}>{z.floorName}</td>
                  <td style={{ padding:'12px 14px' }}>{z.buildingName}</td>
                  <td style={{ padding:'12px 14px' }}>{z.vehicleTypeName ? <span style={badge('#2e7d32','#e8f5e9')}>{z.vehicleTypeName}</span> : '—'}</td>
                  <td style={{ padding:'12px 14px', color:'#555', fontSize:'0.85rem' }}>
                    {z.totalSlots > 0
                      ? <span style={{ fontWeight:600, color:'#1a1a2e' }}>{z.totalSlots}/{z.capacity}</span>
                      : <span style={{ color:'#bbb' }}>—/{z.capacity}</span>}
                  </td>
                  <td style={{ padding:'12px 14px' }}>
                    {z.totalSlots > 0
                      ? <span style={badge('#2e7d32','#e8f5e9')}>{z.emptySlots}</span>
                      : <span style={{ color:'#ccc' }}>—</span>}
                  </td>
                  <td style={{ padding:'12px 14px' }}>
                    {z.totalSlots > 0
                      ? <span style={badge('#c62828','#fdecea')}>{z.occupiedSlots}</span>
                      : <span style={{ color:'#ccc' }}>—</span>}
                  </td>
                  <td style={{ padding:'12px 14px' }}>
                    {z.totalSlots > 0
                      ? <span style={badge('#e65100','#fff3e0')}>{z.maintenanceSlots}</span>
                      : <span style={{ color:'#ccc' }}>—</span>}
                  </td>
                  <td style={{ padding:'12px 14px' }}>
                    <div style={{ display:'flex',gap:6 }}>
                      <button style={btnEdit} onClick={() => { setEditing(z); setShowModal(true); }}>Sửa</button>
                      <button
                        onClick={() => { setGenerating(z); setShowGenerate(true); }}
                        style={{ padding:'5px 12px',borderRadius:6,border:'1px solid #7c4dff',background:'#ede7f6',color:'#4527a0',cursor:'pointer',fontSize:'0.82rem',fontWeight:600 }}>
                        ⚡ Tạo slot
                      </button>
                      <button style={btnDel} onClick={() => handleDelete(z)}>Xóa</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showModal && <ZoneFormModal zone={editing} floors={floors} defaultFloorId={selectedFloor} onSaved={() => { setShowModal(false); fetchZones(); fetchFloors(); }} onClose={() => setShowModal(false)}/>}
      {showGenerate && generating && (
        <GenerateSlotsModal
          zone={generating}
          onGenerated={() => { fetchZones(); }}
          onClose={() => { setShowGenerate(false); setGenerating(null); }}
        />
      )}
    </div>
  );
}

export default ZoneListPage;
