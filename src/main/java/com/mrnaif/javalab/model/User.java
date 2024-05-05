package com.mrnaif.javalab.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "email")
  private String email;

  @Column(name = "hashed_password")
  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;

  @OneToMany(
      mappedBy = "user",
      cascade = {CascadeType.REMOVE, CascadeType.MERGE})
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<Store> stores;

  @OneToMany(
      mappedBy = "user",
      cascade = {CascadeType.REMOVE, CascadeType.MERGE})
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<Product> products;

  @CreatedDate
  @Column(name = "created", nullable = false, updatable = false)
  private Instant created;
}