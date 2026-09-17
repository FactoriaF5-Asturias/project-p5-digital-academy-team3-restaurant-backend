ALTER TABLE order_items
    DROP CONSTRAINT fk_order_id;

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_id
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE;