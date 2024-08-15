package com.intelli.africa.service.Impl;


import com.intelli.africa.response.ApiResponse;
import com.intelli.africa.service.KeycloakUserService;
import com.intelli.africa.service.RoleService;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    private final KeycloakUserService keycloakUserService;

    @Override
    public ResponseEntity<ApiResponse> assignRole(String email, String roleName) {
        ApiResponse apiResponse = new ApiResponse();

        List<UserRepresentation> existingUsers = getUsersByEmail(email);

        if (!existingUsers.isEmpty()) {
            // Use the first user in the list
            String userId = existingUsers.get(0).getId();
            UserResource userResource = keycloakUserService.getUserResource(userId);
            RolesResource rolesResource = getRolesResource();

            RoleRepresentation representation;
            try {
                representation = rolesResource.get(roleName).toRepresentation();
            } catch (Exception e) {
                apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                apiResponse.setMessage("Error : " + e.getMessage());
                apiResponse.setResults(0);
                apiResponse.setData(null);

                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            }

            userResource.roles().realmLevel().add(Collections.singletonList(representation));

            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setMessage("Role assigned successfully to user: " + email);
            apiResponse.setResults(1);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);

        } else {
            apiResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            apiResponse.setMessage("Account with provided email not found: " + email);
            apiResponse.setResults(0);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> unassignRole(String email, String roleName) {
        ApiResponse apiResponse = new ApiResponse();

        List<UserRepresentation> existingUsers = getUsersByEmail(email);

        if (!existingUsers.isEmpty()) {
            // Use the first user in the list
            String userId = existingUsers.get(0).getId();
            UserResource userResource = keycloakUserService.getUserResource(userId);
            RolesResource rolesResource = getRolesResource();

            RoleRepresentation representation;
            try {
                representation = rolesResource.get(roleName).toRepresentation();
            } catch (Exception e) {
                apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                apiResponse.setMessage("Error : " + e.getMessage());
                apiResponse.setResults(0);
                apiResponse.setData(null);

                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            }

            userResource.roles().realmLevel().remove(Collections.singletonList(representation));

            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setMessage("Role Unassigned successfully from user: " + email);
            apiResponse.setResults(1);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);

        } else {
            apiResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            apiResponse.setMessage("Account with provided email not found: " + email);
            apiResponse.setResults(0);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }
    }


    private RolesResource getRolesResource() {
        return keycloak.realm(realm).roles();
    }


    public List<UserRepresentation> getUsersByEmail(String email) {
        UsersResource usersResource = getUsersResource();

        return usersResource.searchByEmail(email, true);

    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }


}
