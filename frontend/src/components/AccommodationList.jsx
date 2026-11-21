import { useState, useEffect } from 'react'
import AccommodationCard from './AccommodationCard'
import './AccommodationList.css'

function AccommodationList() {
    const [accommodations, setAccommodations] = useState([])
    const [city, setCity] = useState('')

    function loadAccommodations(searchCity = '') {
        let url = '/api/accommodations'

        if (searchCity.trim() !== '') {
            url += `?city=${encodeURIComponent(searchCity.trim())}`
        }

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Sikertelen adatlekérés')
                }
                return response.json()
            })
            .then(data => setAccommodations(data))
            .catch(error => console.error(error))
    }

    useEffect(() => {
        loadAccommodations()
    }, [])

    function handleSearch(event) {
        event.preventDefault()
        loadAccommodations(city)
    }

    function handleReset() {
        setCity('')
        loadAccommodations()
    }

    return (
        <section className="accommodation-section">
            <h2>Elérhető szállások</h2>

            <form className="search-form" onSubmit={handleSearch}>
                <input
                    type="text"
                    placeholder="Melyik városban keresel?"
                    value={city}
                    onChange={event => setCity(event.target.value)}
                />

                <button type="submit">Keresés</button>

                <button type="button" onClick={handleReset}>
                    Összes
                </button>
            </form>

            <p>Szállások száma: {accommodations.length}</p>

            <div className="accommodation-list">
                {accommodations.map(accommodation => (
                    <AccommodationCard
                        key={accommodation.id}
                        accommodation={accommodation}
                    />
                ))}
            </div>
        </section>
    )
}

export default AccommodationList