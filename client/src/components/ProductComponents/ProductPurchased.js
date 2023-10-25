import { useContext, useEffect, useState } from "react"
import { Button, Card, Col, Row, Spinner } from "react-bootstrap"
import { getProductsByCustomer } from "../../API"
import { AuthenticationContext } from "../contexts/AuthenticationContext"
import { Link } from "react-router-dom"
import Spacer from "../Spacer"
import Sorter from "../Sorter"
const dayjs = require('dayjs')

const ProductPurchased = (props) => {

    const user = useContext(AuthenticationContext).user
    const [products, setProducts] = useState()
    const [isLoading, setIsLoading] = useState(true)

    useEffect(() => {
        getProductsByCustomer(user.id).then((s) => {
            setProducts(s)
            setIsLoading(false)
        })
    }, [])

    return (
        <>
            <Spacer height='2rem' />
            <h3>My Purchases</h3>
            <Row>
                {isLoading ? <div className='loading-overlay'><Spinner className='spinner' animation="border" variant="light" /></div>
                    :
                    <>
                        {
                            products?.error === null ?
                                <>
                                    <Row className='d-flex mx-auto'>
                                        <Card className='ticket-header'>
                                            <Card.Body>
                                                <Row className='align-items-center'>
                                                    <Col md={2} className='d-none d-md-flex justify-content-start'><b>Ean</b></Col>
                                                    <Col md={2} className='d-none d-md-flex justify-content-start'><b>Brand</b></Col>
                                                    <Col md className='d-none d-md-flex justify-content-start'><b>Name</b></Col>
                                                    <Col md={3} className='d-none d-md-flex justify-content-start'><b>Dates</b></Col>
                                                    <Col md={1} className='d-none d-md-flex justify-content-start'><b>Price</b></Col>
                                                    <Col md={1} className='d-none d-md-flex justify-content-start'></Col>
                                                    {/* <Sorter sorters={sorters} setSorterObject={setSorterObject} /> */}
                                                </Row>
                                            </Card.Body>
                                        </Card>
                                    </Row>
                                    {products.list.map((product, idx) => (
                                        <Row className='mx-auto' key={'ticket_' + idx}>
                                            <Card className='ticket-card bg-opacity-25' bg='secondary'>
                                                <Card.Body>
                                                    <Row>
                                                        <Col md={2} className='d-flex justify-content-center justify-content-md-start align-self-center py-2'>
                                                            {product.ean}
                                                        </Col>
                                                        <Col md={2} className='d-flex justify-content-center justify-content-md-start align-self-center py-2'>
                                                            {product.brand}
                                                        </Col>
                                                        <Col md className='d-flex justify-content-center justify-content-md-start align-self-center py-2'>
                                                            <Row className='justify-content-center justify-content-md-start'>{product.name}</Row>
                                                        </Col>
                                                        <Col md={3} className='py-2'>
                                                            <Row><Col md={12} className='d-flex justify-content-center justify-content-md-start align-self-center'><b>purchased on:&nbsp;</b>{dayjs(product.timestamp).format('YYYY/MM/DD hh:mm')}</Col></Row>
                                                            <Row><Col md={12} className='d-flex justify-content-center justify-content-md-start align-self-center'><b>warranty until:&nbsp;</b>{dayjs(product.warranty).format('YYYY/MM/DD hh:mm')}</Col></Row>
                                                        </Col>
                                                        <Col md={1} className='d-flex justify-content-center justify-content-md-start align-self-center py-2'>
                                                            {product.price}€
                                                        </Col>
                                                        <Col md={1} className='d-flex justify-content-center justify-content-md-end align-self-center py-2'>
                                                            <Link to='/new_ticket' state={{sale: product}}>
                                                                <Button size='sm' variant='success'>New Ticket</Button>
                                                            </Link>
                                                        </Col>
                                                    </Row>
                                                </Card.Body>
                                            </Card>
                                            <Spacer height='1rem' />
                                        </Row>
                                    ))}
                                </>
                                :
                                <Col className='d-flex justify-content-center'>You have never purchased a product</Col>
                        }
                    </>
                }
            </Row>
        </>
    )
}

export default ProductPurchased