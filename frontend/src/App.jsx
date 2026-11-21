import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import AccommodationDetailsPage from './pages/AccommodationDetailsPage'
import MyBookingsPage from './pages/MyBookingsPage'
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
                <Route path="/accommodations/:id" element={<AccommodationDetailsPage />} />
                <Route path="/my-bookings" element={<MyBookingsPage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App