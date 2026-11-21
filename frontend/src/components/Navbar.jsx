import { Link } from 'react-router-dom'
import './Navbar.css'

function Navbar() {
    return (
        <nav className="navbar">
            <h1 className="navbar-logo">StayManager</h1>

            <div className="navbar-links">
                <Link to="/">Szállások</Link>
                <Link to="/login">Bejelentkezés</Link>
                <Link to="/register">Regisztráció</Link>
            </div>
        </nav>
    )
}

export default Navbar