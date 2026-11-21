
import { createContext, useContext, useEffect, useState } from 'react'
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

function getInitialToken() {
    const savedToken = localStorage.getItem('token')

    if (!getUserFromToken(savedToken)) {
        localStorage.removeItem('token')
        return null
    }

    return savedToken
}

export function AuthProvider({ children }) {
    const [token, setToken] = useState(getInitialToken)

    const user = getUserFromToken(token)

    function login(newToken) {
        if (!getUserFromToken(newToken)) {
            localStorage.removeItem('token')
            setToken(null)
            return
        }

        localStorage.setItem('token', newToken)
        setToken(newToken)
    }

    function logout() {
        localStorage.removeItem('token')
        setToken(null)
    }

    useEffect(() => {
        if (!token) return

        let decoded

        try {
            decoded = jwtDecode(token)
        } catch {
            logout()
            return
        }

        const remainingTime = decoded.exp * 1000 - Date.now()

        if (!Number.isFinite(remainingTime) || remainingTime <= 0) {
            logout()
            return
        }

        const timeoutId = setTimeout(() => {
            logout()
        }, Math.min(remainingTime, 2147483647))

        return () => {
            clearTimeout(timeoutId)
        }
    }, [token])

    return (
        <AuthContext.Provider value={{ token, user, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    return useContext(AuthContext)
}
