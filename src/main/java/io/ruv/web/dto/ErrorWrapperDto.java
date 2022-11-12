package io.ruv.web.dto;

import lombok.Data;

@Data
public class ErrorWrapperDto {

    private final String errorCode;
    private final String message;
}
