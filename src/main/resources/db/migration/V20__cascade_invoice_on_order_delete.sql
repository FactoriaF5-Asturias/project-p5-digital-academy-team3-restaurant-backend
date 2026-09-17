ALTER TABLE invoices
    DROP CONSTRAINT fk_order_id;

ALTER TABLE invoices
    ADD CONSTRAINT fk_order_id
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE;