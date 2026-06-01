import { useState } from 'react';
import StaffList from './pages/StaffList';
import PricingPolicy from './pages/PricingPolicy';
import CustomerBooking from './pages/CustomerBooking';
import './App.css';

function App() {
  const [currentScreen, setCurrentScreen] = useState('home');

  const renderScreen = () => {
    switch (currentScreen) {
      case 'staff':
        return <StaffList />;
      case 'pricing':
        return <PricingPolicy />;
      case 'booking':
        return <CustomerBooking />;
      default:
        return (
          <div style={{ padding: '2rem', textAlign: 'center' }}>
            <h1>Bảng Điều Khiển (Dashboard)</h1>
            <p>Vui lòng chọn một chức năng bên dưới để test:</p>
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', marginTop: '2rem' }}>
              <button onClick={() => setCurrentScreen('staff')} style={btnStyle}>
                Staff Management
              </button>
              <button onClick={() => setCurrentScreen('pricing')} style={btnStyle}>
                Pricing Policy
              </button>
              <button onClick={() => setCurrentScreen('booking')} style={btnStyle}>
                Customer Booking
              </button>
            </div>
          </div>
        );
    }
  };

  return (
    <div>
      {currentScreen !== 'home' && (
        <div style={{ padding: '1rem', background: '#f8f9fa', borderBottom: '1px solid #ddd', marginBottom: '1rem' }}>
          <button onClick={() => setCurrentScreen('home')} style={backBtnStyle}>
            &larr; Quay lại trang chủ
          </button>
        </div>
      )}
      {renderScreen()}
    </div>
  );
}

const btnStyle = {
  padding: '12px 24px',
  fontSize: '16px',
  cursor: 'pointer',
  backgroundColor: '#007bff',
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
};

const backBtnStyle = {
  padding: '8px 16px',
  fontSize: '14px',
  cursor: 'pointer',
  backgroundColor: '#6c757d',
  color: 'white',
  border: 'none',
  borderRadius: '4px'
};

export default App;
