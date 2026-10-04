INSERT INTO users_auth (
    username,
    password_hash,
    must_change_password
) VALUES 
(
    'Kitchen',
    '$2a$12$F5D/IGDfEdHXjAlhY4P0rOM2hBOv4WchNf4zvpLTIR2TyVyT.OC7S',
    FALSE
),
(
    'Admin',
    '$2a$12$NWcz1owiH0urn5QOkcX.LeXqYSgjj6TRt.22EDT8mv2QjYm1fulhq',
    TRUE
)