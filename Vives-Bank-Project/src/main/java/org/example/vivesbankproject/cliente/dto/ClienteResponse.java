package org.example.vivesbankproject.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cliente.models.Direccion;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse implements Serializable {
    private String guid;
    private String dni;
    private String nombre;
    private String apellidos;
    private String calle;
    private String numero;
    private String codigoPostal;
    private String piso;
    private String letra;
    private String email;
    private String telefono;
    private String fotoPerfil;
    private String fotoDni;
    private String userId;
    private String createdAt;
    private String updatedAt;
    private Boolean isDeleted;
}