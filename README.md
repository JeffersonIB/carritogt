# CarritoGT — Migración de PHP a Java con Spring Boot

Proyecto del curso **Programación II**. Es la migración del carrito de compras [`carrito-compra-basico`](https://github.com/mvelasquezo/carrito-compra-basico), escrito en PHP 7 orientado a objetos, hacia una **API REST con Spring Boot** organizada por capas (**Controller → Service → Repository → Entity**). Usa persistencia en **MySQL/MariaDB** y un frontend en **HTML, CSS y JavaScript** que consume la API con `fetch`.

> Estudiante: _su nombre_ · Carné: _su carné_ · Fecha: _dd/mm/aaaa_ · Versión 1.0

## Tecnologías

| Componente | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 17 o superior (probado con 21) |
| Framework | Spring Boot (Web, Data JPA, Validation) | 3.5.16 |
| ORM | Hibernate (incluido en Spring Data JPA) | 6.6 |
| Base de datos | MySQL / MariaDB (perfil `mysql`) · H2 en memoria (perfil `h2`) | MySQL 8.x |
| Build | Maven + Maven Wrapper | 3.9.16 |
| Frontend | HTML5, CSS3, JavaScript (módulos ES, `fetch`) | — |
| Pruebas | JUnit 5, Mockito, MockMvc, AssertJ | incluidas en Spring Boot |
| Entorno | Lubuntu en VirtualBox (Windows como host) | — |

## Estructura

```
carritogt/
├── backend/                      API Spring Boot
│   ├── pom.xml, mvnw, mvnw.cmd
│   ├── config/application-local.properties.example   ← plantilla SIN contraseñas
│   └── src/
│       ├── main/java/com/carritogt/
│       │   ├── controller/   ProductoController, CarritoController
│       │   ├── service/      ProductoService, CarritoService
│       │   ├── repository/   ProductoRepository, ItemCarritoRepository
│       │   ├── entity/       Producto, ItemCarrito, CodigoUsuario
│       │   ├── dto/          *Request, *Response, ErrorResponse
│       │   ├── mapper/       CarritoMapper
│       │   ├── exception/    CarritoGtException (+3 subclases), GlobalExceptionHandler
│       │   └── config/       WebConfig (CORS)
│       ├── main/resources/   application*.properties, db/h2/*.sql
│       └── test/java/...     38 pruebas (unitarias + integración)
├── frontend/                 index.html, carrito.html, css/, js/, images/
├── database/                 01_cc.sql, 02_usuario_aplicacion.sql, 03_verificacion.sql
├── php/                      versión PHP de la Fase 1 (ver php/LEEME.md)
└── docs/                     documentación técnica
```

## Inicio rápido (Lubuntu)

**Requisitos:** JDK 17+, Maven (o el `./mvnw` incluido), MySQL/MariaDB.

```bash
# 1. Base de datos (antes, cambie CAMBIE_ESTA_CLAVE en 02_usuario_aplicacion.sql)
sudo mysql < database/01_cc.sql
sudo mysql < database/02_usuario_aplicacion.sql

# 2. Configuración local (escriba su contraseña; este archivo no se sube a Git)
cd backend
cp config/application-local.properties.example config/application-local.properties
nano config/application-local.properties

# 3. Pruebas y ejecución
mvn test
mvn spring-boot:run
```

Abra **http://localhost:8080**.

**Demostración sin MySQL** (base H2 en memoria):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

Las imágenes de los productos se copian del proyecto PHP: `cp <ruta-php>/public/images/product/*.jpg frontend/images/product/`.

La guía completa (Lubuntu y Windows, paso a paso, con solución de errores) está en [docs/06_GUIA_INSTALACION.md](docs/06_GUIA_INSTALACION.md).

## Configuración

| Propiedad / variable | Uso | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/cc?...` |
| `DB_USER` | Usuario de MySQL | `carritogt` |
| `DB_PASSWORD` | Contraseña de MySQL | *(vacío; debe definirse)* |
| `CORS_ORIGINS` | Orígenes permitidos si el frontend se sirve aparte | `http://carrito-compra.localhost,http://localhost:5500` |
| `PORT` | Puerto HTTP | `8080` |
| `--spring.profiles.active` | `mysql` (por defecto) o `h2` | `mysql` |

Pueden definirse como variables de entorno o en `backend/config/application-local.properties`, que está **excluido de Git**.

## API REST

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/productos` | Lista de productos | 200 |
| GET | `/api/productos/{id}` | Detalle de producto | 200 / 404 |
| GET | `/api/carrito` | Contenido del carrito | 200 |
| POST | `/api/carrito/items` | Agregar `{idProducto, cantidad}` | 201 / 400 / 404 / 422 |
| PUT | `/api/carrito/items/{idItem}` | Cambiar cantidad `{cantidad}` | 200 / 400 / 404 |
| DELETE | `/api/carrito/items/{idItem}` | Eliminar artículo | 204 / 404 |
| DELETE | `/api/carrito` | Vaciar carrito | 204 |

Los endpoints del carrito requieren el encabezado `X-Usuario`, un GUID que genera el frontend. El contrato completo, con ejemplos JSON, curl y Postman, está en [docs/04_API.md](docs/04_API.md).

## Pruebas

```bash
cd backend
mvn test
```
Resultado registrado: **38 pruebas, 0 fallos, BUILD SUCCESS** (detalle en [docs/07_PRUEBAS.md](docs/07_PRUEBAS.md)).

## Documentación

| # | Documento |
|---|---|
| 1 | [Análisis del sistema PHP original](docs/01_ANALISIS_PHP.md) |
| 2 | [Arquitectura y POO](docs/02_ARQUITECTURA.md) |
| 3 | [Base de datos y diccionario de datos](docs/03_BASE_DE_DATOS.md) |
| 4 | [Contrato de la API](docs/04_API.md) |
| 5 | [Requerimientos y casos de uso](docs/05_CASOS_DE_USO.md) |
| 6 | [Guía de instalación (Lubuntu y Windows)](docs/06_GUIA_INSTALACION.md) |
| 7 | [Plan de pruebas y resultados](docs/07_PRUEBAS.md) |
| 8 | [Manual de usuario](docs/08_MANUAL_USUARIO.md) |
| 9 | [Bitácora de desarrollo](docs/09_BITACORA.md) |
| 10 | [Publicación en GitHub](docs/10_GITHUB.md) |
| 11 | [Checklist de entrega y defensa](docs/11_DEFENSA_Y_CHECKLIST.md) |

## Problemas conocidos y limitaciones

- La conexión con **MySQL** debe validarse en su máquina virtual; ver la sección 7.4 de [docs/07_PRUEBAS.md](docs/07_PRUEBAS.md). Las pruebas automatizadas usan H2.
- El carrito se identifica por navegador (`localStorage`), igual que la sesión de la versión PHP. No hay cuentas, pagos ni pedidos (fuera de alcance según la sección 14.2 del enunciado).
- Las imágenes de los productos no se incluyen; se copian del proyecto PHP original. Mientras no estén, se muestra una imagen de respaldo.
- Si modifica archivos de `frontend/`, reinicie `mvn spring-boot:run` para que se publiquen.

## Video demostrativo

_Agregue aquí el enlace a su video de 3 a 5 minutos._
