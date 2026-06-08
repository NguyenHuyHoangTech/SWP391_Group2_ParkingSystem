import { useCallback, useEffect, useMemo, useState } from 'react';
import './ReportingAnalytics.css';

// Backend reporting API base path for PBMS-34 and PBMS-35.
const REPORT_API_URL = 'http://localhost:8080/api/reports';
// Period filters supported by backend aggregation and displayed as segmented buttons.
const PERIODS = [
  { label: 'Daily', value: 'daily' },
  { label: 'Weekly', value: 'weekly' },
  { label: 'Monthly', value: 'monthly' },
];

function ReportingAnalytics() {
  const [period, setPeriod] = useState('daily');
  const [revenueData, setRevenueData] = useState([]);
  const [flowData, setFlowData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Fetches revenue and occupancy flow datasets together so the dashboard updates as one report view.
  const fetchReportData = useCallback(async () => {
    try {
      setLoading(true);
      setError('');

      const [revenueResponse, flowResponse] = await Promise.all([
        fetch(`${REPORT_API_URL}/revenue?period=${period}`),
        fetch(`${REPORT_API_URL}/occupancy-flow?period=${period}`),
      ]);

      if (!revenueResponse.ok) {
        const errorData = await revenueResponse.json().catch(() => ({}));
        throw new Error(errorData.message || 'Failed to load revenue statistics.');
      }

      if (!flowResponse.ok) {
        const errorData = await flowResponse.json().catch(() => ({}));
        throw new Error(errorData.message || 'Failed to load occupancy flow statistics.');
      }

      const [revenue, flow] = await Promise.all([
        revenueResponse.json(),
        flowResponse.json(),
      ]);

      setRevenueData(revenue);
      setFlowData(flow);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [period]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchReportData();
  }, [fetchReportData]);

  // Summary values feed the metric cards above the detailed charts.
  const totalRevenue = useMemo(
    () => revenueData.reduce((total, item) => total + Number(item.revenue || 0), 0),
    [revenueData],
  );

  const totalPayments = useMemo(
    () => revenueData.reduce((total, item) => total + Number(item.paymentCount || 0), 0),
    [revenueData],
  );

  const totalEntries = useMemo(
    () => flowData.reduce((total, item) => total + Number(item.entryCount || 0), 0),
    [flowData],
  );

  const totalExits = useMemo(
    () => flowData.reduce((total, item) => total + Number(item.exitCount || 0), 0),
    [flowData],
  );

  const maxRevenue = Math.max(...revenueData.map((item) => Number(item.revenue || 0)), 1);
  const maxFlow = Math.max(
    ...flowData.map((item) => Math.max(Number(item.entryCount || 0), Number(item.exitCount || 0))),
    1,
  );

  // Revenue amounts are displayed in VND to match parking payment data.
  const formatCurrency = (value) => new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    maximumFractionDigits: 0,
  }).format(Number(value || 0));

  return (
    <div className="analytics-container">
      <div className="analytics-header">
        <div>
          <h1>Reporting Analytics</h1>
          <p className="analytics-subtitle">Revenue and occupancy flow statistics for parking operations</p>
        </div>
        <div className="period-controls" aria-label="Reporting period">
          {PERIODS.map((item) => (
            <button
              key={item.value}
              type="button"
              className={`period-btn ${period === item.value ? 'period-btn-active' : ''}`}
              onClick={() => setPeriod(item.value)}
            >
              {item.label}
            </button>
          ))}
        </div>
      </div>

      {error && <div className="analytics-message analytics-error">{error}</div>}

      <div className="metric-grid">
        <div className="metric-card">
          <span>Total Revenue</span>
          <strong>{formatCurrency(totalRevenue)}</strong>
        </div>
        <div className="metric-card">
          <span>Payments</span>
          <strong>{totalPayments}</strong>
        </div>
        <div className="metric-card">
          <span>Entries</span>
          <strong>{totalEntries}</strong>
        </div>
        <div className="metric-card">
          <span>Exits</span>
          <strong>{totalExits}</strong>
        </div>
      </div>

      {loading ? (
        <div className="analytics-loading">
          <div className="analytics-spinner"></div>
          <p>Loading analytics...</p>
        </div>
      ) : (
        <>
          <section className="analytics-section">
            <div className="section-title-row">
              <h2>Revenue Statistics</h2>
              <span>{PERIODS.find((item) => item.value === period)?.label}</span>
            </div>

            {revenueData.length === 0 ? (
              <div className="analytics-empty">No revenue data found.</div>
            ) : (
              <>
                {/* Bar lengths are scaled from the largest revenue bucket for chart readability. */}
                <div className="revenue-chart" aria-label="Revenue chart">
                  {revenueData.map((item) => {
                    const width = `${Math.max((Number(item.revenue || 0) / maxRevenue) * 100, 2)}%`;
                    return (
                      <div className="chart-row" key={item.label}>
                        <span className="chart-label">{item.label}</span>
                        <div className="chart-track">
                          <div className="chart-bar revenue-bar" style={{ width }}></div>
                        </div>
                        <span className="chart-value">{formatCurrency(item.revenue)}</span>
                      </div>
                    );
                  })}
                </div>

                <div className="analytics-table-wrapper">
                  <table className="analytics-table">
                    <thead>
                      <tr>
                        <th>Period</th>
                        <th>Revenue</th>
                        <th>Payments</th>
                      </tr>
                    </thead>
                    <tbody>
                      {revenueData.map((item) => (
                        <tr key={item.label}>
                          <td>{item.label}</td>
                          <td>{formatCurrency(item.revenue)}</td>
                          <td>{item.paymentCount}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </>
            )}
          </section>

          <section className="analytics-section">
            <div className="section-title-row">
              <h2>Occupancy Flow Statistics</h2>
              <span>Entry and exit by time frame</span>
            </div>

            {flowData.length === 0 ? (
              <div className="analytics-empty">No occupancy flow data found.</div>
            ) : (
              <>
                {/* Entry and exit bars share one max value so both flows can be compared in each time frame. */}
                <div className="flow-chart" aria-label="Occupancy flow chart">
                  {flowData.map((item) => {
                    const entryWidth = `${Math.max((Number(item.entryCount || 0) / maxFlow) * 100, 2)}%`;
                    const exitWidth = `${Math.max((Number(item.exitCount || 0) / maxFlow) * 100, 2)}%`;
                    return (
                      <div className="flow-row" key={item.label}>
                        <span className="flow-label">{item.label}</span>
                        <div className="flow-bars">
                          <div className="flow-track">
                            <span>Entry</span>
                            <div className="chart-track">
                              <div className="chart-bar entry-bar" style={{ width: entryWidth }}></div>
                            </div>
                            <strong>{item.entryCount}</strong>
                          </div>
                          <div className="flow-track">
                            <span>Exit</span>
                            <div className="chart-track">
                              <div className="chart-bar exit-bar" style={{ width: exitWidth }}></div>
                            </div>
                            <strong>{item.exitCount}</strong>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>

                <div className="analytics-table-wrapper">
                  <table className="analytics-table">
                    <thead>
                      <tr>
                        <th>Time Frame</th>
                        <th>Entry Count</th>
                        <th>Exit Count</th>
                      </tr>
                    </thead>
                    <tbody>
                      {flowData.map((item) => (
                        <tr key={item.label}>
                          <td>{item.label}</td>
                          <td>{item.entryCount}</td>
                          <td>{item.exitCount}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </>
            )}
          </section>
        </>
      )}
    </div>
  );
}

export default ReportingAnalytics;
