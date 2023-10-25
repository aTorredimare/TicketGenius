import { Button, Card, Col, Form, Row } from "react-bootstrap"
import ExpertsTable from "./ExpertsTable"
import { getExperts, newExpert as APInewExpert } from "../../API"
import { useEffect, useState } from "react"
import validator from 'validator'
import { notify } from "../../Utils"
import Spacer from "../Spacer"
import { useNavigate } from "react-router-dom"

const ManageExperts = (props) => {

    const defaultExpert = {
        name: '',
        surname: '',
        email: '',
        password: '',
        confirmPassword: '',
        phonenumber: '',
        birthdate: '',
        expertiseArea: '',
        role: 'EXPERT',
        employeeId: 'fakeId'
    }
    const [newExpert, setNewExpert] = useState(defaultExpert)
    const [validated, setValidated] = useState(false)
    const [experts, setExperts] = useState([])
    const [reload, setReload] = useState(false)
    const nav = useNavigate()

    const handleSubmit = (event) => {
        const form = event.currentTarget
        console.log(form)
        event.preventDefault()
        if (form.checkValidity() === false || !validator.isEmail(newExpert.email) || newExpert.password !== newExpert.confirmPassword) {
            event.stopPropagation()
            console.log('not valid')
            setValidated(true)
            console.log(form)
            return
        }
        setValidated(false)
        console.log(newExpert)
        APInewExpert((({ confirmPassword, ...exp }) => exp)(newExpert))
            .then(r => {
                setReload(s => !s)
                setNewExpert(defaultExpert)
                notify(r)
            })
            .catch(e => {
                notify(e.message, false)
            })
    }

    const handleClear = () => {
        setNewExpert(defaultExpert)
        setValidated(false)
    }

    const getToday = () => {
        const tmp = new Date()
        return tmp.toISOString().split('T')[0]
    }

    useEffect(() => {
        getExperts().then(e => {
            console.debug(e[0])
            setExperts(e)
        }).catch(err =>
            console.warn(err.message)
        )
    }, [reload])

    return (
        <>
            <Spacer height='2rem' />
            <h3>Manage Experts</h3>
            <Spacer height='1rem' />
            <Card className='ticket-card'>
                <Card.Body className=''>
                    <h4>Add Expert</h4>
                    <Form id='expert-form' noValidate validated={validated} onSubmit={handleSubmit}>
                        <Row>
                            <Form.Group as={Col} md={6} className='mb-3'>
                                <Form.Label>Name</Form.Label>
                                <Form.Control required size='sm' type='text' placeholder={'First Name'}
                                    pattern='[A-Za-z]{2,}|(?:[A-Za-z]+\s[A-Za-z]{2,})*$'
                                    value={newExpert.name}
                                    onChange={(ev) => setNewExpert({ ...newExpert, name: ev.target.value })} />
                                <Form.Control.Feedback type='invalid'>Please insert a valid name, length between 2 and 30 chars.</Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group as={Col} md={6} className='mb-3'>
                                <Form.Label>Surname</Form.Label>
                                <Form.Control required size='sm' type='text' placeholder={'Last Name'}
                                    pattern='[A-Za-z]{2,}|(?:[A-Za-z]+\s[A-Za-z]{2,})*$'
                                    value={newExpert.surname}
                                    onChange={(ev) => setNewExpert({ ...newExpert, surname: ev.target.value })} />
                                <Form.Control.Feedback type='invalid'>Please insert a valid surname, length between 2 and 30 chars.</Form.Control.Feedback>
                            </Form.Group>
                        </Row>
                        <Row>
                            <Form.Group as={Col} md={6} className='mb-3'>
                                <Form.Label>Password</Form.Label>
                                <Form.Control required size='sm' type='password' placeholder={'Password'}
                                    pattern='((?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,})'
                                    value={newExpert.password}
                                    onChange={(ev) => setNewExpert({ ...newExpert, password: ev.target.value })} />
                                <Form.Control.Feedback type='invalid'>Please insert a password at least 8 chars long, one capitol and alphanumeric.</Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group as={Col} md={6} className='mb-3'>
                                <Form.Label>Confirm Password</Form.Label>
                                <Form.Control required size='sm' type='password' placeholder={'Confirm Password'}
                                    isValid={newExpert.password === newExpert.confirmPassword ? null : false}
                                    value={newExpert.confirmPassword}
                                    onChange={(ev) => setNewExpert({ ...newExpert, confirmPassword: ev.target.value })} />
                                <Form.Control.Feedback type='invalid'>The two inserted password dont match.</Form.Control.Feedback>
                            </Form.Group>
                        </Row>
                        <Row>
                            <Form.Group as={Col} md={6} className='mb-3'>
                                <Form.Label>Email</Form.Label>
                                <Form.Control required size='sm' type='text' placeholder={'Email Address'}
                                    pattern='^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9]+)*$'
                                    value={newExpert.email}
                                    onChange={(ev) => setNewExpert({ ...newExpert, email: ev.target.value })} />
                                <Form.Control.Feedback type='invalid'>Please insert a valid email address.</Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group as={Col} md={6} className='mb-3'>
                                <Form.Label>Phonenumber</Form.Label>
                                <Form.Control required size='sm' type='text' placeholder={'0000000000'}
                                    pattern='^([0-9]{10})$'
                                    value={newExpert.phonenumber}
                                    onChange={(ev) => setNewExpert({ ...newExpert, phonenumber: ev.target.value })} />
                                <Form.Control.Feedback type='invalid'>Please insert a valid phonenumber.</Form.Control.Feedback>
                            </Form.Group>
                        </Row>
                        <Row>
                            <Form.Group as={Col} md={6} className='mb-3'>
                                <Form.Label>Birthdate</Form.Label>
                                <Form.Control required size='sm' type='date' max={getToday()}
                                    value={newExpert.birthdate}
                                    onChange={(ev) => setNewExpert({ ...newExpert, birthdate: ev.target.value })} />
                                <Form.Control.Feedback type='invalid'>Please select a birthdate.</Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group as={Col} md={6} className='mb-3'>
                                <Form.Label>Expertise Area</Form.Label>
                                <Form.Select required size='sm' value={newExpert.expertiseArea}
                                    onChange={(ev) => setNewExpert({ ...newExpert, expertiseArea: ev.target.value })}>
                                    <option value='' disabled>Select an expertise area...</option>
                                    <option value='PRODUCT_WARRANTY'>PRODUCT WARRANTY</option>
                                    <option value='PRODUCT_DELIVERY'>PRODUCT DELIVERY</option>
                                    <option value='PRODUCT_RETURN'>PRODUCT RETURN</option>
                                    <option value='TECHNICAL_SUPPORT'>TECHNICAL SUPPORT</option>
                                </Form.Select>
                                <Form.Control.Feedback type='invalid'>Please select an expertise area.</Form.Control.Feedback>
                            </Form.Group>
                        </Row>
                        <Form.Group className='mb-3'>
                            <Form.Check size='sm'
                                required
                                label='Check that inserted data is correct'
                                feedback='You must agree before submitting.'
                                feedbackType='invalid'
                            />
                        </Form.Group>
                    </Form>
                </Card.Body>
                <Card.Footer as={Row} className='d-flex justify-content-between'>
                    <Col sm={6} className='d-flex justify-content-center justify-content-sm-start p-1'><Button variant='danger' size='sm' onClick={handleClear}>Clear</Button></Col>
                    <Col sm={6} className='d-flex justify-content-center justify-content-sm-end p-1'><Button variant='success' size='sm' form='expert-form' type='submit'>Add Expert</Button></Col>
                </Card.Footer>
            </Card>
            <Card className='dashboard-card'>
                <Card.Body className='dashboard-card-body'>
                    <h4>Experts</h4>
                    {
                        experts.length === 0 ?
                            <>No expert found</>
                            :
                            <>{(reload || !reload) && <ExpertsTable experts={experts} />}</>
                    }
                </Card.Body>
            </Card>
        </>
    )
}

export default ManageExperts