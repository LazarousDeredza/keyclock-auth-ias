package com.intelli.africa.service.Impl;



import com.intelli.africa.model.UserRegistrationRecord;
import com.intelli.africa.response.ApiResponse;
import com.intelli.africa.service.KeycloakUserService;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    @Override
    public ResponseEntity<ApiResponse> createUser(UserRegistrationRecord userRegistrationRecord) {

        ApiResponse apiResponse=new ApiResponse();

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
            apiResponse.setMessage("Email already exists");
            apiResponse.setStatusCode(Response.Status.CONFLICT.getStatusCode());
            apiResponse.setResults(0);
            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
        }else{
            log.info(" Email doesnt exist");
        }


        UserRepresentation user=new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRegistrationRecord.username());
        user.setEmail(userRegistrationRecord.email());
        user.setFirstName(userRegistrationRecord.firstName());
        user.setLastName(userRegistrationRecord.lastName());
        user.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation=new CredentialRepresentation();
        credentialRepresentation.setValue(userRegistrationRecord.password());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        List<CredentialRepresentation> list = new ArrayList<>();
        list.add(credentialRepresentation);
        user.setCredentials(list);



        Response response = usersResource.create(user);

        log.info("Status Code : "+response.getStatus());

        if(Objects.equals(201,response.getStatus())){

            log.info("User Created");

            List<UserRepresentation> representationList = usersResource.searchByUsername(userRegistrationRecord.username(), true);
            if(!CollectionUtils.isEmpty(representationList)){
                UserRepresentation userRepresentation1 = representationList.stream().filter(userRepresentation -> Objects.equals(false, userRepresentation.isEmailVerified())).findFirst().orElse(null);
                assert userRepresentation1 != null;
               // emailVerification(userRepresentation1.getId());
            }

            apiResponse.setData(userRegistrationRecord);
            apiResponse.setMessage("User Created Successfully");
            apiResponse.setResults(1);
            apiResponse.setStatusCode(response.getStatus());


            return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse) ;
        }

        log.info("Signup failed : "+response.getStatusInfo().getReasonPhrase());

        apiResponse.setData(null);
        apiResponse.setMessage("User Creation Failed: " + response.getStatusInfo().getReasonPhrase());
        apiResponse.setResults(0);
        apiResponse.setStatusCode(response.getStatus());

        return   ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse) ;
    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }

    @Override
    public UserRepresentation getUserById(String userId) {

        return  getUsersResource().get(userId).toRepresentation();
    }

    @Override
    public void deleteUserById(String userId) {

        getUsersResource().delete(userId);
    }


    @Override
    public void emailVerification(String userId){

        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();
    }

    public UserResource getUserResource(String userId){
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }

    @Override
    public void updatePassword(String userId) {

        UserResource userResource = getUserResource(userId);
        List<String> actions= new ArrayList<>();
        actions.add("UPDATE_PASSWORD");
        userResource.executeActionsEmail(actions);

    }
}
