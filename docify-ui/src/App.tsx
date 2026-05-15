/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import type React from 'react';
import { BrowserRouter as Router, Navigate, Routes, Route } from 'react-router-dom';
import { AppHeader } from './components/layout/AppHeader';
import { AppFooter } from './components/layout/AppFooter';
import { LandingPage } from './pages/LandingPage';
import { ConvertPage } from './pages/ConvertPage';
import { HistoryPage } from './pages/HistoryPage';
import { AuthPage } from './pages/AuthPage';
import { ScrollToTop } from './components/layout/ScrollToTop';
import { authService } from './services/auth-service';

function ProtectedRoute({ children }: { children: React.ReactElement }) {
  if (!authService.isAuthenticated()) {
    return <Navigate to="/auth" replace />;
  }
  return children;
}

export default function App() {
  return (
    <Router>
      <ScrollToTop />
      <div className="min-h-screen flex flex-col">
        <AppHeader />
        <main className="flex-1">
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/convert" element={<ProtectedRoute><ConvertPage /></ProtectedRoute>} />
            <Route path="/history" element={<ProtectedRoute><HistoryPage /></ProtectedRoute>} />
            <Route path="/auth" element={<AuthPage />} />
          </Routes>
        </main>
        <AppFooter />
      </div>
    </Router>
  );
}

