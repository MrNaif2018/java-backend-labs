package com.mrnaif.javalab.payload.user;

import java.time.Instant;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DisplayUser extends BaseUser {
    private Long id;

    private Instant created;
}
