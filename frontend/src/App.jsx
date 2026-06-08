import { useState } from 'react';
import StaffList from './pages/StaffList';
import ReportingAnalytics from './pages/ReportingAnalytics';
import PricingPolicy from './pages/PricingPolicy';
import CustomerBooking from './pages/CustomerBooking';
import FloorListPage from './pages/Floors/FloorListPage';
import ZoneListPage from './pages/Zones/ZoneListPage';
import './App.css';

// Temporary role seed used by local screens that expect an admin role in browser storage.
localStorage.setItem('userRole', 'ADMIN');

// Main navigation items route users between parking management modules without introducing a router.
const NAV_ITEMS = [
  { key: 'floors',      label: '🏢 Quản lý Tầng' },
  { key: 'zones',       label: '🅿️ Quản lý Khu vực' },
  { key: 'staff',       label: '👤 Nhân viên' },
  { key: 'pricing',     label: '💰 Chính sách giá' },
  { key: 'booking',     label: '📋 Đặt chỗ' },
  { key: 'analytics',   label: '📊 Báo cáo thống kê' },
];

function App() {
  const [currentScreen, setCurrentScreen] = useState('floors');

  // Selects the active page component while keeping existing page state isolated per screen.
  const renderScreen = () => {
    switch (currentScreen) {
      case 'floors':    return <FloorListPage />;
      case 'zones':     return <ZoneListPage />;
      case 'staff':     return <StaffList />;
      case 'pricing':   return <PricingPolicy />;
      case 'booking':   return <CustomerBooking />;
      case 'analytics': return <ReportingAnalytics />;
      default:          return <FloorListPage />;
    }
  };

  return (
    <div style={{ minHeight: '100vh', background: '#f5f7fb' }}>
      {/* Navbar */}
      <nav style={{
        background: '#1a1a2e', color: '#fff', padding: '0 24px',
        display: 'flex', alignItems: 'center', gap: 4,
        boxShadow: '0 2px 8px rgba(0,0,0,0.2)', position: 'sticky', top: 0, zIndex: 500
      }}>
        <span style={{ fontWeight: 700, fontSize: '1.05rem', marginRight: 24, color: '#90caf9', whiteSpace: 'nowrap' }}>
          🚗 Parking System
        </span>
        {NAV_ITEMS.map(item => (
          <button key={item.key} onClick={() => setCurrentScreen(item.key)} style={{
            background: currentScreen === item.key ? 'rgba(255,255,255,0.15)' : 'transparent',
            color: currentScreen === item.key ? '#90caf9' : '#ccc',
            border: 'none', padding: '16px 14px', cursor: 'pointer',
            fontSize: '0.85rem', fontWeight: currentScreen === item.key ? 700 : 400,
            borderBottom: currentScreen === item.key ? '3px solid #90caf9' : '3px solid transparent',
            transition: 'all 0.2s', whiteSpace: 'nowrap'
          }}>
            {item.label}
          </button>
        ))}
      </nav>

      {/* Content */}
      <main>{renderScreen()}</main>
    </div>
  );
}

export default App;
