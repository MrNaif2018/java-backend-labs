package com.mrnaif.javalab.payload.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateUser extends BaseUser {

    private String password;

}
