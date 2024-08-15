package com.intelli.africa.service.Impl;

import com.intelli.africa.response.ApiResponse;
import com.intelli.africa.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service



public class GroupServiceImpl implements GroupService {
    @Value("${keycloak.realm}")
    private String realm;

    private final KeycloakUserServiceImpl userService;

    private final Keycloak keycloak;

    @Override
    public ResponseEntity<ApiResponse> assignGroup(String email, String groupId) {
        ApiResponse apiResponse = new ApiResponse();

        try {
            // Validate inputs
            if (email == null || email.isEmpty()) {
                apiResponse.setData(null);
                apiResponse.setResults(0);
                apiResponse.setMessage("User email cannot be null or empty ");
                apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);

            }

            if (groupId == null || groupId.isEmpty()) {
                apiResponse.setData(null);
                apiResponse.setResults(0);
                apiResponse.setMessage("Group ID cannot be null or empty.");
                apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            }

            // Attempt to retrieve the user
            UserResource userResource = userService.getUserResourceByEmail(email);

            if (userResource == null) {
                apiResponse.setData(null);
                apiResponse.setResults(0);
                apiResponse.setMessage("User not found with email: " + email);
                apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            }

            // Attempt to add the user to the group
            userResource.joinGroup(groupId);

            // Set success response
            apiResponse.setData(null);
            apiResponse.setResults(1);
            apiResponse.setMessage("User joined group successfully");
            apiResponse.setStatusCode(HttpStatus.OK.value());
        } catch (Exception ex) {
            // Handle case where user is not found
            apiResponse.setData(null);
            apiResponse.setResults(0);
            apiResponse.setMessage(ex.getMessage());
            apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }


    @Override
    public ResponseEntity<ApiResponse> deleteFromGroup(String email, String groupId) {
        ApiResponse apiResponse = new ApiResponse();

        try {
            // Validate inputs
            if (email == null || email.isEmpty()) {
                apiResponse.setData(null);
                apiResponse.setResults(0);
                apiResponse.setMessage("User email cannot be null or empty ");
                apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);

            }

            if (groupId == null || groupId.isEmpty()) {
                apiResponse.setData(null);
                apiResponse.setResults(0);
                apiResponse.setMessage("Group ID cannot be null or empty.");
                apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            }

            // Attempt to retrieve the user
            UserResource userResource = userService.getUserResourceByEmail(email);

            if (userResource == null) {
                apiResponse.setData(null);
                apiResponse.setResults(0);
                apiResponse.setMessage("User not found with email: " + email);
                apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            }

            // Attempt to add the user to the group
            userResource.leaveGroup(groupId);

            // Set success response
            apiResponse.setData(null);
            apiResponse.setResults(1);
            apiResponse.setMessage("User removed from group successfully");
            apiResponse.setStatusCode(HttpStatus.OK.value());
        } catch (Exception ex) {
            // Handle case where user is not found
            apiResponse.setData(null);
            apiResponse.setResults(0);
            apiResponse.setMessage(ex.getMessage());
            apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }
}
