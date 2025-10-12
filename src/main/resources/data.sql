-- Datos iniciales para MySQL Database
-- Este archivo se ejecuta automáticamente al iniciar la aplicación

-- Limpiar datos existentes y insertar nuevos
DELETE FROM productos;
DELETE FROM usuarios;

-- Insertar usuarios base con contrasena hasheada correctamente para "password"
INSERT INTO usuarios (nombre, email, password, rol, tipo_usuario) VALUES 
('Admin Sistema', 'admin@artify.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'USUARIO');

INSERT INTO usuarios (nombre, email, password, rol, tipo_usuario, telefono) VALUES 
('Juan Perez', 'juanperez@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USUARIO', 'USUARIO', '3517845202');

-- Insertar artesano con datos específicos (herencia SINGLE_TABLE)
INSERT INTO usuarios (nombre, email, password, rol, tipo_usuario, nombre_emprendimiento, descripcion, ubicacion, telefono) VALUES 
('Carolina Comba', 'carolinacomba422@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ARTESANO', 'ARTESANO', 'Artesanías Únicas', 'Creaciones únicas hechas a mano con amor y dedicación', 'Villa María, Córdoba', '3534216181');

-- Insertar productos de ejemplo para el artesano
INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Mate de Calabaza', 'Mate tradicional hecho con calabaza, curado a mano. Incluye bombilla de acero inoxidable.', 22000.00, 'MATE', 15, 'https://i.ibb.co/N20Km6Mp/mate-calabaza-cuidar-argentino-yerba-600x600.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Vela Aromática Lavanda', 'Vela de cera de soja con esencia de lavanda. Hecha a mano.', 9000.00, 'AROMAS_VELAS', 25, 'https://i.ibb.co/QFf5xNgs/61me6rf-R5v-L.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Taza de Cerámica Pintada', 'Taza de cerámica esmaltada con diseño único pintado a mano. Capacidad 300ml. Plato incluido.', 10000.00, 'CERAMICA', 8, 'https://i.ibb.co/PXMNwjz/taza-ceramica.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Pulsera de Cuero Trenzado', 'Pulsera de cuero genuino trenzado a mano. Ajustable.', 8000.00, 'CUERO', 20, 'https://i.ibb.co/6JvTR2fm/designed-with-edit-org-2021-08-17t140402-4961-bdd918c323319df47116292094026773-640-0.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Collar de Plata 925', 'Collar de plata 925. Incluye cadena de 45cm.', 25000.00, 'JOYERIA_ARTESANAL', 5, 'https://i.ibb.co/cKb9SbZH/img-8489-47f364e566d00f5f4617473497508127-1024-1024.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Cesto de Mimbre Grande', 'Cesto de mimbre natural tejido a mano. Ideal para almacenamiento. 40cm x 30cm.', 28000.00, 'CESTERIA_FIBRAS', 12, 'https://i.ibb.co/vCN2qXBh/archivo-productos-web-finales-0000s-0018s-0000s-0000-canasto-de-mimbre-con-tapa-0652574af8de256be217.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Cuadro de Madera Tallada', 'Cuadro decorativo tallado en madera de pino. 30x20cm.', 1.00, 'MADERA', 6, 'https://i.ibb.co/Kx81CZD7/il-570x-N-4254232893-oufl.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Jarrón de Vidrio Soplado', 'Jarrón de vidrio soplado a mano con colores únicos. Altura 25cm.', 30000.00, 'VIDRIO', 4, 'https://i.ibb.co/BV6gY581/Sentza-Home-Florerogotacolorhumodevidriosopladoespejosadornosjarravasosjarronjarronesdecoracion03.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Bufanda de Lana Merino', 'Bufanda tejida a mano con lana merino 100%. 150cm de largo.', 10000.00, 'TEXTILES', 18, 'https://i.ibb.co/zV4zB1dF/BUFANDA-FRANCESCO.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Lámpara de Metal Forjado', 'Lámpara de mesa con base de metal forjado a mano. Incluye cable y foco.', 35000.00, 'METALES', 0, 'https://i.ibb.co/R5shYJg/original.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

-- Insertar órdenes finalizadas para Juan Pérez (compras a Carolina Comba)
-- Orden 1: Mate de Calabaza + Vela Aromática Lavanda
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456789', 'ORDER-20241201-001', 'PAGADO', 31000.00, '2024-12-01 10:30:00', '2024-12-01 10:35:00', (SELECT id FROM usuarios WHERE email = 'juanperez@gmail.com'));

-- Items de la orden 1
INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20241201-001'), (SELECT id FROM productos WHERE nombre = 'Mate de Calabaza'), 1, 22000.00, 22000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20241201-001'), (SELECT id FROM productos WHERE nombre = 'Vela Aromática Lavanda'), 1, 9000.00, 9000.00);

