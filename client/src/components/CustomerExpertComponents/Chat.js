import { AuthenticationContext } from "../contexts/AuthenticationContext"
import { useContext, useEffect, useState, useRef } from "react"
import { getTicketChat, newMessage } from '../../API'
import React from "react";
import {  MDBRow, MDBCol, MDBCard, MDBCardBody, MDBCardFooter, MDBIcon} from "mdb-react-ui-kit";
import CircularProgress from '@mui/material/CircularProgress';
import Message from "./Message"

const Chat = (props) => {
    const user = useContext(AuthenticationContext).user
    const { ticketId, ticketStatus } = props
    console.log(ticketStatus)

    const [input, setInput] = useState("")
    const [chat, setChat] = useState("")
    const [chatEmpty, setChatEmpty] = useState(true)
    const [reloadChat, setReloadChat] = useState(true)
    const [file, setFile] = useState(null);

    const [uploading, setUploading] = useState(false)

    const inputRef = useRef(null);

    useEffect(() => {
        const interval = setInterval(() => {
            setReloadChat(r => !r)
        }, 5000);
        return () => clearInterval(interval);
      }, []);


    useEffect(() => {
        getTicketChat(ticketId)
            .then(c => {
                if (c != "") {
                    c.map(item => item.timestamp = new Date(item.timestamp))
                    c.sort((a,b) => b.timestamp - a.timestamp);
                    setChat(c);
                    setChatEmpty(false);
                }
            })
    }, [reloadChat])

    const handleFileChange = (e) => {
        if (e.target.files && e.target.files[0]) {
            setFile(e.target.files[0]);
        }
    };

    const resetFileInput = () => {
        setFile(null)
        inputRef.current.value = null;
      };

    const SendMessage = () => {
        if (input != "") {
            let formData = new FormData();
            formData.append("ticketId", ticketId);
            formData.append("content", input);
            formData.append("attachment", file);

            setUploading(true)
            newMessage(formData)
                .then(m => {
                    setReloadChat(r => !r)
                    setUploading(false)
                })
                .catch(e => console.log(e))

            setInput("")
            resetFileInput()
        }
    }

    return (
        <AuthenticationContext.Consumer>
            {(authObject) => (
                <MDBRow className="d-flex justify-content-center">
                    <MDBCol>
                    <MDBCard style={{ borderRadius: "15px" }}>
                        <MDBCardBody style={{paddingBottom: 0, height: "400px", overflowY: 'auto',
                        display: "flex", flexDirection: "column-reverse"}}>
                            {
                                chatEmpty ?
                                <div style={{textAlign: "center", transform: "translateY(170px)", color:"gray"}}>
                                    No messages
                                </div> :
                                <>
                                {chat.map(c => (
                                    <Message key={c.id}
                                        sentByExpert={c.sentByExpert}
                                        text={c.content}
                                        time={c.timestamp}
                                        attachment={c.attachmentDescription}
                                        ticketId={ticketId}
                                        messageId={c.id}
                                    />
                                ))}
                                </>
                            }
                        </MDBCardBody>
                        {
                            (ticketStatus === "CLOSED" || ticketStatus === "RESOLVED") ? null :
                                <MDBCardFooter className="text-muted d-flex justify-content-start align-items-center p-3 pb-2">
                                    <i className="fa-solid fa-user fa-2xl" style={{paddingRight: 10, color: "blue"}}></i>
                                    <input
                                        type="text"
                                        className="form-control form-control-lg"
                                        id="exampleFormControlInput1"
                                        placeholder="Type message"
                                        onChange={ (e) => setInput(e.target.value)}
                                        value={input}
                                        onKeyDown={(e) => {
                                            if (e.key === "Enter"){
                                                SendMessage()
                                            }
                                        }}
                                        required
                                    ></input>
                                    <a className="ms-3 text-muted " style={{cursor:'pointer'}} href="#!">
                                        <label style={{cursor: "pointer"}} htmlFor="file">
                                            {
                                                uploading ?
                                                    <CircularProgress size="1.1rem"/>
                                                    :
                                                    file === null ?
                                                        <MDBIcon fas icon="paperclip" /> :
                                                        <MDBIcon fas icon="paperclip" style={{color: "blue"}}/>
                                            }

                                        </label>
                                        <input ref={inputRef} style={{display: "none"}} id="file" type="file" onChange={handleFileChange} />
                                    </a>
                                    {
                                        input != "" ?
                                            <a className="ms-3" style={{cursor:'pointer'}} onClick={() => SendMessage()}>
                                                <MDBIcon fas icon="paper-plane" />
                                            </a>
                                            :
                                            <a className="ms-3" style={{cursor:'default', pointerEvents: "none"}} onClick={() => SendMessage()}>
                                                <MDBIcon fas icon="paper-plane" style={{color: "gray"}}/>
                                            </a>
                                    }
                                </MDBCardFooter>
                        }
                            {file === null?
                                <></>
                                // <div style={{paddingLeft: "20px", paddingBottom: "10px", fontStyle: "italic"}}>no file</div>
                                :
                                <div className="align-items-center d-flex" style={{paddingBottom: "10px"}}>
                                    <div style={{paddingLeft: "20px"}}>{file.name}</div>
                                    <MDBIcon fas icon="times" onClick={resetFileInput} style={{paddingLeft: "10px"}}/>
                                </div>
                            }
                    </MDBCard>
                    </MDBCol>
                </MDBRow>
            )}
        </AuthenticationContext.Consumer>
    )
}

export default Chat
