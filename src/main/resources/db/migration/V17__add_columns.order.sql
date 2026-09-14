ALTER TABLE orders
    ADD COLUMN order_type_name VARCHAR(30),
    ADD COLUMN payment_method_name VARCHAR(30);

ALTER TABLE orders
    ADD CONSTRAINT fk_order_type_name
        FOREIGN KEY (order_type_name)
        REFERENCES order_type(name);

ALTER TABLE orders
    ADD CONSTRAINT fk_payment_method_name
        FOREIGN KEY (payment_method_name)
        REFERENCES payment_method(name);