package com.intelli.africa.response;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private int statusCode;
    private String message;
    private int results;
    private Object data;

}

