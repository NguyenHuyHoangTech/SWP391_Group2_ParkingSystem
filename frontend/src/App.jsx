import { useState } from 'react'
import StaffList from './pages/StaffList'
import ReportingAnalytics from './pages/ReportingAnalytics'
import './App.css'

function App() {
  const [activePage, setActivePage] = useState('staff')

  return (
    <div className="app-shell">
      <nav className="app-nav" aria-label="Main navigation">
        <div className="app-brand">Parking System</div>
        <div className="app-tabs">
          <button
            type="button"
            className={`app-tab ${activePage === 'staff' ? 'app-tab-active' : ''}`}
            onClick={() => setActivePage('staff')}
          >
            Staff Management
          </button>
          <button
            type="button"
            className={`app-tab ${activePage === 'analytics' ? 'app-tab-active' : ''}`}
            onClick={() => setActivePage('analytics')}
          >
            Reporting Analytics
          </button>
        </div>
      </nav>

      {activePage === 'staff' ? <StaffList /> : <ReportingAnalytics />}
    </div>
  )
}

export default App
