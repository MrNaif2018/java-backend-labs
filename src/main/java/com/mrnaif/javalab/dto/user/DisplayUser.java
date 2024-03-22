package com.mrnaif.javalab.dto.user;

import java.time.Instant;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DisplayUser extends BaseUser {
    private Long id;

    private Instant created;
}
