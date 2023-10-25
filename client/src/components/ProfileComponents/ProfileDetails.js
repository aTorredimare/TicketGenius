import { useContext, useEffect, useState } from 'react'
import { Card, Col, Form, InputGroup, Row, Spinner } from 'react-bootstrap'
import { getEmployeeProfile, getUserProfile } from '../../API.js'
import { AuthenticationContext } from '../contexts/AuthenticationContext.js'

const ProfileDetails = (props) => {
    const user = useContext(AuthenticationContext).user
    const [profileInfo, setProfileInfo] = useState()
    const [serverError, setServerError] = useState(false)
    const [errorMessage, setErrorMessage] = useState('')

    useEffect(() => {
        ((user.role === 'Client') ?
            getUserProfile(user.email)
            :
            getEmployeeProfile(user.id))
            .then((profile) => {
                setProfileInfo(user.role === 'Client' ?
                    { ...profile, birthdate: profile.birthdate.format('YYYY//MM/DD') } :
                    user.role === 'Expert' ?
                        {
                            email: profile.email, name: profile.name, surname: profile.surname, birthdate: profile.birthdate,
                            phonenumber: profile.phonenumber, phonenumber: profile.phonenumber, role: profile.role, 'expertise area': profile.expertiseArea
                        }
                        :
                        {
                            email: profile.email, name: profile.name, surname: profile.surname, birthdate: profile.birthdate,
                            phonenumber: profile.phonenumber, phonenumber: profile.phonenumber, role: profile.role
                        }
                )
            }).catch((err) => {
                setServerError(true);
                setErrorMessage(err.message);
            })

    }, [props.email])

    return (
        <>
            <h3 className='p-4 text-center'>My Profile</h3>
            <Row className='mx-auto'>
                <Card as={Col} lg={{ offset: 3, span: 6 }} className='ticket-card justify-content-center'>
                    <Card.Body>
                        {serverError ?
                            <h3 style={{ color: 'red' }}>Error: {errorMessage}</h3>
                            :
                            <>
                                {profileInfo && Object.entries(profileInfo).map(([key, val], idx) => (
                                    <InputGroup className='p-2' size='md' key={key + '_' + idx}>
                                        <InputGroup.Text >{key.charAt(0).toUpperCase() + key.slice(1)}</InputGroup.Text>
                                        <Form.Control readOnly value={val} />
                                    </InputGroup>
                                ))
                                }
                                {!profileInfo && <div className='loading-overlay'><Spinner className='spinner' animation='border' variant='light' /></div>}
                            </>
                        }
                    </Card.Body>
                </Card>
            </Row>
        </>
    )
}

export default ProfileDetails