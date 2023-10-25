import { Col, Container, Row } from "react-bootstrap"
import { Outlet } from "react-router-dom"
import ProductTable from "./ProductComponents/ProductTable"
import ProfileDetails from "./ProfileComponents/ProfileDetails"
import { ProfileForm } from "./ProfileComponents/ProfileForm"
import { useParams } from "react-router";
import { Navigate, useLocation } from "react-router-dom";
import { LoginForm } from "./AuthComponents/LoginForm"
import { AuthenticationContext } from "./contexts/AuthenticationContext"
import NavBar from "./NavBar"
import { SignupForm } from "./AuthComponents/SignupForm"
import ManagerDashboard from "./ManagerComponents/ManagerDashboard"
import TicketPage from "./CustomerExpertComponents/TicketPage"
import TicketDetail from "./CustomerExpertComponents/TicketDetail"
import NewTicketPage from "./NewTicketComponents/NewTicketPage"
import { ToastContainer } from "react-toastify"
import '../../node_modules/react-toastify/dist/ReactToastify.css'
import ProductPurchased from "./ProductComponents/ProductPurchased"
import ManageExperts from "./ManagerComponents/ManageExperts"
import AddSale from "./ManagerComponents/AddSale"

const DefaultRoute = () => {
    return (
        <>
            <h1>Nothing here...</h1>
        </>
    )
}

const AppLayout = () => {
    const location = useLocation()
    return (
        <AuthenticationContext.Consumer>
            {(authObject) => (
                <>
                    <Container fluid className="PageContainer">
                        {location.pathname !== '/login' && location.pathname !== '/signup' && <Row>
                            <NavBar />
                        </Row>}
                        <Row>
                            <Col xxl={2} />
                            <Col>
                                <Outlet />
                            </Col>
                            <Col xxl={2} />
                        </Row>
                        <ToastContainer theme='colored' position='top-center' />
                    </Container>
                </>
            )}
        </AuthenticationContext.Consumer>
    )
}

const Login = () => {
    return (
        <>
            <LoginForm />
        </>
    )
}

const Signup = () => {
    return (
        <>
            <SignupForm />
        </>
    )
}

const HomeByRole = () => {
    const location = useLocation()
    return (
        <AuthenticationContext.Consumer>
            {(authObject) => (
                <>
                    {
                        authObject.user === null && location.pathname !== '/signup' ? <Navigate to='/login' /> :
                            authObject.user.role === "Manager" ? <Navigate to='/dashboard' /> :
                                authObject.user.role === "Expert" ? <Navigate to='/tickets' /> :
                                    authObject.user.role === "Client" ? <Navigate to='/purchased_products' /> : authObject.api.logout()
                    }
                </>
            )}
        </AuthenticationContext.Consumer>
    )
}

const BrowseProducts = () => {
    return (
        <>
            <ProductTable />
        </>
    )
}

const PurchasedProducts = () => {
    return (
        <>
            <ProductPurchased/>
        </>
    )
}

const Profile = () => {
    return (
        <>
            <ProfileDetails/>
        </>
    )
}

const AddProfile = () => {
    return (
        <>
            <ProfileForm type="post" />
        </>
    )
}

const UpdateProfile = () => {
    const { email } = useParams()
    return (
        <>
            <ProfileForm type="put" email={email} />
        </>
    )
}

const ManagerHome = () => {
    return (
        <ManagerDashboard />
    )
}

const AddExpert = () => {
    return (
        <ManageExperts />
    )
}

const CustomerExpertHome = () => {
    return (
        <TicketPage />
    )
}

const TicketDetails = () => {
    const { id } = useParams()
    return (
        <TicketDetail ticketId={id}/>
    )
}


const NewTicket = () => {
    return (
        <NewTicketPage />
    )
}

const NewSale = () => {
    return (
        <AddSale />
    )
}

export { DefaultRoute, AppLayout, Login, Signup, HomeByRole, BrowseProducts, PurchasedProducts, Profile, AddProfile, UpdateProfile, ManagerHome, AddExpert, CustomerExpertHome, TicketDetails, NewTicket, NewSale }