ALTER TABLE orders ADD COLUMN paid_at TIMESTAMP;

UPDATE orders o
SET paid_at = i.issued_at
FROM invoices i
WHERE i.order_id = o.id;
