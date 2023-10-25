import { useState } from "react";
import { Table } from "react-bootstrap";
import TablePageHandler from "../TablePageHandler";

const TicketsTable = (props) => {
    const { tickets, setSelectedTicket } = props
    const ticket4page = 5
    const [index, setIndex] = useState(0)
    const [pageTickets, setPageTickets] = useState(tickets.slice(0, ticket4page))
    const [checkedState, setCheckedState] = useState(undefined)

    const handleSelectedTicket = (selected) => {
        setCheckedState(checkedState === selected ? undefined : selected)
        setSelectedTicket(checkedState === selected ? undefined : tickets[selected])
    }

    const computeIndex = () => parseInt(tickets.length / ticket4page) + (tickets.length % ticket4page ? 1 : 0)

    const handlePageChange = (idx) => {
        setIndex(idx);
        setPageTickets(tickets.slice(idx * ticket4page, idx * ticket4page + ticket4page));
    }

    return (
        <>
            <Table>
                <thead>
                    <tr>
                        <th>Ticket number</th>
                        <th>Message</th>
                        <th />
                    </tr>
                </thead>
                <tbody>
                    {pageTickets.map((ticket, idx) => (
                        <tr key={`ticket_${idx}`}>
                            <td>{ticket.ticketId}</td>
                            <td>{ticket.message}</td>
                            <td><input type='checkbox' checked={checkedState === index*ticket4page+idx} onChange={() => handleSelectedTicket(index*ticket4page+idx)} /></td>
                        </tr>
                    ))}
                </tbody>
            </Table>
            <TablePageHandler index={index} pageNum={computeIndex()} handlePageChange={handlePageChange} />
        </>
    )
}

export default TicketsTable