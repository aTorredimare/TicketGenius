import Product from './models/Product'
import Profile from './models/User'
import axios from 'axios'

/*
** BASE SERVER URL
*/

const client = axios.create({ proxy: { protocol: 'http', host: 'localhost', port: '8081' } })

/*
** AUTH APIs
*/

// function to retrieve auth header from localstorage
const getAuthHeader = () => {
    const user = JSON.parse(localStorage.getItem('authObject'));

    if (user && user.jwts) {
        const authHeader = { Authorization: 'Bearer ' + user.jwts.access_token }
        const expiration = JSON.parse(atob(user.jwts.access_token.split('.')[1])).exp
        const now_inseconds = Date.now() / 1000
        const timeout = 2 * 60
        if (expiration - now_inseconds < timeout) {
            client.post('/API/token/refresh', { refresh_token: user.jwts.refresh_token }, { headers: authHeader }).then((response) => {
                if (response.status === 200) {
                    localStorage.setItem('authObject', JSON.stringify({ jwts: response.data, ...user }))
                    return { Authorization: 'Bearer ' + response.data.access_token }
                }
            }).catch(() => {
                localStorage.removeItem('authObject')
            })
        } else
            return authHeader
    }
    return {}
}

const logIn = async (email, password) => {
    const requestBody = {
        email: email,
        password: password
    }

    const response = await client.post('/API/login', requestBody)
    if (response.status === 200 && String(response.data).length > 0)
        return response.data
    throw response.data
}

const signUp = async (user) => {
    const url = '/API/signup'
    const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
    })
    const responseBody = await response.text()
    if (response.ok)
        return responseBody
    else
        throw responseBody
}

/*
** PRODUCTS APIs
*/

const getAllProducts = async () => {
    const response = await fetch('/API/products', { headers: getAuthHeader() })

    if (!response.ok) {
        try {
            throw new Error(await response.text())
        } catch (error) {
            throw new Error(error.message ? error.message : 'Server Unreachable')
        }
    } else {
        const jsonProducts = await response.json()
        return jsonProducts.map(p => Product.from(p))
    }
}


const getProductDetails = async (productId) => {
    const url = '/API/products/' + productId
    const response = await fetch(url, { headers: getAuthHeader() })

    if (!response.ok) {
        try {
            throw new Error(await response.text())
        } catch (error) {
            throw new Error(error.message ? error.message : 'Server Unreachable')
        }
    } else {
        const jsonProduct = await response.json()
        return Product.from(jsonProduct)
    }
}

/*
** PROFILES APIs
*/

const getUserProfile = async (email) => {
    const url = '/API/profiles/mail/' + email
    const response = await fetch(url, { headers: getAuthHeader() })

    if (!response.ok) {
        try {
            throw new Error(await response.text())
        } catch (error) {
            throw new Error(error.message ? error.message : 'Server Unreachable')
        }
    } else {
        const jsonProfile = await response.json()
        return Profile.from(jsonProfile)
    }
}


const getEmployeeProfile = async (employeeId) => {
    const url = '/API/employees/id/' + employeeId
    const response = await fetch(url, { headers: getAuthHeader() })

    if (!response.ok) {
        try {
            throw new Error(await response.text())
        } catch (error) {
            throw new Error(error.message ? error.message : 'Server Unreachable')
        }
    } else
        return await response.json()
}


const addNewUserProfile = async (profile) => {
    const url = '/API/profiles'
    const body = {
        email: profile.email,
        name: profile.name,
        surname: profile.surname,
        birthdate: profile.birthdate,
        phonenumber: profile.phonenumber
    }
    const response = await fetch(url, {
        method: 'POST',
        headers: [{ 'Content-Type': 'application/json' }, getAuthHeader()],
        body: JSON.stringify(body)
    })
    if (!response.ok) {
        throw await response.text()
    }
}


const updateUserProfile = async (profile) => {
    const url = '/API/profiles/' + profile.email
    const body = {
        email: profile.email,
        name: profile.name,
        surname: profile.surname,
        birthdate: profile.birthdate,
        phonenumber: profile.phonenumber
    }
    const response = await fetch(url, {
        method: 'PUT',
        headers: [{ 'Content-Type': 'application/json' }, await getAuthHeader()],
        body: JSON.stringify(body)
    })
    if (!response.ok) {
        throw await response.text()
    }
}


const getCustomers = async () => {
    const url = 'API/profiles'

    const response = await fetch(url, { headers: getAuthHeader() })
    if (response.ok)
        return { list: await response.json(), error: null }
    return { list: [], error: 'Something went wrong getting customers, try again later...' }
}


/*
** SALE APIs
*/

const addSale = async (sale) => {
    const url = 'API/sales'

    try {
        await client.post(url, sale, { headers: getAuthHeader() })
    } catch (err) {
        console.log(err)
        throw new Error('Something went wrong on submit the sale, please try again...')
    }
    return 'Sale added successfuly'

}


/*
** TICKETS APIs
*/

