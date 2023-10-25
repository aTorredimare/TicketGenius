import { useEffect, useState } from 'react'
import { Button, Card, Col, Row } from 'react-bootstrap'
import { getTicketsToAssign, getTicketsStats, getExperts } from '../../API'
import StatsProgressBar from '../StatsProgressBar'
import { HiOutlineExclamationCircle } from 'react-icons/hi'
import TicketsTable from './TicketsTable'
import ExpertsTable from './ExpertsTable'
import TicketModal from './TicketModal'
import Spacer from '../Spacer'



const ManagerDashboard = () => {
    const [tickets, setTickets] = useState([])
    const [stats, setStats] = useState({ Open: 0, Progress: 0, Resolved: 0, Closed: 0, total: 1 })
    const stat_colors = ['#dc3545', '#ffc107', '#0dcaf0', '#198754']
    const stat_texts = ['danger', 'warning', 'info', 'success']
    const [experts, setExperts] = useState([])
    const [selectedTicket, setSelectedTicket] = useState(undefined)
    const [showTicketModal, setShowTicketModal] = useState(false)

    useEffect(() => {
        getTicketsStats().then(ts => {
            setStats(ts)
        }).catch(err =>
            console.warn(err.message)
        )
        getTicketsToAssign().then(t => {
            setTickets(t)
        }).catch(err =>
            console.warn(err.message)
        )
        getExperts().then(e => {
            setExperts(e)
        }).catch(err =>
            console.warn(err.message)
        )
    }, [])

    return (
        <>
            <Spacer height='2rem' />
            <h3>Ticket Dashboard</h3>
            <Row>
                {Object.keys(stats).filter(k => k !== 'total').map((key, idx) => (
                    <Col key={`stat_${key}`}>
                        <Card className='dashboard-card' text={stat_texts[idx]}>
                            <Card.Body className='dashboard-card-body stats'>
                                <Row>
                                    <Col md='6'>
                                        <Row><span className='d-flex justify-content-center'><HiOutlineExclamationCircle />{key}</span></Row>
                                        <Row className='d-flex justify-content-center stats-counter'>{stats[key]}</Row>
                                    </Col>
                                    <Col>
                                        <StatsProgressBar ratio={(stats[key] / stats.total * 100).toFixed(2)} color={stat_colors[idx]} />
                                    </Col>
                                </Row>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>
            <Row>
                <Col md='8'>
                    <Card className='dashboard-card manager-dashboard-table'>
                        <Card.Body className='dashboard-card-body'>
                            <Row className='d-flex justify-content-between'>
                                <Col><h4>To assign</h4></Col>
                                <Col className='d-flex justify-content-end'>
                                    {tickets.length > 0 && <Button
                                        className='ticket-button'
                                        variant={selectedTicket ? 'outline-info' : 'outline-secondary'}
                                        size='sm'
                                        disabled={!selectedTicket}
                                        onClick={() => selectedTicket ? setShowTicketModal(true) : null}>
                                        Assign
                                    </Button>
                                    }
                                </Col>
                            </Row>
                            {
                                tickets.length === 0 ?
                                    <>All tickets has been asigned</>
                                    :
                                    <TicketsTable tickets={tickets} setSelectedTicket={setSelectedTicket} />
                            }
                        </Card.Body>
                    </Card>
                </Col>
                <Col md='4'>
                    <Card className='dashboard-card manager-dashboard-table'>
                        <Card.Body className='dashboard-card-body'>
                            <h4>Experts</h4>
                            {
                                experts.length === 0 ?
                                    <>No expert found</>
                                    :
                                    <ExpertsTable experts={experts} />
                            }
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
            <TicketModal ticket={selectedTicket} experts={experts} show={showTicketModal} onHide={() => setShowTicketModal(false)} />
        </>
    )
}

export default ManagerDashboard