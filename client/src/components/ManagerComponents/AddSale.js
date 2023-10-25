import { redirect, useLocation, useNavigate } from "react-router-dom"
import Spacer from "../Spacer"
import { Button, Card, Col, Form, InputGroup, Row, Spinner } from "react-bootstrap"
import { useEffect, useState } from "react"
import { addSale, getCustomers } from "../../API"
import { notify } from "../../Utils"
const dayjs = require('dayjs')

const AddSale = (props) => {

    const product = useLocation().state?.product
    const time = dayjs()
    const warranty = time.add(2, 'year')
    const [customers, setCustomers] = useState({ list: [], error: '' })
    const [sale, setSale] = useState({ saleId: -1,
                                       timestamp: time.toISOString(),
                                       warranty: warranty.toISOString(),
                                       price: null,
                                       ean: product.ean,
                                       customerId: null,
                                       ticketIds: []
                                    })
    const [isLoading, setIsLoading] = useState(true)
    const [validated, setValidated] = useState(false)
    const nav = useNavigate()

    const handleSubmit = (event) => {
        const form = event.currentTarget
        console.log(form)
        event.preventDefault()
        if (form.checkValidity() === false) {
            event.stopPropagation()
            console.log('not valid')
            setValidated(true)
            return
        }
        setValidated(false)
        console.log(sale)
        addSale(sale).then(r => {
            nav('/')
            notify(r)
        })
        .catch(e => {
            notify(e.message, false)
        })
    }

    useEffect(() => {
        getCustomers().then(r => {
            console.log(r)
            setCustomers(r)
            setIsLoading(false)
        })
    }, [])

    return (
        <>
            {isLoading ? <div className='loading-overlay'><Spinner className='spinner' animation="border" variant="light" /></div>
                :
                <>
                    <Spacer height='2rem' />
                    <h3>Add Sale</h3>
                    <Spacer height='1rem' />
                    <Card className='ticket-card'>
                        <Card.Body>
                            {customers.error ? <>{customers.error}</> :
                                <Form id='sale-form' noValidate validated={validated} onSubmit={handleSubmit}>
                                    <h4>Product Info</h4>
                                    <Row className='mb-3'>
                                        <Col md={3}>
                                            <InputGroup size='sm' className='p-1'>
                                                <InputGroup.Text>Ean</InputGroup.Text>
                                                <Form.Control size='sm' type='text' readOnly placeholder={product.ean} />
                                            </InputGroup>
                                        </Col>
                                        <Col md>
                                            <InputGroup size='sm' className='p-1'>
                                                <InputGroup.Text>Name</InputGroup.Text>
                                                <Form.Control size='sm' type='text' readOnly placeholder={product.name} />
                                            </InputGroup>
                                        </Col>
                                        <Col md={2}>
                                            <InputGroup size='sm' className='p-1'>
                                                <InputGroup.Text>Brand</InputGroup.Text>
                                                <Form.Control size='sm' type='text' readOnly placeholder={product.brand} />
                                            </InputGroup>
                                        </Col>
                                    </Row>
                                    <Row className='mb-3'>
                                        <Col md={6}>
                                            <h4>Select Customer</h4>
                                            <Form.Group className='mb-3'>
                                                <InputGroup size='sm'>
                                                    <InputGroup.Text>Customer name</InputGroup.Text>
                                                    <Form.Select required defaultValue={''} onChange={(ev) => { setSale({ ...sale, customerId: ev.currentTarget.value }) }}>
                                                        <option value='' disabled>Select a customer...</option>
                                                        {customers.list.map((customer, idx) => (
                                                            <option key={'option_' + idx} value={customer.profileId}>{customer.name + ' ' + customer.surname}</option>
                                                        ))}
                                                    </Form.Select>
                                                    <Form.Control.Feedback type='invalid'>Please assign a customer to sale.</Form.Control.Feedback>
                                                </InputGroup>
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <h4>Insert Price</h4>
                                            <Form.Group className='mb-3'>
                                                <InputGroup size='sm'>
                                                    <InputGroup.Text>price</InputGroup.Text>
                                                    <Form.Control type='float' min={1} required pattern="^\d+(.\d{1,2})?$" placeholder='0.00' onChange={(ev) => (setSale({...sale, price: parseFloat(ev.currentTarget.value)}))} />
                                                    <Form.Control.Feedback type='invalid'>Please insert a price.</Form.Control.Feedback>
                                                    <InputGroup.Text>$</InputGroup.Text>
                                                </InputGroup>
                                            </Form.Group>
                                        </Col>
                                    </Row>
                                </Form>}
                        </Card.Body>
                        <Card.Footer as={Row}>
                            <Col md className='d-flex justify-content-center p-1'><Button variant='success' size='sm' form='sale-form' type='submit'>Submit</Button></Col>
                        </Card.Footer>
                    </Card>
                </>
            }
        </>
    )
}

export default AddSale