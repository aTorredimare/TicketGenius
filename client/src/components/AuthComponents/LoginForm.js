import { Form, Button, Alert, Card, Col, Row } from 'react-bootstrap'
import { useContext, useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { AuthenticationContext } from '../contexts/AuthenticationContext'
import { RiCustomerServiceLine } from 'react-icons/ri'
import Spacer from '../Spacer'
import isEmail from 'validator/lib/isEmail'


const LoginForm = () => {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [errorMessage, setErrorMessage] = useState('')
    const { login } = useContext(AuthenticationContext).api

    const handleSubmit = (event) => {
        event.preventDefault()
        setErrorMessage('')

        if (isEmail(email)) {
            login(email, password)
                .catch((err) => {
                    console.log(err)
                    setErrorMessage('Login error. Verify email or password')
                })
        } else {
            setErrorMessage('Login error. Verify email or password')
        }
    }

    const handleEmail = (event) => {
        setEmail(event.target.value)
    }

    const handlePassword = (event) => {
        setPassword(event.target.value)
    }

    return (

        <AuthenticationContext.Consumer>
            {(authObject) => (
                <>
                    {authObject.user === null ?
                        <Col className='justify-content-center' sm={{ span: 4, offset: 4 }}>
                            <Spacer height='4rem' />
                            <Row className='align-items-center'><RiCustomerServiceLine size='12rem'/></Row>
                            <h2 className='login-form p-4'>Sign in to TicketGenius</h2>
                            <Spacer height='2rem' />
                            <Card>
                                <Card.Body>
                                    <Form onSubmit={handleSubmit}>
                                        {errorMessage ? <Alert variant='danger'>{errorMessage}</Alert> : ''}
                                        {authObject.authErr ? <Alert variant='danger'>{authObject.authErr}</Alert> : ''}
                                        <Form.Group controlId='email'>
                                            <Form.Label >Email</Form.Label>
                                            <Form.Control className='email-input' type='text' value={email} onChange={handleEmail} />
                                        </Form.Group>
                                        <Form.Group controlId='password'>
                                            <Form.Label >Password</Form.Label>
                                            <Form.Control className='password-input' type='password' value={password} onChange={handlePassword} autoComplete="on" />
                                        </Form.Group>
                                        <Spacer height='1rem' />
                                        <Button type="submit" className='loginbtn' style={{ width: '100%' }} >Login</Button>
                                    </Form>
                                </Card.Body>
                            </Card>
                            <Spacer height='1rem' />
                            <Card>
                                <Card.Body className='login-form'>
                                    Or <Link to={'/signup'}>create an account</Link>.
                                </Card.Body>
                            </Card>
                        </Col>
                        :
                        <Navigate to='/'/>
                    }
                </>
            )}
        </AuthenticationContext.Consumer>

    )
}

export { LoginForm }