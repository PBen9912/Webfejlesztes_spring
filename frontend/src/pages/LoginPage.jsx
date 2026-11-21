import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import './LoginPage.css'
import { useAuth } from '../context/AuthContext'

function LoginPage() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    const navigate = useNavigate()
    const { login } = useAuth()
    async function handleSubmit(event) {
        event.preventDefault()

        setError('')

        const loginRequest = {
            email: email,
            password: password
        }

        setLoading(true)

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginRequest)
            })

            const data = await response.json()

            if (!response.ok) {
                throw new Error(data.message || 'Sikertelen bejelentkezés.')
            }

            login(data.token)

            navigate('/')

        } catch (error) {
            setError(error.message)
        } finally {
            setLoading(false)
        }
    }
    return (
        <main className="login-page">
            <section className="login-container">
                <h1>Bejelentkezés</h1>

                <form onSubmit={handleSubmit}>
                    <label htmlFor="email">E-mail-cím</label>
                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={event => setEmail(event.target.value)}
                        required
                    />

                    <label htmlFor="password">Jelszó</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={event => setPassword(event.target.value)}
                        required
                    />

                    {error && <p className="login-error">{error}</p>}

                    <button type="submit" disabled={loading}>
                        {loading ? 'Bejelentkezés folyamatban...' : 'Bejelentkezés'}
                    </button>
                </form>

                <p className="login-register">
                    Még nincs fiókod? <Link to="/register">Regisztráció</Link>
                </p>
            </section>
        </main>
    )
}

export default LoginPage