\connect ticketingservice

CREATE TABLE IF NOT EXISTS products(
    ean VARCHAR(15) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS profiles(
    profile_id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    birthdate DATE NOT NULL,
    phonenumber VARCHAR(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS employee(
    employee_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    birthdate DATE NOT NULL,
    phonenumber VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    expertise_area VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS sale(
    sale_id BIGINT PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    warranty TIMESTAMP NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    ean VARCHAR(255) NOT NULL,
    FOREIGN KEY (customer_id)
        REFERENCES profiles (profile_id),
    FOREIGN KEY (ean)
        REFERENCES products (ean)
);

CREATE TABLE IF NOT EXISTS ticket(
    ticket_id BIGINT PRIMARY KEY,
    message VARCHAR(255) NOT NULL,
    priority VARCHAR(255),
    expert_id VARCHAR(255),
    sale_id BIGINT NOT NULL,
    FOREIGN KEY (expert_id)
        REFERENCES employee (employee_id),
    FOREIGN KEY (sale_id)
        REFERENCES sale (sale_id)
);

CREATE TABLE IF NOT EXISTS ticket_status_history(
    history_id BIGINT PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    ticket_ticket_id BIGINT NOT NULL,
    FOREIGN KEY (ticket_ticket_id)
        REFERENCES ticket (ticket_id)
);

CREATE TABLE IF NOT EXISTS chat(
    chat_id BIGINT PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_sent_by_expert BOOLEAN NOT NULL,
    ticket_ticket_id BIGINT NOT NULL,
    FOREIGN KEY (ticket_ticket_id)
        REFERENCES ticket (ticket_id)
);

CREATE TABLE IF NOT EXISTS attachment(
    attachment_id BIGINT PRIMARY KEY,
    content     BYTEA NOT NULL,
    content_type    VARCHAR(255) NOT NULL,
    byte_length INTEGER NOT NULL,
    timestamp TIMESTAMP(6) NOT NULL,
    filename VARCHAR(100) NOT NULL,
    chat_id INTEGER NOT NULL,
    FOREIGN KEY (chat_id)
        REFERENCES chat (chat_id)
);

create sequence chat_seq start with 1 increment by 50 minvalue 1;
--create sequence employee_seq start with 3 increment by 50 minvalue 3;
--create sequence profiles_seq start with 2 increment by 50 minvalue 2;
create sequence sale_seq start with 5 increment by 50 minvalue 5;
create sequence ticket_seq start with 3 increment by 50 minvalue 3;
create sequence ticket_status_history_seq start with 3 increment by 50 minvalue 3;
create sequence attachment_seq start with 1 increment by 50 minvalue 1;
