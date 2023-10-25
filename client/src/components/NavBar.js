import { Navbar, DropdownButton, Dropdown, Button, Container, Row, Col, Nav } from 'react-bootstrap';
import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import { RiCustomerServiceLine } from 'react-icons/ri'
import { FaRegUserCircle } from 'react-icons/fa'
import { AuthenticationContext } from './contexts/AuthenticationContext';

const NavBar = (props) => {
    const navigate = useNavigate();
    const path = useLocation().pathname;

    return (
        <AuthenticationContext.Consumer>
            {(authObject) => (
                <>
                    <Container fluid className='NavBarContainer'>
                        <Row>
                            <Col xxl={2} />
                            <Col>
                                <Navbar expand="sm">
                                    <Navbar.Brand style={{ cursor: "pointer" }} onClick={() => navigate('/')}>
                                        <h3><RiCustomerServiceLine className='nav-icon' /> TicketGenius</h3>
                                    </Navbar.Brand>
                                    <Navbar.Toggle aria-controls="nav-toggle" />
                                    <Nav className="justify-content-end flex-grow-1 pe-3">

                                        <Navbar.Collapse className='row justify-content-sm-end' id="nav-toggle">
                                            <Col sm={9} className='d-sm-flex justify-content-sm-start'>
                                                {authObject.user !== null &&
                                                    <>
                                                        {authObject.user.role.toLowerCase() === 'manager' &&
                                                            <>
                                                                <Nav.Link className='d-flex justify-content-center' onClick={() => navigate('/dashboard')}>Dashboard</Nav.Link>
                                                                <Nav.Link className='d-flex justify-content-center' onClick={() => navigate('/manage_experts')}>Manage Experts</Nav.Link>
                                                                <Nav.Link className='d-flex justify-content-center' onClick={() => navigate('/products')}>Add Sale</Nav.Link>
                                                            </>}
                                                        {authObject.user.role.toLowerCase() === 'client' &&
                                                            <>
                                                                <Nav.Link className='d-flex justify-content-center' onClick={() => navigate('/purchased_products')}>Purchased Products</Nav.Link>
                                                                <Nav.Link className='d-flex justify-content-center' onClick={() => navigate('/tickets')}>My Tickets</Nav.Link>
                                                                <Nav.Link className='d-flex justify-content-center' onClick={() => navigate('/new_ticket')}>New Ticket</Nav.Link>
                                                                <Nav.Link className='d-flex justify-content-center' onClick={() => navigate('/products')}>Browse Products</Nav.Link>
                                                            </>}
                                                        {authObject.user.role.toLowerCase() === 'expert' &&
                                                            <>
                                                                <Nav.Link className='d-flex justify-content-center' onClick={() => navigate('/tickets')}>Tickets</Nav.Link>
                                                            </>}
                                                    </>
                                                }
                                            </Col>
                                            <hr className='d-sm-none' style={{ width: '95%', margin: 'auto' }} />
                                            {/* hiker navbar */}
                                            {/* show on display larger than sm */}
                                            <Col sm={3} className='d-none d-sm-flex justify-content-sm-end'>
                                                {authObject.user !== null &&
                                                    <DropdownButton
                                                        className='userDropdownButton'
                                                        variant='outline-dark'
                                                        drop='down'
                                                        align='end'
                                                        menuVariant='dark'
                                                        title={<><FaRegUserCircle className='mb-1' />{'   '}{authObject.user.name.toUpperCase()}</>}
                                                    >
                                                        {!path.startsWith('/profile/') &&

                                                            <Dropdown.Item
                                                                className='nav-profile-link'
                                                                variant='link'
                                                                onClick={() => navigate(`/profiles/${authObject.user.email}`)}
                                                            >
                                                                Your Profile
                                                            </Dropdown.Item>
                                                        }
                                                        <Dropdown.Divider />
                                                        <Dropdown.Item className='logOutBtn' onClick={() => { authObject.api.logout(); navigate('/'); }}>Sign out</Dropdown.Item>
                                                    </DropdownButton>
                                                }
                                                {authObject.user === null &&
                                                    <>
                                                        {path !== '/login' && <NavLink to='/login'><Button variant='link-dark'>Sign In</Button></NavLink>}
                                                        {path !== '/addprofile' && <NavLink to='/addprofile'><Button variant='outline-dark'>Sign Up</Button></NavLink>}
                                                    </>
                                                }
                                            </Col>
                                            {/* show on small display */}
                                            <Col className='d-sm-none'>
                                                {authObject.user !== null &&
                                                    <>
                                                        {!path.startsWith('/profiles/') &&
                                                            <Nav.Link className='d-flex justify-content-center' onClick={() => navigate(`/profiles/${authObject.user.email}`)}>Your profile</Nav.Link>}
                                                        <Nav.Link className='d-flex justify-content-center' onClick={() => authObject.api.logout()}>Sign Out</Nav.Link>
                                                    </>
                                                }
                                                {authObject.user === null &&
                                                    <>
                                                        {path !== '/login' && <Nav.Link className='d-flex justify-content-center' onClick={() => navigate('/login')}>Sign In</Nav.Link>}
                                                        {path !== '/addprofile' && <Nav.Link className='d-flex justify-content-center' onClick={() => navigate('/addprofile')}>Sign Up</Nav.Link>}
                                                    </>
                                                }
                                            </Col>
                                            <hr className='d-sm-none' style={{ width: '95%', margin: 'auto' }} />
                                        </Navbar.Collapse>
                                    </Nav>
                                </Navbar>
                            </Col>
                            <Col xxl={2} />
                        </Row>
                    </Container>
                </>
            )}
        </AuthenticationContext.Consumer>
    );
};

export default NavBar;