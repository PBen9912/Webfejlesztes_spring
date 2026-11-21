import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Navbar.css'

function Navbar() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()

    function handleLogout() {
        logout()
        navigate('/')
    }

    return (
        <nav className="navbar">
            <Link to="/" className="navbar-logo">
                StayManager
            </Link>

            <div className="navbar-links">
                <Link to="/">Szállások</Link>

                {!user && (
                    <>
                        <Link to="/login">Bejelentkezés</Link>
                        <Link to="/register">Regisztráció</Link>
                    </>
                )}

                {user?.role === 'USER' && (
                    <Link to="/my-bookings">Foglalásaim</Link>
                )}

                {user?.role === 'HOST' && (
                    <Link to="/host/accommodations">
                        Saját szállásaim
                    </Link>
                )}

                {user?.role === 'ADMIN' && (
                    <Link to="/admin">Adminisztráció</Link>
                )}

                {user && (
                    <button
                        className="logout-button"
                        onClick={handleLogout}
                    >
                        Kijelentkezés
                    </button>
                )}
            </div>
        </nav>
    )
}

export default Navbar