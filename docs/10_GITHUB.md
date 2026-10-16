# 10. Publicación en GitHub

## 10.1 Importante: commits incrementales

El enunciado (10.1) indica que *"un repositorio creado al final con uno o dos commits generales no demuestra el proceso de aprendizaje y será motivo de revisión adicional"*. Por eso **no** suba todo en un solo commit: vaya agregando y probando por partes, y registre cada paso en la bitácora.

## 10.2 Preparar el repositorio local

**Carpeta:** raíz del proyecto (`~/carritogt`)

**1. Identidad de Git** (solo la primera vez):
```bash
git config --global user.name "SU_USUARIO_GITHUB"
```
```bash
git config --global user.email "su_correo@ejemplo.com"
```

**2. Iniciar el repositorio:**
```bash
git init
```
```bash
git branch -M main
```

**3. Antes de cada commit, verifique que no se suban secretos.** No deben aparecer `application-local.properties` ni la carpeta `target/`:
```bash
git status
```

## 10.3 Plan sugerido de commits

Haga cada commit **después** de comprobar que esa parte funciona:

| # | Qué agregar | Comandos |
|---|---|---|
| 1 | Versión PHP (Fase 1) | `git add php .gitignore` → `git commit -m "Fase 1: version PHP del carrito ejecutandose en Lubuntu"` |
| 2 | Scripts de base de datos | `git add database` → `git commit -m "Scripts de la base cc y usuario de la aplicacion"` |
| 3 | Proyecto Spring Boot + Entity | `git add backend/pom.xml backend/src/main/java/com/carritogt/CarritoGtApplication.java backend/src/main/java/com/carritogt/entity` → `git commit -m "Spring Boot: entidades Producto e ItemCarrito"` |
| 4 | Repository | `git add backend/src/main/java/com/carritogt/repository` → `git commit -m "Capa Repository con Spring Data JPA"` |
| 5 | Service, DTO, mapper y excepciones | `git add backend/src/main/java/com/carritogt/{service,dto,mapper,exception}` → `git commit -m "Capa Service, DTO y manejo de errores"` |
| 6 | Controller y configuración | `git add backend/src/main/java/com/carritogt/{controller,config} backend/src/main/resources backend/config` → `git commit -m "Endpoints REST y configuracion de MySQL y CORS"` |
| 7 | Pruebas | `git add backend/src/test` → `git commit -m "Pruebas unitarias y de integracion"` |
| 8 | Frontend | `git add frontend` → `git commit -m "Frontend con fetch que consume la API"` |
| 9 | Maven Wrapper | `git add backend/mvnw backend/mvnw.cmd backend/.mvn` → `git commit -m "Maven Wrapper"` |
| 10 | Documentación | `git add README.md docs` → `git commit -m "Documentacion tecnica, manual y bitacora"` |

Después agregue commits pequeños por cada corrección o mejora que haga, y actualice la bitácora.

## 10.4 Crear el repositorio remoto y publicar

1. En <https://github.com/new>, cree el repositorio `carritogt`. Debe ser **Public** y **vacío** (sin README, sin .gitignore y sin licencia).
2. Copie la URL que muestra GitHub, por ejemplo `https://github.com/SU_USUARIO/carritogt.git`.
3. **Carpeta:** `~/carritogt`. Sustituya `URL_DE_TU_REPOSITORIO` por esa URL real:
```bash
git remote add origin URL_DE_TU_REPOSITORIO
```
```bash
git push -u origin main
```
- **Username:** su usuario de GitHub
- **Password:** un **token** personal (https://github.com/settings/tokens, con la casilla `repo` marcada). GitHub ya no acepta la contraseña de la cuenta.

4. Verifique en una ventana de incógnito que se vean `backend`, `frontend`, `database`, `php`, `docs` y el README. **Ese enlace es el que se entrega.**

## 10.5 Lo que nunca debe subirse

| Archivo | Por qué | Protección |
|---|---|---|
| `backend/config/application-local.properties` | Contiene su contraseña de MySQL | Está en `.gitignore` |
| `backend/target/` | Archivos compilados, se regeneran | Está en `.gitignore` |
| `php/App/Config.php` con una contraseña real | Credenciales | Reemplace la contraseña por un texto de ejemplo antes del commit, o documente que es solo de la VM local |
| Tokens de GitHub (`ghp_...`) | Dan acceso a su cuenta | Nunca los escriba en archivos del proyecto |

Si por error subió una contraseña: cámbiela de inmediato en MySQL. Borrarla en un commit nuevo no la elimina del historial.
