ALTER TABLE orders ADD COLUMN paid_at TIMESTAMP;

UPDATE orders o
SET paid_at = COALESCE(i.issued_at, o.created_at)
FROM invoices i
WHERE i.order_id = o.id;
