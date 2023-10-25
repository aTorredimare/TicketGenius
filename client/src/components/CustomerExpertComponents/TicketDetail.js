import { Badge, Button, Card, Col, Row, Spinner, Container, Form, Dropdown } from "react-bootstrap"
import { AuthenticationContext } from "../contexts/AuthenticationContext"
import { useNavigate } from "react-router-dom"
import { useContext, useEffect, useState } from "react"
import { getTicketById, getTicketCurrentStatus, getSaleById, updateTicketStatus } from '../../API'
import { RxDoubleArrowDown, RxDoubleArrowRight, RxTriangleDown } from 'react-icons/rx'
import Chat from "./Chat"
import Spacer from "../Spacer"
import { notify } from "../../Utils"

const TicketDetails = (props) => {
    const { ticketId } = props
    const user = useContext(AuthenticationContext).user
    const nav = useNavigate()

    const [ticket, setTicket] = useState()
    const [sale, setSale] = useState()
    const [status, setStatus] = useState()
    const [isLoading, setIsLoading] = useState(true)
    const [selectedFutureStatus, setSelectedFutureStatus] = useState()
    const cardVariantOnStatus = { OPEN: 'primary', IN_PROGRESS: 'warning', RESOLVED: 'info', REOPENED: 'primary', CLOSED: 'secondary' }
    const availableFutureStatus = {
        'OPEN': ['IN_PROGRESS', 'RESOLVED', 'CLOSED'],
        'IN_PROGRESS': ['OPEN', 'RESOLVED', 'CLOSED'],
        'RESOLVED': ['CLOSED', 'REOPENED'],
        'REOPENED': ['IN_PROGRESS', 'RESOLVED', 'CLOSED'],
        'CLOSED': ['REOPENED']
    }

    useEffect(() => {
        getTicketById(ticketId)
            .then(t => {
                setTicket(t);
                getSaleById(t.saleId)
                    .then(s => {
                        setSale(s)
                        console.log(t)
                        console.log(s)
                        setIsLoading(false)
                    })
            })

        getTicketCurrentStatus(ticketId)
            .then(s => {
                setStatus(s)
            })
    }, [isLoading])


    const handleStatusUpdate = () => {
        updateTicketStatus(ticketId, selectedFutureStatus)
            .then(r => {
                //nav(0)
                // setTicket(r.ticket) non aggiorna
                setSelectedFutureStatus("")
                setIsLoading(true)
                notify(r.message)
            })
            .catch(e => {
                notify(e.message, false)
            })
    }

    return (
        <AuthenticationContext.Consumer>
            {(authObject) => (
                <>
                    {isLoading ? <div className='loading-overlay'><Spinner className='spinner' animation="border" variant="light" /></div>
                        :
                        <>
                            <Spacer height='2rem' />
                            <h4>Ticket Info</h4>

                            <Card className='ticket-card'>
                                <Card.Body>
                                    <Row>
                                        <Col>
                                            <div>Ticket Id: <b>{ticketId}</b></div>
                                            <div style={{ paddingTop: "10px" }}>Status: <b>{status}</b></div>
                                        </Col>
                                        <Col>
                                            {user?.role === 'Expert' ?
                                                <div>Customer Id: <b>{sale.sale.customerId ? <>{sale.sale.customerId}</> : <>Not Set</>}</b></div> :
                                                <div>Expert Id: <b>{ticket.expertId ? <>{ticket.expertId}</> : <>Not Set</>}</b></div>
                                            }
                                            <div style={{ paddingTop: "10px" }}>Priority: <b>{ticket.priority ? <>{ticket.priority}</> : <>Not Set</>}</b></div>
                                        </Col>
                                        <div style={{ paddingTop: "10px" }}>Product: <b>{sale.sale.product}</b></div>
                                    </Row>
                                </Card.Body>
                            </Card>
                            <Spacer height='2rem' />
                            <h4>Message</h4>
                            <Card className='ticket-card'>
                                <Card.Body>
                                    <Row>
                                        {ticket.message}
                                    </Row>
                                </Card.Body>
                            </Card>
                            {user?.role === 'Expert' &&
                                <>
                                    <Spacer height='2rem' />
                                    <h4>Update Status</h4>
                                    <Card className='ticket-card'>
                                        <Card.Body>
                                            <Row className="justify-content-center">
                                                <Col md={3} className='d-flex justify-content-center align-self-center'>
                                                    <h5><Badge bg={cardVariantOnStatus[status]}>{status}</Badge></h5>
                                                </Col>
                                                <Col md={3} className='d-none d-md-flex justify-content-center'>
                                                    <Row className='align-items-center'><RxDoubleArrowRight className='m-auto' size='3rem' /></Row>
                                                </Col>
                                                <Col className='d-flex d-md-none justify-content-center'>
                                                    <Row className='align-items-center'><RxDoubleArrowDown className='m-auto' size='3rem' /></Row>
                                                </Col>
                                                <Col md={4} className='d-flex justify-content-center align-self-center'>
                                                    <Dropdown onSelect={(evkey) => setSelectedFutureStatus(evkey)}>
                                                        <Dropdown.Toggle className='status-toggle' variant='light'>
                                                            <h5>
                                                                <Col>
                                                                    {selectedFutureStatus ?
                                                                        <Badge bg={cardVariantOnStatus[selectedFutureStatus]}>{selectedFutureStatus.replace('_', ' ')}</Badge>
                                                                        :
                                                                        <>Select status</>
                                                                    }
                                                                    <RxTriangleDown size='2rem' />
                                                                </Col>
                                                            </h5>
                                                        </Dropdown.Toggle>
                                                        <Dropdown.Menu>
                                                            {status && availableFutureStatus[status].map((futureStatus, idx) => (
                                                                <Dropdown.Item
                                                                    key={'futureState_' + idx}
                                                                    eventKey={futureStatus}
                                                                >
                                                                    <h4><Badge bg={cardVariantOnStatus[futureStatus]}>{futureStatus.replace('_', ' ')}</Badge></h4>
                                                                </Dropdown.Item>
                                                            ))
                                                            }
                                                        </Dropdown.Menu>
                                                    </Dropdown>
                                                </Col>
                                                <Col md={2} className='d-flex justify-content-center align-self-center'>
                                                    <Button
                                                        variant='success'
                                                        {...selectedFutureStatus === undefined && { disabled: true }}
                                                        onClick={handleStatusUpdate}
                                                    >Update</Button>
                                                </Col>
                                            </Row>
                                        </Card.Body>
                                    </Card>
                                </>
                            }
                            <Spacer height='2rem' />
                            <h4>Expert Chat</h4>
                            <Chat className='ticket-card' ticketId={ticketId} ticketStatus={status}/>
                            <Spacer height='10rem' />
                        </>
                    }
                </>
            )
            }
        </AuthenticationContext.Consumer >
    )
}

export default TicketDetails
