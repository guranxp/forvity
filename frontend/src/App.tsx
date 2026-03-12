import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { LoginPage } from './pages/LoginPage'
import { SystemAdminsPage } from './pages/SystemAdminsPage'
import { ClubsPage } from './pages/ClubsPage'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: false },
  },
})

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/system-admins" element={<SystemAdminsPage />} />
          <Route path="/clubs" element={<ClubsPage />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  )
}
