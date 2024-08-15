package com.intelli.africa.service;

import com.intelli.africa.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface RoleService {

    ResponseEntity<ApiResponse> assignRole(String userId, String roleName);
    ResponseEntity<ApiResponse> unassignRole(String userId, String roleName);
}
