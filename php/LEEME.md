# Versión PHP (Fase 1)

El enunciado pide que el repositorio contenga **la versión PHP y la versión Spring Boot** (sección 13).

Copie aquí **su** proyecto PHP funcionando, con los ajustes que hizo en la máquina virtual (por ejemplo, el cambio en `Core/Error.php` para PHP 8), excluyendo la carpeta `vendor/`:

```bash
rsync -a --exclude vendor --exclude .git /var/www/html/carrito-compra-basico/ ~/carritogt/php/
```

Después de copiarlo:

1. Abra `php/App/Config.php` y **reemplace la contraseña real** por un texto de ejemplo, por ejemplo `'CAMBIAR_EN_LOCAL'`, antes de hacer commit. Las credenciales no deben publicarse.
2. Para ejecutarlo en otra máquina: `composer install` dentro de `php/`, importar `script/cc.sql` y configurar el VirtualHost `carrito-compra.localhost` apuntando a `php/public`.

Origen: <https://github.com/mvelasquezo/carrito-compra-basico> (repositorio base del catedrático).
