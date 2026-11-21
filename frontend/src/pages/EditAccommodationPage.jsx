
import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './CreateAccommodationPage.css'

function EditAccommodationPage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const { token, user } = useAuth()

    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
    const [city, setCity] = useState('')
    const [address, setAddress] = useState('')
    const [capacity, setCapacity] = useState(1)
    const [pricePerNight, setPricePerNight] = useState('')

    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')
    const [saveError, setSaveError] = useState('')

    const userId = user?.id
    const isHost = user?.role === 'HOST'

    useEffect(() => {
        if (!isHost || !userId) {
            setLoading(false)
            return
        }

        let active = true

        setLoading(true)
        setError('')

        fetch(`/api/accommodations/${id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('A szállás nem tölthető be.')
                }

                return response.json()
            })
            .then(data => {
                if (!active) return

                if (Number(data.ownerId) !== userId) {
                    throw new Error(
                        'Nincs jogosultságod ennek a szállásnak a szerkesztéséhez.'
                    )
                }

                setName(data.name)
                setDescription(data.description)
                setCity(data.city)
                setAddress(data.address)
                setCapacity(data.capacity)
                setPricePerNight(data.pricePerNight)
            })
            .catch(error => {
                if (active) setError(error.message)
            })
            .finally(() => {
                if (active) setLoading(false)
            })

        return () => {
            active = false
        }
    }, [id, isHost, userId])

    async function handleSubmit(event) {
        event.preventDefault()
        setSaveError('')

        if (!isHost || !token) {
            setSaveError('Nincs jogosultságod a szállás módosításához.')
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

        setSaving(true)

        try {
            const response = await fetch(`/api/accommodations/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + token
                },
                body: JSON.stringify(accommodation)
            })

            if (!response.ok) {
                let message = 'A szállás módosítása sikertelen.'

                try {
                    const data = await response.json()
                    if (data.message) message = data.message
                } catch {
                    // Nem JSON-válasz esetén marad az alap hibaüzenet.
                }

                throw new Error(message)
            }

            navigate('/host/accommodations')
        } catch (error) {
            setSaveError(error.message)
        } finally {
            setSaving(false)
        }
    }

    if (!user) {
        return (
            <main className="create-accommodation-page">
                <h1>Szállás szerkesztése</h1>
                <p>A szerkesztéshez jelentkezz be.</p>
                <Link to="/login">Bejelentkezés</Link>
            </main>
        )
    }

    if (!isHost) {
        return (
            <main className="create-accommodation-page">
                <h1>Hozzáférés megtagadva</h1>
                <p>Szállást kizárólag szállásadók szerkeszthetnek.</p>
                <Link to="/">Vissza a főoldalra</Link>
            </main>
        )
    }

    if (loading) {
        return (
            <main className="create-accommodation-page">
                <p>Szállás adatainak betöltése...</p>
            </main>
        )
    }

    if (error) {
        return (
            <main className="create-accommodation-page">
                <h1>Szállás szerkesztése</h1>
                <p className="create-accommodation-error">{error}</p>
                <Link to="/host/accommodations">
                    Vissza a saját szállásaimhoz
                </Link>
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
                <h1>Szállás szerkesztése</h1>

                <form onSubmit={handleSubmit}>
                    <label htmlFor="name">Szállás neve</label>
                    <input
                        id="name"
                        type="text"
                        value={name}
                        onChange={event => setName(event.target.value)}
                        maxLength={100}
                        required
                    />

                    <label htmlFor="description">Leírás</label>
                    <textarea
                        id="description"
                        value={description}
                        onChange={event => setDescription(event.target.value)}
                        rows={5}
                        required
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

                    {saveError && (
                        <p className="create-accommodation-error">
                            {saveError}
                        </p>
                    )}

                    <button type="submit" disabled={saving}>
                        {saving
                            ? 'Módosítás mentése...'
                            : 'Módosítások mentése'}
                    </button>
                </form>
            </section>
        </main>
    )
}

export default EditAccommodationPage
