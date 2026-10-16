-- Mismo modelo de 2 tablas del script original cc.sql, adaptado a la sintaxis de H2.
CREATE TABLE producto (
    idProducto INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(255),
    codigo     VARCHAR(255) NOT NULL,
    imagen     VARCHAR(1000),
    precio     DECIMAL(10, 2)
);

CREATE TABLE shopping_cart (
    idShoppingCart INT AUTO_INCREMENT PRIMARY KEY,
    idProducto     INT,
    codUsuario     VARCHAR(128),
    cantidad       INT,
    CONSTRAINT cc_profk_1 FOREIGN KEY (idProducto) REFERENCES producto (idProducto)
);
