import { useState } from 'react';

const overlay = { position:'fixed',inset:0,background:'rgba(0,0,0,0.5)',display:'flex',alignItems:'center',justifyContent:'center',zIndex:1000 };
const box     = { background:'#fff',borderRadius:12,padding:28,width:420,boxShadow:'0 8px 32px rgba(0,0,0,0.18)' };
const lbl     = { display:'block',marginBottom:4,fontWeight:600,fontSize:'0.88rem',color:'#444' };
const inp     = { width:'100%',padding:'9px 12px',border:'1.5px solid #ddd',borderRadius:7,fontSize:'0.9rem',boxSizing:'border-box',outline:'none' };
const hint    = { fontSize:'0.8rem',color:'#888',marginTop:4 };
const errBox  = { background:'#fdecea',color:'#c62828',padding:'10px 14px',borderRadius:7,marginBottom:14,fontSize:'0.88rem' };

function GenerateSlotsModal({ zone, onGenerated, onClose }) {
  const [prefix,    setPrefix]    = useState(zone?.name ? zone.name + '-' : '');
  const [count,     setCount]     = useState('');
  const [startFrom, setStartFrom] = useState('1');
  const [loading,   setLoading]   = useState(false);
  const [error,     setError]     = useState('');
  const [result,    setResult]    = useState(null); // kết quả sau khi generate

  // Tính remaining slots
  const existing  = zone?.totalSlots ?? 0;
  const capacity  = zone?.capacity   ?? 0;
  const remaining = capacity - existing;

  // Preview tên slot đầu tiên và cuối
  const safeCount = parseInt(count) || 0;
  const safeStart = parseInt(startFrom) || 1;
  const maxNum    = safeStart + safeCount - 1;
  const padLen    = String(maxNum).length || 2;
  const padNum    = (n) => String(n).padStart(Math.max(padLen, 2), '0');
  const preview   = safeCount > 0
    ? (safeCount === 1
        ? `${prefix}${padNum(safeStart)}`
        : `${prefix}${padNum(safeStart)} → ${prefix}${padNum(safeStart + safeCount - 1)}`)
    : '';

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!prefix.trim())           { setError('Tiền tố không được để trống'); return; }
    if (!count || parseInt(count) < 1) { setError('Số lượng phải lớn hơn 0'); return; }
    if (parseInt(count) > remaining) {
      setError(`Chỉ còn ${remaining} chỗ trống trong zone này!`); return;
    }

    setLoading(true); setError('');
    try {
      const res = await fetch('http://localhost:8080/api/slots/bulk-generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          zoneId:    zone.id,
          prefix:    prefix.trim(),
          count:     parseInt(count),
          startFrom: parseInt(startFrom) || 1,
        }),
      });
      const data = await res.json();
      if (!data.success) throw new Error(data.message);
      setResult(data.data);
    } catch (err) {
      setError(err.message || 'Tạo slot thất bại!');
    } finally {
      setLoading(false);
    }
  };

  // Sau khi thành công, đóng và refresh
  const handleDone = () => {
    onGenerated();
    onClose();
  };

  return (
    <div style={overlay} onClick={onClose}>
      <div style={box} onClick={e => e.stopPropagation()}>
        {/* Header */}
        <div style={{ display:'flex',justifyContent:'space-between',alignItems:'center',marginBottom:18 }}>
          <div>
            <h2 style={{ margin:0,fontSize:'1.1rem',color:'#1a1a2e' }}>⚡ Tạo slot hàng loạt</h2>
            <p style={{ margin:'4px 0 0',fontSize:'0.82rem',color:'#888' }}>Khu vực: <strong>{zone?.name}</strong></p>
          </div>
          <button onClick={onClose} style={{ border:'none',background:'none',fontSize:18,cursor:'pointer',color:'#888' }}>✕</button>
        </div>

        {/* Kết quả (sau khi generate) */}
        {result ? (
          <div>
            <div style={{ background:'#e8f5e9',borderRadius:10,padding:'18px 20px',marginBottom:16 }}>
              <p style={{ margin:'0 0 6px',fontWeight:700,color:'#2e7d32',fontSize:'1rem' }}>
                ✅ Đã tạo {result.created} ô đỗ thành công!
              </p>
              {result.skipped > 0 && (
                <p style={{ margin:'4px 0 0',color:'#e65100',fontSize:'0.85rem' }}>
                  ⚠️ Bỏ qua {result.skipped} ô đã tồn tại: {result.skippedNames.join(', ')}
                </p>
              )}
              <div style={{ marginTop:10,maxHeight:120,overflowY:'auto',fontSize:'0.8rem',color:'#555',lineHeight:1.8 }}>
                {result.createdNames.join(' · ')}
              </div>
            </div>
            <div style={{ display:'flex',justifyContent:'flex-end' }}>
              <button onClick={handleDone}
                style={{ padding:'9px 24px',borderRadius:7,border:'none',background:'#1a73e8',color:'#fff',cursor:'pointer',fontWeight:600 }}>
                Xong
              </button>
            </div>
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            {error && <div style={errBox}>{error}</div>}

            {/* Thông tin zone */}
            <div style={{ background:'#f8f9ff',border:'1px solid #e0e0ff',borderRadius:8,padding:'10px 14px',marginBottom:16,fontSize:'0.85rem',color:'#555' }}>
              Capacity: <strong>{capacity}</strong> &nbsp;|&nbsp;
              Đã có: <strong>{existing}</strong> ô đỗ &nbsp;|&nbsp;
              Còn trống: <strong style={{ color: remaining === 0 ? '#e53935' : '#2e7d32' }}>{remaining}</strong> chỗ
            </div>

            <div style={{ marginBottom:14 }}>
              <label style={lbl}>Tiền tố tên ô *</label>
              <input style={inp} value={prefix} onChange={e => setPrefix(e.target.value)}
                placeholder={`VD: ${zone?.name || 'A1'}-`} />
              <p style={hint}>Tên slot = tiền tố + số thứ tự. VD: "A1-" → A1-01, A1-02...</p>
            </div>

            <div style={{ display:'flex',gap:12,marginBottom:14 }}>
              <div style={{ flex:1 }}>
                <label style={lbl}>Số lượng cần tạo * <span style={{ color:'#aaa',fontWeight:400 }}>(tối đa {remaining})</span></label>
                <input style={inp} type="number" min="1" max={remaining} value={count}
                  onChange={e => setCount(e.target.value)} placeholder="VD: 50" />
              </div>
              <div style={{ flex:1 }}>
                <label style={lbl}>Bắt đầu từ số</label>
                <input style={inp} type="number" min="1" value={startFrom}
                  onChange={e => setStartFrom(e.target.value)} placeholder="1" />
              </div>
            </div>

            {/* Preview */}
            {preview && (
              <div style={{ background:'#e8f0fe',borderRadius:8,padding:'10px 14px',marginBottom:16,fontSize:'0.85rem' }}>
                📋 Preview: <strong style={{ color:'#1a73e8' }}>{preview}</strong>
                {safeCount > 0 && <span style={{ color:'#888' }}> ({safeCount} ô)</span>}
              </div>
            )}

            <div style={{ display:'flex',gap:10,justifyContent:'flex-end',marginTop:4 }}>
              <button type="button" onClick={onClose}
                style={{ padding:'9px 20px',borderRadius:7,border:'1px solid #ccc',background:'#fff',cursor:'pointer' }}>
                Hủy
              </button>
              <button type="submit" disabled={loading || remaining === 0}
                style={{ padding:'9px 20px',borderRadius:7,border:'none',background: remaining === 0 ? '#aaa' : '#1a73e8',color:'#fff',cursor: remaining === 0 ? 'not-allowed' : 'pointer',fontWeight:600,opacity:loading ? 0.7 : 1 }}>
                {loading ? 'Đang tạo...' : `⚡ Tạo ${count || ''} ô đỗ`}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}

export default GenerateSlotsModal;
