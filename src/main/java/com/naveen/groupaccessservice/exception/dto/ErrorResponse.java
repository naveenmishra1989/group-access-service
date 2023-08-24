package com.naveen.groupaccessservice.exception.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ErrorResponse {
    private Integer errorCode;
    private String errorMessage;

}
