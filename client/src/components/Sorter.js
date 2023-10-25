import { Col, Form } from "react-bootstrap"

const Sorter = (props) => {

    const { sorters, setSorterObject } = props

    return (
        <>
            <Col md={2} className='p-2 py-md-0'>
                <Form.Select onChange={(ev) => setSorterObject(JSON.parse(ev.target.value))}>
                    {sorters.map((sorter, idx) => (
                        <option key={'sorter_' + idx} value={JSON.stringify(sorter)}>{Object.values(sorter)[0] === 0 ? 'least ' : 'last '}{Object.keys(sorter)[0]}</option>
                    ))}
                </Form.Select>
            </Col>
        </>
    )
}

export default Sorter