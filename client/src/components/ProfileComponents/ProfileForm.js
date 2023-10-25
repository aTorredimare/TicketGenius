import { useState } from "react";
import { Button, Container, Form, Row, Alert } from "react-bootstrap";
import { addNewUserProfile, updateUserProfile } from "../../API.js"
import Profile from "../../models/User.js";

function ProfileForm(props) {
  // Form state
  const [success, setSuccess] = useState('');

  const [email, setEmail] = useState("");
  const [name, setName] = useState("");
  const [surname, setSurname] = useState("");
  const [birthdate, setBirthdate] = useState("");
  const [telephone, setTelephone] = useState("");

  const [errorMessage, setErrorMessage] = useState("");
  const [showErr, setShowErr] = useState(false);

  const cancelInput = () => {
    setShowErr(false);
    setEmail('');
    setName('');
    setSurname('');
    setBirthdate('');
    setTelephone('');
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    let flag = false;

    if (email === '' && props.type==="post") { setShowErr(true); setEmail(''); flag = true; }
    if (name === '') { setShowErr(true); setEmail(''); flag = true; }
    if (surname === '') { setShowErr(true); setEmail(''); flag = true; }
    if (birthdate === '') { setShowErr(true); setEmail(''); flag = true; }
    if (telephone === '') { setShowErr(true); setEmail(''); flag = true; }

    if (flag) return;

    setShowErr(false);

    
    if (props.type === "post") {
      const profile = new Profile(email, name, surname, birthdate, telephone);
        addNewUserProfile(profile)
            .then(() => {
                cancelInput(true);
                setSuccess('yes');
            })
            .catch((err) => {
                setSuccess('no');
                //console.log(err);
                setErrorMessage(err);
            })
    } else {
      const profile = new Profile(props.email, name, surname, birthdate, telephone);
      updateUserProfile(profile)
          .then(() => {
              cancelInput(true);
              setSuccess('yes');
            })
          .catch((err) => {
              setSuccess('no');
              //console.log(err);
              setErrorMessage(err);
          })
    }

    /*

    */
  };

  return (
    <Container fluid style={{ "padding": "0", "margin": "10px" }}>
      <Row className="text-center" style={{ "paddingLeft": "0.7rem" }}>
        { props.type === "post" ? 
            <b style={{ "fontSize": "1.3rem", "color": 'black', "paddingBottom": "0.6rem" }}>Add Profile</b>
            : 
            <b style={{ "fontSize": "1.3rem", "color": 'black', "paddingBottom": "0.6rem" }}>Modify Profile of  {props.email}</b>
        }
      </Row>

      {
        success === "yes" ?
          <Alert variant="success" onClose={() => setSuccess('')} dismissible>
            <Alert.Heading>Profile inserted correctly!</Alert.Heading>
          </Alert>
          :
          <>{
            success === "no" ?
              <Alert variant="danger" onClose={() => setSuccess('')} dismissible>
                <Alert.Heading>Error: {errorMessage}</Alert.Heading>
              </Alert> : null
          }</>
      }


      <Container className="border border-4 rounded" style={{ "marginTop": "0.5rem", "padding": "1rem" }}>
        <Form noValidate onSubmit={handleSubmit}>

        { props.type === "post" ? 
            <Form.Group className="mt-3 mb-2">
                <Form.Label>Email</Form.Label>
                <Form.Control isInvalid={showErr && email === ''}
                    type="text"
                    placeholder="Email"
                    value={email}
                    onChange={event => { setEmail(event.target.value); }} />
                <Form.Control.Feedback type="invalid">
                    Email cant be empty
                </Form.Control.Feedback>
            </Form.Group> : null
        }
          
          <Form.Group className="mt-3">
            <Form.Label>Name</Form.Label>
            <Form.Control isInvalid={showErr && name === ''}
              type="text"
              placeholder="Name"
              value={name}
              onChange={event => { setName(event.target.value); }} />
            <Form.Control.Feedback type="invalid">
              Name cannot be empty
            </Form.Control.Feedback>
          </Form.Group>

          <Form.Group className="mt-3 mb-2">
            <Form.Label>Surname</Form.Label>
            <Form.Control isInvalid={showErr && surname === ''}
              type="text"
              placeholder="Surname"
              value={surname}
              onChange={event => { setSurname(event.target.value); }} />
            <Form.Control.Feedback type="invalid">
              Surname cant be empty
            </Form.Control.Feedback>
          </Form.Group>

          <Form.Group className="mt-3 mb-2">
            <Form.Label>Birthdate</Form.Label>
            <Form.Control isInvalid={showErr && birthdate === ''}
              type="text"
              placeholder="Birthdate (YYYY-MM-DD)"
              value={birthdate}
              onChange={event => { setBirthdate(event.target.value); }} />
            <Form.Control.Feedback type="invalid">
                Birthdate cant be empty
            </Form.Control.Feedback>
          </Form.Group>

          <Form.Group className="mt-3 mb-2">
            <Form.Label>Telephone</Form.Label>
            <Form.Control isInvalid={showErr && telephone === ''}
              type="text"
              placeholder="Telephone (10 digits, no prefix)"
              value={telephone}
              onChange={event => { setTelephone(event.target.value); }} />
            <Form.Control.Feedback type="invalid">
                Telephone cant be empty
            </Form.Control.Feedback>
          </Form.Group>

          <Button type="submit" variant="warning" >Submit</Button>
        </Form>
      </Container>
    </Container>
  );
}

export { ProfileForm };