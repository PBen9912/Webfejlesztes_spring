
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './AdminPage.css'

function AdminPage() {
    const { user, token, logout } = useAuth()

    const [accommodations, setAccommodations] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [actionError, setActionError] = useState('')
    const [deletingId, setDeletingId] = useState(null)

    const isAdmin = user?.role === 'ADMIN'

    useEffect(() => {
        if (!isAdmin || !token) {
            setAccommodations([])
            setLoading(false)
            return
        }

        let active = true

        setLoading(true)
        setError('')

        fetch('/api/accommodations')
            .then(response => {
                if (!response.ok) {
                    throw new Error('A szállások nem tölthetők be.')
                }

                return response.json()
            })
            .then(data => {
                if (active) {
                    setAccommodations(data)
                }
            })
            .catch(error => {
                if (active) {
                    setError(error.message)
                }
            })
            .finally(() => {
                if (active) {
                    setLoading(false)
                }
            })

        return () => {
            active = false
        }
    }, [isAdmin, token])

    async function handleDelete(accommodationId) {
        if (!isAdmin || !token || deletingId !== null) return

        const confirmed = window.confirm(
            'Biztosan törölni szeretnéd ezt a szállást? A művelet nem vonható vissza.'
        )

        if (!confirmed) return

        setActionError('')
        setDeletingId(accommodationId)

        try {
            const response = await fetch(
                `/api/accommodations/${accommodationId}`,
                {
                    method: 'DELETE',
                    headers: {
                        Authorization: 'Bearer ' + token
                    }
                }
            )


            if (!response.ok) {
                if (response.status === 401) {
                    logout()
                    return
                }

                if (response.status === 403) {
                    throw new Error(
                        'Nincs jogosultságod a szállás törléséhez.'
                    )
                }

                if (response.status === 404) {
                    throw new Error(
                        'A szállás nem található. Lehetséges, hogy már törölték.'
                    )
                }

                if (response.status === 409) {
                    throw new Error(
                        'A szállás nem törölhető, mert már tartozik hozzá foglalás.'
                    )
                }

                throw new Error('A szállás törlése sikertelen.')
            }

            setAccommodations(previousAccommodations =>
                previousAccommodations.filter(
                    accommodation => accommodation.id !== accommodationId
                )
            )
        } catch (error) {
            setActionError(error.message)
        } finally {
            setDeletingId(null)
        }
    }

    if (!user) {
        return (
            <main className="admin-page">
                <h1>Adminisztráció</h1>
                <p>Az oldal megtekintéséhez jelentkezz be.</p>
                <Link to="/login">Bejelentkezés</Link>
            </main>
        )
    }

    if (!isAdmin) {
        return (
            <main className="admin-page">
                <h1>Hozzáférés megtagadva</h1>
                <p>Ez az oldal kizárólag adminisztrátoroknak érhető el.</p>
                <Link to="/">Vissza a főoldalra</Link>
            </main>
        )
    }

    return (
        <main className="admin-page">
            <div className="admin-page-header">
                <div>
                    <h1>Adminisztráció</h1>
                    <p>Az alkalmazásban található szállások kezelése.</p>
                </div>
            </div>

            {loading && <p>Betöltés...</p>}

            {error && (
                <p className="admin-error">{error}</p>
            )}

            {actionError && (
                <p className="admin-error">{actionError}</p>
            )}

            {!loading && !error && (
                <>
                    <p>Szállások száma: {accommodations.length}</p>

                    {accommodations.length === 0 && (
                        <div className="admin-empty-state">
                            <p>Jelenleg nincs egyetlen szállás sem.</p>
                        </div>
                    )}

                    <div className="admin-accommodations-list">
                        {accommodations.map(accommodation => (
                            <article
                                className="admin-accommodation-card"
                                key={accommodation.id}
                            >
                                <div className="admin-card-header">
                                    <h2>{accommodation.name}</h2>
                                    <span className="admin-id">
                                        #{accommodation.id}
                                    </span>
                                </div>

                                <p>
                                    <strong>Város: </strong>
                                    {accommodation.city}
                                </p>

                                <p>
                                    <strong>Cím: </strong>
                                    {accommodation.address}
                                </p>

                                <p>
                                    <strong>Leírás: </strong>
                                    {accommodation.description}
                                </p>

                                <p>
                                    <strong>Férőhely: </strong>
                                    {accommodation.capacity} fő
                                </p>

                                <p>
                                    <strong>Tulajdonos azonosítója: </strong>
                                    {accommodation.ownerId}
                                </p>

                                <p className="admin-accommodation-price">
                                    {Number(accommodation.pricePerNight)
                                        .toLocaleString('hu-HU')} Ft / éjszaka
                                </p>

                                <div className="admin-accommodation-actions">
                                    <Link
                                        className="admin-view-button"
                                        to={`/accommodations/${accommodation.id}`}
                                    >
                                        Megtekintés
                                    </Link>

                                    <button
                                        type="button"
                                        className="admin-delete-button"
                                        onClick={() => handleDelete(accommodation.id)}
                                        disabled={deletingId !== null}
                                    >
                                        {deletingId === accommodation.id
                                            ? 'Törlés folyamatban...'
                                            : 'Szállás törlése'}
                                    </button>
                                </div>
                            </article>
                        ))}
                    </div>
                </>
            )}
        </main>
    )
}

export default AdminPage
