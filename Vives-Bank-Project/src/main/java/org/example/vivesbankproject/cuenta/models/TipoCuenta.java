package org.example.vivesbankproject.cuenta.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "tipoCuenta")
@NoArgsConstructor
@AllArgsConstructor
public class TipoCuenta {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre del tipo de cuenta no puede estar vacío")
    private String nombre;

    @Column(nullable = false)
    @Digits(integer = 3, fraction = 2, message = "El interés debe ser un número válido")
    @PositiveOrZero(message = "El interés no puede ser negativo")
    private BigDecimal interes;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}