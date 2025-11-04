-- Datos iniciales para MySQL Database
-- Este archivo se ejecuta automáticamente al iniciar la aplicación

-- Limpiar datos existentes y insertar nuevos
DELETE FROM items_orden;
DELETE FROM ordenes;
DELETE FROM productos;
DELETE FROM usuarios;

-- Insertar usuarios base con contrasena hasheada correctamente para "password"
INSERT INTO usuarios (nombre, email, password, rol, tipo_usuario, telefono) VALUES 
('Admin Sistema', 'admin@artify.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'USUARIO', '3511111111');

-- Insertar usuarios normales
INSERT INTO usuarios (nombre, email, password, rol, tipo_usuario, telefono) VALUES 
('Juan Perez', 'juanperez@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USUARIO', 'USUARIO', '3517845202'),
('María González', 'maria.gonzalez@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USUARIO', 'USUARIO', '3512345678'),
('Carlos Rodríguez', 'carlos.rodriguez@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USUARIO', 'USUARIO', '3518765432'),
('Ana Martínez', 'ana.martinez@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USUARIO', 'USUARIO', '3519876543'),
('Luis Fernández', 'luis.fernandez@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USUARIO', 'USUARIO', '3514567890'),
('Sofia López', 'sofia.lopez@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USUARIO', 'USUARIO', '3513456789'),
('Diego Herrera', 'diego.herrera@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USUARIO', 'USUARIO', '3515678901'),
('Valentina Silva', 'valentina.silva@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USUARIO', 'USUARIO', '3516789012');

-- Insertar artesanos con datos específicos (herencia SINGLE_TABLE)
INSERT INTO usuarios (nombre, email, password, rol, tipo_usuario, nombre_emprendimiento, descripcion, ubicacion, telefono) VALUES 
('Carolina Comba', 'carolinacomba422@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ARTESANO', 'ARTESANO', 'Artesanías Únicas', 'Creaciones únicas hechas a mano con amor y dedicación', 'Villa María, Córdoba', '3534095045'),
('Francisco Comba', 'fran@gnail.con', '$2a$10$piZ/tQZ3ZXn06Mw7pjzSSOdqiFyot0Ql.9iBwjLNG4WhS2TfvEbui', 'ARTESANO', 'ARTESANO', 'Cerámica para tu vida', 'me gusta hacer cosas de ceramica', 'Cordoba, Argentina', '3512345678'),
('Elena Morales', 'elena.morales@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ARTESANO', 'ARTESANO', 'Textiles Artesanales', 'Tejidos únicos en lana y algodón orgánico', 'Buenos Aires, Argentina', '3519876543'),
('Roberto Vega', 'roberto.vega@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ARTESANO', 'ARTESANO', 'Maderas del Sur', 'Muebles y objetos decorativos en madera reciclada', 'Bariloche, Río Negro', '3514567890'),
('Isabel Ruiz', 'isabel.ruiz@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ARTESANO', 'ARTESANO', 'Joyas Naturales', 'Bisutería artesanal con piedras semipreciosas', 'Mendoza, Argentina', '3513456789'),
('Miguel Torres', 'miguel.torres@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ARTESANO', 'ARTESANO', 'Cuero Artesanal', 'Accesorios y artículos de cuero genuino', 'Córdoba, Argentina', '3515678901'),
('Carmen Díaz', 'carmen.diaz@gnail.con', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ARTESANO', 'ARTESANO', 'Velas Aromáticas', 'Velas de cera de soja con esencias naturales', 'Rosario, Santa Fe', '3516789012');

-- ===========================================
-- PRODUCTOS DE FRANCISCO COMBA (Cerámica para tu vida)
-- ===========================================
INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Set de Tazas Cerámicas', 'Set de 4 tazas de cerámica con esmalte mate. Diseño minimalista.', 20000.00, 'CERAMICA', 5, 'https://i.ibb.co/C5HfmJxg/set-de-4-tazas-para-cafe-de-ceramica-beige-y-marron-1000-12-36-247430-1.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'fran@gnail.con')),
('Plato Hondo Artesanal', 'Plato hondo de cerámica con textura natural. Ideal para sopas.', 17500.00, 'CERAMICA', 8, 'https://i.ibb.co/V0VJsYRH/D-Q-NP-649958-MLA89458940275-082025-O.webp', true, NOW(), (SELECT id FROM usuarios WHERE email = 'fran@gnail.con')),
('Jarrón Decorativo', 'Jarrón de cerámica con patrón abstracto. Altura 30cm.', 22000.00, 'CERAMICA', 5, 'https://i.ibb.co/Ncb4CTr/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'fran@gnail.con')),
('Tazón para Cereal', 'Tazón de cerámica esmaltada con asas. Capacidad 400ml.', 100.00, 'CERAMICA', 15, 'https://i.ibb.co/PvsXGSm6/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'fran@gnail.con')),
('Maceta Cerámica', 'Maceta búho de cerámica con drenaje. Ideal para plantas de interior.', 12000.00, 'CERAMICA', 0, 'https://i.ibb.co/5hWM6M4w/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'fran@gnail.con')),
('Cuenco de Cerámica', 'Cuenco de cerámica con textura rugosa. Perfecto para ensaladas.', 9000.00, 'CERAMICA', 0, 'https://i.ibb.co/Pv50pN1T/image.png', false, NOW(), (SELECT id FROM usuarios WHERE email = 'fran@gnail.con'));

-- ===========================================
-- PRODUCTOS DE ELENA MORALES (Textiles Artesanales)
-- ===========================================
INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Manta de Lana', 'Manta de lana merino tejida a mano. 150x200cm. Colores naturales.', 45000.00, 'TEXTILES', 3, 'https://i.ibb.co/4ZCrmRX2/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'elena.morales@gnail.con')),
('Bufanda de Alpaca', 'Bufanda de alpaca peruana. 180cm de largo. Suave y cálida.', 18000.00, 'TEXTILES', 10, 'https://i.ibb.co/8nRXPqx1/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'elena.morales@gnail.con')),
('Cojín Bordado', 'Cojín de algodón con bordado artesanal. 40x40cm.', 12000.00, 'TEXTILES', 8, 'https://i.ibb.co/S7R22kyj/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'elena.morales@gnail.con')),
('Mantel de Lino', 'Mantel de lino con bordado tradicional. 120x180cm.', 25000.00, 'TEXTILES', 4, 'https://i.ibb.co/PvgJfgcv/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'elena.morales@gnail.con')),
('Chal de Seda', 'Chal de seda con estampado floral. 70x180cm.', 35000.00, 'TEXTILES', 0, 'https://i.ibb.co/LdrVZSWm/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'elena.morales@gnail.con')),
('Alfombra Tejida', 'Alfombra de lana tejida a mano. 80x120cm. Diseño geométrico.', 55000.00, 'TEXTILES', 0, 'https://i.ibb.co/qF9GSQ01/image.png', false, NOW(), (SELECT id FROM usuarios WHERE email = 'elena.morales@gnail.con'));

-- ===========================================
-- PRODUCTOS DE ROBERTO VEGA (Maderas del Sur)
-- ===========================================
INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Tabla de Madera', 'Tabla de cortar de madera de roble. 40x25cm. Tratada con aceite natural.', 15000.00, 'MADERA', 6, 'https://i.ibb.co/G3fBTKm9/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'roberto.vega@gnail.con')),
('Porta Vino', 'Porta vino de madera reciclada. Para 6 botellas.', 25000.00, 'MADERA', 4, 'https://i.ibb.co/bjLyp3vB/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'roberto.vega@gnail.con')),
('Caja de Madera', 'Caja de madera de pino con tapa deslizante. 30x20x10cm.', 18000.00, 'MADERA', 8, 'https://i.ibb.co/5b13HkS/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'roberto.vega@gnail.con')),
('Soporte para Libros', 'Soporte para libros de madera de nogal. Par de piezas.', 12000.00, 'MADERA', 10, 'https://i.ibb.co/xKmr41c9/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'roberto.vega@gnail.con')),
('Lámpara de Madera', 'Lámpara de mesa con base de madera torneada. Incluye cable.', 120000.00, 'MADERA', 0, 'https://i.ibb.co/nNZSM780/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'roberto.vega@gnail.con')),
('Reloj de Pared', 'Reloj de pared de madera con números tallados. 30cm diámetro.', 22000.00, 'MADERA', 0, 'https://i.ibb.co/Y4VmyLF7/image.png', false, NOW(), (SELECT id FROM usuarios WHERE email = 'roberto.vega@gnail.con'));

-- ===========================================
-- PRODUCTOS DE ISABEL RUIZ (Joyas Naturales)
-- ===========================================
INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Collar de Amatista', 'Collar con piedra de amatista en plata 925. Cadena 45cm.', 228000.00, 'JOYERIA_ARTESANAL', 5, 'https://i.ibb.co/PztHKsk6/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'isabel.ruiz@gnail.con')),
('Anillo de Cuarzo', 'Anillo de cuarzo rosa en plata 925. Tallas 6-8.', 150000.00, 'JOYERIA_ARTESANAL', 8, 'https://i.ibb.co/gZSk2PXF/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'isabel.ruiz@gnail.con')),
('Pulsera de Ágata', 'Pulsera de ágata con cierre de plata. Ajustable.', 120000.00, 'JOYERIA_ARTESANAL', 12, 'https://i.ibb.co/wrK6zwLP/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'isabel.ruiz@gnail.con')),
('Aros Turquesa', 'Aros turquesa en plata 925. Gancho de seguridad.', 99000.00, 'JOYERIA_ARTESANAL', 6, 'https://i.ibb.co/SD0JWGnL/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'isabel.ruiz@gnail.con')),
('Dije de Jade', 'Dije de jade verde en plata 925. Con cadena incluida.', 100000.00, 'JOYERIA_ARTESANAL', 0, 'https://i.ibb.co/s9t8q8G9/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'isabel.ruiz@gnail.con')),
('Broche de Ópalo', 'Broche de ópalo en plata 925. Para ropa o bolso.', 80000.00, 'JOYERIA_ARTESANAL', 0, 'https://i.ibb.co/hx0PWtQv/image.png', false, NOW(), (SELECT id FROM usuarios WHERE email = 'isabel.ruiz@gnail.con'));

-- ===========================================
-- PRODUCTOS DE MIGUEL TORRES (Cuero Artesanal)
-- ===========================================
INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Billetera de Cuero', 'Billetera de cuero genuino con costura visible. 12cm x 9cm.', 18000.00, 'CUERO', 10, 'https://i.ibb.co/ycSnTGqv/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'miguel.torres@gnail.con')),
('Cinturón de Cuero', 'Cinturón de cuero vacuno con hebilla de acero. 100cm de largo.', 25000.00, 'CUERO', 6, 'https://i.ibb.co/chwJCZ0p/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'miguel.torres@gnail.con')),
('Bolso de Cuero', 'Bolso de cuero con asas de cuero. 35x25x10cm.', 45000.00, 'CUERO', 4, 'https://i.ibb.co/hx8dHpfZ/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'miguel.torres@gnail.con')),
('Porta Documentos', 'Porta documentos de cuero con cierre de cremallera.', 22000.00, 'CUERO', 8, 'https://i.ibb.co/6RxdQ00W/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'miguel.torres@gnail.con')),
('Guantes de Cuero', 'Guantes de cuero para moto. Tallas M y L.', 30000.00, 'CUERO', 0, 'https://i.ibb.co/8Lh9kYm2/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'miguel.torres@gnail.con')),
('Cartera de Cuero', 'Cartera de cuero con múltiples compartimentos.', 35000.00, 'CUERO', 0, 'https://i.ibb.co/Ps1v01dS/image.png', false, NOW(), (SELECT id FROM usuarios WHERE email = 'miguel.torres@gnail.con'));

-- ===========================================
-- PRODUCTOS DE CARMEN DÍAZ (Velas Aromáticas)
-- ===========================================
INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES 
('Vela de Lavanda', 'Vela de cera de soja con esencia de lavanda. 200g. 30 horas de duración.', 8000.00, 'AROMAS_VELAS', 15, 'https://i.ibb.co/8gm8Ktrb/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carmen.diaz@gnail.con')),
('Vela de Vainilla', 'Vela de cera de soja con esencia de vainilla. 200g. 30 horas de duración.', 8000.00, 'AROMAS_VELAS', 12, 'https://i.ibb.co/RqhyDHF/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carmen.diaz@gnail.con')),
('Vela de Eucalipto', 'Vela de cera de soja con esencia de eucalipto. 200g. 30 horas de duración.', 8000.00, 'AROMAS_VELAS', 10, 'https://i.ibb.co/GvdT2mTb/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carmen.diaz@gnail.con')),
('Vela de Canela', 'Vela de cera de soja con esencia de canela. 200g. 30 horas de duración.', 8000.00, 'AROMAS_VELAS', 8, 'https://i.ibb.co/nSqPLqh/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carmen.diaz@gnail.con')),
('Set de Velas', 'Set de 3 velas aromáticas de 100g cada una. Lavanda, vainilla y eucalipto.', 20000.00, 'AROMAS_VELAS', 0, 'https://i.ibb.co/Xfvm274g/image.png', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carmen.diaz@gnail.con')),
('Vela de Rosa', 'Vela de cera de soja con esencia de rosa. 200g. 30 horas de duración.', 8000.00, 'AROMAS_VELAS', 0, 'https://i.ibb.co/YB4Qns8y/image.png', false, NOW(), (SELECT id FROM usuarios WHERE email = 'carmen.diaz@gnail.con'));


-- ===========================================
-- PRODUCTOS DE CAROLINA COMBA (Artesanías Únicas)
-- ===========================================
INSERT INTO productos (nombre, descripcion, precio, categoria, stock, imagen_url, es_activo, fecha_creacion, usuario_id) VALUES
                                                                                                                             ('Mate de Calabaza', 'Mate tradicional hecho con calabaza, curado a mano. Incluye bombilla de acero inoxidable.', 22000.00, 'MATE', 15, 'https://i.ibb.co/N20Km6Mp/mate-calabaza-cuidar-argentino-yerba-600x600.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Vela Aromática Lavanda', 'Vela de cera de soja con esencia de lavanda. Hecha a mano.', 9000.00, 'AROMAS_VELAS', 25, 'https://i.ibb.co/QFf5xNgs/61me6rf-R5v-L.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Taza de Cerámica Pintada', 'Taza de cerámica esmaltada con diseño único pintado a mano. Capacidad 300ml. Plato incluido.', 10000.00, 'CERAMICA', 8, 'https://i.ibb.co/PXMNwjz/taza-ceramica.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Pulsera de Cuero Trenzado', 'Pulsera de cuero genuino trenzado a mano. Ajustable.', 8000.00, 'CUERO', 20, 'https://i.ibb.co/6JvTR2fm/designed-with-edit-org-2021-08-17t140402-4961-bdd918c323319df47116292094026773-640-0.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Collar de Plata 925', 'Collar de plata 925. Incluye cadena de 45cm.', 25000.00, 'JOYERIA_ARTESANAL', 5, 'https://i.ibb.co/cKb9SbZH/img-8489-47f364e566d00f5f4617473497508127-1024-1024.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Cesto de Mimbre Grande', 'Cesto de mimbre natural tejido a mano. Ideal para almacenamiento. 40cm x 30cm.', 28000.00, 'CESTERIA_FIBRAS', 12, 'https://i.ibb.co/vCN2qXBh/archivo-productos-web-finales-0000s-0018s-0000s-0000-canasto-de-mimbre-con-tapa-0652574af8de256be217.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Cuadro de Madera Tallada', 'Cuadro decorativo tallado en madera de pino. 30x20cm.', 15000.00, 'MADERA', 6, 'https://i.ibb.co/Kx81CZD7/il-570x-N-4254232893-oufl.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Jarrón de Vidrio Soplado', 'Jarrón de vidrio soplado a mano con colores únicos. Altura 25cm.', 30000.00, 'VIDRIO', 4, 'https://i.ibb.co/BV6gY581/Sentza-Home-Florerogotacolorhumodevidriosopladoespejosadornosjarravasosjarronjarronesdecoracion03.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Bufanda de Lana Merino', 'Bufanda tejida a mano con lana merino 100%. 150cm de largo.', 10000.00, 'TEXTILES', 18, 'https://i.ibb.co/zV4zB1dF/BUFANDA-FRANCESCO.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Lámpara de Metal Forjado', 'Lámpara de mesa con base de metal forjado a mano. Incluye cable y foco.', 35000.00, 'METALES', 0, 'https://i.ibb.co/R5shYJg/original.jpg', true, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Porta Maceta Cerámico', 'Porta maceta de cerámica con diseño geométrico. Ideal para plantas pequeñas.', 12000.00, 'CERAMICA', 0, 'https://i.ibb.co/yBQT7nVT/image.png', false, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com')),
                                                                                                                             ('Cuenco de Madera', 'Cuenco de madera de olivo tallado a mano. Perfecto para ensaladas.', 18000.00, 'MADERA', 0, 'https://i.ibb.co/rGDQt3VN/image.png', false, NOW(), (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));


-- ===========================================
-- ÓRDENES Y COMPRAS
-- ===========================================

-- Órdenes de Juan Pérez (compras a diferentes artesanos)
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456789', 'ORDER-20250901-001', 'PAGADO', 31000.00, '2025-09-01 10:30:00', '2025-09-01 10:35:00', (SELECT id FROM usuarios WHERE email = 'juanperez@gnail.con')),
('MP123456790', 'ORDER-20250905-001', 'PAGADO', 18000.00, '2025-09-05 14:20:00', '2025-09-05 14:25:00', (SELECT id FROM usuarios WHERE email = 'juanperez@gnail.con')),
('MP123456791', 'ORDER-20250910-001', 'PAGADO', 50000.00, '2025-09-10 09:15:00', '2025-09-10 09:20:00', (SELECT id FROM usuarios WHERE email = 'juanperez@gnail.con')),
('MP123456792', 'ORDER-20250915-001', 'PAGADO', 46000.00, '2025-09-15 16:45:00', '2025-09-15 16:50:00', (SELECT id FROM usuarios WHERE email = 'juanperez@gnail.con')),
('MP123456793', 'ORDER-20250920-001', 'PAGADO', 30000.00, '2025-09-20 11:30:00', '2025-09-20 11:35:00', (SELECT id FROM usuarios WHERE email = 'juanperez@gnail.con'));

-- Items de las órdenes de Juan Pérez
INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Orden 1: Mate + Vela
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250901-001'), (SELECT id FROM productos WHERE nombre = 'Mate de Calabaza'), 1, 22000.00, 22000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250901-001'), (SELECT id FROM productos WHERE nombre = 'Vela Aromática Lavanda'), 1, 9000.00, 9000.00),
-- Orden 2: Taza + Pulsera
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250905-001'), (SELECT id FROM productos WHERE nombre = 'Taza de Cerámica Pintada'), 1, 10000.00, 10000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250905-001'), (SELECT id FROM productos WHERE nombre = 'Pulsera de Cuero Trenzado'), 1, 8000.00, 8000.00),
-- Orden 3: Collar x2
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250910-001'), (SELECT id FROM productos WHERE nombre = 'Collar de Plata 925'), 2, 25000.00, 50000.00),
-- Orden 4: Cesto + Bufanda + Vela
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250915-001'), (SELECT id FROM productos WHERE nombre = 'Cesto de Mimbre Grande'), 1, 28000.00, 28000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250915-001'), (SELECT id FROM productos WHERE nombre = 'Bufanda de Lana Merino'), 1, 10000.00, 10000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250915-001'), (SELECT id FROM productos WHERE nombre = 'Vela Aromática Lavanda'), 1, 9000.00, 9000.00),
-- Orden 5: Jarrón
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250920-001'), (SELECT id FROM productos WHERE nombre = 'Jarrón de Vidrio Soplado'), 1, 30000.00, 30000.00);

-- Órdenes de María González
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456794', 'ORDER-20250925-001', 'PAGADO', 33000.00, '2025-09-25 15:20:00', '2025-09-25 15:25:00', (SELECT id FROM usuarios WHERE email = 'maria.gonzalez@gnail.con')),
('MP123456795', 'ORDER-20250930-001', 'PAGADO', 25000.00, '2025-09-30 11:45:00', '2025-09-30 11:50:00', (SELECT id FROM usuarios WHERE email = 'maria.gonzalez@gnail.con'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- María: Set de Tazas + Bufanda de Alpaca
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250925-001'), (SELECT id FROM productos WHERE nombre = 'Set de Tazas Cerámicas'), 1, 15000.00, 15000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250925-001'), (SELECT id FROM productos WHERE nombre = 'Bufanda de Alpaca'), 1, 18000.00, 18000.00),
-- María: Tabla de Madera + Anillo
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250930-001'), (SELECT id FROM productos WHERE nombre = 'Tabla de Madera'), 1, 15000.00, 15000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250930-001'), (SELECT id FROM productos WHERE nombre = 'Anillo de Cuarzo'), 1, 10000.00, 10000.00);

-- Órdenes de Carlos Rodríguez
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456796', 'ORDER-20251002-001', 'PAGADO', 42000.00, '2025-10-02 09:30:00', '2025-10-02 09:35:00', (SELECT id FROM usuarios WHERE email = 'carlos.rodriguez@gnail.con')),
('MP123456797', 'ORDER-20251005-001', 'PENDIENTE', 18000.00, '2025-10-05 16:15:00', '2025-10-05 16:15:00', (SELECT id FROM usuarios WHERE email = 'carlos.rodriguez@gnail.con'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Carlos: Billetera + Cuenco Bordado
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251002-001'), (SELECT id FROM productos WHERE nombre = 'Billetera de Cuero'), 1, 18000.00, 18000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251002-001'), (SELECT id FROM productos WHERE nombre = 'Cojín Bordado'), 1, 12000.00, 12000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251002-001'), (SELECT id FROM productos WHERE nombre = 'Vela de Lavanda'), 1, 8000.00, 8000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251002-001'), (SELECT id FROM productos WHERE nombre = 'Vela de Vainilla'), 1, 4000.00, 4000.00),
-- Carlos: Pulsera de Ágata (pendiente)
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251005-001'), (SELECT id FROM productos WHERE nombre = 'Pulsera de Ágata'), 1, 12000.00, 12000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251005-001'), (SELECT id FROM productos WHERE nombre = 'Vela de Eucalipto'), 1, 6000.00, 6000.00);

-- Órdenes de Ana Martínez
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456798', 'ORDER-20251008-001', 'PAGADO', 55000.00, '2025-10-08 14:20:00', '2025-10-08 14:25:00', (SELECT id FROM usuarios WHERE email = 'ana.martinez@gnail.con'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Ana: Manta de Lana + Collar de Amatista
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251008-001'), (SELECT id FROM productos WHERE nombre = 'Manta de Lana'), 1, 45000.00, 45000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251008-001'), (SELECT id FROM productos WHERE nombre = 'Collar de Amatista'), 1, 10000.00, 10000.00);

-- Órdenes de Luis Fernández
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456799', 'ORDER-20251010-001', 'PAGADO', 30000.00, '2025-10-10 10:15:00', '2025-10-10 10:20:00', (SELECT id FROM usuarios WHERE email = 'luis.fernandez@gnail.con'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Luis: Lámpara de Madera + Aros Turquesa
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251010-001'), (SELECT id FROM productos WHERE nombre = 'Lámpara de Madera'), 1, 30000.00, 30000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251010-001'), (SELECT id FROM productos WHERE nombre = 'Aros Turquesa'), 1, 0.00, 0.00); -- Sin stock

-- Órdenes de Sofia López
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456800', 'ORDER-20251012-001', 'PAGADO', 25000.00, '2025-10-12 13:30:00', '2025-10-12 13:35:00', (SELECT id FROM usuarios WHERE email = 'sofia.lopez@gnail.con'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Sofia: Cinturón de Cuero + Porta Vino
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251012-001'), (SELECT id FROM productos WHERE nombre = 'Cinturón de Cuero'), 1, 25000.00, 25000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251012-001'), (SELECT id FROM productos WHERE nombre = 'Porta Vino'), 1, 0.00, 0.00); -- Sin stock

-- Órdenes de Diego Herrera
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456801', 'ORDER-20251015-001', 'PAGADO', 18000.00, '2025-10-15 16:45:00', '2025-10-15 16:50:00', (SELECT id FROM usuarios WHERE email = 'diego.herrera@gnail.con'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Diego: Caja de Madera + Dije de Jade
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251015-001'), (SELECT id FROM productos WHERE nombre = 'Caja de Madera'), 1, 18000.00, 18000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251015-001'), (SELECT id FROM productos WHERE nombre = 'Dije de Jade'), 1, 0.00, 0.00); -- Sin stock

-- Órdenes de Valentina Silva
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456802', 'ORDER-20251018-001', 'PAGADO', 20000.00, '2025-10-18 12:00:00', '2025-10-18 12:05:00', (SELECT id FROM usuarios WHERE email = 'valentina.silva@gnail.con'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Valentina: Set de Velas + Mantel de Lino
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251018-001'), (SELECT id FROM productos WHERE nombre = 'Set de Velas'), 1, 20000.00, 20000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251018-001'), (SELECT id FROM productos WHERE nombre = 'Mantel de Lino'), 1, 0.00, 0.00); -- Sin stock

