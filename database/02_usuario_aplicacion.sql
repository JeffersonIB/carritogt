-- =====================================================================
--  CarritoGT - Usuario de MySQL exclusivo para la API
--
--  Buena practica: la aplicacion NO usa "root". Este usuario solo puede
--  leer y modificar datos de la base "cc" (no puede borrar tablas).
--
--  ANTES DE EJECUTAR: reemplace CAMBIE_ESTA_CLAVE por una contrasena propia.
--  Esa misma contrasena va en backend/config/application-local.properties
--  (archivo que NO se sube a GitHub).
--
--  Ejecucion (Lubuntu):   sudo mysql < database/02_usuario_aplicacion.sql
--  (si root ya tiene contrasena:  mysql -u root -p < database/02_usuario_aplicacion.sql)
-- =====================================================================

CREATE USER IF NOT EXISTS 'carritogt'@'localhost' IDENTIFIED BY 'Jeff4243*$';

GRANT SELECT, INSERT, UPDATE, DELETE ON cc.* TO 'carritogt'@'localhost';

FLUSH PRIVILEGES;
