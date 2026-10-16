# 7. Plan de pruebas y resultados

## 7.1 Cómo ejecutar las pruebas automatizadas

**Carpeta:** `backend`
```bash
mvn test
```
(En Windows: `.\mvnw.cmd test`)

**Cómo leer el resultado:**
- `Tests run: N, Failures: 0, Errors: 0` → todas las pruebas pasaron.
- `Failures` → una verificación no dio el valor esperado. Maven muestra `expected:<...> but was:<...>`.
- `Errors` → la prueba lanzó una excepción inesperada.
- El detalle de cada clase queda en `backend/target/surefire-reports/`.

Las pruebas usan el perfil `h2`, una base en memoria con las mismas 2 tablas y los 4 productos, así que **no** tocan la base MySQL.

## 7.2 Pruebas automatizadas incluidas (38)

| Clase | Tipo | Pruebas | Qué verifica |
|---|---|---|---|
| `ItemCarritoTest` | Unitaria (dominio) | 6 | Subtotal = precio × cantidad; acumular unidades; cambiar cantidad; rechazo de 0, negativos y más de 99 |
| `CodigoUsuarioTest` | Unitaria (dominio) | 9 | Acepta GUID válidos; rechaza vacíos, cortos, HTML/JS e intentos de inyección SQL |
| `CarritoServiceTest` | Unitaria (servicio con Mockito) | 8 | Agregar nuevo, acumular existente, producto inexistente, límite de 99, total calculado, eliminar artículo ajeno, actualizar, vaciar |
| `CarritoApiIntegrationTest` | Integración (API completa + H2) | 15 | Catálogo (4 productos), 404, 400, flujo completo del carrito, validaciones, 422, aislamiento entre usuarios, 405, CORS permitido y rechazado |

## 7.3 Resultados registrados

Ejecución realizada durante el desarrollo, el 16/10/2026, con **JDK Temurin 21.0.12, Maven 3.9.16, Windows 11, perfil `h2`**:

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0 -- in com.carritogt.CarritoApiIntegrationTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0 -- in com.carritogt.entity.CodigoUsuarioTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 -- in com.carritogt.entity.ItemCarritoTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0 -- in com.carritogt.service.CarritoServiceTest
[INFO] Tests run: 38, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Pruebas manuales de la API en ejecución** (JAR iniciado con `--spring.profiles.active=h2`, peticiones con curl):

| Prueba | Resultado obtenido |
|---|---|
| `GET /api/productos` | 200, 4 productos con imagen y precio |
| `GET /api/productos/999` | 404, `"No existe el producto con id 999"` |
| `POST` cámara × 2 | 201, `total: 1342.42` |
| `POST` cámara × 1 (otra vez) | 201, misma fila con `cantidad: 3`, `total: 2013.63` |
| `POST` disco × 1 | 201, `totalArticulos: 4`, `total: 2228.73` |
| `POST` cantidad 0 | 400, `detalles: ["cantidad: debe ser al menos 1"]` |
| `POST` reloj × 99 y luego × 1 | 201 y luego **422** `"...entre 1 y 99"` |
| `GET /api/carrito` sin `X-Usuario` | 400, `"Falta el encabezado obligatorio X-Usuario"` |

**Pruebas de extremo a extremo en el navegador** (frontend servido por Spring Boot):

| Paso | Resultado obtenido |
|---|---|
| Abrir `http://localhost:8080` | Catálogo con 4 productos, precios en quetzales |
| Agregar cámara × 2 y disco × 1 | Mensajes verdes y contador del carrito en 3 |
| Abrir el carrito | 2 filas, total Q 1,557.52 |
| Cambiar la cantidad de la cámara a 5 | Subtotal Q 3,356.05, total Q 3,571.15 |
| Cambiar la cantidad a 150 | Mensaje rojo de validación; el campo vuelve a 5 |
| Eliminar el disco | Fila eliminada, total Q 3,356.05 |
| Vaciar carrito | Mensaje "Carrito vaciado" y vista de carrito vacío |
| Vista móvil (375 px) | Tabla en formato de tarjetas, sin desplazamiento horizontal |

## 7.4 Pendiente de validar en su máquina virtual (perfil `mysql`)

En el entorno donde se desarrolló **no había MySQL**, así que la conexión real a MySQL/MariaDB **no se probó aquí**. Hágalo usted en la VM y guarde las capturas como evidencia:

| # | Prueba | Cómo | Resultado esperado |
|---|---|---|---|
| 1 | Conexión a MySQL | `mvn spring-boot:run` | Log con `profile is active: "mysql"` y `Started CarritoGtApplication`, sin errores de conexión |
| 2 | Catálogo desde MySQL | `curl http://localhost:8080/api/productos` | Los 4 productos de la tabla `producto` |
| 3 | Persistencia | Agregar desde el navegador y luego `mysql -u carritogt -p -e "SELECT * FROM cc.shopping_cart;"` | Las filas aparecen con su `codUsuario` y `cantidad` |
| 4 | Eliminar | Eliminar desde el navegador y repetir el `SELECT` | La fila ya no aparece |
| 5 | Imágenes reales | Copiar los `.jpg` (guía, paso A4) | Se ven las fotos de los productos |
| 6 | CORS con Apache (opcional) | Guía, paso A11 | El frontend en `carrito-compra.localhost` funciona sin errores CORS |
