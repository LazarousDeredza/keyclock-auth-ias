package com.intelli.africa.service;

import com.intelli.africa.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface GroupService {

    ResponseEntity<ApiResponse> assignGroup(String email, String groupId);
    ResponseEntity<ApiResponse> deleteFromGroup(String email, String groupId);
}
