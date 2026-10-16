/** Utilidades de interfaz compartidas por las dos paginas. */

const formatoMoneda = new Intl.NumberFormat('es-GT', {
    style: 'currency',
    currency: 'GTQ',
    minimumFractionDigits: 2
});

export function formatearMoneda(valor) {
    return formatoMoneda.format(Number(valor || 0));
}

export const IMAGEN_RESPALDO = 'images/sin-imagen.svg';

/** Crea una etiqueta <img> que muestra una imagen de respaldo si la URL no existe (RF-07). */
export function crearImagen(ruta, textoAlternativo, clase) {
    const img = document.createElement('img');
    img.src = ruta || IMAGEN_RESPALDO;
    img.alt = textoAlternativo || 'Producto';
    img.loading = 'lazy';
    if (clase) {
        img.className = clase;
    }
    img.addEventListener('error', () => {
        if (!img.src.endsWith(IMAGEN_RESPALDO)) {
            img.src = IMAGEN_RESPALDO;
        }
    }, { once: true });
    return img;
}

/** Muestra un mensaje temporal de confirmacion o error (RF-08). */
export function mostrarMensaje(texto, tipo = 'exito') {
    const contenedor = document.getElementById('mensajes');
    const aviso = document.createElement('div');
    aviso.className = `mensaje mensaje--${tipo}`;
    aviso.textContent = texto;
    contenedor.appendChild(aviso);
    setTimeout(() => aviso.remove(), tipo === 'error' ? 5000 : 2500);
}

/** Convierte un ApiError en un texto legible para el usuario. */
export function textoDeError(error) {
    if (error && error.detalles && error.detalles.length) {
        return `${error.message}: ${error.detalles.join('; ')}`;
    }
    return (error && error.message) || 'Ocurrió un error inesperado';
}

/** Actualiza el globo con la cantidad de articulos del carrito. */
export function actualizarContador(totalArticulos) {
    const globo = document.getElementById('contador-carrito');
    if (globo) {
        globo.textContent = String(totalArticulos || 0);
    }
}