-- ===========================================
-- ÓRDENES DE CAROLINA COMBA (carolinacomba422@gmail.com) COMO COMPRADORA
-- ===========================================

-- Orden 1: Carolina compra productos de Francisco (cerámica)
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456803', 'ORDER-20250922-001', 'PAGADO', 37500.00, '2025-09-22 09:30:00', '2025-09-22 09:35:00', (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Carolina: Set de Tazas + Plato Hondo + Jarrón
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250922-001'), (SELECT id FROM productos WHERE nombre = 'Set de Tazas Cerámicas'), 1, 20000.00, 20000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250922-001'), (SELECT id FROM productos WHERE nombre = 'Plato Hondo Artesanal'), 1, 17500.00, 17500.00);

-- Orden 2: Carolina compra productos de Elena (textiles)
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456804', 'ORDER-20250928-001', 'PAGADO', 63000.00, '2025-09-28 14:20:00', '2025-09-28 14:25:00', (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Carolina: Manta de Lana + Bufanda de Alpaca + Cojín Bordado
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250928-001'), (SELECT id FROM productos WHERE nombre = 'Manta de Lana'), 1, 45000.00, 45000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20250928-001'), (SELECT id FROM productos WHERE nombre = 'Bufanda de Alpaca'), 1, 18000.00, 18000.00);

