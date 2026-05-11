/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AppHeader } from './components/layout/AppHeader';
import { AppFooter } from './components/layout/AppFooter';
import { LandingPage } from './pages/LandingPage';
import { ConvertPage } from './pages/ConvertPage';
import { HistoryPage } from './pages/HistoryPage';
import { AuthPage } from './pages/AuthPage';
import { ScrollToTop } from './components/layout/ScrollToTop';

export default function App() {
  return (
    <Router>
      <ScrollToTop />
      <div className="min-h-screen flex flex-col">
        <AppHeader />
        <main className="flex-1">
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/convert" element={<ConvertPage />} />
            <Route path="/history" element={<HistoryPage />} />
            <Route path="/auth" element={<AuthPage />} />
          </Routes>
        </main>
        <AppFooter />
      </div>
    </Router>
  );
}

