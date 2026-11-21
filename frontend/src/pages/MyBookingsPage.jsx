import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './MyBookingsPage.css'

function MyBookingsPage() {
    const { token, user } = useAuth()

    const [bookings, setBookings] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [cancelingId, setCancelingId] = useState(null)
    const [actionError, setActionError] = useState('')

    const userId = user?.id

    useEffect(() => {
        if (!userId || !token) {
            setBookings([])
            setError('A foglalások megtekintéséhez jelentkezz be.')
            setLoading(false)
            return
        }

        let active = true

        setLoading(true)
        setError('')

        fetch('/api/bookings/my', {
            headers: {
                Authorization: 'Bearer ' + token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('A foglalások nem tölthetők be.')
                }
                return response.json()
            })
            .then(data => {
                if (active) setBookings(data)
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
    }, [token, userId])

    async function handleCancel(bookingId) {
        const confirmed = window.confirm(
            'Biztosan le szeretnéd mondani ezt a foglalást?'
        )

        if (!confirmed) return

        setActionError('')
        setCancelingId(bookingId)

        try {
            const response = await fetch(
                `/api/bookings/${bookingId}/cancel`,
                {
                    method: 'PATCH',
                    headers: {
                        Authorization: 'Bearer ' + token
                    }
                }
            )

            if (!response.ok) {
                throw new Error('A foglalás lemondása sikertelen.')
            }

            setBookings(previousBookings =>
                previousBookings.map(booking =>
                    booking.id === bookingId
                        ? { ...booking, status: 'CANCELLED' }
                        : booking
                )
            )
        } catch (error) {
            setActionError(error.message)
        } finally {
            setCancelingId(null)
        }
    }

    if (!user) {
        return (
            <main className="my-bookings-page">
                <h1>Foglalásaim</h1>
                <p>A foglalások megtekintéséhez jelentkezz be.</p>
                <Link to="/login">Bejelentkezés</Link>
            </main>
        )
    }

    return (
        <main className="my-bookings-page">
            <h1>Foglalásaim</h1>

            {loading && <p>Betöltés...</p>}

            {error && (
                <p className="my-bookings-error">{error}</p>
            )}

            {actionError && (
                <p className="my-bookings-error">{actionError}</p>
            )}

            {!loading && !error && (
                <>
                    <p>Foglalások száma: {bookings.length}</p>

                    {bookings.length === 0 && (
                        <p>Még nincs egyetlen foglalásod sem.</p>
                    )}

                    <div className="my-bookings-list">
                        {bookings.map(booking => (
                            <article
                                className="my-booking-card"
                                key={booking.id}
                            >
                                <div className="my-booking-header">
                                    <h2>Foglalás #{booking.id}</h2>

                                    <span className={`booking-status ${
                                        booking.status === 'CONFIRMED'
                                            ? 'status-confirmed'
                                            : 'status-cancelled'
                                    }`}>
                                        {booking.status === 'CONFIRMED'
                                            ? 'Visszaigazolva'
                                            : 'Lemondva'}
                                    </span>
                                </div>

                                <p>
                                    <strong>Vendég neve: </strong>
                                    {booking.guestName}
                                </p>

                                <p>
                                    <strong>E-mail-cím: </strong>
                                    {booking.guestEmail}
                                </p>

                                <p>
                                    <strong>Érkezés: </strong>
                                    {new Date(booking.checkIn)
                                        .toLocaleDateString('hu-HU', {
                                            timeZone: 'UTC'
                                        })}
                                </p>

                                <p>
                                    <strong>Távozás: </strong>
                                    {new Date(booking.checkOut)
                                        .toLocaleDateString('hu-HU', {
                                            timeZone: 'UTC'
                                        })}
                                </p>

                                <p>
                                    <strong>Vendégek száma: </strong>
                                    {booking.guestCount} fő
                                </p>

                                <p className="my-booking-price">
                                    Teljes ár: {Number(booking.totalPrice)
                                    .toLocaleString('hu-HU')} Ft
                                </p>

                                <div className="my-booking-actions">
                                    <Link
                                        className="view-accommodation-button"
                                        to={`/accommodations/${booking.accommodationId}`}
                                    >
                                        Szállás megtekintése
                                    </Link>

                                    {booking.status === 'CONFIRMED' && (
                                        <button
                                            type="button"
                                            className="cancel-booking-button"
                                            onClick={() => handleCancel(booking.id)}
                                            disabled={cancelingId !== null}
                                        >
                                            {cancelingId === booking.id
                                                ? 'Lemondás folyamatban...'
                                                : 'Foglalás lemondása'}
                                        </button>
                                    )}
                                </div>
                            </article>
                        ))}
                    </div>
                </>
            )}
        </main>
    )
}

export default MyBookingsPage