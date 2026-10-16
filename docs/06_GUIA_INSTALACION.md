# 6. Guía de instalación y ejecución

Esta guía tiene dos partes:
- **Parte A: Lubuntu en la máquina virtual.** Es el entorno que exige el enunciado (RNF‑01: el desarrollo y el servidor deben ejecutarse dentro de la máquina virtual GNU/Linux).
- **Parte B: Windows con PowerShell.** Alternativa para probar el proyecto directamente en Windows.

En cada paso se indica **en qué carpeta** ejecutar el comando y **qué resultado** esperar.

## Estructura del proyecto

```
carritogt/                  ← carpeta RAÍZ del repositorio
├── backend/                ← API Spring Boot (comandos Maven aquí)
├── frontend/               ← HTML, CSS, JavaScript e imágenes
├── database/               ← scripts SQL
├── php/                    ← versión PHP (Fase 1)
└── docs/                   ← documentación
```

---

# PARTE A — Lubuntu (máquina virtual)

## A1. Instalar Java 21, Maven y Git

**Carpeta:** cualquiera
```bash
sudo apt update
```
```bash
sudo apt install -y openjdk-21-jdk maven git
```
**Verificar:**
```bash
java -version
```
```bash
mvn -version
```
```bash
git --version
```
Resultado esperado: `openjdk version "21..."` (o 17, o 25), `Apache Maven 3.x` y `git version 2.x`.

Si `java -version` muestra otra versión y quiere usar la 21: `sudo update-alternatives --config java`.

## A2. MySQL / MariaDB

Si ya lo instaló en la Fase 1 (tarea LAMP), solo verifique que esté activo:
```bash
sudo systemctl status mysql
```
Debe decir `active (running)`. Si no está instalado:
```bash
sudo apt install -y mysql-server
```
```bash
sudo systemctl enable --now mysql
```

## A3. Obtener el proyecto

**Opción 1, desde el ZIP:** descomprímalo en su carpeta personal y entre a ella:
```bash
cd ~/carritogt
```

**Opción 2, desde su repositorio de GitHub** (cuando ya lo haya publicado):
```bash
cd ~
```
```bash
git clone https://github.com/SU_USUARIO/carritogt.git
```
```bash
cd ~/carritogt
```

## A4. Copiar las imágenes y la versión PHP

**Carpeta:** `~/carritogt`

Imágenes de los productos, tomadas del proyecto PHP de la Fase 1:
```bash
cp /var/www/html/carrito-compra-basico/public/images/product/*.jpg frontend/images/product/
```
Si no tiene el proyecto PHP, clónelo primero con `git clone https://github.com/mvelasquezo/carrito-compra-basico.git ~/carrito-compra-basico` y copie desde `~/carrito-compra-basico/public/images/product/`.

Copia de **su** versión PHP funcionando (entregable: "repositorio con la versión PHP y la versión Spring Boot"):
```bash
rsync -a --exclude vendor --exclude .git /var/www/html/carrito-compra-basico/ php/
```

## A5. Crear la base de datos

**Carpeta:** `~/carritogt`

**1.** Base `cc` con las 2 tablas y los 4 productos. Si ya existe de la Fase 1, el script no la duplica:
```bash
sudo mysql < database/01_cc.sql
```
Si `root` tiene contraseña (en la tarea del carrito se configuró `USAC123`), use en su lugar:
```bash
mysql -u root -p < database/01_cc.sql
```

**2.** Usuario exclusivo para la API. **Primero** edite el archivo y cambie `CAMBIE_ESTA_CLAVE` por una contraseña propia:
```bash
nano database/02_usuario_aplicacion.sql
```
```bash
mysql -u root -p < database/02_usuario_aplicacion.sql
```

**3.** Verificación. Pedirá la contraseña del usuario `carritogt`:
```bash
mysql -u carritogt -p < database/03_verificacion.sql
```
Resultado esperado: las tablas `producto` y `shopping_cart`, los 4 productos y la clave foránea `cc_profk_1`.

## A6. Configurar la conexión (sin subir contraseñas a Git)

**Carpeta:** `~/carritogt/backend`
```bash
cp config/application-local.properties.example config/application-local.properties
```
```bash
nano config/application-local.properties
```
Cambie `ESCRIBA_AQUI_SU_CONTRASENA` por la contraseña del paso A5. Guarde con **Ctrl + O**, **Enter** y **Ctrl + X**.

Alternativa con variables de entorno (sin archivo):
```bash
export DB_USER=carritogt
```
```bash
export DB_PASSWORD='su_contraseña'
```

## A7. Ejecutar las pruebas automatizadas

