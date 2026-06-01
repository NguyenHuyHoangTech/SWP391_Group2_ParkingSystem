import { NavLink } from 'react-router-dom';
import './Navbar.css';

function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-brand">🅿 Parking System</div>
      <div className="navbar-links">
        <NavLink
          to="/floors"
          className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}
        >
          Quản lý Tầng
        </NavLink>
        <NavLink
          to="/zones"
          className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}
        >
          Quản lý Khu vực
        </NavLink>
      </div>
      <div className="navbar-role">
        <span className="role-badge">
          {localStorage.getItem('userRole') || 'GUEST'}
        </span>
      </div>
    </nav>
  );
}

export default Navbar;
