
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './CreateAccommodationPage.css'

function CreateAccommodationPage() {
    const { token, user } = useAuth()
    const navigate = useNavigate()

    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
    const [city, setCity] = useState('')
    const [address, setAddress] = useState('')
    const [capacity, setCapacity] = useState(1)
    const [pricePerNight, setPricePerNight] = useState('')

    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    async function handleSubmit(event) {
        event.preventDefault()
        setError('')

        if (user?.role !== 'HOST' || !token) {
            setError('Nincs jogosultságod szállás létrehozásához.')
            return
        }

        const accommodation = {
            name: name.trim(),
            description: description.trim(),
            city: city.trim(),
            address: address.trim(),
            capacity: Number(capacity),
            pricePerNight: Number(pricePerNight)
        }

        setLoading(true)

        try {
            const response = await fetch('/api/accommodations', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + token
                },
                body: JSON.stringify(accommodation)
            })

            if (!response.ok) {
                let message = 'A szállás létrehozása sikertelen.'

                try {
                    const data = await response.json()
                    if (data.message) message = data.message
                } catch {
                    // Ha a backend nem JSON-választ küld,
                    // az alapértelmezett hibaüzenetet használjuk.
                }

                throw new Error(message)
            }

            navigate('/host/accommodations')
        } catch (error) {
            setError(error.message)
        } finally {
            setLoading(false)
        }
    }

    if (!user) {
        return (
            <main className="create-accommodation-page">
                <h1>Új szállás hozzáadása</h1>
                <p>A szállásfeltöltéshez jelentkezz be.</p>
                <Link to="/login">Bejelentkezés</Link>
            </main>
        )
    }

    if (user.role !== 'HOST') {
        return (
            <main className="create-accommodation-page">
                <h1>Hozzáférés megtagadva</h1>
                <p>Szállást kizárólag szállásadók tölthetnek fel.</p>
                <Link to="/">Vissza a főoldalra</Link>
            </main>
        )
    }

    return (
        <main className="create-accommodation-page">
            <Link
                to="/host/accommodations"
                className="create-accommodation-back"
            >
                ← Vissza a saját szállásaimhoz
            </Link>

            <section className="create-accommodation-container">
                <h1>Új szállás hozzáadása</h1>

                <form onSubmit={handleSubmit}>
                    <label htmlFor="name">Szállás neve</label>
                    <input
                        id="name"
                        type="text"
                        value={name}
                        onChange={event => setName(event.target.value)}
                        required
                        maxLength={100}
                    />

                    <label htmlFor="description">Leírás</label>
                    <textarea
                        id="description"
                        value={description}
                        onChange={event => setDescription(event.target.value)}
                        required
                        rows={5}
                    />

                    <label htmlFor="city">Város</label>
                    <input
                        id="city"
                        type="text"
                        value={city}
                        onChange={event => setCity(event.target.value)}
                        required
                    />

                    <label htmlFor="address">Cím</label>
                    <input
                        id="address"
                        type="text"
                        value={address}
                        onChange={event => setAddress(event.target.value)}
                        required
                    />

                    <label htmlFor="capacity">Maximum férőhely</label>
                    <input
                        id="capacity"
                        type="number"
                        min={1}
                        step={1}
                        value={capacity}
                        onChange={event => setCapacity(event.target.value)}
                        required
                    />

                    <label htmlFor="pricePerNight">
                        Éjszakánkénti ár (Ft)
                    </label>
                    <input
                        id="pricePerNight"
                        type="number"
                        min={1}
                        step={1}
                        value={pricePerNight}
                        onChange={event => setPricePerNight(event.target.value)}
                        required
                    />

                    {error && (
                        <p className="create-accommodation-error">
                            {error}
                        </p>
                    )}

                    <button type="submit" disabled={loading}>
                        {loading
                            ? 'Szállás létrehozása...'
                            : 'Szállás létrehozása'}
                    </button>
                </form>
            </section>
        </main>
    )
}

export default CreateAccommodationPage
