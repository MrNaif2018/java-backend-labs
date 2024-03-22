package com.mrnaif.javalab.dto.product;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateProduct extends BaseProduct {

    private Long userId;

}
