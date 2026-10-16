# 11. Checklist de entrega y preparación de la defensa

## 11.1 Checklist de entrega (secciones 13 y 14.3 del enunciado)

| ✔ | Entregable / criterio | Dónde |
|---|---|---|
| ☐ | VM Lubuntu funcionando junto a Windows, con evidencia de recursos (RAM, CPU, disco, red) | Documento técnico, apartados 2 y 3 |
| ☐ | Apache y MySQL activos (`systemctl status`) | Documento técnico, apartado 4 |
| ☐ | Versión PHP abre desde el VirtualHost `carrito-compra.localhost` con sus recursos | Carpeta `php/` + capturas |
| ☐ | Base `cc` con 2 tablas y 4 productos | `database/` + `03_verificacion.sql` |
| ☐ | API: endpoints responden JSON con códigos HTTP coherentes | `docs/04_API.md` + capturas de curl/Postman |
| ☐ | Controller, Service, Repository y Entity identificables | `backend/src/main/java/com/carritogt/` |
| ☐ | Frontend lista productos, agrega y elimina mediante la API | `frontend/` + capturas |
| ☐ | `mvn test` → BUILD SUCCESS | `docs/07_PRUEBAS.md` + captura |
| ☐ | Diagrama de casos de uso | `docs/05_CASOS_DE_USO.md` (y versión en DIA) |
| ☐ | Diagramas de arquitectura PHP y Spring Boot | `docs/02_ARQUITECTURA.md` |
| ☐ | Diccionario de datos | `docs/03_BASE_DE_DATOS.md` |
| ☐ | Contrato de la API | `docs/04_API.md` |
| ☐ | Manual de usuario | `docs/08_MANUAL_USUARIO.md` |
| ☐ | Bitácora completa y coherente con los commits | `docs/09_BITACORA.md` |
| ☐ | Historial de commits incremental (no uno solo) | GitHub → *Commits* |
| ☐ | README con instrucciones | `README.md` |
| ☐ | Documento de análisis técnico en Word/PDF con los 12 apartados de la sección 9 | Se arma con los archivos de `docs/` y sus capturas |
| ☐ | Video de 3 a 5 minutos: VM, versión PHP, API y aplicación integrada | Enlace en el README |
| ☐ | Ninguna contraseña real en el repositorio | `git status` antes de cada commit |

## 11.2 Capturas recomendadas

1. VirtualBox con la VM encendida y su configuración (RAM, CPU, red).
2. `systemctl status apache2` y `systemctl status mysql` en `active (running)`.
3. Versión PHP en `http://carrito-compra.localhost`.
4. `mysql ... < 03_verificacion.sql` con las 2 tablas y los 4 productos.
5. `mvn test` con **BUILD SUCCESS**.
6. Terminal con `Started CarritoGtApplication` y el perfil `mysql`.
7. `curl` o Postman: GET productos (200), POST item (201), DELETE (204) y un error 400/404.
8. Navegador: catálogo, mensaje "agregado", carrito con total, carrito vacío.
9. `SELECT * FROM cc.shopping_cart;` mostrando lo que se agregó desde el navegador.

## 11.3 Preguntas probables en la defensa y cómo responderlas

**¿Por qué Spring Boot y no Quarkus?**
El enunciado CarritoGT pide Spring Boot con las capas Controller, Service, Repository y Entity. Se eligió la versión 3.5.16 por ser estable, compatible con Java 17–25 y la más documentada.

**¿Qué hace cada capa? Muestre un ejemplo.**
`CarritoController.agregar()` recibe el JSON y lo valida con `@Valid`. `CarritoService.agregarProducto()` aplica la regla de "si existe, sumar". `ItemCarritoRepository` consulta la base. `ItemCarrito` representa la fila de `shopping_cart` y protege su cantidad.

**¿Por qué el controlador no tiene lógica?**
Para que la regla de negocio pueda probarse sin HTTP (`CarritoServiceTest`) y reutilizarse desde otros controladores.

**¿Dónde hay polimorfismo?**
En las excepciones: `GlobalExceptionHandler` trata cualquier `CarritoGtException` llamando a `getEstado()`, y cada subclase devuelve su propio código (404, 400 o 422).

**¿Cómo se identifica al usuario si no hay sesión?**
En PHP era un GUID en `$_SESSION`. Una API REST no guarda estado, así que el navegador genera el GUID, lo guarda en `localStorage` y lo envía en `X-Usuario`. `CodigoUsuario` valida su formato.

**¿Qué problemas de seguridad tenía la versión PHP y cómo se resolvieron?**
Inyección SQL (consultas concatenadas), borrar artículos de otros usuarios y credenciales en el código. Se resolvió con consultas parametrizadas de JPA, búsqueda por id **y** usuario, y configuración externa excluida de Git.

**¿Por qué el total lo calcula el servidor?**
Porque el navegador puede ser manipulado. El precio siempre se toma de la base de datos (`BigDecimal`).

**¿Para qué sirve `PhysicalNamingStrategyStandardImpl`?**
Para respetar los nombres de columnas del script original (`idProducto`). Sin esa propiedad, Spring buscaría `id_producto`.

**¿Qué es CORS y cuándo se necesita?**
Es el mecanismo del navegador que bloquea peticiones a otro origen. Aquí no hace falta si el frontend lo sirve Spring Boot. Si se sirve desde Apache (`carrito-compra.localhost`), ese origen está permitido en `WebConfig` y `app.cors.allowed-origins`.

**¿Por qué hay dos perfiles (mysql y h2)?**
`mysql` es el entorno real (requisito). `h2` permite ejecutar las pruebas automatizadas y una demostración sin depender de MySQL.

**¿Qué pasa si agrego 100 unidades?**
La solicitud con `cantidad: 100` da **400** (validación del DTO). Si se acumulan más de 99 en el carrito da **422** (regla de negocio en `ItemCarrito`) y la transacción se revierte.

## 11.4 Modificaciones en vivo que podrían pedirle (y dónde se hacen)

| Pedido | Archivo |
|---|---|
| Cambiar el máximo de 99 a 50 unidades | `ItemCarrito.CANTIDAD_MAXIMA` (y el mensaje `@Max` de los DTO) |
| Agregar un endpoint que devuelva solo el total | `CarritoController` (nuevo `@GetMapping("/total")`) llamando a `CarritoService.obtenerCarrito()` |
| Cambiar el puerto | `server.port` en `application.properties` o `--server.port=8081` |
| Mostrar el código del producto con otro formato | `frontend/js/catalogo.js` (`crearTarjeta`) |
| Agregar un producto nuevo | `INSERT INTO producto ...` en MySQL (no requiere cambiar código) |
| Cambiar el símbolo de moneda | `frontend/js/ui.js` (`formatoMoneda`) |
