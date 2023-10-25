\connect ticketingservice

COPY products FROM '/data/products.csv' DELIMITER ',' CSV HEADER;
COPY profiles FROM '/data/profiles.csv' DELIMITER ',' CSV HEADER;
COPY employee FROM '/data/employees.csv' DELIMITER ',' CSV HEADER;
COPY sale FROM '/data/sales.csv' DELIMITER ',' CSV HEADER;
COPY ticket FROM '/data/tickets.csv' DELIMITER ',' CSV HEADER;
COPY ticket_status_history FROM '/data/ticket_status_history.csv' DELIMITER ',' CSV HEADER;
