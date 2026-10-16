import { api } from './api.js';
import { crearImagen, formatearMoneda, mostrarMensaje, textoDeError } from './ui.js';

/**
 * Pagina del carrito (reemplaza App/Views/ShoppingCart/index.html de Twig).
 * Todo cambio se envia a la API y la tabla se vuelve a dibujar con la respuesta del servidor,
 * de modo que los subtotales y el total siempre los calcula el backend.
 */

const estado = document.getElementById('estado-carrito');
const seccionVacia = document.getElementById('carrito-vacio');
const seccionItems = document.getElementById('carrito-con-items');
const filas = document.getElementById('filas-carrito');
const totalCarrito = document.getElementById('total-carrito');
const totalArticulos = document.getElementById('total-articulos');
const botonVaciar = document.getElementById('btn-vaciar');

function celda(etiqueta, contenido) {
    const td = document.createElement('td');
    td.dataset.label = etiqueta;
    if (contenido instanceof Node) {
        td.appendChild(contenido);
    } else {
        td.textContent = contenido;
    }
    return td;
}

function crearFila(item) {
    const tr = document.createElement('tr');

    const cantidad = document.createElement('input');
    cantidad.type = 'number';
    cantidad.min = '1';
    cantidad.max = '99';
    cantidad.value = String(item.cantidad);
    cantidad.className = 'campo-cantidad';
    cantidad.setAttribute('aria-label', `Cantidad de ${item.nombre}`);
    cantidad.addEventListener('change', async () => {
        try {
            // Mejora: modificar cantidades (PUT)
            dibujar(await api.actualizarCantidad(item.idItem, Number(cantidad.value)));
            mostrarMensaje('Cantidad actualizada');
        } catch (error) {
            cantidad.value = String(item.cantidad);
            mostrarMensaje(textoDeError(error), 'error');
        }
    });

    const eliminar = document.createElement('button');
    eliminar.type = 'button';
    eliminar.className = 'btn btn--enlace';
    eliminar.textContent = 'Eliminar';
    eliminar.addEventListener('click', async () => {
        eliminar.disabled = true;
        try {
            // RF-05: eliminar un articulo (DELETE)
            await api.eliminarItem(item.idItem);
            mostrarMensaje(`${item.nombre} eliminado del carrito`);
            await cargar();
        } catch (error) {
            eliminar.disabled = false;
            mostrarMensaje(textoDeError(error), 'error');
        }
    });

    tr.append(
        celda('Producto', crearImagen(item.imagen, item.nombre, 'miniatura')),
        celda('Nombre', item.nombre),
        celda('Código', item.codigo),
        celda('Cantidad', cantidad),
        celda('Precio', formatearMoneda(item.precio)),
        celda('Subtotal', formatearMoneda(item.subtotal)),
        celda('', eliminar)
    );
    return tr;
}

function dibujar(carrito) {
    estado.hidden = true;
    const vacio = carrito.items.length === 0;
    seccionVacia.hidden = !vacio;
    seccionItems.hidden = vacio;

    filas.replaceChildren(...carrito.items.map(crearFila));
    totalCarrito.textContent = formatearMoneda(carrito.total);
    totalArticulos.textContent = `${carrito.totalArticulos} artículo${carrito.totalArticulos === 1 ? '' : 's'}`;
}

async function cargar() {
    try {
        // RF-04: ver el contenido actual del carrito (GET)
        dibujar(await api.obtenerCarrito());
    } catch (error) {
        estado.hidden = false;
        estado.textContent = textoDeError(error);
        estado.classList.add('estado--error');
    }
}

botonVaciar.addEventListener('click', async () => {
    if (!window.confirm('¿Desea vaciar el carrito de compra?')) {
        return;
    }
    try {
        await api.vaciarCarrito();
        mostrarMensaje('Carrito vaciado');
        await cargar();
    } catch (error) {
        mostrarMensaje(textoDeError(error), 'error');
    }
});

cargar();
