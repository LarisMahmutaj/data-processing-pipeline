package com.laris.dataprocessingpipeline.dto.response;

import com.laris.dataprocessingpipeline.domain.ErrorHandlingStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineResponse {
    private Long id;
    private String name;
    private String description;
    private ErrorHandlingStrategy errorHandlingStrategy;
    private UserSummary owner;
    private List<PipelineNodeResponse> nodes;
    private List<PipelinePermissionResponse> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String username;
        private String email;
    }
}