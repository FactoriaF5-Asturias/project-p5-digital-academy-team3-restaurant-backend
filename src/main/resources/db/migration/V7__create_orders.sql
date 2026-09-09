CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    tablet_id INTEGER,
    status_name VARCHAR(50),
    total_amount DECIMAL(10,2),
    created_at TIMESTAMP,

    CONSTRAINT fk_tablet_id
        FOREIGN KEY (tablet_id)
        REFERENCES tablets(id)

    CONSTRAINT fk_status_name
        FOREIGN KEY (status_name)
        REFERENCES status(name) 
);

