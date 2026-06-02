import { useState, useEffect } from 'react';
import floorApi from '../../api/floorApi';
import FloorFormModal from './FloorFormModal';

const page = { padding:'24px',maxWidth:1100,margin:'0 auto',fontFamily:'Segoe UI,sans-serif' };
const header = { display:'flex',alignItems:'center',justifyContent:'space-between',marginBottom:20 };
const btnPrimary = { background:'#1a73e8',color:'#fff',border:'none',padding:'10px 20px',borderRadius:8,fontWeight:600,cursor:'pointer',fontSize:'0.9rem' };
const btnEdit = { background:'#fff3e0',color:'#e65100',border:'1px solid #ffcc80',padding:'5px 14px',borderRadius:6,cursor:'pointer',fontSize:'0.82rem',fontWeight:500 };
const btnDel = { background:'#fdecea',color:'#c62828',border:'1px solid #ef9a9a',padding:'5px 14px',borderRadius:6,cursor:'pointer',fontSize:'0.82rem',fontWeight:500 };
const badge = (color, bg) => ({ background:bg,color,padding:'3px 10px',borderRadius:12,fontSize:'0.82rem',fontWeight:500 });

function FloorListPage() {
  const [floors, setFloors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);

  const fetchFloors = async () => {
    setLoading(true); setError('');
    try { const r = await floorApi.getAll(); setFloors(r.data.data || []); }
    catch { setError('Không thể tải danh sách tầng.'); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchFloors(); }, []);

  const handleDelete = async (floor) => {
    if (!window.confirm(`Xóa tầng "${floor.name}"?`)) return;
    try { await floorApi.delete(floor.id); fetchFloors(); }
    catch(err) { alert(err.response?.data?.message || 'Xóa thất bại!'); }
  };

  return (
    <div style={page}>
      <div style={header}>
        <div>
          <h1 style={{ margin:'0 0 4px',fontSize:'1.6rem',fontWeight:700,color:'#1a1a2e' }}>Quản lý Tầng</h1>
          <p style={{ margin:0,color:'#666',fontSize:'0.9rem' }}>Cấu hình các tầng, sức chứa và loại xe</p>
        </div>
        <button style={btnPrimary} onClick={() => { setEditing(null); setShowModal(true); }}>+ Thêm Tầng</button>
      </div>

      {error && <div style={{ background:'#fdecea',color:'#c62828',padding:'10px 14px',borderRadius:7,marginBottom:14 }}>{error}</div>}

      {loading ? <p style={{ textAlign:'center',color:'#888' }}>Đang tải...</p> : (
        <div style={{ borderRadius:10,overflow:'hidden',boxShadow:'0 2px 12px rgba(0,0,0,0.08)' }}>
          <table style={{ width:'100%',borderCollapse:'collapse',background:'#fff' }}>
            <thead>
              <tr style={{ background:'#1a1a2e',color:'#fff' }}>
                {['#','Tên tầng','Cấp tầng','Loại xe','Tòa nhà','Sức chứa','Đã dùng','Còn trống','Tình trạng','Thao tác'].map(h => (
                  <th key={h} style={{ padding:'12px 14px',textAlign:'left',fontSize:'0.82rem',fontWeight:600,letterSpacing:'0.3px' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {floors.length === 0 ? (
                <tr><td colSpan={10} style={{ textAlign:'center',padding:40,color:'#999' }}>Chưa có tầng nào</td></tr>
              ) : floors.map((f, i) => {
                const pct = f.capacity ? Math.round((f.usedCapacity / f.capacity) * 100) : 0;
                return (
                  <tr key={f.id} style={{ borderBottom:'1px solid #f0f0f0' }}>
                    <td style={{ padding:'12px 14px',fontSize:'0.9rem' }}>{i+1}</td>
                    <td style={{ padding:'12px 14px',fontWeight:600 }}>{f.name}</td>
                    <td style={{ padding:'12px 14px' }}><span style={badge('#1a73e8','#e8f0fe')}>{f.floorLevel < 0 ? `Hầm ${Math.abs(f.floorLevel)}` : `Tầng ${f.floorLevel}`}</span></td>
                    <td style={{ padding:'12px 14px' }}>{f.vehicleTypeName ? <span style={badge('#2e7d32','#e8f5e9')}>{f.vehicleTypeName}</span> : <span style={{ color:'#999' }}>Chưa cấu hình</span>}</td>
                    <td style={{ padding:'12px 14px' }}>{f.buildingName}</td>
                    <td style={{ padding:'12px 14px' }}>{f.capacity}</td>
                    <td style={{ padding:'12px 14px' }}>{f.usedCapacity ?? 0}</td>
                    <td style={{ padding:'12px 14px',fontWeight:600,color: f.remainingCapacity === 0 ? '#e53935' : '#43a047' }}>{f.remainingCapacity ?? f.capacity}</td>
                    <td style={{ padding:'12px 14px',minWidth:110 }}>
                      <div style={{ background:'#f0f0f0',borderRadius:6,height:18,position:'relative',overflow:'hidden' }}>
                        <div style={{ height:'100%',borderRadius:6,background: pct>=100?'#e53935':pct>=80?'#fb8c00':'#43a047',width:`${pct}%`,transition:'width 0.3s' }}/>
                        <span style={{ position:'absolute',top:'50%',left:'50%',transform:'translate(-50%,-50%)',fontSize:'0.72rem',fontWeight:700,color:'#333' }}>{pct}%</span>
                      </div>
                    </td>
                    <td style={{ padding:'12px 14px' }}>
                      <div style={{ display:'flex',gap:6 }}>
                        <button style={btnEdit} onClick={() => { setEditing(f); setShowModal(true); }}>Sửa</button>
                        <button style={btnDel} onClick={() => handleDelete(f)}>Xóa</button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {showModal && <FloorFormModal floor={editing} onSaved={() => { setShowModal(false); fetchFloors(); }} onClose={() => setShowModal(false)}/>}
    </div>
  );
}

export default FloorListPage;
