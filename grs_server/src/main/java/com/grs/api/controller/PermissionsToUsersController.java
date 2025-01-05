package com.grs.api.controller;

import com.grs.api.model.UserType;
import com.grs.core.dao.PermissionsToUsersDAO;
import com.grs.core.domain.grs.PermissionsToUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PermissionsToUsersController {

    private final PermissionsToUsersDAO permissionsToUsersDAO;

    @GetMapping("/api/permissions-to-users/findOne/{id}")
    public PermissionsToUsers findOne(@PathVariable Long id) {
        return permissionsToUsersDAO.findOne(id);
    }

    @GetMapping("/api/permissions-to-users/findByOisfUserId/{id}")
    public List<PermissionsToUsers> findByOisfUserId(@PathVariable Long id) {
        return permissionsToUsersDAO.findByOisfUserId(id);
    }

    @GetMapping("/api/permissions-to-users/findall")
    public List<PermissionsToUsers> findAll() {
        return permissionsToUsersDAO.findAll();
    }
}
