import { type ReactNode } from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import { Shield, Building2, LogOut } from 'lucide-react'

interface LayoutProps {
  children: ReactNode
}

export function Layout({ children }: LayoutProps) {
  const navigate = useNavigate()

  function handleLogout() {
    // Session is cleared server-side on next unauthenticated request
    navigate('/login')
  }

  const navItem = 'flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium text-gray-600 hover:bg-gray-100 transition-colors'
  const activeNavItem = 'flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium bg-indigo-50 text-indigo-700'

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <aside className="w-56 shrink-0 bg-white border-r border-gray-200 flex flex-col">
        <div className="px-5 py-5 border-b border-gray-100">
          <span className="text-lg font-bold text-indigo-600">Forvity</span>
          <p className="text-xs text-gray-400 mt-0.5">Admin</p>
        </div>
        <nav className="flex-1 p-3 flex flex-col gap-1">
          <NavLink to="/system-admins" className={({ isActive }) => isActive ? activeNavItem : navItem}>
            <Shield size={16} />
            System Admins
          </NavLink>
          <NavLink to="/clubs" className={({ isActive }) => isActive ? activeNavItem : navItem}>
            <Building2 size={16} />
            Clubs
          </NavLink>
        </nav>
        <div className="p-3 border-t border-gray-100">
          <button onClick={handleLogout} className={`${navItem} w-full`}>
            <LogOut size={16} />
            Log out
          </button>
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 p-8 overflow-auto">
        {children}
      </main>
    </div>
  )
}
