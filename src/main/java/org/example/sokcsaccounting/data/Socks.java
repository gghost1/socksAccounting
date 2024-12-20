package org.example.sokcsaccounting.data;

import lombok.Getter;
import org.example.sokcsaccounting.exception.IllegalOperationException;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Entity
@Table
public class Socks {

    @Id
    private UUID id;
    @Column(nullable = false, updatable = false)
    private String color;
    @Column(nullable = false, updatable = false)
    private Double cottonPart;
    @Column(nullable = false)
    private Integer quantity;

    public static Socks of(String color, Double cottonPart, Integer quantity) {
        checkParameters(color, cottonPart, quantity);
        return new Socks(color, cottonPart, quantity);
    }

    private Socks(String color, Double cottonPart, Integer quantity) {
        this.color = color;
        this.cottonPart = cottonPart;
        this.quantity = quantity;
    }

    public Socks() {
    }

    @PrePersist
    private void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    public Socks increaseQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity += quantity;
        return this;
    }

    public Socks decreaseQuantity(int quantity) {
        if (quantity > this.quantity) {
            throw new IllegalOperationException("Not enough socks");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity -= quantity;
        return this;
    }

    public Socks update(String color, Double cottonPart, Integer quantity) {
        checkParameters(color, cottonPart, quantity);
        this.color = color;
        this.cottonPart = cottonPart;
        this.quantity = quantity;
        return this;
    }

    private static void checkParameters(String color, Double cottonPart, Integer quantity) {
        if (color == null || color.isEmpty() || cottonPart == null || quantity == null) {
            throw new IllegalArgumentException("Color, cotton part and quantity cannot be null or empty");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (cottonPart < 0 || cottonPart > 100) {
            throw new IllegalArgumentException("Cotton part must be between 0 and 100");
        }
    }
}
