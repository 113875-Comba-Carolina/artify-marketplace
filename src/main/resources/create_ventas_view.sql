-- Script para crear una vista de ventas que facilite las consultas
-- Este script se puede ejecutar en la base de datos para crear una vista

-- Crear vista de ventas por artesano
CREATE OR REPLACE VIEW vista_ventas_artesano AS
SELECT 
    o.id as orden_id,
    o.estado as estado_orden,
    o.total as total_orden,
    o.fecha_creacion,
    o.mercadopago_id,
    o.external_reference,
    
    -- Información del comprador
    u.id as comprador_id,
    u.nombre as comprador_nombre,
    u.email as comprador_email,
    
    -- Información del producto
    p.id as producto_id,
    p.nombre as producto_nombre,
    p.categoria as producto_categoria,
    
    -- Información del artesano
    artesano.id as artesano_id,
    artesano.nombre as artesano_nombre,
    artesano.email as artesano_email,
    
    -- Información del item
    io.id as item_id,
    io.cantidad,
    io.precio_unitario,
    io.subtotal
FROM items_orden io
JOIN ordenes o ON io.orden_id = o.id
JOIN productos p ON io.producto_id = p.id
JOIN usuarios artesano ON p.usuario_id = artesano.id
JOIN usuarios u ON o.usuario_id = u.id
ORDER BY o.fecha_creacion DESC;

-- Crear vista de estadísticas por artesano
CREATE OR REPLACE VIEW vista_estadisticas_artesano AS
SELECT 
    artesano.id as artesano_id,
    artesano.nombre as artesano_nombre,
    artesano.email as artesano_email,
    COUNT(DISTINCT o.id) as total_ordenes,
    SUM(io.subtotal) as total_ingresos,
    SUM(io.cantidad) as total_productos_vendidos,
    COUNT(DISTINCT CASE WHEN o.estado = 'PAGADO' THEN o.id END) as ordenes_pagadas,
    COUNT(DISTINCT CASE WHEN o.estado = 'PENDIENTE' THEN o.id END) as ordenes_pendientes,
    COUNT(DISTINCT CASE WHEN o.estado = 'ENVIADO' THEN o.id END) as ordenes_enviadas,
    COUNT(DISTINCT CASE WHEN o.estado = 'ENTREGADO' THEN o.id END) as ordenes_entregadas,
    COUNT(DISTINCT CASE WHEN o.estado = 'CANCELADO' THEN o.id END) as ordenes_canceladas
FROM items_orden io
JOIN ordenes o ON io.orden_id = o.id
JOIN productos p ON io.producto_id = p.id
JOIN usuarios artesano ON p.usuario_id = artesano.id
GROUP BY artesano.id, artesano.nombre, artesano.email;






