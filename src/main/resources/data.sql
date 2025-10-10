-- Datos iniciales para MySQL Database
-- Este archivo se ejecuta automáticamente al iniciar la aplicación

-- Limpiar datos existentes y insertar nuevos
DELETE FROM productos;
DELETE FROM artesanos;
DELETE FROM usuarios;

-- Insertar usuarios base con contrasena hasheada correctamente para "password"
INSERT INTO usuarios (nombre, email, password, rol, tipo_usuario) VALUES 
('Admin Sistema', 'admin@artify.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'USUARIO');

INSERT INTO usuarios (nombre, email, password, rol, tipo_usuario) VALUES 
('Usuario Test', 'usuario@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USUARIO', 'USUARIO');

INSERT INTO usuarios (nombre, email, password, rol, tipo_usuario) VALUES 
('Artesano Test', 'artesano@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ARTESANO', 'ARTESANO');

-- Insertar datos específicos del artesano (herencia JOINED)
INSERT INTO artesanos (id, nombre_emprendimiento, descripcion, ubicacion) 
SELECT id, 'Artesanías Únicas', 'Creaciones únicas hechas a mano con amor y dedicación', 'Buenos Aires, Argentina' 
FROM usuarios WHERE email = 'artesano@test.com';

-- Insertar productos de ejemplo para el artesano
INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, artesano_id) VALUES 
('Mate de Calabaza Artesanal', 'Mate tradicional hecho con calabaza natural, curado a mano. Incluye bombilla de acero inoxidable.', 2500.00, 'MATE', 15, 'https://i.ibb.co/N20Km6Mp/mate-calabaza-cuidar-argentino-yerba-600x600.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'artesano@test.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, artesano_id) VALUES 
('Vela Aromática Lavanda', 'Vela de cera de soja con esencia de lavanda. Quema limpia por 40 horas. Hecha a mano.', 1800.00, 'AROMAS_VELAS', 25, 'https://i.ibb.co/QFf5xNgs/61me6rf-R5v-L.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'artesano@test.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, artesano_id) VALUES 
('Taza de Cerámica Pintada', 'Taza de cerámica esmaltada con diseño único pintado a mano. Capacidad 300ml.', 3200.00, 'CERAMICA', 8, 'https://i.ibb.co/PXMNwjz/taza-ceramica.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'artesano@test.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, artesano_id) VALUES 
('Pulsera de Cuero Trenzado', 'Pulsera de cuero genuino trenzado a mano. Ajustable, diseño minimalista.', 1200.00, 'CUERO', 20, 'https://i.ibb.co/6JvTR2fm/designed-with-edit-org-2021-08-17t140402-4961-bdd918c323319df47116292094026773-640-0.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'artesano@test.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, artesano_id) VALUES 
('Collar de Plata 925', 'Collar de plata 925 con diseño geométrico. Incluye cadena de 45cm.', 4500.00, 'JOYERIA_ARTESANAL', 5, 'https://i.ibb.co/Kjd31fpf/Collar-de-piedras-naturales-color-oliva-y-azul.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'artesano@test.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, artesano_id) VALUES 
('Cesto de Mimbre Grande', 'Cesto de mimbre natural tejido a mano. Ideal para almacenamiento. 40cm x 30cm.', 2800.00, 'CESTERIA_FIBRAS', 12, 'https://i.ibb.co/vCN2qXBh/archivo-productos-web-finales-0000s-0018s-0000s-0000-canasto-de-mimbre-con-tapa-0652574af8de256be217.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'artesano@test.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, artesano_id) VALUES 
('Cuadro de Madera Tallada', 'Cuadro decorativo tallado en madera de pino. Diseño de paisaje serrano. 30x20cm.', 3500.00, 'MADERA', 6, 'https://i.ibb.co/Kx81CZD7/il-570x-N-4254232893-oufl.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'artesano@test.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, artesano_id) VALUES 
('Jarrón de Vidrio Soplado', 'Jarrón de vidrio soplado a mano con colores únicos. Altura 25cm.', 4200.00, 'VIDRIO', 4, 'https://i.ibb.co/BV6gY581/Sentza-Home-Florerogotacolorhumodevidriosopladoespejosadornosjarravasosjarronjarronesdecoracion03.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'artesano@test.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, artesano_id) VALUES 
('Bufanda de Lana Merino', 'Bufanda tejida a mano con lana merino 100%. Colores naturales, 150cm de largo.', 2800.00, 'TEXTILES', 18, 'https://i.ibb.co/zV4zB1dF/BUFANDA-FRANCESCO.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'artesano@test.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, artesano_id) VALUES 
('Lámpara de Metal Forjado', 'Lámpara de mesa con base de metal forjado a mano. Incluye cable y bombilla LED.', 5500.00, 'METALES', 3, 'https://i.ibb.co/R5shYJg/original.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'artesano@test.com'));

-- Nota: La contrasena para todos los usuarios de prueba es 'password'
-- Hash BCrypt verificado: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- Usuarios disponibles para testing:
-- admin@artify.com - password (ROL: ADMIN)
-- usuario@test.com - password (ROL: USUARIO)  
-- artesano@test.com - password (ROL: ARTESANO)
-- 
-- Productos de ejemplo creados para el artesano@test.com:
-- - 10 productos en diferentes categorías
-- - Precios entre $1,200 y $5,500
-- - Stock variado entre 3 y 25 unidades
-- - Todos activos y listos para venta