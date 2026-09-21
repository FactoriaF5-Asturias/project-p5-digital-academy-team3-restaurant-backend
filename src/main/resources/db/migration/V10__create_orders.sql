CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    tablet_id INTEGER,
    order_type_name VARCHAR(30),
    payment_method_name VARCHAR(30),
    status_name VARCHAR(50),
    total_amount DECIMAL(10,2),
    created_at TIMESTAMP,

    CONSTRAINT fk_tablet_id
        FOREIGN KEY (tablet_id)
        REFERENCES tablets(id),

    CONSTRAINT fk_status_name
        FOREIGN KEY (status_name)
        REFERENCES status(name) ,

    CONSTRAINT fk_order_type_name
        FOREIGN KEY (order_type_name)
        REFERENCES order_type(name),

    CONSTRAINT fk_payment_method_name
        FOREIGN KEY (payment_method_name)
        REFERENCES payment_method(name)
);