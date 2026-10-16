package com.carritogt.entity;

import com.carritogt.exception.SolicitudInvalidaException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Pruebas unitarias del objeto de valor CodigoUsuario. */
class CodigoUsuarioTest {

    @ParameterizedTest
    @ValueSource(strings = {"3f2b8c1e-9d4a-4c7b-8e2f-1a2b3c4d5e6f", "usuario1", "ABC-123-xyz"})
    void aceptaCodigosValidos(String valor) {
        assertThat(CodigoUsuario.de(valor).valor()).isEqualTo(valor);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"corto", "<script>alert(1)</script>", "con espacios 123", "x' OR '1'='1"})
    void rechazaCodigosInvalidos(String valor) {
        assertThatThrownBy(() -> CodigoUsuario.de(valor))
                .isInstanceOf(SolicitudInvalidaException.class);
    }
}
