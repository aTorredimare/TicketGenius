import 'bootstrap/dist/css/bootstrap.min.css'
import { Pagination } from 'react-bootstrap'

const TablePageHandler = (props) => {

    const { index = 0, pageNum, handlePageChange, pageToShow = 0 } = props
    //render the page item based on respNum state
    const renderPaginationItems = () => {
        let PaginationItems = []
        const maxPage = pageToShow !== 0 && pageNum > pageToShow ? pageToShow : pageNum
        const indexes = Array.from({ length: maxPage }, (v, i) => ((((index - parseInt(maxPage / 2)) % pageNum) + pageNum) + i) % pageNum)
        indexes.forEach((idx) => {
            PaginationItems.push(
                <Pagination.Item key={'page' + idx} id={idx} {...((index === idx) ? { active: true } : null)} onClick={(ev) => { handlePageChange(parseInt(ev.target.id)) }} >{idx + 1}</Pagination.Item>
            )
        })
        return PaginationItems

    }


    return (
        <>
            {(pageNum > 1) ?
                <Pagination className='table-pagination d-flex justify-content-center' color='secondary'>
                    <Pagination.First onClick={() => { handlePageChange(0) }} />
                    <Pagination.Prev onClick={() => { handlePageChange((index + pageNum - 1) % pageNum) }} />
                    <>
                        {pageNum && renderPaginationItems()}
                    </>
                    <Pagination.Next onClick={() => { props.handlePageChange((index + pageNum + 1) % pageNum) }} />
                    <Pagination.Last onClick={() => { handlePageChange(pageNum - 1) }} />
                </Pagination> : null
            }
        </>
    )
}

export default TablePageHandler