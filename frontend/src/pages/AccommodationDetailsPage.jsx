import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import './AccommodationDetailsPage.css'
import BookingForm from '../components/BookingForm'
function AccommodationDetailsPage() {
    const { id } = useParams()

    const [accommodation, setAccommodation] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        setLoading(true)
        setError('')

        fetch(`/api/accommodations/${id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('A szállás nem tölthető be.')
                }

                return response.json()
            })
            .then(data => setAccommodation(data))
            .catch(error => setError(error.message))
            .finally(() => setLoading(false))
    }, [id])

    if (loading) {
        return <p>Betöltés...</p>
    }

    if (error) {
        return (
            <main className="accommodation-details">
                <p>{error}</p>
                <Link to="/">Vissza a szállásokhoz</Link>
            </main>
        )
    }

    return (
        <main className="accommodation-details">

            <Link to="/" className="back-link">
                ← Vissza a szállásokhoz
            </Link>

            <section className="details-container">

                <h1>{accommodation.name}</h1>

                <p>{accommodation.city}, {accommodation.address}</p>

                <h2>A szállás bemutatása</h2>

                <p>{accommodation.description}</p>

                <p>
                    Maximum férőhely: {accommodation.capacity} fő
                </p>

                <p className="details-price">
                    {accommodation.pricePerNight.toLocaleString('hu-HU')} Ft / éjszaka
                </p>
                <BookingForm accommodation={accommodation} />
            </section>

        </main>
    )
}

export default AccommodationDetailsPage