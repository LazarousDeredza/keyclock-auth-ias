package com.intelli.africa.api;

import com.intelli.africa.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class KeycloakRoleApi {

    private final RoleService roleService;


    @PutMapping("/assign-role/user/{email}")
    public ResponseEntity<?>  assignRole(@PathVariable String email, @RequestParam String roleName){

        return roleService.assignRole(email, roleName);

    }
    @PutMapping("/unassign-role/user/{email}")
    public ResponseEntity<?>  unassignRole(@PathVariable String email, @RequestParam String roleName){

        return roleService.unassignRole(email, roleName);

    }



}
