package com.naveen.groupaccessservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private String userName;
    private String password;
    private Boolean active;
    @JsonIgnore
    private String roles;
}
