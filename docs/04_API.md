# 4. Contrato de la API REST

- **URL base:** `http://localhost:8080`
- **Formato:** JSON (`Content-Type: application/json`)
- **Identificación del comprador:** encabezado `X-Usuario` (GUID de 8 a 128 caracteres: letras, números o guiones). Lo genera el frontend y lo guarda en `localStorage`. Es obligatorio en todos los endpoints `/api/carrito`.

## 4.1 Resumen de endpoints

| Método | Ruta | Descripción | Encabezado | Cuerpo | Respuesta OK | Errores |
|---|---|---|---|---|---|---|
| GET | `/api/productos` | Lista el catálogo (RF‑01, RF‑02) | — | — | 200 `ProductoResponse[]` | — |
| GET | `/api/productos/{id}` | Detalle de un producto | — | — | 200 `ProductoResponse` | 400 id no numérico, 404 no existe |
| GET | `/api/carrito` | Contenido del carrito (RF‑04) | `X-Usuario` | — | 200 `CarritoResponse` | 400 encabezado ausente/inválido |
| POST | `/api/carrito/items` | Agrega un producto; si ya está, suma la cantidad (RF‑03) | `X-Usuario` | `{ "idProducto": 1, "cantidad": 2 }` | **201** `CarritoResponse` | 400 validación, 404 producto, 422 supera 99 |
| PUT | `/api/carrito/items/{idItem}` | Cambia la cantidad (*mejora*) | `X-Usuario` | `{ "cantidad": 3 }` | 200 `CarritoResponse` | 400, 404 artículo ajeno o inexistente |
| DELETE | `/api/carrito/items/{idItem}` | Elimina un artículo (RF‑05) | `X-Usuario` | — | **204** sin cuerpo | 404 artículo ajeno o inexistente |
| DELETE | `/api/carrito` | Vacía el carrito | `X-Usuario` | — | **204** sin cuerpo | 400 encabezado |

## 4.2 Modelos JSON

**ProductoResponse**
```json
{ "id": 1, "nombre": "FinePix Pro2 3D Camera", "codigo": "3DcAM01",
  "imagen": "images/product/camera.jpg", "precio": 671.21 }
```

**CarritoResponse**
```json
{
  "items": [
    { "idItem": 7, "idProducto": 1, "nombre": "FinePix Pro2 3D Camera", "codigo": "3DcAM01",
      "imagen": "images/product/camera.jpg", "precio": 671.21, "cantidad": 2, "subtotal": 1342.42 }
  ],
  "totalArticulos": 2,
  "total": 1342.42
}
```
`subtotal`, `total` y `totalArticulos` siempre los calcula el servidor. La API **no** acepta precios enviados por el navegador.

**ErrorResponse** (todas las respuestas de error tienen este formato)
```json
{
  "timestamp": "2026-10-16T10:15:30.123-06:00",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Los datos enviados no son validos",
  "ruta": "/api/carrito/items",
  "detalles": ["cantidad: debe ser al menos 1"]
}
```

## 4.3 Códigos HTTP utilizados

| Código | Cuándo |
|---|---|
| 200 OK | Consulta o actualización correcta |
| 201 Created | Producto agregado al carrito |
| 204 No Content | Artículo eliminado o carrito vaciado |
| 400 Bad Request | JSON mal formado, campos faltantes, cantidad fuera de 1–99 en la solicitud, `X-Usuario` ausente o inválido, id no numérico |
| 404 Not Found | Producto inexistente, artículo que no existe o que pertenece a otro usuario, ruta inexistente |
| 405 Method Not Allowed | Método HTTP no soportado en la ruta |
| 422 Unprocessable Entity | Regla de negocio: la suma de unidades de un producto superaría 99 |
| 500 Internal Server Error | Error inesperado (se registra en el log y no se exponen detalles) |

## 4.4 Pruebas con curl (Lubuntu)

Defina primero un identificador de prueba:
```bash
U=$(cat /proc/sys/kernel/random/uuid)
```

```bash
curl -i http://localhost:8080/api/productos
```
```bash
curl -i -X POST http://localhost:8080/api/carrito/items -H "Content-Type: application/json" -H "X-Usuario: $U" -d '{"idProducto":1,"cantidad":2}'
```
```bash
curl -i http://localhost:8080/api/carrito -H "X-Usuario: $U"
```
```bash
curl -i -X PUT http://localhost:8080/api/carrito/items/ID_ITEM -H "Content-Type: application/json" -H "X-Usuario: $U" -d '{"cantidad":5}'
```
```bash
curl -i -X DELETE http://localhost:8080/api/carrito/items/ID_ITEM -H "X-Usuario: $U"
```
```bash
curl -i -X DELETE http://localhost:8080/api/carrito -H "X-Usuario: $U"
```
Reemplace `ID_ITEM` por el valor de `idItem` que devolvió el POST.

Errores esperados:
```bash
curl -i -X POST http://localhost:8080/api/carrito/items -H "Content-Type: application/json" -H "X-Usuario: $U" -d '{"idProducto":1,"cantidad":0}'
```
```bash
curl -i http://localhost:8080/api/productos/999
```

## 4.5 Pruebas con curl (Windows PowerShell)

En PowerShell use `curl.exe` (no el alias `curl`):
```powershell
$U = [guid]::NewGuid().ToString()
curl.exe -i http://localhost:8080/api/productos
curl.exe -i -X POST http://localhost:8080/api/carrito/items -H "Content-Type: application/json" -H "X-Usuario: $U" -d '{\"idProducto\":1,\"cantidad\":2}'
curl.exe -i http://localhost:8080/api/carrito -H "X-Usuario: $U"
```

## 4.6 Pruebas con Postman

1. Cree una petición nueva (Ctrl + T).
2. En **Headers** agregue `X-Usuario` con un valor como `3f2b8c1e-9d4a-4c7b-8e2f-1a2b3c4d5e6f`. Use el mismo valor en todas las peticiones del carrito.
3. Para POST y PUT: **Body → raw → JSON**.
4. Verifique el código de estado (201, 200 o 204) y el JSON de respuesta.
