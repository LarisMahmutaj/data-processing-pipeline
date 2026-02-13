package com.laris.dataprocessingpipeline.dto.response;

import com.laris.dataprocessingpipeline.domain.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelinePermissionResponse {
    private Long id;
    private Long roleId;
    private String roleName;
    private PermissionType permissionType;
    private LocalDateTime createdAt;
}
