import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import AccommodationDetailsPage from './pages/AccommodationDetailsPage'
import MyBookingsPage from './pages/MyBookingsPage'
import HostAccommodationsPage from './pages/HostAccommodationsPage'
import CreateAccommodationPage from './pages/CreateAccommodationPage'
import EditAccommodationPage from './pages/EditAccommodationPage'
import './App.css'

function App() {
    return (
        <BrowserRouter>
            <Navbar />
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/accommodations/:id" element={<AccommodationDetailsPage />} />
                <Route path="/my-bookings" element={<MyBookingsPage />} />
                <Route path="/host/accommodations" element={<HostAccommodationsPage />} />
                <Route path="/host/accommodations/create" element={<CreateAccommodationPage />} />
                <Route path="/host/accommodations/:id/edit" element={<EditAccommodationPage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App