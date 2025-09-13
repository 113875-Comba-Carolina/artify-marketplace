-- Datos iniciales para MySQL Database
-- Este archivo se ejecuta automáticamente al iniciar la aplicación

-- Limpiar datos existentes y insertar nuevos
DELETE FROM artesanos;
DELETE FROM usuarios;

-- Insertar usuarios base con contraseña hasheada correctamente para "password"
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

-- Nota: La contraseña para todos los usuarios de prueba es 'password'
-- Hash BCrypt verificado: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- Usuarios disponibles para testing:
-- admin@artify.com - password (ROL: ADMIN)
-- usuario@test.com - password (ROL: USUARIO)  
-- artesano@test.com - password (ROL: ARTESANO)