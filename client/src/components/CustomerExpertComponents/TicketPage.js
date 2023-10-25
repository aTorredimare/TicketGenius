import StatsProgressBar from '../StatsProgressBar'
import TicketTable from './TicketTable'
import '../../../node_modules/react-toastify/dist/ReactToastify.css'
import { Card, Col, Row, Spinner } from 'react-bootstrap'
import { AuthenticationContext } from '../contexts/AuthenticationContext'
import { useContext, useEffect, useState } from 'react'
import { HiOutlineExclamationCircle } from 'react-icons/hi'
import { getTicketByCustomer, getTicketByExpert, getTicketsStats } from '../../API'
import Spacer from '../Spacer'
import Sorter from '../Sorter'
const dayjs = require('dayjs')

const TicketPage = (props) => {

    const user = useContext(AuthenticationContext).user
    const isExpert = user.role === 'Expert'
    const [tickets, setTickets] = useState()
    const [SortedTickets, setSortedTickets] = useState()
    const [sorterObject, setSorterObject] = useState({ updated: 1 }) // key -> which property, value: 0 -> desc, 1 -> asc
    const [stats, setStats] = useState({ Open: 0, Progress: 0, Resolved: 0, Closed: 0, total: 1 })
    const [isLoading, setIsLoading] = useState(true)
    const stat_colors = ['#dc3545', '#ffc107', '#0dcaf0', '#198754']
    const stat_texts = ['danger', 'warning', 'info', 'success']
    const sorters = [{ updated: 1 }, { updated: 0 }, { created: 1 }, { created: 0 }]

    useEffect(() => {
        if (user.role === 'Expert') {
            getTicketsStats(user.id).then(ts => {
                setStats(ts)
            }).catch(err =>
                console.warn(err.message)
            )
            getTicketByExpert(user.id)
                .then(t => {
                    setTickets(t)
                    setIsLoading(false)
                })
        } else {
            getTicketByCustomer(user.id)
                .then(t => {
                    setTickets(t)
                    setIsLoading(false)
                })
        }
    }, [])

    useEffect(() => {
        const temp = tickets && tickets.list ? tickets.list.sort((a, b) => {
            const sorter = Object.keys(sorterObject)[0]
            const asc_desc = Object.values(sorterObject)[0]
            const mills = (t) => dayjs(t).valueOf()
            return asc_desc === 0 ? mills(a[sorter]) - mills(b[sorter]) : mills(b[sorter]) - mills(a[sorter])
        }) : []
        tickets && tickets.list && setSortedTickets(temp)
    }, [tickets, sorterObject])

    return (
        <AuthenticationContext.Consumer >
            {(authObject) => (
                <>
                    <Spacer height='2rem' />
                    <h3>My Tickets</h3>
                    <Row>
                        {authObject.user?.role === 'Expert' && Object.keys(stats).filter(k => k !== 'total').map((key, idx) => (
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
                    <Spacer height='2rem' />
                    <Row>
                        {isLoading ? <div className='loading-overlay'><Spinner className='spinner' animation="border" variant="light" /></div>
                            :
                            <>
                                {
                                    tickets?.error === null ?
                                        <>
                                            <Row className='d-flex mx-auto'>
                                                <Card className='ticket-header'>
                                                    <Card.Body>
                                                        <Row className='align-items-center'>
                                                            <Col md={2} className='d-none d-md-flex justify-content-start'><b>Status</b></Col>
                                                            {isExpert && <Col md={2} className='d-none d-md-flex justify-content-start'><b>Priority</b></Col>}
                                                            <Col md className='d-none d-md-flex justify-content-start'><b>Message</b></Col>
                                                            <Sorter sorters={sorters} setSorterObject={setSorterObject} />
                                                        </Row>
                                                    </Card.Body>
                                                </Card>
                                            </Row>
                                            {SortedTickets && <TicketTable tickets={SortedTickets} isExpert={isExpert} />}
                                        </>
                                        :
                                        <Col className='d-flex justify-content-center'>{tickets.error}</Col>
                                }
                            </>
                        }
                    </Row>
                </>
            )}
        </AuthenticationContext.Consumer>
    )
}

export default TicketPage