/**
 * Direccion base de la API REST.
 *
 * - Vacio (''): el frontend lo sirve el mismo Spring Boot (http://localhost:8080).
 * - 'http://localhost:8080': el frontend lo sirve otro servidor (por ejemplo Apache en
 *   http://carrito-compra.localhost). En ese caso el origen debe estar permitido por CORS
 *   en la propiedad app.cors.allowed-origins del backend.
 */
export const API_BASE = '';
