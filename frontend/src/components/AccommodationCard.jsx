import './AccommodationCard.css'

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

            <button className="details-button">
                Részletek
            </button>
        </article>
    )
}

export default AccommodationCard