package com.intelli.africa.api;

import com.intelli.africa.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class KeycloakGroupApi {

    private final GroupService groupService;


    @PutMapping("/assign-group/user/{email}")
    public ResponseEntity<?>  assignRole(@PathVariable String email, @RequestParam String groupId){

        return groupService.assignGroup(email, groupId);

    }
    @PutMapping("/unassign-group/user/{email}")
    public ResponseEntity<?>  removeFromGroup(@PathVariable String email, @RequestParam String groupId){

        return groupService.deleteFromGroup(email, groupId);

    }



}
