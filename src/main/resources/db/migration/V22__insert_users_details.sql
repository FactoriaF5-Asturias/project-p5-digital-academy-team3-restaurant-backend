INSERT INTO users_details (
    user_id,
    email,
    role_id
)
SELECT auth.id, profile.email, role.id
FROM (VALUES
    ('Kitchen', 'hellskitchen@gmail.com', 'KITCHEN'),
    ('Admin', 'adminpoderoso@gmail.com', 'ADMIN')
) AS profile(username, email, role_name)
JOIN users_auth auth ON auth.username = profile.username
JOIN users_role role ON role.name = profile.role_name;