import { Form, Button, Alert, Card, Col, Row } from 'react-bootstrap'
import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { AuthenticationContext } from '../contexts/AuthenticationContext'
import { signUp } from '../../API'
import { RiCustomerServiceLine } from 'react-icons/ri'
import Spacer from '../Spacer'
import validator from 'validator'
import { notify } from '../../Utils'


const SignupForm = () => {
    const [user, setUser] = useState({email: '', name: '', surname: '', birthdate: '', phonenumber: '', password: '', verify_password: ''})
    const [errorMessage, setErrorMessage] = useState('')
    const nav = useNavigate()

    const handleSubmit = (event) => {
        event.preventDefault()
        setErrorMessage('')
        console.log(user.birthdate)

        if (validator.isEmail(user.email) && 
            validator.isMobilePhone(user.phonenumber) && 
            validator.isBefore(user.birthdate, (new Date()).toISOString().split('T')[0]) &&
            !validator.isEmpty(user.name) &&
            !validator.isEmpty(user.surname) &&
            user.password === user.verify_password) {
            signUp((({verify_password, ...rest}) => rest)(user))
                .then(() => {
                    nav('/')
                    notify('Thanks for signing up, please login')
                })
                .catch((err) => {
                    console.log(err)
                    setErrorMessage('Registration error. Verify inserted data')
                })
        } else {
            setErrorMessage('Registration error. Verify inserted data')
        }
    }

    const getToday = () => {
        const tmp = new Date()
        return tmp.toISOString().split('T')[0]
    }

    return (

        <AuthenticationContext.Consumer>
            {(authObject) => (
                <>
                    {authObject.user === null ?
                        <Col className='justify-content-center' sm={{ span: 4, offset: 4 }}>
                            <Spacer height='4rem' />
                            <Row className='align-items-center'><RiCustomerServiceLine size='12rem'/></Row>
                            <h2 className='login-form p-4'>Sign up to TicketGenius</h2>
                            <Spacer height='2rem' />
                            <Card>
                                <Card.Body>
                                    <Form onSubmit={handleSubmit}>
                                        {errorMessage ? <Alert variant='danger'>{errorMessage}</Alert> : ''}
                                        <Form.Group controlId='email'>
                                            <Form.Label >Email</Form.Label>
                                            <Form.Control className='email-input' type='text' placeholder='Email Address' value={user.email} onChange={(ev) => {setUser({...user, email: ev.target.value})}} />
                                        </Form.Group>
                                        <Form.Group controlId='name'>
                                            <Form.Label >Name</Form.Label>
                                            <Form.Control className='name-input' type='text' placeholder='Name' value={user.name} onChange={(ev) => {setUser({...user, name: ev.target.value})}} />
                                        </Form.Group>
                                        <Form.Group controlId='surname'>
                                            <Form.Label >Surname</Form.Label>
                                            <Form.Control className='surname-input' type='text' placeholder='Surname' value={user.surname} onChange={(ev) => {setUser({...user, surname: ev.target.value})}} />
                                        </Form.Group>
                                        <Form.Group controlId='surname'>
                                            <Form.Label >Birthdate</Form.Label>
                                            <Form.Control className='birthdate-input' type='date' max={getToday()} value={user.birthdate} onChange={(ev) => {setUser({...user, birthdate: ev.target.value})}} />
                                        </Form.Group>
                                        <Form.Group controlId='surname'>
                                            <Form.Label >Phone number</Form.Label>
                                            <Form.Control className='phonenumber-input' type='text' placeholder='0000000000' value={user.phonenumber} onChange={(ev) => {setUser({...user, phonenumber: ev.target.value})}} />
                                        </Form.Group>
                                        <Form.Group controlId='password'>
                                            <Form.Label >Password</Form.Label>
                                            <Form.Control className='password-input' type='password' placeholder='Password' value={user.password} onChange={(ev) => {setUser({...user, password: ev.target.value})}} autoComplete="on" />
                                        </Form.Group>
                                        <Form.Group controlId='verify-password'>
                                            <Form.Label >Confirm Password</Form.Label>
                                            <Form.Control className='verify-password-input' type='password' placeholder='Confirm Password' value={user.verify_password} onChange={(ev) => {setUser({...user, verify_password: ev.target.value})}} autoComplete="on" />
                                        </Form.Group>
                                        <Spacer height='1rem' />
                                        <Button type="submit" className='loginbtn' style={{ width: '100%' }} >Sign up</Button>
                                    </Form>
                                </Card.Body>
                            </Card>
                            <Spacer height='1rem' />
                            <Card>
                                <Card.Body className='login-form'>
                                    Or <Link to={'/login'}>sign in</Link>.
                                </Card.Body>
                            </Card>
                            <Spacer height='4rem' />
                        </Col>
                        :
                        <Navigate to='/' />
                    }
                </>
            )}
        </AuthenticationContext.Consumer>

    )
}

export { SignupForm }