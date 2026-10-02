UPDATE products
SET image_url = REPLACE(
    image_url,
    '/images/products/',
    'https://humjxfnaqjxirkknngvf.supabase.co/storage/v1/object/public/images/'
)
WHERE image_url LIKE '/images/products/%';
