
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function ProtectedRoute({ allowedRoles }) {
    const { user } = useAuth()
    const location = useLocation()

    if (!user) {
        return (
            <Navigate
                to="/login"
                state={{ from: location }}
                replace
            />
        )
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {
        return (
            <main style={{ maxWidth: '800px', margin: '40px auto', padding: '20px' }}>
                <h1>Hozzáférés megtagadva</h1>
                <p>Nincs jogosultságod az oldal megtekintéséhez.</p>
            </main>
        )
    }

    return <Outlet />
}

export default ProtectedRoute
