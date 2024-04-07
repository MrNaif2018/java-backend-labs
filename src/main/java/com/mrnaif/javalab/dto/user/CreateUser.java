package com.mrnaif.javalab.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateUser extends BaseUser {

  private String password;
}
