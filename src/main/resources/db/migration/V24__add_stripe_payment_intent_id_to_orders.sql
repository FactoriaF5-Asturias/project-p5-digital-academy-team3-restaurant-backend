ALTER TABLE orders
    ADD COLUMN stripe_payment_intent_id VARCHAR(255) NULL;

ALTER TABLE orders
    ADD CONSTRAINT uq_orders_stripe_payment_intent_id UNIQUE (stripe_payment_intent_id);
