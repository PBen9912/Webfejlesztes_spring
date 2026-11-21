import { createContext, useContext, useState } from 'react'
import { jwtDecode } from 'jwt-decode'

const AuthContext = createContext(null)

function getUserFromToken(token) {
    if (!token) {
        return null
    }

    try {
        const decoded = jwtDecode(token)

        if (!decoded.exp || decoded.exp * 1000 <= Date.now()) {
            return null
        }

        return {
            id: Number(decoded.sub),
            role: decoded.role
        }

    } catch {
        return null
    }
}

export function AuthProvider({ children }) {
    const [token, setToken] = useState(
        () => localStorage.getItem('token')
    )

    const user = getUserFromToken(token)

    function login(newToken) {
        localStorage.setItem('token', newToken)
        setToken(newToken)
    }

    function logout() {
        localStorage.removeItem('token')
        setToken(null)
    }

    return (
        <AuthContext.Provider value={{ token, user, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    return useContext(AuthContext)
}