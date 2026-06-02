import { useState, useEffect } from 'react';
import floorApi from '../../api/floorApi';
import buildingApi from '../../api/buildingApi';
import vehicleTypeApi from '../../api/vehicleTypeApi';

const modalOverlay = { position:'fixed',inset:0,background:'rgba(0,0,0,0.5)',display:'flex',alignItems:'center',justifyContent:'center',zIndex:1000 };
const modalBox = { background:'#fff',borderRadius:12,padding:28,minWidth:400,maxWidth:480,width:'90%',boxShadow:'0 8px 32px rgba(0,0,0,0.18)' };
const formGroup = { marginBottom:16 };
const label = { display:'block',marginBottom:4,fontWeight:600,fontSize:'0.88rem',color:'#444' };
const input = { width:'100%',padding:'9px 12px',border:'1.5px solid #ddd',borderRadius:7,fontSize:'0.9rem',boxSizing:'border-box',outline:'none' };
const errorBox = { background:'#fdecea',color:'#c62828',padding:'10px 14px',borderRadius:7,marginBottom:14,fontSize:'0.88rem' };
const actions = { display:'flex',gap:10,justifyContent:'flex-end',marginTop:20 };
const btnCancel = { padding:'9px 20px',borderRadius:7,border:'1px solid #ccc',background:'#fff',cursor:'pointer',fontWeight:500 };
const btnSave = { padding:'9px 20px',borderRadius:7,border:'none',background:'#1a73e8',color:'#fff',cursor:'pointer',fontWeight:600 };

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
    buildingApi.getAll().then(r => setBuildings(r.data.data || []));
    vehicleTypeApi.getAll().then(r => setVehicleTypes(r.data.data || []));
  }, []);

  const handleChange = e => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async e => {
    e.preventDefault();
    if (!form.name.trim()) { setError('Tên tầng không được để trống'); return; }
    if (form.floorLevel === '') { setError('Cấp tầng không được để trống'); return; }
    if (!form.capacity || Number(form.capacity) < 1) { setError('Sức chứa phải lớn hơn 0'); return; }
    if (!form.buildingId) { setError('Vui lòng chọn tòa nhà'); return; }
    if (!form.vehicleTypeId) { setError('Vui lòng chọn loại xe'); return; }
    setLoading(true); setError('');
    try {
      const payload = { name: form.name.trim(), floorLevel: Number(form.floorLevel), capacity: Number(form.capacity), buildingId: Number(form.buildingId), vehicleTypeId: Number(form.vehicleTypeId) };
      if (isEdit) await floorApi.update(floor.id, payload);
      else await floorApi.create(payload);
      onSaved();
    } catch(err) {
      setError(err.response?.data?.message || 'Đã xảy ra lỗi. Vui lòng thử lại.');
    } finally { setLoading(false); }
  };

  return (
    <div style={modalOverlay} onClick={onClose}>
      <div style={modalBox} onClick={e => e.stopPropagation()}>
        <div style={{ display:'flex',justifyContent:'space-between',alignItems:'center',marginBottom:18 }}>
          <h2 style={{ margin:0,fontSize:'1.15rem',color:'#1a1a2e' }}>{isEdit ? 'Cập nhật Tầng' : 'Thêm Tầng mới'}</h2>
          <button onClick={onClose} style={{ border:'none',background:'none',fontSize:18,cursor:'pointer',color:'#888' }}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          {error && <div style={errorBox}>{error}</div>}
          <div style={formGroup}><label style={label}>Tên tầng *</label><input style={input} name="name" placeholder="VD: Tầng hầm 1, Tầng 1..." value={form.name} onChange={handleChange}/></div>
          <div style={formGroup}><label style={label}>Cấp tầng *</label><input style={input} name="floorLevel" type="number" placeholder="VD: -1 (hầm), 1, 2..." value={form.floorLevel} onChange={handleChange}/></div>
          <div style={formGroup}><label style={label}>Sức chứa (ô đỗ) *</label><input style={input} name="capacity" type="number" min="1" placeholder="VD: 100" value={form.capacity} onChange={handleChange}/></div>
          <div style={formGroup}>
            <label style={label}>Loại xe *</label>
            <select style={input} name="vehicleTypeId" value={form.vehicleTypeId} onChange={handleChange}>
              <option value="">-- Chọn loại xe --</option>
              {vehicleTypes.map(vt => <option key={vt.id} value={vt.id}>{vt.name}</option>)}
            </select>
          </div>
          <div style={formGroup}>
            <label style={label}>Tòa nhà *</label>
            <select style={input} name="buildingId" value={form.buildingId} onChange={handleChange}>
              <option value="">-- Chọn tòa nhà --</option>
              {buildings.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
            </select>
          </div>
          <div style={actions}>
            <button type="button" style={btnCancel} onClick={onClose}>Hủy</button>
            <button type="submit" style={{ ...btnSave, opacity: loading ? 0.7 : 1 }} disabled={loading}>{loading ? 'Đang lưu...' : isEdit ? 'Cập nhật' : 'Thêm mới'}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default FloorFormModal;
