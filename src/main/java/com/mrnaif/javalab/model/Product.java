package com.mrnaif.javalab.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "price")
  private Double price;

  @Column(name = "quantity")
  private Long quantity;

  @Column(name = "description")
  private String description;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user = new User();

  @ManyToMany(mappedBy = "products")
  @EqualsAndHashCode.Exclude
  private Set<Store> stores = new HashSet<>();

  @CreatedDate
  @Column(name = "created", nullable = false, updatable = false)
  private Instant created;

  public void setUserId(Long userId) {
    this.user.setId(userId);
  }

  public void addStore(Store store) {
    stores.add(store);
    store.getProducts().add(this);
  }

  public void removeStore(Long storeId) {
    Store store =
        this.stores.stream().filter(t -> t.getId().equals(storeId)).findFirst().orElse(null);
    if (store != null) {
      this.stores.remove(store);
      store.getProducts().remove(this);
    }
  }
}
