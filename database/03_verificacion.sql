-- =====================================================================
--  CarritoGT - Consultas de verificacion (no modifican datos)
--  Ejecucion:  mysql -u carritogt -p < database/03_verificacion.sql
-- =====================================================================
USE cc;

-- 1. Deben existir exactamente 2 tablas: producto y shopping_cart
SHOW TABLES;

-- 2. Estructura de cada tabla
DESCRIBE producto;
DESCRIBE shopping_cart;

-- 3. Deben aparecer los 4 productos con su URL de imagen
SELECT idProducto, nombre, codigo, imagen, precio FROM producto;

-- 4. Relacion (clave foranea) shopping_cart.idProducto -> producto.idProducto
SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'cc' AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 5. Contenido actual de los carritos (con subtotal), igual que la consulta de ShoppingCartModel.php
SELECT sc.idShoppingCart, sc.codUsuario, p.nombre, sc.cantidad, p.precio * sc.cantidad AS sub_total
FROM shopping_cart sc
INNER JOIN producto p ON sc.idProducto = p.idProducto
ORDER BY sc.codUsuario, sc.idShoppingCart;