-- Orden 3: Carolina compra productos de Roberto (madera)
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456805', 'ORDER-20251003-001', 'PAGADO', 55000.00, '2025-10-03 11:45:00', '2025-10-03 11:50:00', (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Carolina: Tabla de Madera + Porta Vino + Caja de Madera
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251003-001'), (SELECT id FROM productos WHERE nombre = 'Tabla de Madera'), 1, 15000.00, 15000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251003-001'), (SELECT id FROM productos WHERE nombre = 'Porta Vino'), 1, 25000.00, 25000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251003-001'), (SELECT id FROM productos WHERE nombre = 'Caja de Madera'), 1, 18000.00, 18000.00);

-- Orden 4: Carolina compra productos de Isabel (joyería)
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456806', 'ORDER-20251007-001', 'PAGADO', 270000.00, '2025-10-07 16:30:00', '2025-10-07 16:35:00', (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Carolina: Collar de Amatista + Anillo de Cuarzo + Pulsera de Ágata
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251007-001'), (SELECT id FROM productos WHERE nombre = 'Collar de Amatista'), 1, 228000.00, 228000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251007-001'), (SELECT id FROM productos WHERE nombre = 'Anillo de Cuarzo'), 1, 150000.00, 150000.00);

-- Orden 5: Carolina compra productos de Miguel (cuero)
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456807', 'ORDER-20251013-001', 'PAGADO', 70000.00, '2025-10-13 13:15:00', '2025-10-13 13:20:00', (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Carolina: Billetera de Cuero + Cinturón de Cuero + Bolso de Cuero
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251013-001'), (SELECT id FROM productos WHERE nombre = 'Billetera de Cuero'), 1, 18000.00, 18000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251013-001'), (SELECT id FROM productos WHERE nombre = 'Cinturón de Cuero'), 1, 25000.00, 25000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251013-001'), (SELECT id FROM productos WHERE nombre = 'Bolso de Cuero'), 1, 45000.00, 45000.00);

