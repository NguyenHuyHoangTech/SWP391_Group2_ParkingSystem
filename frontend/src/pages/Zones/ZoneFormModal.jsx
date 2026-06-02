import { useState, useEffect } from 'react';
import floorApi from '../../api/floorApi';
import zoneApi from '../../api/zoneApi';

const modalOverlay = { position:'fixed',inset:0,background:'rgba(0,0,0,0.5)',display:'flex',alignItems:'center',justifyContent:'center',zIndex:1000 };
const modalBox = { background:'#fff',borderRadius:12,padding:28,minWidth:400,maxWidth:480,width:'90%',boxShadow:'0 8px 32px rgba(0,0,0,0.18)' };
const formGroup = { marginBottom:16 };
const labelStyle = { display:'block',marginBottom:4,fontWeight:600,fontSize:'0.88rem',color:'#444' };
const inputStyle = { width:'100%',padding:'9px 12px',border:'1.5px solid #ddd',borderRadius:7,fontSize:'0.9rem',boxSizing:'border-box',outline:'none' };
const errorBox = { background:'#fdecea',color:'#c62828',padding:'10px 14px',borderRadius:7,marginBottom:14,fontSize:'0.88rem' };
const hint = { background:'#f0f7ff',padding:'8px 12px',borderRadius:6,fontSize:'0.82rem',color:'#555',marginTop:6 };
const actions = { display:'flex',gap:10,justifyContent:'flex-end',marginTop:20 };
const btnCancel = { padding:'9px 20px',borderRadius:7,border:'1px solid #ccc',background:'#fff',cursor:'pointer',fontWeight:500 };
const btnSave = { padding:'9px 20px',borderRadius:7,border:'none',background:'#1a73e8',color:'#fff',cursor:'pointer',fontWeight:600 };

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

  useEffect(() => {
    if (form.floorId) {
      floorApi.getById(form.floorId).then(r => {
        const f = r.data.data;
        setFloorDetail(isEdit ? { ...f, remainingCapacity: (f.remainingCapacity ?? 0) + Number(zone.capacity) } : f);
      }).catch(() => setFloorDetail(null));
    } else setFloorDetail(null);
  }, [form.floorId]);

  const handleChange = e => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async e => {
    e.preventDefault();
    if (!form.name.trim()) { setError('Tên khu vực không được để trống'); return; }
    if (!form.floorId) { setError('Vui lòng chọn tầng'); return; }
    if (!form.capacity || Number(form.capacity) < 1) { setError('Sức chứa phải lớn hơn 0'); return; }
    if (floorDetail && Number(form.capacity) > floorDetail.remainingCapacity) {
      setError(`Sức chứa vượt quá giới hạn! Tầng "${floorDetail.name}" còn ${floorDetail.remainingCapacity} chỗ trống.`);
      return;
    }
    setLoading(true); setError('');
    try {
      const payload = { name: form.name.trim(), floorId: Number(form.floorId), capacity: Number(form.capacity) };
      if (isEdit) await zoneApi.update(zone.id, payload);
      else await zoneApi.create(payload);
      onSaved();
    } catch(err) {
      setError(err.response?.data?.message || 'Đã xảy ra lỗi. Vui lòng thử lại.');
    } finally { setLoading(false); }
  };

  return (
    <div style={modalOverlay} onClick={onClose}>
      <div style={modalBox} onClick={e => e.stopPropagation()}>
        <div style={{ display:'flex',justifyContent:'space-between',alignItems:'center',marginBottom:18 }}>
          <h2 style={{ margin:0,fontSize:'1.15rem',color:'#1a1a2e' }}>{isEdit ? 'Cập nhật Khu vực' : 'Thêm Khu vực mới'}</h2>
          <button onClick={onClose} style={{ border:'none',background:'none',fontSize:18,cursor:'pointer',color:'#888' }}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          {error && <div style={errorBox}>{error}</div>}
          <div style={formGroup}><label style={labelStyle}>Tên khu vực *</label><input style={inputStyle} name="name" placeholder="VD: Khu A, Zone 1..." value={form.name} onChange={handleChange}/></div>
          <div style={formGroup}>
            <label style={labelStyle}>Tầng *</label>
            <select style={inputStyle} name="floorId" value={form.floorId} onChange={handleChange}>
              <option value="">-- Chọn tầng --</option>
              {floors.map(f => <option key={f.id} value={f.id}>{f.name} {f.buildingName ? `(${f.buildingName})` : ''}{f.vehicleTypeName ? ` — ${f.vehicleTypeName}` : ''}</option>)}
            </select>
            {floorDetail && (
              <div style={hint}>
                🚗 Loại xe: <strong>{floorDetail.vehicleTypeName || 'Chưa cấu hình'}</strong>
                &nbsp;|&nbsp; Sức chứa: <strong>{floorDetail.capacity}</strong>
                &nbsp;|&nbsp; Đã dùng: <strong>{floorDetail.usedCapacity ?? 0}</strong>
                &nbsp;|&nbsp; Còn trống: <strong style={{ color: floorDetail.remainingCapacity === 0 ? '#e53935' : '#43a047' }}>{floorDetail.remainingCapacity}</strong>
              </div>
            )}
          </div>
          <div style={formGroup}>
            <label style={labelStyle}>Sức chứa *{floorDetail && <span style={{ color:'#888',fontWeight:400 }}> (tối đa {floorDetail.remainingCapacity} chỗ)</span>}</label>
            <input style={inputStyle} name="capacity" type="number" min="1" placeholder="VD: 50" value={form.capacity} onChange={handleChange}/>
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

export default ZoneFormModal;
