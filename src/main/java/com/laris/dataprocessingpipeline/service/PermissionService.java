package com.laris.dataprocessingpipeline.service;

import com.laris.dataprocessingpipeline.domain.PermissionType;
import com.laris.dataprocessingpipeline.domain.Pipeline;
import com.laris.dataprocessingpipeline.domain.Role;
import com.laris.dataprocessingpipeline.domain.User;
import com.laris.dataprocessingpipeline.repository.PipelinePermissionRepository;
import com.laris.dataprocessingpipeline.repository.PipelineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PipelinePermissionRepository permissionRepository;
    private final PipelineRepository pipelineRepository;

    public boolean hasPermission(User user, Long pipelineId, PermissionType requiredPermission) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new EntityNotFoundException("Pipeline not found with id: " + pipelineId));

        if (pipeline.getOwner().getId().equals(user.getId())) {
            return true;
        }

        List<Long> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return false;
        }

        List<PermissionType> permissionsToCheck = new ArrayList<>();
        permissionsToCheck.add(requiredPermission);

        if (requiredPermission == PermissionType.READ) {
            permissionsToCheck.add(PermissionType.WRITE);
        }

        return permissionRepository.existsByPipelineIdAndRoleIdInAndPermissionTypeIn(
                pipelineId, roleIds, permissionsToCheck
        );
    }

    public void validatePermission(User user, Long pipelineId, PermissionType required) {
        if (!hasPermission(user, pipelineId, required)) {
            throw new AccessDeniedException("Insufficient permissions for pipeline " + pipelineId);
        }
    }
}
