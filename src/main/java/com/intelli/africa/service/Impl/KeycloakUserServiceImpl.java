package com.intelli.africa.service.Impl;


import com.intelli.africa.model.UserRegistrationRecord;
import com.intelli.africa.response.ApiResponse;
import com.intelli.africa.service.KeycloakUserService;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class KeycloakUserServiceImpl implements KeycloakUserService {

    @Value("${keycloak.realm}")
    private String realm;
    private Keycloak keycloak;

    public KeycloakUserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

  /*  @Override
    public ResponseEntity<ApiResponse> createUser(UserRegistrationRecord userRegistrationRecord) {

        ApiResponse apiResponse = new ApiResponse();

        UsersResource usersResource = getUsersResource();

        List<UserRepresentation> existingUsers = usersResource.searchByUsername(userRegistrationRecord.username(), true);
        if (!existingUsers.isEmpty()) {
            // User with this username already exists
            apiResponse.setMessage("Username already exists");
            apiResponse.setStatusCode(Response.Status.CONFLICT.getStatusCode());
            apiResponse.setResults(0);
            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }

        existingUsers = usersResource.searchByEmail(userRegistrationRecord.email(), true);
        if (!existingUsers.isEmpty()) {
            // User with this email already exists
            System.out.println(userRegistrationRecord.email());
            System.out.println(existingUsers.size());
            System.out.println(existingUsers);

            apiResponse.setMessage("Email already exists");
            apiResponse.setStatusCode(Response.Status.CONFLICT.getStatusCode());
            apiResponse.setResults(0);
            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        } else {
            log.info(" Email doesnt exist");
        }


        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRegistrationRecord.username());
        user.setEmail(userRegistrationRecord.email());
        user.setFirstName(userRegistrationRecord.firstName());
        user.setLastName(userRegistrationRecord.lastName());
        user.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRegistrationRecord.password());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        List<CredentialRepresentation> list = new ArrayList<>();
        list.add(credentialRepresentation);
        user.setCredentials(list);


        Response response = usersResource.create(user);

        log.info("Status Code : " + response.getStatus());

        if (Objects.equals(201, response.getStatus())) {

            log.info("User Created");

            List<UserRepresentation> representationList = usersResource.searchByUsername(userRegistrationRecord.username(), true);
            if (!CollectionUtils.isEmpty(representationList)) {
                UserRepresentation userRepresentation1 = representationList.stream().filter(userRepresentation -> Objects.equals(false, userRepresentation.isEmailVerified())).findFirst().orElse(null);
                assert userRepresentation1 != null;
                emailVerification(userRepresentation1.getId());
            }

            apiResponse.setData(userRegistrationRecord);
            apiResponse.setMessage("User Created Successfully");
            apiResponse.setResults(1);
            apiResponse.setStatusCode(response.getStatus());


            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }

        log.info("Signup failed : " + response.getStatusInfo().getReasonPhrase());

        apiResponse.setData(null);
        apiResponse.setMessage("User Creation Failed: " + response.getStatusInfo().getReasonPhrase());
        apiResponse.setResults(0);
        apiResponse.setStatusCode(response.getStatus());

        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }
*/

    @Override
    public ResponseEntity<ApiResponse> createUser(UserRegistrationRecord userRegistrationRecord) {
        ApiResponse apiResponse = new ApiResponse();

        // Validate that all required fields are provided
        if (userRegistrationRecord.username() == null || userRegistrationRecord.username().isEmpty() ||
                userRegistrationRecord.email() == null || userRegistrationRecord.email().isEmpty() ||
                userRegistrationRecord.firstName() == null || userRegistrationRecord.firstName().isEmpty() ||
                userRegistrationRecord.lastName() == null || userRegistrationRecord.lastName().isEmpty() ||
                userRegistrationRecord.password() == null || userRegistrationRecord.password().isEmpty()) {

            apiResponse.setMessage("Fields (username, email, firstName, lastName, password) are required.");
            apiResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            apiResponse.setResults(0);
            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }

        try {
            UsersResource usersResource = getUsersResource();

            // Check if username already exists
            List<UserRepresentation> existingUsers = usersResource.searchByUsername(userRegistrationRecord.username(), true);
            if (!existingUsers.isEmpty()) {
                apiResponse.setMessage("Username already exists");
                apiResponse.setStatusCode(HttpStatus.CONFLICT.value());
                apiResponse.setResults(0);
                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            }

            // Check if email already exists
            existingUsers = usersResource.searchByEmail(userRegistrationRecord.email(), true);
            if (!existingUsers.isEmpty()) {
                apiResponse.setMessage("Email already exists");
                apiResponse.setStatusCode(HttpStatus.CONFLICT.value());
                apiResponse.setResults(0);
                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            }

            // Create new user
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(userRegistrationRecord.username());
            user.setEmail(userRegistrationRecord.email());
            user.setFirstName(userRegistrationRecord.firstName());
            user.setLastName(userRegistrationRecord.lastName());
            user.setEmailVerified(false);

            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setValue(userRegistrationRecord.password());
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

            user.setCredentials(Collections.singletonList(credentialRepresentation));

            Response response = usersResource.create(user);

            if (response.getStatus() == HttpStatus.CREATED.value()) {
                List<UserRepresentation> representationList = usersResource.searchByUsername(userRegistrationRecord.username(), true);
                if (!representationList.isEmpty()) {
                    UserRepresentation userRepresentation = representationList.stream()
                            .filter(ur -> !ur.isEmailVerified())
                            .findFirst()
                            .orElse(null);

                    if (userRepresentation != null) {
                        emailVerification(userRepresentation.getId());
                    }
                }

                apiResponse.setData(userRegistrationRecord);
                apiResponse.setMessage("User Created Successfully");
                apiResponse.setResults(1);
                apiResponse.setStatusCode(HttpStatus.CREATED.value());
                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            } else {
                apiResponse.setMessage("Failed to create user, Status code: " + response.getStatus()+" , Message : "+response.getStatusInfo().getReasonPhrase());
                apiResponse.setStatusCode(response.getStatus());
                apiResponse.setResults(0);
                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            }

        } catch (Exception e) {
            // Handle unexpected exceptions
            apiResponse.setMessage("An error occurred while creating the user: " + e.getMessage());
            apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiResponse.setResults(0);
            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }
    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }

    @Override
    public ResponseEntity<ApiResponse> getUserById(String userId) {
        ApiResponse apiResponse = new ApiResponse();

        try {
            UserRepresentation userRepresentation = getUsersResource().get(userId).toRepresentation();

            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setMessage("User retrieved successfully");
            apiResponse.setResults(1);
            apiResponse.setData(userRepresentation);
        } catch (Exception e) {
            apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiResponse.setMessage("Failed to retrieve user: " + e.getMessage());
            apiResponse.setResults(0);
            apiResponse.setData(null);
        }

        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }


    @Override
    public ResponseEntity<ApiResponse> getUsers() {
        ApiResponse apiResponse = new ApiResponse();

        try {
            List<UserRepresentation> users = getUsersResource().list();

            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setMessage("Users retrieved successfully");
            apiResponse.setResults(users.size());
            apiResponse.setData(users);
        } catch (Exception e) {
            apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiResponse.setMessage("Failed to retrieve user: " + e.getMessage());
            apiResponse.setResults(0);
            apiResponse.setData(null);
        }

        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }


    @Override
    public ResponseEntity<ApiResponse> deleteUserByEmail(String email) {


        ApiResponse apiResponse = new ApiResponse();


        List<UserRepresentation> existingUsers = getUsersByEmail(email);

        if (existingUsers.isEmpty()) {
            apiResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            apiResponse.setMessage("Account with provided email not found.");
            apiResponse.setResults(0);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);

        } else {
            // Use the first user in the list
            String userId = existingUsers.get(0).getId();
            getUsersResource().delete(userId);


            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setMessage("Account Removed Successfully");
            apiResponse.setResults(1);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }


    }

    @Override
    public ResponseEntity<ApiResponse> disableUserByEmail(String email) {
        ApiResponse apiResponse = new ApiResponse();

        try {
            // Fetch users by email
            List<UserRepresentation> existingUsers = getUsersByEmail(email);

            if (existingUsers.isEmpty()) {
                apiResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                apiResponse.setMessage("Account with provided email not found.");
                apiResponse.setResults(0);
                apiResponse.setData(null);
                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            } else {
                // Use the first user in the list
                UserRepresentation userRepresentation = existingUsers.get(0);
                String userId = userRepresentation.getId();

                // Logout all sessions for the user
                getUsersResource().get(userId).logout();

                // Disable the user
                userRepresentation.setEnabled(false);
                getUsersResource().get(userId).update(userRepresentation);

                apiResponse.setStatusCode(HttpStatus.OK.value());
                apiResponse.setMessage("Account Disabled Successfully");
                apiResponse.setResults(1);
                apiResponse.setData(null);
                return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
            }
        } catch (Exception ex) {
            // Handle any unexpected errors
            apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiResponse.setMessage("An error occurred while disabling the account: " + ex.getMessage());
            apiResponse.setResults(0);
            apiResponse.setData(null);
            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }
    }


    @Override
    public ResponseEntity<ApiResponse> emailVerification(String email) {
        ApiResponse apiResponse = new ApiResponse();


        List<UserRepresentation> existingUsers = getUsersByEmail(email);

        if (existingUsers.isEmpty()) {
            apiResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            apiResponse.setMessage("Account with provided email not found.");
            apiResponse.setResults(1);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);

        } else {

            try {
                UsersResource usersResource = getUsersResource();
                usersResource.get(existingUsers.get(0).getId()).sendVerifyEmail();

                apiResponse.setStatusCode(HttpStatus.OK.value());
                apiResponse.setResults(1);
                apiResponse.setMessage("Verification Email sent Successfully");

                UserRepresentation representation = existingUsers.get(0);


                UserRegistrationRecord userRegistrationRecord = new UserRegistrationRecord(
                        representation.getUsername(),
                        representation.getEmail(),
                        representation.getFirstName(),
                        representation.getLastName(),
                        "****"
                );


                apiResponse.setData(userRegistrationRecord);

            } catch (Exception e) {
                apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                apiResponse.setResults(0);
                apiResponse.setMessage("Failed to send Verification Email: " + e.getMessage());
                apiResponse.setData(null);
            }

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);

        }
    }


    public UserResource getUserResource(String userId) {
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }

    public UserResource getUserResourceByEmail(String email) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> existingUsers = getUsersByEmail(email);

        return usersResource.get(existingUsers.get(0).getId());
    }

    @Override
    public ResponseEntity<ApiResponse> getUserRoles(String email) {

        ApiResponse apiResponse = new ApiResponse();


        List<UserRepresentation> existingUsers = getUsersByEmail(email);

        if (existingUsers.isEmpty()) {
            apiResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            apiResponse.setMessage("Account with provided email not found.");
            apiResponse.setResults(0);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        } else {
            String userId = existingUsers.get(0).getId();
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setMessage("User Roles for : " + existingUsers.get(0).getEmail());
            apiResponse.setResults(getUserResource(userId).roles().realmLevel().listAll().size());
            apiResponse.setData(getUserResource(userId).roles().realmLevel().listAll());

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);

        }


    }

    @Override
    public ResponseEntity<ApiResponse> getUserGroups(String email) {

        ApiResponse apiResponse = new ApiResponse();


        List<UserRepresentation> existingUsers = getUsersByEmail(email);

        if (existingUsers.isEmpty()) {
            apiResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            apiResponse.setMessage("Account with provided email not found.");
            apiResponse.setResults(0);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        } else {
            String userId = existingUsers.get(0).getId();
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setMessage("User Groups for : " + existingUsers.get(0).getEmail());
            apiResponse.setResults(getUserResource(userId).groups().size());
            apiResponse.setData(getUserResource(userId).groups());

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);

        }


    }

    @Override
    public ResponseEntity<ApiResponse> updatePassword(String email) {
        ApiResponse apiResponse = new ApiResponse();


        List<UserRepresentation> existingUsers = getUsersByEmail(email);

        if (existingUsers.isEmpty()) {
            apiResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            apiResponse.setMessage("Account with provided email not found.");
            apiResponse.setResults(1);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);

        } else {
            // Use the first user in the list
            String userId = existingUsers.get(0).getId();
            UserResource userResource = getUserResource(userId);
            List<String> actions = new ArrayList<>();
            actions.add("UPDATE_PASSWORD");
            userResource.executeActionsEmail(actions);

            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setMessage("Password change link has been sent to: " + userResource.toRepresentation().getEmail());
            apiResponse.setResults(1);
            apiResponse.setData(null);

            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }
    }


    public List<UserRepresentation> getUsersByEmail(String email) {
        UsersResource usersResource = getUsersResource();

        return usersResource.searchByEmail(email, true);

    }
}
