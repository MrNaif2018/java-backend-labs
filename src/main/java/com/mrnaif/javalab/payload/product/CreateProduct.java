package com.mrnaif.javalab.payload.product;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateProduct extends BaseProduct {

    private Long userId;

    private Set<Long> stores = new HashSet<>();

}
