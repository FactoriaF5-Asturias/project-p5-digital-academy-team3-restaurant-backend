CREATE TABLE invoices (
    id SERIAL PRIMARY KEY,
    order_id INTEGER UNIQUE NOT NULL,
    invoice_number VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    issued_at TIMESTAMP,

    CONSTRAINT fk_order_id
        FOREIGN KEY(order_id)
        REFERENCES orders(id)
);