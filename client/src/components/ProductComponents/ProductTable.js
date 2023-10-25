import { useContext, useEffect, useState } from "react"
import { Container, Table, Spinner, Button, Row, Col, Modal, Form, InputGroup, Card } from "react-bootstrap"
import { getAllProducts, getProductDetails } from "../../API.js"
import Spacer from "../Spacer.js"
import { FaSearch } from "react-icons/fa"
import TablePageHandler from "../TablePageHandler.js"
import Sorter from "../Sorter.js"
import { AuthenticationContext } from "../contexts/AuthenticationContext.js"
import { Link } from "react-router-dom"
import isBoolean from "validator/lib/isBoolean.js"


const ProductTable = (props) => {


    // state to hold list of products
    const [productList, setProductList] = useState([])
    // state to display loading when waiting for server response 
    const [loading, setLoading] = useState(true)
    // state to show search modal
    const [showSearchBar, setShowSearchBar] = useState(false)
    // state to validate the ean inserted
    const [validated, setValidated] = useState(false)
    // state to hold the ean to be searched
    const [ean, setEan] = useState(undefined)
    // state do display search/delete filter
    const [switchButtons, setSwitchButtons] = useState(true)
    // state to hold message in case of server error
    const [serverError, setServerError] = useState(undefined)
    const user = useContext(AuthenticationContext).user
    const product4page = 10
    const [index, setIndex] = useState(0)
    const [pageProducts, setPageProducts] = useState([])
    const [sortedProducts, setSortedProducts] = useState([])
    const [sorterObject, setSorterObject] = useState({ ean: 1 }) // key -> which property, value: 0 -> desc, 1 -> asc
    const sorters = [{ ean: 1 }, { ean: 0 }, { name: 1 }, { name: 0 }, { brand: 1 }, { brand: 0 }]

    const computeIndex = () => parseInt(sortedProducts.length / product4page) + (sortedProducts.length % product4page ? 1 : 0)

    const handlePageChange = (idx) => {
        setIndex(idx);
        setPageProducts(sortedProducts.slice(idx * product4page, idx * product4page + product4page));
    }

    // function to load product list
    useEffect(() => {
        getAllProducts().then((products) => {
            setProductList(products)
            setLoading(false)
        }).catch(err => {
            setServerError(err.message)
        })
        setLoading(false)
    }, [])

    useEffect(() => {
        console.log(sorterObject)
        setSortedProducts([...sortProducts(productList)])
    }, [productList, sorterObject])


    useEffect(() => {
        setPageProducts(sortedProducts.slice(0, product4page))
        setIndex(0)
        console.log('here')
    }, [sortedProducts])


    // function to sort list of products
    const sortProducts = (products) => {
        const sorter = Object.keys(sorterObject)[0]
        const asc_desc = Object.values(sorterObject)[0]
        const compareString = (s1, s2) => {
            const stringFormatter = (s) => s.replace(' ', '').toLowerCase()
            s1 = stringFormatter(s1)
            s2 = stringFormatter(s2)
            if(s1 === '') return -1
            if(s2 === '') return 1
            return s1 > s2 ? 1 : s2 > s1 ? -1 : 0
        }
        const sortFunction = (x, y) => isNaN(x[sorter]) ? compareString(x[sorter], y[sorter]) : x[sorter] - y[sorter]
        return asc_desc === 0 ? products.sort((a, b) => sortFunction(a, b)) : products.reverse((a, b) => sortFunction(a, b))
    }


    // function to retrieve searched product from server
    const handleSubmit = (event) => {
        event.preventDefault()
        const form = event.currentTarget
        if (form.checkValidity()) {
            setValidated(false)
            setSwitchButtons(false)
            setShowSearchBar(false)
            getProductDetails(ean).then(r => {
                setSortedProducts([r])
            }).catch(err => {
                console.log(err)
                setServerError(err.message)
            })
        } else {
            setValidated(true)
        }
    }


    const resetProducts = () => {
        setSortedProducts(sortProducts(productList))
        setEan(undefined)
        setServerError(undefined)
        setSwitchButtons(true)
    }


    return (
        <Container fluid className="BrowseProductsContainer">
            <Spacer height='2rem' />
            <h2>Products</h2>
            <Card className='ticket-card'>
                <Card.Body>
                    <Row>
                        <Sorter sorters={sorters} setSorterObject={setSorterObject} />
                        <Col className='d-flex justify-content-end'>
                            {switchButtons ?
                                <Button variant="light" onClick={() => setShowSearchBar(true)}><FaSearch size='1rem' /></Button>
                                :
                                <Button variant="danger" onClick={resetProducts}>Delete filters</Button>
                            }
                        </Col>
                    </Row>
                    {loading ? <div className='loading-overlay'><Spinner className='spinner' animation="border" variant="light" /></div> :
                        <>
                            {!serverError ?
                                <>
                                    <Table striped borderless hover responsive>
                                        <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>ean</th>
                                                <th>Name</th>
                                                <th>Brand</th>
                                                {user.role === 'Manager' && <th />}
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {pageProducts.map((product, idx) =>
                                                <tr key={"table_row_p_" + product.ean}>
                                                    <td>{index * product4page + idx + 1}</td>
                                                    <td>{product.ean}</td>
                                                    <td>{product.name}</td>
                                                    <td>{product.brand}</td>
                                                    {user.role === 'Manager' &&
                                                        <td>
                                                            <Link to='/addsale' state={{ product: product }}>
                                                                <Button variant='success'>Add Sale</Button>
                                                            </Link>
                                                        </td>
                                                    }
                                                </tr>
                                            )}
                                        </tbody>
                                    </Table>
                                    <TablePageHandler index={index} pageNum={computeIndex()} pageToShow={9} handlePageChange={handlePageChange} />
                                </>
                                :
                                <span className="d-flex justify-content-center">error: {serverError}</span>
                            }
                        </>
                    }
                </Card.Body>
            </Card>
            <Modal
                size="sm"
                show={showSearchBar}
                onHide={() => setShowSearchBar(false)}
                centered
            >
                <Modal.Body>
                    <Form noValidate validated={validated} onSubmit={handleSubmit}>
                        <InputGroup>
                            <InputGroup.Text><strong>ean:</strong></InputGroup.Text>
                            <Form.Control
                                required
                                pattern="^([0-9]{13})$"
                                placeholder="000000000000"
                                autoFocus
                                onChange={(ev) => setEan(ev.target.value)}
                            />
                            <Form.Control.Feedback type="invalid">
                                Please insert a number of thirteen cifers.
                            </Form.Control.Feedback>
                        </InputGroup>
                    </Form>
                </Modal.Body>
            </Modal>
        </Container>
    )
}

export default ProductTable