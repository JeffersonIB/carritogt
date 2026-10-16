package com.carritogt;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integracion: levantan toda la aplicacion (Controller + Service + Repository + Entity)
 * con la base H2 en memoria, que contiene las mismas 2 tablas y 4 productos del script original.
 * Cada prueba usa un X-Usuario distinto para que los carritos no se mezclen.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class CarritoApiIntegrationTest {

    private static final String USUARIO = "X-Usuario";

    @Autowired
    private MockMvc mvc;

    private static String nuevoUsuario() {
        return UUID.randomUUID().toString();
    }

    private static String item(int idProducto, int cantidad) {
        return "{\"idProducto\":" + idProducto + ",\"cantidad\":" + cantidad + "}";
    }

    // ---------------------------------------------------------------- productos

    @Test
    void listarProductosDevuelveLosCuatroDelScript() throws Exception {
        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].nombre").value("FinePix Pro2 3D Camera"))
                .andExpect(jsonPath("$[0].precio").value(671.21))
                .andExpect(jsonPath("$[0].imagen").value("images/product/camera.jpg"));
    }

    @Test
    void obtenerProductoPorId() throws Exception {
        mvc.perform(get("/api/productos/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("LPN45"));
    }

    @Test
    void productoInexistenteDevuelve404ConMensaje() throws Exception {
        mvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value(containsString("999")));
    }

    @Test
    void idDeProductoConFormatoInvalidoDevuelve400() throws Exception {
        mvc.perform(get("/api/productos/abc"))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------------- flujo completo

    @Test
    void flujoCompletoDelCarrito() throws Exception {
        String usuario = nuevoUsuario();

        // Carrito vacio al inicio
        mvc.perform(get("/api/carrito").header(USUARIO, usuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total").value(0.0));

        // RF-03: agregar 2 camaras
        MvcResult resultado = mvc.perform(post("/api/carrito/items").header(USUARIO, usuario)
                        .contentType(MediaType.APPLICATION_JSON).content(item(1, 2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].cantidad").value(2))
                .andExpect(jsonPath("$.total").value(1342.42))
                .andReturn();
        Integer idCamara = JsonPath.read(resultado.getResponse().getContentAsString(), "$.items[0].idItem");

        // Agregar otra camara: se acumula en el mismo articulo
        mvc.perform(post("/api/carrito/items").header(USUARIO, usuario)
                        .contentType(MediaType.APPLICATION_JSON).content(item(1, 1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].cantidad").value(3));

        // Agregar un disco duro
        mvc.perform(post("/api/carrito/items").header(USUARIO, usuario)
                        .contentType(MediaType.APPLICATION_JSON).content(item(2, 1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(2)));

        // RF-04: ver el carrito con total calculado por el servidor
        mvc.perform(get("/api/carrito").header(USUARIO, usuario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalArticulos").value(4))
                .andExpect(jsonPath("$.total").value(2228.73));

        // Mejora: cambiar la cantidad de camaras a 1
        mvc.perform(put("/api/carrito/items/" + idCamara).header(USUARIO, usuario)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"cantidad\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(886.31));

        // RF-05: eliminar la camara
        mvc.perform(delete("/api/carrito/items/" + idCamara).header(USUARIO, usuario))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/carrito").header(USUARIO, usuario))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].codigo").value("USB02"));

        // Vaciar el carrito
        mvc.perform(delete("/api/carrito").header(USUARIO, usuario))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/carrito").header(USUARIO, usuario))
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.totalArticulos").value(0));
    }

    // ---------------------------------------------------------------- validaciones y errores (RF-08)

    @Test
    void cantidadCeroDevuelve400ConDetalle() throws Exception {
        mvc.perform(post("/api/carrito/items").header(USUARIO, nuevoUsuario())
                        .contentType(MediaType.APPLICATION_JSON).content(item(1, 0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles", hasItem("cantidad: debe ser al menos 1")));
    }

    @Test
    void sinEncabezadoUsuarioDevuelve400() throws Exception {
        mvc.perform(post("/api/carrito/items")
                        .contentType(MediaType.APPLICATION_JSON).content(item(1, 1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(containsString("X-Usuario")));
    }

    @Test
    void encabezadoUsuarioConFormatoInvalidoDevuelve400() throws Exception {
        mvc.perform(get("/api/carrito").header(USUARIO, "<script>"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregarProductoInexistenteDevuelve404() throws Exception {
        mvc.perform(post("/api/carrito/items").header(USUARIO, nuevoUsuario())
                        .contentType(MediaType.APPLICATION_JSON).content(item(999, 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void jsonMalFormadoDevuelve400() throws Exception {
        mvc.perform(post("/api/carrito/items").header(USUARIO, nuevoUsuario())
                        .contentType(MediaType.APPLICATION_JSON).content("{idProducto: uno"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void superarNoventaYNueveUnidadesDevuelve422() throws Exception {
        String usuario = nuevoUsuario();
        mvc.perform(post("/api/carrito/items").header(USUARIO, usuario)
                        .contentType(MediaType.APPLICATION_JSON).content(item(3, 99)))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/carrito/items").header(USUARIO, usuario)
                        .contentType(MediaType.APPLICATION_JSON).content(item(3, 1)))
                .andExpect(status().isUnprocessableEntity());

        // La cantidad no cambio (la transaccion se revirtio)
        mvc.perform(get("/api/carrito").header(USUARIO, usuario))
                .andExpect(jsonPath("$.items[0].cantidad").value(99));
    }

    @Test
    void unUsuarioNoPuedeEliminarArticulosDeOtro() throws Exception {
        String duenio = nuevoUsuario();
        MvcResult resultado = mvc.perform(post("/api/carrito/items").header(USUARIO, duenio)
                        .contentType(MediaType.APPLICATION_JSON).content(item(2, 1)))
                .andExpect(status().isCreated())
                .andReturn();
        Integer idItem = JsonPath.read(resultado.getResponse().getContentAsString(), "$.items[0].idItem");

        mvc.perform(delete("/api/carrito/items/" + idItem).header(USUARIO, nuevoUsuario()))
                .andExpect(status().isNotFound());

        mvc.perform(get("/api/carrito").header(USUARIO, duenio))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    void metodoNoPermitidoDevuelve405() throws Exception {
        mvc.perform(put("/api/productos"))
                .andExpect(status().isMethodNotAllowed());
    }

    // ---------------------------------------------------------------- CORS (RNF-07)

    @Test
    void corsPermiteElOrigenConfigurado() throws Exception {
        mvc.perform(options("/api/carrito")
                        .header("Origin", "http://carrito-compra.localhost")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "X-Usuario"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://carrito-compra.localhost"));
    }

    @Test
    void corsRechazaOrigenesNoConfigurados() throws Exception {
        mvc.perform(options("/api/carrito")
                        .header("Origin", "http://sitio-desconocido.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
