# 8. Manual de usuario

## 8.1 Iniciar el entorno (resumen)

1. Encienda la máquina virtual Lubuntu.
2. Compruebe que MySQL esté activo:
   ```bash
   sudo systemctl status mysql
   ```
3. Inicie la API:
   ```bash
   cd ~/carritogt/backend
   ```
   ```bash
   mvn spring-boot:run
   ```
4. Abra Firefox en `http://localhost:8080`.

## 8.2 Catálogo (página principal)

- Se muestran los productos con **imagen, nombre, código y precio** en quetzales.
- Escriba la **cantidad** (1 a 99) y pulse **Agregar**.
- Aparece el mensaje verde *"… agregado al carrito"* y el número junto a **Carrito** aumenta.
- Si agrega otra vez el mismo producto, la cantidad se **suma** en el carrito.

## 8.3 Carrito de compra

Pulse **Carrito** (arriba a la derecha).

| Acción | Cómo |
|---|---|
| Ver productos agregados | Tabla con imagen, nombre, código, cantidad, precio y subtotal |
| Cambiar cantidad | Escriba el número nuevo en la columna *Cantidad* y pulse Enter o salga del campo |
| Eliminar un producto | Botón **Eliminar** de la fila |
| Vaciar todo | Botón **Vaciar carrito** y confirme |
| Volver al catálogo | **← Seguir comprando** |

El **total** y la cantidad de artículos se recalculan después de cada cambio.

## 8.4 Mensajes

| Mensaje | Significado |
|---|---|
| Verde: "agregado", "eliminado", "Cantidad actualizada" | La operación se guardó |
| Rojo: "La cantidad de un producto debe estar entre 1 y 99" | Superó el límite de unidades de un producto |
| Rojo: "No se pudo conectar con la API" | El backend no está iniciado |
| "Imagen no disponible" | La imagen del producto no está en `frontend/images/product/` |

## 8.5 Observaciones

- El carrito se asocia a **este navegador**: otro navegador o una ventana privada tiene un carrito distinto, igual que la sesión de la versión PHP.
- No hay inicio de sesión, pagos ni pedidos: están fuera del alcance del proyecto (sección 14.2 del enunciado).
