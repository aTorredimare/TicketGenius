import { Button, Card, Col, Form, Row, Spinner } from "react-bootstrap"
import { AuthenticationContext } from "../contexts/AuthenticationContext"
import { useContext, useEffect, useState } from "react"
import { Link, useLocation, useNavigate } from "react-router-dom"
import { getProductsByCustomer, newTicket } from "../../API"
import Spacer from "../Spacer"
import { notify } from "../../Utils"

const NewTicketPage = (props) => {

    const saleFromProps = useLocation().state?.sale
    const user = useContext(AuthenticationContext).user
    const [selectedSale, setSelectedSale] = useState()
    const [sales, setSales] = useState()
    const [ticket, setTicket] = useState({ ticketId: 0, message: null, saleId: null })
    const [isLoading, setIsLoading] = useState(true)
    const [validated, setValidated] = useState(false)
    const nav = useNavigate()

    const handleSaleSelect = (saleId) => {
        setSelectedSale(saleId)
        setTicket({ ...ticket, saleId: saleId })
    }

    const handleSubmit = (event) => {
        const form = event.currentTarget
        event.preventDefault()
        if (form.checkValidity() === false) {
            event.stopPropagation()
            setValidated(true)
            return
        }
        setValidated(false)

        console.debug(ticket)
        newTicket(ticket)
            .then(r => {
                nav('/')
                notify(r)
            })
            .catch(e => {
                notify(e.message, false)
            })
    }

    useEffect(() => {
        if (!saleFromProps) {
            getProductsByCustomer(user.id).then((s) => {
                setSales(s)
                setIsLoading(false)
            })
        } else {
            setSales({ list: [saleFromProps], error: null })
            setSelectedSale(saleFromProps)
            setTicket({ ...ticket, saleId: saleFromProps.saleId })
            setIsLoading(false)
        }

    }, [])

    return (
        <AuthenticationContext.Consumer>
            {(authObject) => (
                <>
                    <Spacer height='2rem' />
                    <h3>Open a Ticket</h3>
                    <Row className='mx-auto'>
                        {isLoading ? <div className='loading-overlay'><Spinner className='spinner' animation="border" variant="light" /></div>
                            :
                            <Card className='ticket-card'>
                                <Card.Body>
                                    {
                                        sales?.error === null ?
                                            <Form.Select size='sm' defaultValue={saleFromProps ? saleFromProps.saleId : ''} onChange={ev => handleSaleSelect(ev.target.value)} {...saleFromProps && {disabled: true}}>
                                                <option value='' disabled>Select a product...</option>
                                                {sales.list.map((sale, idx) => (
                                                    <option key={'sale_opt_' + idx} value={sale.saleId}>{sale.name}</option>
                                                ))}
                                            </Form.Select>
                                            :
                                            <Col className='d-flex justify-content-center'>{sales.error}</Col>
                                    }
                                </Card.Body>
                            </Card>
                        }
                    </Row>
                    <Spacer height='1rem' />
                    <Row className='mx-auto'>
                        <Card className='ticket-card'>
                            <Card.Body>
                                <Form id='new-ticket-form' noValidate validated={validated} onSubmit={handleSubmit}>
                                    <Form.Group className='mb-5'>
                                        <Form.Label>Message</Form.Label>
                                        <Form.Control
                                            required
                                            size='sm'
                                            as='textarea'
                                            rows={4}
                                            placeholder='Insert here your request...'
                                            minLength={10}
                                            maxLength={500}
                                            {...selectedSale ? null : { disabled: true }}
                                            onChange={ev => setTicket({ ...ticket, message: ev.currentTarget.value })}
                                        />
                                        <Form.Control.Feedback type='invalid'>Please describe here your problem. Message should be at least 10 characters long</Form.Control.Feedback>
                                    </Form.Group>
                                    <Form.Group className='mb-3'>
                                        <Form.Check
                                            required
                                            label='Confirm written ticket message is entirely fullfilled.'
                                            feedback='You must check before submitting.'
                                            feedbackType='invalid'
                                            {...selectedSale ? null : { disabled: true }}
                                        />
                                    </Form.Group>
                                </Form>
                            </Card.Body>
                            <Card.Footer as={Row} className='d-flex justify-content-between'>
                                <Col sm={6} className='d-flex justify-content-center justify-content-sm-start p-1'><Link to='/'><Button variant='danger' size='sm'>Go Back</Button></Link></Col>
                                <Col sm={6} className='d-flex justify-content-center justify-content-sm-end p-1'><Button variant='success' size='sm' form='new-ticket-form' type='submit' {...selectedSale ? null : { disabled: true }}>Submit Ticket</Button></Col>
                            </Card.Footer>
                        </Card>
                    </Row>
                </>
            )}
        </AuthenticationContext.Consumer>
    )
}

export default NewTicketPage