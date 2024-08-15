package com.intelli.africa.service;

import com.intelli.africa.model.UserRegistrationRecord;
import com.intelli.africa.response.ApiResponse;
import org.keycloak.admin.client.resource.UserResource;
import org.springframework.http.ResponseEntity;

public interface KeycloakUserService {

    ResponseEntity<ApiResponse> createUser(UserRegistrationRecord userRegistrationRecord);
    ResponseEntity<ApiResponse> getUserById(String userId);

    ResponseEntity<ApiResponse> getUsers();
    ResponseEntity<ApiResponse> deleteUserByEmail(String userId);
    ResponseEntity<ApiResponse> disableUserByEmail(String userId);
    ResponseEntity<ApiResponse> emailVerification(String email);
    UserResource getUserResource(String userId);
    UserResource getUserResourceByEmail(String email);
    ResponseEntity<ApiResponse> getUserRoles(String userId);
    ResponseEntity<ApiResponse> getUserGroups(String userId);

    ResponseEntity<ApiResponse> updatePassword(String email);
}
