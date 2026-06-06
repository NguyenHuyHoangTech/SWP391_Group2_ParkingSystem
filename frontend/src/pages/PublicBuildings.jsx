import { useState, useEffect } from 'react';

const API_URL = 'http://localhost:8080/api/public/buildings';

function PublicBuildings() {
  const [buildings, setBuildings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch(API_URL)
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch open buildings');
        return res.json();
      })
      .then(data => {
        setBuildings(data || []);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setError(err.message);
        setLoading(false);
      });
  }, []);

  return (
    <div style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto', fontFamily: 'sans-serif' }}>
      <div style={{ textAlign: 'center', marginBottom: '3rem' }}>
        <h1 style={{ color: '#1a1a2e', fontSize: '2.5rem', marginBottom: '0.5rem' }}>Welcome to Parking System</h1>
        <p style={{ color: '#666', fontSize: '1.1rem' }}>Find a safe and convenient parking spot near you.</p>
      </div>

      <h2 style={{ color: '#333', borderBottom: '2px solid #1a73e8', paddingBottom: '0.5rem', display: 'inline-block' }}>
        Open Parking Buildings
      </h2>

      {loading ? (
        <p style={{ textAlign: 'center', color: '#888', marginTop: '2rem' }}>Loading open buildings...</p>
      ) : error ? (
        <div style={{ background: '#fdecea', color: '#c62828', padding: '1rem', borderRadius: '8px', marginTop: '1rem' }}>
          <strong>Error: </strong> {error}
        </div>
      ) : buildings.length === 0 ? (
        <p style={{ textAlign: 'center', color: '#666', marginTop: '2rem', fontStyle: 'italic' }}>
          No parking buildings are currently open. Please check back later.
        </p>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '2rem', marginTop: '2rem' }}>
          {buildings.map(building => (
            <div key={building.id} style={{ 
              background: '#fff', 
              borderRadius: '12px', 
              boxShadow: '0 4px 15px rgba(0,0,0,0.05)',
              overflow: 'hidden',
              transition: 'transform 0.2s',
              cursor: 'pointer',
              border: '1px solid #eee'
            }}
            onMouseOver={e => e.currentTarget.style.transform = 'translateY(-5px)'}
            onMouseOut={e => e.currentTarget.style.transform = 'translateY(0)'}
            >
              <div style={{ background: '#1a73e8', color: '#fff', padding: '1.5rem', textAlign: 'center' }}>
                <h3 style={{ margin: 0, fontSize: '1.4rem' }}>{building.name}</h3>
              </div>
              <div style={{ padding: '1.5rem' }}>
                <p style={{ margin: '0 0 1rem 0', color: '#555', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ fontSize: '1.2rem' }}>📍</span> {building.address || 'Address not available'}
                </p>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ 
                    background: '#e8f5e9', color: '#2e7d32', 
                    padding: '4px 12px', borderRadius: '20px', 
                    fontSize: '0.85rem', fontWeight: '600' 
                  }}>
                    {building.status}
                  </span>
                  <button style={{
                    background: 'transparent', color: '#1a73e8', 
                    border: '1px solid #1a73e8', padding: '6px 16px', 
                    borderRadius: '6px', cursor: 'pointer', fontWeight: '500'
                  }}>
                    View Details
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default PublicBuildings;