-- Orden 6: Carolina compra productos de Carmen (velas)
INSERT INTO ordenes (mercadopago_id, external_reference, estado, total, fecha_creacion, fecha_actualizacion, usuario_id) VALUES 
('MP123456808', 'ORDER-20251019-001', 'PAGADO', 24000.00, '2025-10-19 10:00:00', '2025-10-19 10:05:00', (SELECT id FROM usuarios WHERE email = 'carolinacomba422@gmail.com'));

INSERT INTO items_orden (orden_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
-- Carolina: Vela de Lavanda + Vela de Vainilla + Vela de Eucalipto
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251019-001'), (SELECT id FROM productos WHERE nombre = 'Vela de Lavanda'), 1, 8000.00, 8000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251019-001'), (SELECT id FROM productos WHERE nombre = 'Vela de Vainilla'), 1, 8000.00, 8000.00),
((SELECT id FROM ordenes WHERE external_reference = 'ORDER-20251019-001'), (SELECT id FROM productos WHERE nombre = 'Vela de Eucalipto'), 1, 8000.00, 8000.00);

-- ===========================================
-- RESUMEN DE DATOS POBLADOS
-- ===========================================
-- 
-- USUARIOS:
-- - 1 Admin
-- - 8 Usuarios normales
-- - 7 Artesanos con emprendimientos
-- 
-- PRODUCTOS:
-- - 42 productos en total
-- - Productos con stock (activos)
-- - Productos sin stock (activos)
-- - Productos inactivos
-- - Diferentes categorías y artesanos
-- 
-- ÓRDENES:
-- - 21 órdenes de compra (15 originales + 6 de Carolina como compradora)
-- - Órdenes pagadas y pendientes
-- - Compras de diferentes usuarios a diferentes artesanos
-- - Items con y sin stock
-- - Carolina Comba tiene 6 órdenes finalizadas como compradora
-- 
-- VENTAS:
-- - Cada artesano tiene ventas registradas
-- - Diferentes montos y fechas
-- - Historial de ventas realista

