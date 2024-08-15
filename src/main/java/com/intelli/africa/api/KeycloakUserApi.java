package com.intelli.africa.api;

import com.intelli.africa.model.UserRegistrationRecord;
import com.intelli.africa.response.ApiResponse;
import com.intelli.africa.service.KeycloakUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class KeycloakUserApi {


    private final KeycloakUserService keycloakUserService;


    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserRegistrationRecord userRegistrationRecord) {

        return keycloakUserService.createUser(userRegistrationRecord);
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getUser(Principal principal) {
        return keycloakUserService.getUserById(principal.getName());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getUsers() {
        return keycloakUserService.getUsers();
    }

    @DeleteMapping("/{email}")
    //@PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<ApiResponse> deleteUserByEmail(@PathVariable String email) {

        return keycloakUserService.deleteUserByEmail(email);
    }
    @PutMapping("/disable/{email}")
    //@PreAuthorize("hasAnyRole('admin')")
    public ResponseEntity<ApiResponse> disableUserByEmail(@PathVariable String email) {

        return keycloakUserService.disableUserByEmail(email);
    }


    @PutMapping("/send-verify-email/{email}")
    public ResponseEntity<ApiResponse> sendVerificationEmail(@PathVariable String email) {
        return keycloakUserService.emailVerification(email);
    }

    @PutMapping("/update-password/{email}")
    public ResponseEntity<ApiResponse> updatePassword(@PathVariable String email) {
        return keycloakUserService.updatePassword(email);
    }

    @GetMapping("/roles/{email}")
    public ResponseEntity<ApiResponse> getUserRoles(@PathVariable String email) {
        return keycloakUserService.getUserRoles(email);
    }
    @GetMapping("/groups/{email}")
    public ResponseEntity<ApiResponse> getUserGroups(@PathVariable String email) {
        return keycloakUserService.getUserGroups(email);
    }
}
