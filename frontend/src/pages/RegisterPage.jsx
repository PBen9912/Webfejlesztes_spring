import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import './RegisterPage.css'

function RegisterPage() {
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const [hostRegistration, setHostRegistration] = useState(false)

    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    const navigate = useNavigate()
    async function handleSubmit(event) {
        event.preventDefault()

        setError('')

        if (password !== confirmPassword) {
            setError('A két megadott jelszó nem egyezik.')
            return
        }

        const registration = {
            name: name,
            email: email,
            password: password,
            hostRegistration: hostRegistration
        }

        setLoading(true)

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(registration)
            })

            if (!response.ok) {
                const data = await response.json()
                throw new Error(data.message || 'Sikertelen regisztráció.')
            }

            navigate('/login')

        } catch (error) {
            setError(error.message)
        } finally {
            setLoading(false)
        }
    }
    return (
        <main className="register-page">
            <section className="register-container">
                <h1>Regisztráció</h1>

                <form onSubmit={handleSubmit}>
                    <label htmlFor="name">Név</label>
                    <input
                        id="name"
                        type="text"
                        value={name}
                        onChange={event => setName(event.target.value)}
                        required
                    />

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

                    <label htmlFor="confirmPassword">Jelszó megerősítése</label>
                    <input
                        id="confirmPassword"
                        type="password"
                        value={confirmPassword}
                        onChange={event => setConfirmPassword(event.target.value)}
                        required
                    />

                    <label className="host-checkbox">
                        <input
                            type="checkbox"
                            checked={hostRegistration}
                            onChange={event => setHostRegistration(event.target.checked)}
                        />
                        Szállásadóként szeretnék regisztrálni
                    </label>

                    {error && <p className="register-error">{error}</p>}

                    <button type="submit" disabled={loading}>
                        {loading ? 'Regisztráció folyamatban...' : 'Regisztráció'}
                    </button>
                </form>

                <p className="register-login">
                    Már van fiókod? <Link to="/login">Bejelentkezés</Link>
                </p>
            </section>
        </main>
    )
}

export default RegisterPage