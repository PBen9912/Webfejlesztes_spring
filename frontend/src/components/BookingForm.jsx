
import { useState } from 'react'
import './BookingForm.css'
import { useAuth } from '../context/AuthContext'

function BookingForm({ accommodation }) {
    const { token, user, logout } = useAuth()

    const [guestName, setGuestName] = useState('')
    const [guestEmail, setGuestEmail] = useState('')
    const [checkIn, setCheckIn] = useState('')
    const [checkOut, setCheckOut] = useState('')
    const [guestCount, setGuestCount] = useState(1)

    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')

    let nights = 0

    if (checkIn && checkOut) {
        const startDate = new Date(checkIn)
        const endDate = new Date(checkOut)

        nights = Math.max(0, (endDate - startDate) / 86400000)
    }

    const totalPrice = nights * accommodation.pricePerNight

    async function handleSubmit(event) {
        event.preventDefault()

        setError('')
        setSuccess('')

        if (nights <= 0) {
            setError(
                'A távozás dátumának későbbinek kell lennie az érkezés dátumánál.'
            )
            return
        }

        const booking = {
            accommodationId: accommodation.id,
            guestName: guestName,
            guestEmail: guestEmail,
            checkIn: checkIn,
            checkOut: checkOut,
            guestCount: guestCount
        }

        setLoading(true)

        const headers = {
            'Content-Type': 'application/json'
        }

        if (user && token) {
            headers.Authorization = 'Bearer ' + token
        }

        try {
            const response = await fetch('/api/bookings', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(booking)
            })

            let data = {}

            try {
                data = await response.json()
            } catch {
                // A válasz nem tartalmaz feldolgozható JSON-t.
            }

            if (!response.ok) {
                if (response.status === 401) {
                    if (user && token) {
                        logout()
                    }

                    throw new Error(
                        'A bejelentkezésed lejárt vagy érvénytelen. Jelentkezz be újra.'
                    )
                }

                if (response.status === 403) {
                    throw new Error(
                        'Nincs jogosultságod a foglalás végrehajtásához.'
                    )
                }

                if (response.status === 409) {
                    throw new Error(
                        'A kiválasztott időszakra már van foglalás. Kérlek, válassz másik időpontot.'
                    )
                }

                throw new Error(
                    data.message || 'A foglalás sikertelen. Kérlek, próbáld újra.'
                )
            }

            setSuccess('A foglalás sikeresen megtörtént!')

        } catch (error) {
            setError(error.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <section className="booking-form">
            <h2>Szállásfoglalás</h2>

            <form onSubmit={handleSubmit}>
                <label htmlFor="guestName">Vendég neve</label>
                <input
                    id="guestName"
                    type="text"
                    value={guestName}
                    onChange={event => setGuestName(event.target.value)}
                    required
                />

                <label htmlFor="guestEmail">E-mail-cím</label>
                <input
                    id="guestEmail"
                    type="email"
                    value={guestEmail}
                    onChange={event => setGuestEmail(event.target.value)}
                    required
                />

                <label htmlFor="checkIn">Érkezés dátuma</label>
                <input
                    id="checkIn"
                    type="date"
                    value={checkIn}
                    onChange={event => setCheckIn(event.target.value)}
                    required
                />

                <label htmlFor="checkOut">Távozás dátuma</label>
                <input
                    id="checkOut"
                    type="date"
                    value={checkOut}
                    onChange={event => setCheckOut(event.target.value)}
                    required
                />

                <label htmlFor="guestCount">Vendégek száma</label>
                <input
                    id="guestCount"
                    type="number"
                    min={1}
                    max={accommodation.capacity}
                    value={guestCount}
                    onChange={event =>
                        setGuestCount(Number(event.target.value))
                    }
                    required
                />

                <div className="booking-summary">
                    <p>Éjszakák száma: {nights}</p>

                    <p className="booking-total">
                        Várható teljes ár:{' '}
                        {totalPrice.toLocaleString('hu-HU')} Ft
                    </p>
                </div>

                {error && (
                    <p className="booking-error">{error}</p>
                )}

                {success && (
                    <p className="booking-success">{success}</p>
                )}

                <button type="submit" disabled={loading}>
                    {loading
                        ? 'Foglalás folyamatban...'
                        : 'Foglalás véglegesítése'}
                </button>
            </form>
        </section>
    )
}

export default BookingForm
