package com.laris.dataprocessingpipeline.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laris.dataprocessingpipeline.domain.Pipeline;
import com.laris.dataprocessingpipeline.domain.PipelineNode;
import com.laris.dataprocessingpipeline.domain.PipelinePermission;
import com.laris.dataprocessingpipeline.dto.request.PipelineNodeRequest;
import com.laris.dataprocessingpipeline.dto.response.PipelineNodeResponse;
import com.laris.dataprocessingpipeline.dto.response.PipelinePermissionResponse;
import com.laris.dataprocessingpipeline.dto.response.PipelineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PipelineMapper {

    private final ObjectMapper objectMapper;

    public PipelineResponse toResponse(Pipeline pipeline) {
        return PipelineResponse.builder()
                .id(pipeline.getId())
                .name(pipeline.getName())
                .description(pipeline.getDescription())
                .errorHandlingStrategy(pipeline.getErrorHandlingStrategy())
                .owner(toUserSummary(pipeline))
                .nodes(pipeline.getNodes().stream()
                        .map(this::toNodeResponse)
                        .collect(Collectors.toList()))
                .permissions(pipeline.getPermissions().stream()
                        .map(this::toPermissionResponse)
                        .collect(Collectors.toList()))
                .createdAt(pipeline.getCreatedAt())
                .updatedAt(pipeline.getUpdatedAt())
                .build();
    }

    private PipelineResponse.UserSummary toUserSummary(Pipeline pipeline) {
        return PipelineResponse.UserSummary.builder()
                .id(pipeline.getOwner().getId())
                .username(pipeline.getOwner().getUsername())
                .email(pipeline.getOwner().getEmail())
                .build();
    }

    public PipelineNodeResponse toNodeResponse(PipelineNode node) {
        try {
            Map<String, Object> config = objectMapper.readValue(
                    node.getConfiguration(),
                    new TypeReference<>() {
                    }
            );
            return PipelineNodeResponse.builder()
                    .id(node.getId())
                    .nodeType(node.getNodeType())
                    .orderIndex(node.getOrderIndex())
                    .configuration(config)
                    .createdAt(node.getCreatedAt())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse node configuration: " + e.getMessage(), e);
        }
    }

    public PipelinePermissionResponse toPermissionResponse(PipelinePermission permission) {
        return PipelinePermissionResponse.builder()
                .id(permission.getId())
                .roleId(permission.getRole().getId())
                .roleName(permission.getRole().getName())
                .permissionType(permission.getPermissionType())
                .createdAt(permission.getCreatedAt())
                .build();
    }

    public PipelineNode toEntity(PipelineNodeRequest request, Pipeline pipeline) {
        try {
            String configJson = objectMapper.writeValueAsString(request.getConfiguration());
            return PipelineNode.builder()
                    .pipeline(pipeline)
                    .nodeType(request.getNodeType())
                    .orderIndex(request.getOrderIndex())
                    .configuration(configJson)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize node configuration: " + e.getMessage(), e);
        }
    }
}
