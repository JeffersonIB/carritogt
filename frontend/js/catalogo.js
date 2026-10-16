import { api } from './api.js';
import { actualizarContador, crearImagen, formatearMoneda, mostrarMensaje, textoDeError } from './ui.js';

/**
 * Pagina del catalogo (reemplaza App/Views/Home/index.html de Twig).
 * Los productos ya no los incrusta PHP: se piden a GET /api/productos (RF-01, RF-06).
 */

const lista = document.getElementById('lista-productos');
const estado = document.getElementById('estado-catalogo');

function crearTarjeta(producto) {
    const tarjeta = document.createElement('article');
    tarjeta.className = 'tarjeta';

    const marco = document.createElement('div');
    marco.className = 'tarjeta__imagen';
    marco.appendChild(crearImagen(producto.imagen, producto.nombre));

    const nombre = document.createElement('h3');
    nombre.className = 'tarjeta__nombre';
    nombre.textContent = producto.nombre;

    const codigo = document.createElement('p');
    codigo.className = 'tarjeta__codigo';
    codigo.textContent = `Código: ${producto.codigo}`;

    const precio = document.createElement('p');
    precio.className = 'tarjeta__precio';
    precio.textContent = formatearMoneda(producto.precio);

    const formulario = document.createElement('form');
    formulario.className = 'tarjeta__acciones';

    const cantidad = document.createElement('input');
    cantidad.type = 'number';
    cantidad.min = '1';
    cantidad.max = '99';
    cantidad.value = '1';
    cantidad.required = true;
    cantidad.className = 'campo-cantidad';
    cantidad.setAttribute('aria-label', `Cantidad de ${producto.nombre}`);

    const boton = document.createElement('button');
    boton.type = 'submit';
    boton.className = 'btn btn--primario';
    boton.textContent = 'Agregar';

    formulario.append(cantidad, boton);
    formulario.addEventListener('submit', async (evento) => {
        evento.preventDefault();
        boton.disabled = true;
        try {
            // RF-03: agregar al carrito mediante la API
            const carrito = await api.agregarItem(producto.id, Number(cantidad.value));
            actualizarContador(carrito.totalArticulos);
            mostrarMensaje(`${producto.nombre} agregado al carrito`);
            cantidad.value = '1';
        } catch (error) {
            mostrarMensaje(textoDeError(error), 'error');
        } finally {
            boton.disabled = false;
        }
    });

    tarjeta.append(marco, nombre, codigo, precio, formulario);
    return tarjeta;
}

async function iniciar() {
    try {
        const productos = await api.listarProductos();
        lista.replaceChildren(...productos.map(crearTarjeta));
        estado.hidden = productos.length > 0;
        if (productos.length === 0) {
            estado.textContent = 'No hay productos disponibles.';
        }
    } catch (error) {
        estado.textContent = textoDeError(error);
        estado.classList.add('estado--error');
        return;
    }

    try {
        const carrito = await api.obtenerCarrito();
        actualizarContador(carrito.totalArticulos);
    } catch (error) {
        // El contador es informativo: si falla, el catalogo sigue funcionando.
    }
}

iniciar();