const getTicketsToAssign = async () => {

    const url = '/API/tickets/status/OPEN'
    const response = await fetch(url, { headers: getAuthHeader() })

    if (!response.ok) {
        try {
            throw new Error(await response.text())
        } catch (error) {
            throw new Error(error.message ? error.message : 'Server Unreachable')
        }
    } else {
        return await response.json()
    }
}

const responseError = { list: null, error: 'We are having trouble getting ticket information, try again later.' }
const computeTicket = (tickets) => {
    return new Promise((resolve, reject) => {
        let ticketsWithStatus = []
        const authHeader = getAuthHeader()
        tickets.forEach(async (t, idx, arr) => {
            const currentTicketStatusResponse = await fetch('/API/tickethistory/currentstatus/' + t.ticketId, { headers: authHeader })
            const creationTicketTimeResponse = await fetch('/API/tickets/' + t.ticketId + '/opened', { headers: authHeader })
            if (!currentTicketStatusResponse.ok || !creationTicketTimeResponse.ok)
                resolve(responseError)
            else {
                try {
                    const currentTicketStatus = await currentTicketStatusResponse.json()
                    ticketsWithStatus.push({
                        ...t,
                        status: currentTicketStatus.status.replace('_', ' '),
                        updated: currentTicketStatus.timestamp,
                        created: await creationTicketTimeResponse.json()
                    })
                    if (idx === arr.length - 1)
                        resolve({ list: ticketsWithStatus, error: null })
                } catch (e) { console.error(e) }
            }
        })
    })
}


const getTicketByExpert = async (expertId) => {
    const url = 'API/tickets/expert/' + expertId
    const response = await fetch(url, { headers: getAuthHeader() })

    if (response.ok)
        return await computeTicket(await response.json())
    else {
        if (response.status === 404)
            return { list: null, error: 'You have no tickets assigned.' }
        return { list: null, error: 'We are having trouble getting ticket information, try again later.' }
    }
}

const getTicketById = async (id) => {
    const url = '/API/tickets/id/' + id
    const response = await fetch(url, { headers: getAuthHeader() })

    if (response.ok) {
        return await response.json()
    } else {
        if (response.status === 404)
            return { list: null, error: "This ticket id doesn't exists" }
        return responseError
    }
}


const getTicketCurrentStatus = async (id) => {
    const url = '/API/tickethistory/currentstatus/' + id
    const response = await fetch(url, { headers: getAuthHeader() })

    if (response.ok) {
        return (await response.json()).status
    } else {
        if (response.status === 404)
            return { list: null, error: "This ticket id doesn't exists" }
        return responseError
    }
}


const getTicketByCustomer = async (customerId, email) => {
    const url = 'API/tickets/customer/' + customerId
    const response = await fetch(url, { headers: getAuthHeader() })

    if (response.ok) {
        return await computeTicket(await response.json())
    } else {
        if (response.status === 404)
            return { list: null, error: 'You have no tickets open, please open a new one if you need assistance.' }
        return responseError
    }
}


const getTicketsStats = async (expertIdFilter = null) => {
    let tmp_stats = { Open: 0, Progress: 0, Resolved: 0, Closed: 0 }
    const responseToStatsDict = { 0: 'Open', 1: 'Open', 2: 'Progress', 3: 'Resolved', 4: 'Closed', }
    const url = '/API/tickets/status/'
    const statusResponse = [
        await fetch(url + 'OPEN', { headers: getAuthHeader() }),
        await fetch(url + 'REOPENED', { headers: getAuthHeader() }),
        await fetch(url + 'IN_PROGRESS', { headers: getAuthHeader() }),
        await fetch(url + 'RESOLVED', { headers: getAuthHeader() }),
        await fetch(url + 'CLOSED', { headers: getAuthHeader() }),
    ]

    const computeStats = new Promise((resolve, _) => {
        statusResponse.forEach(async (r, idx, arr) => {
            if (r.ok) {
                let body = await r.json()
                if (expertIdFilter != null)
                    body = body.filter(ticket => ticket.expertId === expertIdFilter)
                tmp_stats[responseToStatsDict[idx]] = tmp_stats[responseToStatsDict[idx]] + body.length
            }
            if (idx === arr.length - 1) resolve()
        })
    })
    await computeStats
    const stats = { ...tmp_stats, total: Object.values(tmp_stats).reduce((acc, val) => (acc + val), 0) }
    return stats.total === 0 ? { ...stats, total: 1 } : stats
}


const getExperts = async () => {
    const url = '/API/employees/role/EXPERT'
    const response = await fetch(url, { headers: getAuthHeader() })

    if (!response.ok) {
        try {
            throw new Error(await response.text())
        } catch (error) {
            throw new Error(error.message ? error.message : 'Server Unreachable')
        }
    } else {
        return await response.json()
    }
}