**Carpeta:** `~/carritogt/backend`
```bash
mvn test
```
La primera vez descarga dependencias (varios minutos). Resultado esperado:
```
Tests run: 38, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
Las pruebas usan H2 en memoria, así que **no** modifican su base MySQL.

## A8. Iniciar el backend (con MySQL)

**Carpeta:** `~/carritogt/backend`
```bash
mvn spring-boot:run
```
Resultado esperado al final:
```
The following 1 profile is active: "mysql"
Tomcat started on port 8080 (http) with context path '/'
Started CarritoGtApplication in X seconds
```
Deje esta terminal abierta. Para detener el backend: **Ctrl + C**.

**Demostración sin MySQL** (base H2 en memoria, se borra al detener):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

## A9. Probar la API

En **otra** terminal (carpeta: cualquiera):
```bash
curl -i http://localhost:8080/api/productos
```
Debe responder `HTTP/1.1 200` y el JSON de los 4 productos. Más ejemplos en [04_API.md](04_API.md).

## A10. Usar la aplicación en el navegador

Abra Firefox en:
```
http://localhost:8080
```
- Catálogo con los 4 productos e imágenes.
- **Agregar**: aparece "agregado al carrito" y sube el contador.
- **Carrito**: tabla con cantidades, subtotales, total, **Eliminar** y **Vaciar carrito**.

Para comprobar que los datos se guardaron en MySQL:
```bash
mysql -u carritogt -p -e "SELECT * FROM cc.shopping_cart;"
```

## A11. (Opcional) Servir el frontend con Apache y demostrar CORS

Esto demuestra un frontend y una API en orígenes distintos (RNF‑07).

1. En `frontend/js/config.js`, cambie la línea a: `export const API_BASE = 'http://localhost:8080';`
2. Copie el frontend a Apache:
   ```bash
   sudo mkdir -p /var/www/carritogt-front
   ```
   ```bash
   sudo cp -r ~/carritogt/frontend/* /var/www/carritogt-front/
   ```
3. Cree un VirtualHost `carrito-compra.localhost` con `DocumentRoot /var/www/carritogt-front`, igual que en la tarea de VirtualHost, y reinicie Apache.
4. Abra `http://carrito-compra.localhost`. El backend ya permite ese origen (`app.cors.allowed-origins`).

## A12. Generar el JAR ejecutable (opcional)

**Carpeta:** `~/carritogt/backend`
```bash
mvn clean package
```
```bash
java -jar target/carritogt-api-1.0.0.jar
```
Si ejecuta el JAR desde otra carpeta, defina `DB_PASSWORD` como variable de entorno, porque el archivo `config/application-local.properties` se busca en la carpeta actual.

---

# PARTE B — Windows (PowerShell)

## B1. Instalar herramientas

Abra **PowerShell** y ejecute:
```powershell
winget install -e --id EclipseAdoptium.Temurin.21.JDK
winget install -e --id Git.Git
winget install -e --id Oracle.MySQL
```
Cierre y vuelva a abrir PowerShell, y verifique:
```powershell
java -version
git --version
```
No hace falta instalar Maven: el proyecto incluye **Maven Wrapper** (`mvnw.cmd`), que lo descarga solo.

Si MySQL no queda configurado con winget, use el instalador oficial *MySQL Installer for Windows* (dev.mysql.com/downloads/installer) y anote la contraseña de `root`.

## B2. Base de datos

**Carpeta:** raíz del proyecto (`carritogt`). Antes, cambie `CAMBIE_ESTA_CLAVE` en `database\02_usuario_aplicacion.sql`.
```powershell
cd $HOME\Downloads\carritogt
Get-Content database\01_cc.sql | mysql -u root -p
Get-Content database\02_usuario_aplicacion.sql | mysql -u root -p
```
Si `mysql` no se reconoce, agregue su carpeta al PATH de la sesión (ajuste la versión):
`$env:PATH += ";C:\Program Files\MySQL\MySQL Server 8.4\bin"`

## B3. Configuración, pruebas y ejecución

**Carpeta:** `carritogt\backend`
```powershell
cd backend
Copy-Item config\application-local.properties.example config\application-local.properties
notepad config\application-local.properties
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```
Para la demostración sin MySQL:
```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"
```
Abra `http://localhost:8080` en el navegador. Las imágenes se copian igual que en A4:
```powershell
Copy-Item ..\..\carrito-compra-basico\public\images\product\*.jpg ..\frontend\images\product\
```

---

# Solución de errores frecuentes

| Síntoma | Causa | Solución |
|---|---|---|
| `Communications link failure` o `Connection refused` al iniciar | MySQL apagado | `sudo systemctl start mysql` |
| `Access denied for user 'carritogt'@'localhost'` | Contraseña incorrecta o usuario no creado | Repita A5 paso 2 y revise `config/application-local.properties` |
| `Unknown database 'cc'` | No se ejecutó `01_cc.sql` | Ejecute A5 paso 1 |
| `Table 'cc.producto' doesn't exist` | La base existe pero está vacía | Ejecute `01_cc.sql` (no borra datos existentes) |
| `Unknown column 'id_producto'` | Falta la estrategia de nombres | Revise que `application.properties` tenga `PhysicalNamingStrategyStandardImpl` |
| `Port 8080 was already in use` | Otro programa usa el puerto | Detenga el otro proceso o ejecute `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081` |
| `release version 17 not supported` | JDK menor que 17 | Instale `openjdk-21-jdk` |
| `mvn: orden no encontrada` | Maven no instalado | `sudo apt install -y maven` o use `./mvnw` |
| `./mvnw: Permiso denegado` | Falta permiso de ejecución | `chmod +x mvnw` |
| Las imágenes salen como "Imagen no disponible" | No se copiaron los `.jpg` | Paso A4 |
| La página dice "No se pudo conectar con la API" | El backend no está iniciado o `API_BASE` es incorrecto | Inicie el backend; revise `frontend/js/config.js` |
| En el navegador: `blocked by CORS policy` | El origen del frontend no está permitido | Agregue el origen en `app.cors.allowed-origins` (o en la variable `CORS_ORIGINS`) |
| El carrito aparece vacío en otro navegador | Cada navegador tiene su propio `X-Usuario` | Es el comportamiento esperado (como la sesión de PHP) |
| Cambios en `frontend/` no se ven | El frontend se copia al compilar | Detenga e inicie de nuevo `mvn spring-boot:run` y recargue con Ctrl + Shift + R |
