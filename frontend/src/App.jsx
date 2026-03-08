import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import "bootstrap/dist/css/bootstrap.min.css";
import AuthService from './services/AuthService';
import Login from './components/Login';
import Register from './components/Register';
import UserList from './components/UserList';
import UserForm from './components/UserForm';
import Dashboard from './components/Dashboard';
import TestConfig from './components/TestConfig';
import TestQuestion from './components/TestQuestion';
import TestResult from './components/TestResult';
import TestHistory from './components/TestHistory';
import QuestionList from './components/QuestionList';
import QuestionForm from './components/QuestionForm';

// Guard: requires login
function ProtectedRoute({ children }) {
  if (!AuthService.isLoggedIn()) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

// Guard: requires logout (for login/register pages)
function PublicRoute({ children }) {
  if (AuthService.isLoggedIn()) {
    return <Navigate to="/dashboard" replace />;
  }
  return children;
}

function App() {
  return (
    <Routes>
      {/* Auth */}
      <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
      <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />

      {/* Protected user routes */}
      <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
      <Route path="/test/config" element={<ProtectedRoute><TestConfig /></ProtectedRoute>} />
      <Route path="/test/play" element={<ProtectedRoute><TestQuestion /></ProtectedRoute>} />
      <Route path="/test/result" element={<ProtectedRoute><TestResult /></ProtectedRoute>} />
      <Route path="/test/history" element={<ProtectedRoute><TestHistory /></ProtectedRoute>} />

      {/* Admin routes */}
      <Route path="/admin/users" element={<ProtectedRoute><UserList /></ProtectedRoute>} />
      <Route path="/admin/users/new" element={<ProtectedRoute><UserForm /></ProtectedRoute>} />
      <Route path="/admin/users/:id" element={<ProtectedRoute><UserForm /></ProtectedRoute>} />
      <Route path="/admin/questions" element={<ProtectedRoute><QuestionList /></ProtectedRoute>} />
      <Route path="/admin/questions/new" element={<ProtectedRoute><QuestionForm /></ProtectedRoute>} />
      <Route path="/admin/questions/:id" element={<ProtectedRoute><QuestionForm /></ProtectedRoute>} />

      {/* Default redirect */}
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}

export default App;