-- Orden 2: Taza de Cerámica + Pulsera de Cuero
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456790', 'ORDER-20241202-001', 'PAGADO', 18000.00, '2024-12-02 14:20:00', '2024-12-02 14:25:00', (SELECT id FROM usuarios WHERE email = 'juanperez@gmail.com'));

-- Items de la orden 2
INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20241202-001'), (SELECT id FROM productos WHERE nombre = 'Taza de Cerámica Pintada'), 1, 10000.00, 10000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20241202-001'), (SELECT id FROM productos WHERE nombre = 'Pulsera de Cuero Trenzado'), 1, 8000.00, 8000.00);

-- Orden 3: Collar de Plata (cantidad 2)
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456791', 'ORDER-20241203-001', 'PAGADO', 50000.00, '2024-12-03 09:15:00', '2024-12-03 09:20:00', (SELECT id FROM usuarios WHERE email = 'juanperez@gmail.com'));

-- Items de la orden 3
INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20241203-001'), (SELECT id FROM productos WHERE nombre = 'Collar de Plata 925'), 2, 25000.00, 50000.00);

-- Orden 4: Cesto de Mimbre + Bufanda de Lana (orden grande)
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456792', 'ORDER-20241204-001', 'PAGADO', 46000.00, '2024-12-04 16:45:00', '2024-12-04 16:50:00', (SELECT id FROM usuarios WHERE email = 'juanperez@gmail.com'));

-- Items de la orden 4
INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20241204-001'), (SELECT id FROM productos WHERE nombre = 'Cesto de Mimbre Grande'), 1, 28000.00, 28000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20241204-001'), (SELECT id FROM productos WHERE nombre = 'Bufanda de Lana Merino'), 1, 10000.00, 10000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20241204-001'), (SELECT id FROM productos WHERE nombre = 'Vela Aromática Lavanda'), 1, 9000.00, 9000.00);

-- Orden 5: Jarrón de Vidrio (orden reciente)
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456793', 'ORDER-20241210-001', 'PAGADO', 30000.00, '2024-12-10 11:30:00', '2024-12-10 11:35:00', (SELECT id FROM usuarios WHERE email = 'juanperez@gmail.com'));

-- Items de la orden 5
INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20241210-001'), (SELECT id FROM productos WHERE nombre = 'Jarrón de Vidrio Soplado'), 1, 30000.00, 30000.00);

-- Nota: La contrasena para todos los usuarios de prueba es 'password'
-- Hash BCrypt verificado: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- Usuarios disponibles para testing:
-- admin@artify.com - password (ROL: ADMIN)
-- juanperez@gmail.com - password (ROL: USUARIO) - Tiene 5 órdenes finalizadas
-- carolinacomba422@gmail.com - password (ROL: ARTESANO) - Vendedora de productos
-- 
-- Productos de ejemplo creados para carolinacomba422@gmail.com:
-- - 10 productos en diferentes categorías
-- - Precios entre $1.00 y $35,000
-- - Stock variado entre 0 y 25 unidades
-- - Todos activos y listos para venta
-- 
-- Órdenes de ejemplo para juanperez@gmail.com:
-- - 5 órdenes finalizadas (estado PAGADO)
-- - Total gastado: $175,000
-- - Productos comprados: Mate, Vela, Taza, Pulsera, Collar (x2), Cesto, Bufanda, Jarrón
-- - Fechas entre diciembre 2024