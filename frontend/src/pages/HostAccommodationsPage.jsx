
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './HostAccommodationsPage.css'

function HostAccommodationsPage() {
    const { user } = useAuth()

    const [accommodations, setAccommodations] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    const userId = user?.id
    const isHost = user?.role === 'HOST'

    useEffect(() => {
        if (!isHost || !userId) {
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
                    const ownAccommodations = data.filter(
                        accommodation =>
                            Number(accommodation.ownerId) === userId
                    )

                    setAccommodations(ownAccommodations)
                }
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
    }, [isHost, userId])

    if (!user) {
        return (
            <main className="host-accommodations-page">
                <h1>Saját szállásaim</h1>
                <p>Az oldal megtekintéséhez jelentkezz be.</p>
                <Link to="/login">Bejelentkezés</Link>
            </main>
        )
    }

    if (!isHost) {
        return (
            <main className="host-accommodations-page">
                <h1>Hozzáférés megtagadva</h1>
                <p>Ez az oldal kizárólag szállásadóknak érhető el.</p>
                <Link to="/">Vissza a főoldalra</Link>
            </main>
        )
    }

    return (
        <main className="host-accommodations-page">
            <div className="host-page-header">
                <div>
                    <h1>Saját szállásaim</h1>
                    <p>Itt kezelheted a feltöltött szállásaidat.</p>
                </div>

                <Link
                    to="/host/accommodations/create"
                    className="host-view-button"
                >
                    + Új szállás hozzáadása
                </Link>
            </div>

            {loading && <p>Betöltés...</p>}

            {error && (
                <p className="host-accommodations-error">{error}</p>
            )}

            {!loading && !error && (
                <>
                    <p>Szállásaim száma: {accommodations.length}</p>

                    {accommodations.length === 0 && (
                        <div className="host-empty-state">
                            <h2>Még nincs feltöltött szállásod.</h2>
                            <p>
                                Az Új szállás hozzáadása gombbal
                                létrehozhatod az első szállásodat.
                            </p>
                        </div>
                    )}

                    <div className="host-accommodations-list">
                        {accommodations.map(accommodation => (
                            <article
                                className="host-accommodation-card"
                                key={accommodation.id}
                            >
                                <h2>{accommodation.name}</h2>

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

                                <p className="host-accommodation-price">
                                    {Number(accommodation.pricePerNight)
                                        .toLocaleString('hu-HU')} Ft / éjszaka
                                </p>

                                <div className="host-accommodation-actions">
                                    <Link
                                        className="host-view-button"
                                        to={`/accommodations/${accommodation.id}`}
                                    >
                                        Megtekintés
                                    </Link>

                                    <Link
                                        className="host-view-button"
                                        to={`/host/accommodations/${accommodation.id}/edit`}
                                    >
                                        Szerkesztés
                                    </Link>
                                </div>
                            </article>
                        ))}
                    </div>
                </>
            )}
        </main>
    )
}

export default HostAccommodationsPage
