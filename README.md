# TicketGenius
## Authors:
- Anfossi Davide (https://github.com/davideanfossi, frontend)
- Mistruzzi Luca Guglielmo (https://github.com/mistru97, frontend and deployment)
- Serra Matteo (https://github.com/srrmtt, backend)
- Torredimare Andrea (me, backend and deployment)

## Description
This is the project we developed for the course WebApplicationsII at Politecnico di Torino, graded by the professor with the maximum score. It's a ticketing management system, where users can register and (after the system manager adds their purchases into the db by using the appropriate page) open tickets regarding different kind of problems encountered with the products they own; he can also chat with the expert assignet to the ticket, describing the problem and sending different type of attachments.

*(I will provide a description also for all the containers involved in the build process and their purpose, this is only the first commit of this project and I'm writing a quick general readme)*

## Involved techologies
- Kotlin
- Javascript
- Docker and Docker Compose
- PostgreSQL
- Keycloak
- Tempo
- Loki
- Prometheus
- Graphana

## How to run 
### Prerequisites:
- Docker
- Docker Compose plugin
- Bash shell (if you want to use the provided `cleanBuild.sh` script)

#### Easy way
Just run the provided script mentioned before, it will clean previous client and server builds, perform all the necessary build steps (including server image build by using the provided Dockerfile) and then launch the docker compose command in order to build all the containers needed.

**NOTE**: be sure to check the path to the project assigned to the `BASE` variable in the script, and modify it accordingly to your own machine.

**NOTE2**: feel free to "translate" the script from bash to PowerShell in you are a Windows user and have no access to WSL


## Accounts already created on start
- John Doe, client user (can open tickets and sees his own products) -> `john.doe@mail.com` : `password`
- Linus Torvalds, employee (can manage tickets and interact with users) -> `linus.torvalds@mail.com` : `password`
- Steve Jobs, manager (assigns tickets to employees, creates new employee profiles and registers sales for customers) -> `steve.joe@mail.com` : `password`

