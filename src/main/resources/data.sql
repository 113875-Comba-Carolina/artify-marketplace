-- Datos iniciales para H2 Database
-- Este archivo se ejecuta automáticamente al iniciar la aplicación

-- Insertar un usuario ADMIN por defecto
INSERT INTO usuarios (id, nombre, email, contraseña, rol, dtype) VALUES 
(1, 'Admin', 'admin@marketplace.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'Usuario');

-- Insertar un cliente de prueba
INSERT INTO usuarios (id, nombre, email, contraseña, rol, dtype) VALUES 
(2, 'Cliente Test', 'cliente@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'CLIENTE', 'Cliente');

-- Insertar un artesano de prueba
INSERT INTO usuarios (id, nombre, email, contraseña, rol, dtype, nombre_emprendimiento, descripcion, ubicacion) VALUES 
(3, 'Artesano Test', 'artesano@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ARTESANO', 'Artesano', 'Artesanías Test', 'Creaciones únicas hechas a mano', 'Buenos Aires');

-- Nota: La contraseña para todos los usuarios de prueba es 'password'
