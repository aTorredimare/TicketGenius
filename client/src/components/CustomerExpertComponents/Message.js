import { AuthenticationContext } from "../contexts/AuthenticationContext"
import { useContext, useState } from "react"
import React from "react";
import { MDBIcon} from "mdb-react-ui-kit";
import CircularProgress from '@mui/material/CircularProgress';
import { getAttachment } from '../../API'


const Message = (props) => {
    const {sentByExpert, text, time, attachment, ticketId, messageId} = props
    const user = useContext(AuthenticationContext).user
    const sentBy = sentByExpert ? "Expert" : "Client"
    const [downloading, setDownloading] = useState(false)

    const downloadAttachment = () => {
        if (attachment != null) {
            setDownloading(true)
            getAttachment(ticketId, messageId, attachment.url, attachment.filename)
            .then(() => setDownloading(false))
                .catch(e => console.log(e))
        }
    }

    return (
        <AuthenticationContext.Consumer>
            {(authObject) => (
                <>
                {sentBy == user.role ?
                    <div className="d-flex flex-row justify-content-end">
                        <div>

                            <p className="small p-2 me-3 mb-1 text-white rounded-3 bg-primary">
                            {
                            attachment != null ?
                                downloading ?
                                <CircularProgress size="1rem" color="inherit" style={{marginRight: "10px"}}/> :
                                <MDBIcon fas icon="paperclip" onClick={() => downloadAttachment()}
                                style={{paddingRight: "15px", cursor: "pointer"}}/> : null
                            }
                            {text}
                            </p>
                            <p className="small me-3 mb-3 rounded-3 text-muted d-flex justify-content-end">
                            {time.toLocaleString()}
                            </p>
                        </div>
                            <i className="fa-solid fa-user fa-2xl" style={{marginTop: "20px", color: "blue"}}></i>

                    </div>
                    :
                    <div className="d-flex flex-row justify-content-start">
                        <i className="fa-solid fa-user fa-2xl" style={{marginTop: "20px", color: "gray"}}></i>
                        <div>
                            <p className="small p-2 ms-3 mb-1 rounded-3 d-flex justify-content-between align-items-center"
                            style={{ backgroundColor: "#f5f6f7" }}>
                            {text}
                            {
                            attachment != null ?
                                downloading ?
                                <CircularProgress size="1.1rem" style={{marginLeft: "10px"}}/> :
                                <MDBIcon fas icon="paperclip" onClick={() => downloadAttachment()}
                                style={{paddingLeft: "15px", cursor: "pointer"}}/> : null
                            }
                            </p>
                            <p className="small ms-3 mb-3 rounded-3 text-muted">
                            {time.toLocaleString()}
                            </p>
                        </div>
                    </div>
                }
                </>
            )}
        </AuthenticationContext.Consumer>
    )
}

export default Message