-- Nota: La contrasena para todos los usuarios de prueba es 'password'
-- Hash BCrypt verificado: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- Usuarios disponibles para testing:
-- admin@artify.com - password (ROL: ADMIN)
-- juanperez@gnail.con - password (ROL: USUARIO) - Tiene 5 órdenes finalizadas
-- carolinacomba422@gmail.com - password (ROL: ARTESANO) - Vendedora de productos
-- 
-- Productos de ejemplo creados para carolinacomba422@gmail.com:
-- - 10 productos en diferentes categorías
-- - Precios entre $1.00 y $35,000
-- - Stock variado entre 0 y 25 unidades
-- - Todos activos y listos para venta
-- 
-- Órdenes de ejemplo para juanperez@gnail.con:
-- - 5 órdenes finalizadas (estado PAGADO)
-- - Total gastado: $175,000
-- - Productos comprados: Mate, Vela, Taza, Pulsera, Collar (x2), Cesto, Bufanda, Jarrón
-- - Fechas entre 1-20 septiembre 2025
-- 
-- Órdenes de ejemplo para carolinacomba422@gmail.com (como compradora):
-- - 6 órdenes finalizadas (estado PAGADO)
-- - Total gastado: $515,500
-- - Productos comprados: Cerámica (Francisco), Textiles (Elena), Madera (Roberto), 
--   Joyería (Isabel), Cuero (Miguel), Velas (Carmen)
-- - Fechas entre 22 septiembre - 19 octubre 2025
-- - Diversidad de categorías y artesanos