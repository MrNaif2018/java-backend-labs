package com.mrnaif.javalab.payload.product;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DisplayProduct extends BaseProduct {

    private Long id;

    private Set<ProductStoreInfo> stores = new HashSet<>();

    private Instant created;

}
