import React, { useReducer } from 'react'
import { logIn } from '../../API.js'

const AuthenticationContext = React.createContext()

const AuthenticationProvider = ({ children }) => {

    // unauthenticated user object
    const unAuthObject = {
        jwts: undefined,
        loading: true,
        user: null,
        authErr: undefined
    }
    //state to store authentication
    const [auth, setAuth] = useReducer((_oldState, newState) => newState,
        localStorage.getItem('authObject') ? JSON.parse(localStorage.getItem('authObject')) : unAuthObject
    )
    // function to decode jwt
    const getUserInfoFromJwt = jwts => {
        if (jwts) {
            try {
                let tmp = JSON.parse(atob(jwts.access_token.split('.')[1]))
                return {id: tmp.sub, email: tmp.email, name: tmp.name, role: tmp.resource_access["springboot-keycloak-client"].roles[0]}
            } catch (err) {
                console.log(err)
            }
        }
    }
    //login and logut functions
    const login = async (email, password) => {
        logIn(email, password).then((jwts) => {
            const authObject = {
                jwts,
                loading: false,
                user: getUserInfoFromJwt(jwts),
                authErr: undefined
            }
            localStorage.setItem('authObject', JSON.stringify(authObject))
            setAuth(authObject)
        }).catch((err) => {
            const authErr = {
                jwts: undefined,
                loading: true,
                user: null,
                authErr: 'Something went wrong try again.'
            }
            localStorage.removeItem('authObject')
            setAuth(authErr)
        })
    }

    const logout = async () => {
        localStorage.removeItem('authObject')
        setAuth(unAuthObject)
    }

    return (
        <AuthenticationContext.Provider value={{ api: { login, logout }, ...auth }} >
            {children}
        </AuthenticationContext.Provider>
    )
}

export default AuthenticationProvider
export { AuthenticationContext }