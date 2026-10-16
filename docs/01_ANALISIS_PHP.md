# 1. Análisis del sistema original (PHP)

Repositorio analizado: <https://github.com/mvelasquezo/carrito-compra-basico>

Todo lo descrito aquí se comprobó leyendo el código fuente del repositorio. Las funciones que **no** existen en el original se marcan como *mejora*.

## 1.1 Estructura del proyecto PHP

| Carpeta / archivo | Contenido | Rol en la arquitectura |
|---|---|---|
| `public/index.php` | Front controller: carga Composer, registra rutas y despacha | Entrada única de peticiones |
| `public/.htaccess` | Reescribe toda URL hacia `index.php` | Configuración de Apache |
| `Core/Router.php` | Enrutador propio (rutas `{controller}/{action}`, `{id}`, `{token}`) | Infraestructura |
| `Core/Controller.php`, `Core/View.php`, `Core/Model.php`, `Core/Error.php` | Clases base del mini‑framework MVC (PDO, Twig, manejo de errores) | Infraestructura |
| `App/Controllers/Home.php` | Muestra el catálogo | Controlador |
| `App/Controllers/ShoppingCart.php` | Ver carrito, agregar, eliminar, vaciar | Controlador |
| `App/Models/ProductoModel.php` | `getAll()` de la tabla `producto` | Modelo / datos |
| `App/Models/ShoppingCartModel.php` | `getAll`, `agregar`, `eliminar`, `eliminarTodos` sobre `shopping_cart` | Modelo / datos |
| `App/Models/Resultado.php` | Objeto de respuesta (`errno`, `resultado`, `mensaje`) | Modelo auxiliar |
| `App/Auth.php`, `App/Token.php` | Genera un GUID por sesión (`$_SESSION['codUsr']`) | Identificación anónima |
| `App/Flash.php` | Mensajes temporales ("Producto agregado") | Presentación |
| `App/Config.php` | Credenciales de BD, `SHOW_ERRORS`, `HOST` | Configuración |
| `App/Views/*.html` | Plantillas Twig (`base`, `Home/index`, `ShoppingCart/index`, 404, 500) | Vista |
| `public/assets/`, `public/images/` | CSS, JavaScript y las 4 imágenes de productos | Recursos estáticos |
| `script/cc.sql` | Base `cc` con 2 tablas y 4 productos | Base de datos |
| `vendor/` | Dependencias de Composer (Twig, PHPUnit) | Librerías |
| `tests/ProductoModelTest.php` | Prueba PHPUnit | Pruebas |

## 1.2 Flujo de una petición (ejemplo: agregar al carrito)

```
Navegador ──POST /shopping-cart/{token}/agregar──► Apache (.htaccess)
   └─► public/index.php ─► Core\Router::dispatch()
         └─► App\Controllers\ShoppingCart::agregarAction()
               ├─► new ProductoModel($_POST)          (id, cantidad)
               ├─► ShoppingCartModel::agregar($token, $item)
               │      └─► Core\Model::gld(...)  INSERT o "cantidad = cantidad + n"
               ├─► Flash::agregarMensaje('Producto agregado')
               └─► redirect('/')  ─► Home::indexAction() ─► View::renderTemplate('Home/index.html')
```

## 1.3 Funcionalidades verificadas en el código

| Funcionalidad | Dónde está | ¿Se conserva en Spring Boot? |
|---|---|---|
| Listar productos (nombre, código, imagen, precio) | `Home::indexAction` + `ProductoModel::getAll` | Sí → `GET /api/productos` (RF‑01, RF‑02, RF‑07) |
| Agregar producto con cantidad; si ya existe, suma la cantidad | `ShoppingCart::agregarAction` + `ShoppingCartModel::agregar` (`gld`) | Sí → `POST /api/carrito/items` (RF‑03) |
| Ver carrito con cantidad y subtotal (`precio * cantidad`) | `ShoppingCart::indexAction` + `ShoppingCartModel::getAll` | Sí → `GET /api/carrito` (RF‑04) |
| Eliminar un artículo | `ShoppingCart::eliminarAction` | Sí → `DELETE /api/carrito/items/{id}` (RF‑05) |
| Vaciar el carrito | `ShoppingCart::vaciarAction` | Sí → `DELETE /api/carrito` |
| Identificar al comprador con un GUID anónimo | `Auth::getUsuario` + `Token::getGuidv4` | Sí → encabezado `X-Usuario` |
| Mensajes de confirmación | `Flash` | Sí → mensajes en JavaScript (RF‑08) |
| **No existe:** modificar cantidades | — | *Mejora* → `PUT /api/carrito/items/{id}` |
| **No existe:** total general del carrito | — | *Mejora* → campo `total` calculado en el servidor |
| **No existe:** confirmar compra / pedidos | — | Fuera de alcance (sección 14.2 del enunciado) |

## 1.4 Cómo se almacenan los datos

- **Productos:** tabla `producto` (4 filas). La imagen es una **ruta relativa** (`images/product/camera.jpg`) que el navegador resuelve contra `public/`.
- **Carrito:** tabla `shopping_cart`. Cada fila es (producto, usuario, cantidad). El usuario es un **GUID** guardado en la sesión de PHP; no hay cuentas ni contraseñas.
- **Relación:** `shopping_cart.idProducto` → `producto.idProducto` (clave foránea `cc_profk_1`).

## 1.5 Problemas detectados (y cómo los resuelve la migración)

| # | Problema en PHP | Riesgo | Solución en Spring Boot |
|---|---|---|---|
| 1 | `ShoppingCartModel::getAll` concatena `$cod_usr` dentro del SQL (`... like '$cod_usr'`) | Inyección SQL | Consultas JPQL con parámetros (`ItemCarritoRepository`) |
| 2 | `eliminar` concatena `$idShoppingCart` en el `DELETE` | Inyección SQL | `findByIdItemAndCodUsuario` con parámetros |
| 3 | `eliminar` no verifica el dueño: cualquiera puede borrar el artículo de otro usuario cambiando el id de la URL | Acceso indebido | Se busca el artículo **por id y por usuario**; si no es suyo → 404 |
| 4 | `vaciar` toma el token de la URL, no de la sesión | Un usuario podría vaciar el carrito de otro | El usuario sale del encabezado validado `X-Usuario` |
| 5 | `new ProductoModel($_POST)` asigna cualquier campo recibido | Asignación masiva | DTO `AgregarItemRequest` solo acepta `idProducto` y `cantidad` |
| 6 | No se valida la cantidad (acepta 0, negativos o texto) | Datos incoherentes | Bean Validation (`@Min(1) @Max(99)`) + reglas en `ItemCarrito` |
| 7 | No se verifica que el producto exista antes de insertar | Error de clave foránea sin mensaje claro | `ProductoService.buscarEntidad` → 404 con mensaje |
| 8 | Credenciales `root` / contraseña escritas en `App/Config.php` y `SHOW_ERRORS = true` | Exposición de secretos y de detalles internos | Variables de entorno / archivo local excluido de Git; errores genéricos para el cliente |
| 9 | Vistas y lógica acopladas: Twig recibe datos ya armados por PHP | No se puede reutilizar desde otro cliente | API REST JSON independiente del frontend |
| 10 | Con PHP 8 los avisos *deprecated* se convierten en errores fatales (`Core/Error.php`) | La app no abre en servidores actuales | No aplica: Java 17+ |