const newExpert = async (employee) => {
    const url = '/API/createexpert'
    const authHeader = getAuthHeader()
    try {
        await client.post(url, employee, { headers: authHeader })
    } catch (err) {
        console.log(err)
        throw new Error('Something went wrong on submit the new expert, please try again...')
    }
    return 'Expert added successfuly'
}


const getSaleById = async (saleId) => {
    const url = '/API/sales/sale/' + saleId

    const salePromise = new Promise((resolve, reject) => {
        client.get(url, { headers: getAuthHeader() })
            .then(sales => {
                client.get('/API/products/' + sales.data.ean, { headers: getAuthHeader() })
                    .then(productResponse => {
                        resolve({
                            sale:
                            {
                                ...sales.data,
                                product: productResponse.data.name
                            },
                            saleError: null
                        })
                    }, () => {
                        resolve({
                            sale:
                            {
                                ...sales.data,
                                product: 'Product not found'
                            },
                            saleError: null
                        })
                    })
            }, () => {
                reject({ sale: null, saleError: 'We are having trouble getting ticket information, try again later.' })
            })
    })
    return await salePromise
}


const getProductsByCustomer = async (customerId) => {
    const url = '/API/sales/customer/' + customerId
    const productPromise = new Promise((resolve, _) => {
        let products = []
        let productsNotFound = 0
        client.get(url, { headers: getAuthHeader() })
            .then(sales => {
                sales.data.forEach(async (s, idx, arr) => {
                    client.get('/API/products/' + s.ean, { headers: getAuthHeader() })
                        .then(productResponse => {
                            products.push({ ...s, ...(({ ean, ...o }) => o)(productResponse.data) })
                            if (idx === arr.length - 1) {
                                if (products.length === 0)
                                    resolve({ list: null, error: 'You dont have any product purchased' })
                                resolve({ list: products, error: null })
                            }
                        }, () => productsNotFound += 1)
                })
            }, () => {
                resolve({ list: null, error: 'We are having trouble getting products information, try again later.' })
            })
    })
    return await productPromise
}


const assignTicket = async (ticket) => {
    const url = 'API/ticket/assign/' + ticket.ticketId
    try {
        await client.put(url, ticket, { headers: getAuthHeader() })
    } catch (err) {
        console.log(err)
        throw new Error('Something went wrong on ticket assign, please try again...')
    }
    return 'Ticket assign submitted successfuly'
}

const updateTicketStatus = async (ticketId, status) => {
    console.log(ticketId, status)
    const url = '/API/ticket/changestatus/' + ticketId
    const body = { newStatus: status }
    try {
        const response = await client.put(url, body, { headers: getAuthHeader() })
        return { ticket: response.data, message: 'Status updated successfuly' }
    } catch (err) {
        console.log(err)
        throw new Error('Something went wrong on status update, please try again...')
    }
}

const newTicket = async (ticket) => {
    const url = '/API/ticket'
    const authHeader = getAuthHeader()
    try {
        await client.post(url, ticket, { headers: authHeader })
    } catch (err) {
        console.log(err)
        throw new Error('Something went wrong on submit the ticket, please try again...')
    }
    return 'Ticket opened successfuly, you can find updates into \'Your Tickets\' section'
}

const newMessage = async (formData) => {
    const url = '/API/ticket/newmessage'
    const authHeader = getAuthHeader()

    try {
        await client.post(url, formData, { headers: { ...authHeader, 'Content-Type': 'multipart/form-data' } })
    } catch (err) {
        console.log(err)
        throw new Error('Something went wrong on submit the message, please try again...')
    }
    return 'Message sent successfuly'
}

const getTicketChat = async (ticketId) => {
    const url = '/API/ticket/' + ticketId + '/chat'
    const response = await fetch(url, { headers: getAuthHeader() })

    if (response.ok) {
        if (response.status === 204) {
            return ""
        }
        return await response.json()
    } else {
        if (response.status === 404)
            return { list: null, error: "This ticket id doesn't exists" }
        return responseError
    }
}

const getAttachment = async (ticketId, messageId, attachmentId, filename) => {
    const url = '/API/ticket/' + ticketId + '/chat/' + messageId + '/' + attachmentId
    const response = await fetch(url, { headers: getAuthHeader() })

    if (response.ok) {
        return response.blob()
            .then(blob => {
                var url = window.URL.createObjectURL(blob);
                var a = document.createElement('a');
                a.href = url;
                a.download = filename;
                document.body.appendChild(a);
                a.click();
                a.remove();
            })
    } else {
        if (response.status === 404)
            return { list: null, error: "This ticket id doesn't exists" }
        return responseError
    }
}

export {
    logIn, signUp, getAllProducts, getProductDetails, getUserProfile, getEmployeeProfile, addNewUserProfile, updateUserProfile, getCustomers, addSale, getTicketsToAssign,
    getTicketByExpert, getTicketByCustomer, getTicketsStats, getExperts, newExpert, getSaleById, assignTicket, updateTicketStatus, newTicket, getTicketById,
    getTicketCurrentStatus, getProductsByCustomer, getTicketChat, newMessage, getAttachment
}


