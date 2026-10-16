-- =====================================================================
--  CarritoGT - Base de datos "cc"
--  Mismo esquema de 2 tablas del script provisto por el catedratico
--  (carrito-compra-basico/script/cc.sql). Unicos cambios:
--    * CREATE ... IF NOT EXISTS para poder ejecutarlo mas de una vez.
--    * Se crean las claves y la relacion dentro de CREATE TABLE.
--  Las columnas, tipos, claves y los 4 productos son identicos al original.
--
--  Ejecucion (Lubuntu):   sudo mysql < database/01_cc.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS cc CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE cc;

-- ---------------------------------------------------------------------
-- Tabla producto: catalogo de productos
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS producto (
    idProducto INT(11)        NOT NULL AUTO_INCREMENT,
    nombre     VARCHAR(255)   DEFAULT NULL,
    codigo     VARCHAR(255)   NOT NULL,
    imagen     TEXT,
    precio     DOUBLE(10, 2)  DEFAULT NULL,
    PRIMARY KEY (idProducto)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Tabla shopping_cart: articulos del carrito de cada usuario
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shopping_cart (
    idShoppingCart INT(11)      NOT NULL AUTO_INCREMENT,
    idProducto     INT(11)      DEFAULT NULL,
    codUsuario     VARCHAR(128) DEFAULT NULL,
    cantidad       INT(11)      DEFAULT NULL,
    PRIMARY KEY (idShoppingCart),
    KEY cc_profk_1 (idProducto),
    CONSTRAINT cc_profk_1 FOREIGN KEY (idProducto) REFERENCES producto (idProducto)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Datos: los mismos 4 productos del script original (solo si la tabla esta vacia)
-- ---------------------------------------------------------------------
INSERT INTO producto (idProducto, nombre, codigo, imagen, precio)
SELECT * FROM (
    SELECT 1, 'FinePix Pro2 3D Camera', '3DcAM01', 'images/product/camera.jpg', 671.21
    UNION ALL SELECT 2, 'EXP Portable Hard Drive', 'USB02', 'images/product/external-hard-drive.jpg', 215.10
    UNION ALL SELECT 3, 'Luxury Ultra thin Wrist Watch', 'wristWear03', 'images/product/watch.jpg', 2121.86
    UNION ALL SELECT 4, 'XP 1155 Intel Core Laptop', 'LPN45', 'images/product/laptop.jpg', 10232.35
) AS datos
WHERE NOT EXISTS (SELECT 1 FROM producto);
