import { Badge, Button, Card, Col, Row, Table } from "react-bootstrap"
import Spacer from "../Spacer"
import { AuthenticationContext } from "../contexts/AuthenticationContext"
import { Link } from "react-router-dom"
const dayjs = require('dayjs')

const TicketsTable = (props) => {

    const { tickets, isExpert } = props
    const cardVariantOnStatus = { OPEN: 'primary', 'IN PROGRESS': 'warning', RESOLVED: 'info', REOPENED: 'primary', CLOSED: 'secondary' }
    const cardVariantOnPrio = { LOW: 'info', NORMAL: 'primary', HIGH: 'warning', CRITICAL: 'danger' }

    return (
        <AuthenticationContext.Consumer>
            {(authObject) => (
                <>
                    {tickets.map((ticket, idx) => (
                        <Row className='mx-auto' key={'ticket_' + idx}>
                            <Card className='ticket-card bg-opacity-25' bg={isExpert ? cardVariantOnPrio[ticket.priority] : cardVariantOnStatus[ticket.status]}>
                                <Card.Body>
                                    <Row>
                                        <Col md={2} className='d-flex justify-content-center justify-content-md-start align-self-center py-2'>
                                            <Badge bg={cardVariantOnStatus[ticket.status]}>
                                                {ticket.status}
                                            </Badge>
                                        </Col>
                                        {isExpert && <Col md={2} className='d-flex justify-content-center justify-content-md-start align-self-center py-2'>
                                            <Badge bg={cardVariantOnPrio[ticket.priority]}>
                                                {ticket.priority}
                                            </Badge>
                                        </Col>}
                                        <Col md>
                                            <Row className='justify-content-center justify-content-md-start'>{ticket.message}</Row>
                                            <Row>
                                                <Col className='px-0'><b>created: </b>{dayjs(ticket.created).format('YYYY/MM/DD hh:mm')}</Col>
                                                <Col className='px-0'><b>updated: </b>{dayjs(ticket.updated).format('YYYY/MM/DD hh:mm')}</Col>
                                            </Row>
                                        </Col>
                                        <Col md={1} className='d-flex justify-content-center justify-content-md-end align-self-center py-2'>
                                            <Link to={'/tickets/' + ticket.ticketId}>
                                                <Button size='sm' variant={isExpert ? cardVariantOnPrio[ticket.priority] : cardVariantOnStatus[ticket.status]}>Open</Button>
                                            </Link>
                                        </Col>
                                    </Row>
                                </Card.Body>
                            </Card>
                            <Spacer height='1rem' />
                        </Row>
                    ))}
                </>
            )}
        </AuthenticationContext.Consumer>
    )
}

export default TicketsTable