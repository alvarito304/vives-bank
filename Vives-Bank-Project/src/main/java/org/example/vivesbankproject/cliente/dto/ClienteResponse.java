package org.example.vivesbankproject.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.models.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private UUID id;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String fotoPerfil;
    private String fotoDni;
    @Builder.Default
    private List<Cuenta> cuentas = new ArrayList<>();
    private User user;
}