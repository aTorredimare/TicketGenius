import { Card, Col, Row } from "react-bootstrap"

const ProductCard = (props) => {

    const p = props.product

    return (
        <>
            <Card>
                <Card.Header>
                    <strong>ean:&nbsp;</strong>{p.ean}
                </Card.Header>
                <Card.Body>
                    <Row>
                        <Col md={10}>
                            <strong>name:&nbsp;</strong>{p.name}
                        </Col>
                        <Col md={2}>
                            <strong>brand:&nbsp;</strong>{p.brand}
                        </Col>
                    </Row>
                </Card.Body>
            </Card>
        </>
    )
}

export default ProductCard