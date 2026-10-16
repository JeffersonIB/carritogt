import { API_BASE } from './config.js';

/** Error de la API con el codigo HTTP y el mensaje que envia el backend. */
export class ApiError extends Error {
    constructor(status, mensaje, detalles = []) {
        super(mensaje);
        this.status = status;
        this.detalles = detalles;
    }
}

const CLAVE_USUARIO = 'carritogt.usuario';

/**
 * Identificador anonimo del comprador (reemplaza a $_SESSION['codUsr'] de PHP).
 * Se genera una sola vez y se guarda en el navegador.
 */
function obtenerUsuario() {
    let usuario = null;
    try {
        usuario = localStorage.getItem(CLAVE_USUARIO);
    } catch (e) {
        // localStorage no disponible (modo privado): se usa un codigo temporal
    }
    if (!usuario) {
        usuario = generarUuid();
        try {
            localStorage.setItem(CLAVE_USUARIO, usuario);
        } catch (e) {
            // sin almacenamiento: el carrito durara mientras la pagina este abierta
        }
    }
    return usuario;
}

function generarUuid() {
    if (window.crypto && typeof window.crypto.randomUUID === 'function') {
        return window.crypto.randomUUID();
    }
    // Alternativa para navegadores sin randomUUID (por ejemplo, sitios sin HTTPS)
    const bytes = new Uint8Array(16);
    window.crypto.getRandomValues(bytes);
    bytes[6] = (bytes[6] & 0x0f) | 0x40;
    bytes[8] = (bytes[8] & 0x3f) | 0x80;
    const hex = Array.from(bytes, b => b.toString(16).padStart(2, '0')).join('');
    return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}`;
}

const usuario = obtenerUsuario();

/**
 * Realiza una peticion HTTP a la API y devuelve el JSON de la respuesta.
 * Si el backend responde con error, lanza ApiError con su mensaje.
 */
async function solicitar(metodo, ruta, cuerpo) {
    const opciones = {
        method: metodo,
        headers: { 'X-Usuario': usuario, 'Accept': 'application/json' }
    };
    if (cuerpo !== undefined) {
        opciones.headers['Content-Type'] = 'application/json';
        opciones.body = JSON.stringify(cuerpo);
    }

    let respuesta;
    try {
        respuesta = await fetch(API_BASE + ruta, opciones);
    } catch (e) {
        throw new ApiError(0, 'No se pudo conectar con la API. ¿Está iniciado el backend?');
    }

    if (respuesta.status === 204) {
        return null;
    }

    let datos = null;
    try {
        datos = await respuesta.json();
    } catch (e) {
        datos = null;
    }

    if (!respuesta.ok) {
        const mensaje = datos && datos.mensaje ? datos.mensaje : `Error HTTP ${respuesta.status}`;
        throw new ApiError(respuesta.status, mensaje, (datos && datos.detalles) || []);
    }
    return datos;
}

/** Operaciones disponibles de la API REST de CarritoGT. */
export const api = {
    listarProductos: () => solicitar('GET', '/api/productos'),
    obtenerCarrito: () => solicitar('GET', '/api/carrito'),
    agregarItem: (idProducto, cantidad) => solicitar('POST', '/api/carrito/items', { idProducto, cantidad }),
    actualizarCantidad: (idItem, cantidad) => solicitar('PUT', `/api/carrito/items/${idItem}`, { cantidad }),
    eliminarItem: (idItem) => solicitar('DELETE', `/api/carrito/items/${idItem}`),
    vaciarCarrito: () => solicitar('DELETE', '/api/carrito')
};
