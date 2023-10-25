import { useEffect, useState } from "react";
import { Table } from "react-bootstrap";
import TablePageHandler from "../TablePageHandler";

const ExpertsTable = (props) => {
    const [ experts , setExperts] = useState(props.experts)
    const expert4page = 5
    const [index, setIndex] = useState(0)
    const [pageExperts, setPageExperts] = useState(experts.slice(0, expert4page))

    const computeIndex = () => parseInt(experts.length / expert4page) + (experts.length % expert4page ? 1 : 0)

    const handlePageChange = (idx) => {
        setIndex(idx);
        setPageExperts(experts.slice(idx * expert4page, idx * expert4page + expert4page));
    }

    useEffect(() => {setExperts(props.experts)}, [props])

    return (
        <>
            <Table hover>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Expertise Area</th>
                    </tr>
                </thead>
                <tbody>
                    {pageExperts.map((expert, idx) => (
                        <tr key={`expert_${idx}`}>
                            <td>{expert.name + ' ' + expert.surname}</td>
                            <td>{expert.expertiseArea.replace('_', ' ')}</td>
                        </tr>
                    ))}
                </tbody>
            </Table>
            <TablePageHandler index={index} pageNum={computeIndex()} handlePageChange={handlePageChange} />
        </>
    )
}

export default ExpertsTable