package com.laris.dataprocessingpipeline.dto.request;

import com.laris.dataprocessingpipeline.domain.PermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PipelinePermissionRequest {
    @NotNull
    private Long roleId;

    @NotNull
    private PermissionType permissionType;
}
