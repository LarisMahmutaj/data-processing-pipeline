package com.laris.dataprocessingpipeline.dto.request;

import com.laris.dataprocessingpipeline.domain.ErrorHandlingStrategy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePipelineRequest {
    @NotBlank(message = "Pipeline name is required")
    @Size(min = 3, max = 100)
    private String name;

    private String description;

    @NotNull
    @Builder.Default
    private ErrorHandlingStrategy errorHandlingStrategy = ErrorHandlingStrategy.STOP_ON_ERROR;

    @Valid
    @NotEmpty(message = "Pipeline must have at least one node")
    private List<PipelineNodeRequest> nodes;

    @Valid
    private List<PipelinePermissionRequest> permissions;
}
