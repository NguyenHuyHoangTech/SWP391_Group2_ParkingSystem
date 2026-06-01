import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import FloorListPage from './pages/Floors/FloorListPage';
import ZoneListPage from './pages/Zones/ZoneListPage';

// Tạm thời LUÔN set role ADMIN để test (sau này lấy từ JWT login)
localStorage.setItem('userRole', 'ADMIN');

function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<Navigate to="/floors" replace />} />
        <Route path="/floors" element={<FloorListPage />} />
        <Route path="/zones" element={<ZoneListPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
