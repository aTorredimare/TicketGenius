import { Button, Col, Form, InputGroup, Modal, Row } from 'react-bootstrap'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getSaleById, assignTicket } from '../../API'
import { notify } from '../../Utils'

const TicketModal = (props) => {
    const { ticket, experts, ...modalProps } = props
    const priorityLevels = ['LOW', 'NORMAL', 'HIGH', 'CRITICAL']
    const [validated, setValidated] = useState(false)
    const [sale, setSale] = useState(undefined)
    const [expertFilter, setExpertFilter] = useState('none')
    const [priority, setPriority] = useState('')
    const [expertId, setExpertId] = useState('')
    const nav = useNavigate()

    const handleSubmit = (event) => {
        const form = event.currentTarget
        console.log(form)
        event.preventDefault()
        if (form.checkValidity() === false) {
            event.stopPropagation()
            console.log('not valid')
            setValidated(true)
            console.log(form)
            return
        }
        setValidated(false)

        const assignedTicket = { ...ticket, expertId: expertId, priority: priority }
        console.log(assignedTicket)
        assignTicket(assignedTicket)
            .then(r => {
                nav('/')
                notify(r)
            })
            .catch(e => {
                nav('/')
                notify(e.message, false)
            })
    }

    useEffect(() => {
        ticket && getSaleById(ticket.saleId).then((s) => {
            console.log(s)
            console.log(ticket)
            setSale(s)
        })
    }, [ticket])

    return (
        <>
            {ticket &&
                <Modal {...modalProps} dialogClassName='ticket-modal' centered>
                    <Modal.Header className='addInfo m-0'>
                        <Modal.Title>Ticket Info</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        {
                            sale && sale.sale ?
                                <Form id='ticket-form' noValidate validated={validated} onSubmit={handleSubmit}>
                                    <Form.Group className='mb-3'>
                                        <Form.Label>Product</Form.Label>
                                        <Form.Control size='sm' type='text' readOnly placeholder={sale.sale.product} />
                                    </Form.Group>
                                    <Form.Group className='mb-3'>
                                        <Form.Label>Customer Request</Form.Label>
                                        <Form.Control size='sm' tpye='text' readOnly placeholder={ticket.message} />
                                    </Form.Group>
                                    <Row>
                                        <Form.Group as={Col} md='4' className='mb-3'>
                                            <Form.Label>Priority</Form.Label>
                                            <Form.Select size='sm' defaultValue={''} required onChange={ev => setPriority(ev.target.value)}>
                                                <option value='' disabled>Select a priority level...</option>
                                                {priorityLevels.map((prio, idx) => (
                                                    <option key={'prio_opt_' + idx} value={prio}>{prio}</option>
                                                ))}
                                            </Form.Select>
                                            <Form.Control.Feedback type='invalid'>Please select a ticket priority.</Form.Control.Feedback>
                                        </Form.Group>
                                        <Form.Group as={Col} md='8' className='mb-3'>
                                            <Form.Label>Expert</Form.Label>
                                            <InputGroup size='sm'>
                                                <InputGroup.Text>Filter by area:</InputGroup.Text>
                                                <Form.Select
                                                    className='prevent-validation'
                                                    onChange={(ev) => setExpertFilter(ev.currentTarget.value)}>
                                                    <option value='none'>none</option>
                                                    <option value='PRODUCT_WARRANTY'>PRODUCT WARRANTY</option>
                                                    <option value='PRODUCT_DELIVERY'>PRODUCT DELIVERY</option>
                                                    <option value='PRODUCT_RETURN'>PRODUCT RETURN</option>
                                                    <option value='TECHNICAL_SUPPORT'>TECHNICAL SUPPORT</option>
                                                </Form.Select>
                                                <Form.Select required defaultValue={''} onChange={ev => setExpertId(ev.target.value)}>
                                                    <option value='' disabled>Select an expert...</option>
                                                    {experts.filter(e => expertFilter === 'none' ? true : e.expertiseArea === expertFilter).map(expert => (
                                                        <option key={expert.employeeId} value={expert.employeeId}>{expert.name + ' ' + expert.surname}</option>
                                                    ))}
                                                </Form.Select>
                                                <Form.Control.Feedback type='invalid'>Please assign an expert to ticket.</Form.Control.Feedback>
                                            </InputGroup>
                                        </Form.Group>
                                    </Row>
                                    <Form.Group className='mb-3'>
                                        <Form.Check
                                            required
                                            label='Check to confirm, this operation is irreversible'
                                            feedback='You must agree before submitting.'
                                            feedbackType='invalid'
                                        />
                                    </Form.Group>
                                </Form>
                                :
                                <>{sale && sale.saleError}</>
                        }
                    </Modal.Body>
                    <Modal.Footer className='addInfo'>
                        <Button variant='success' size='sm' form='ticket-form' type='submit'>Assign Ticket</Button>
                        <Button size='sm' onClick={props.onHide}>Close</Button>
                    </Modal.Footer>
                </Modal >
            }
        </>
    )
}

export default TicketModal