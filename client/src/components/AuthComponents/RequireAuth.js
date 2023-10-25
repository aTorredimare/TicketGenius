import { useContext } from "react"
import { AuthenticationContext } from "../contexts/AuthenticationContext"
import { Navigate } from "react-router-dom"

const RequireAuth = ({ roles, children }) => {
    const { user } = useContext(AuthenticationContext)
    return  user && roles.includes(user?.role) ? children : <Navigate to='/login' replace />
}

export default RequireAuth