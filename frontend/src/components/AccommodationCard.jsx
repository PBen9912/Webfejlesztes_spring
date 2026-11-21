import './AccommodationCard.css'
import { Link } from 'react-router-dom'
function AccommodationCard({ accommodation }) {
    return (
        <article className="accommodation-card">
            <h3>{accommodation.name}</h3>

            <p>{accommodation.city}, {accommodation.address}</p>

            <p>{accommodation.description}</p>

            <p>Maximum férőhely: {accommodation.capacity} fő</p>

            <p className="accommodation-price">
                {accommodation.pricePerNight.toLocaleString('hu-HU')} Ft / éjszaka
            </p>

            <Link
                to={`/accommodations/${accommodation.id}`}
                className="details-button"
            >
                Részletek
            </Link>
        </article>
    )
}

export default AccommodationCard