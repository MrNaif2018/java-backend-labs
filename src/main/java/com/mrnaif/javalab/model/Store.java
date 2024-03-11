package com.mrnaif.javalab.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "stores")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "stores_products", joinColumns = { @JoinColumn(name = "store_id") }, inverseJoinColumns = {
            @JoinColumn(name = "product_id") })
    private Set<Product> products = new HashSet<>();

    @CreatedDate
    @Column(name = "created", nullable = false, updatable = false)
    private Instant created;

    public void addProduct(Product product) {
        products.add(product);
        product.getStores().add(this);
    }

    public void removeProduct(Long productId) {
        Product product = this.products.stream().filter(t -> t.getId() == productId).findFirst().orElse(null);
        if (product != null) {
            this.products.remove(product);
            product.getStores().remove(this);
        }
    }

}
