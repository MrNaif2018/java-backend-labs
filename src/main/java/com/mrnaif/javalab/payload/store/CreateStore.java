package com.mrnaif.javalab.payload.store;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateStore extends BaseStore {

    private Long userId;

    private Set<Long> products = new HashSet<>();

}
