import './Navbar.css'

function Navbar() {
    return (
        <nav className="navbar">
            <h1 className="navbar-logo">StayManager</h1>

            <div className="navbar-links">
                <button>Szállások</button>
                <button>Bejelentkezés</button>
                <button>Regisztráció</button>
            </div>
        </nav>
    )
}

export default Navbar