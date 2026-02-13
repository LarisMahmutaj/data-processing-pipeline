package com.laris.dataprocessingpipeline.dto.request;

import com.laris.dataprocessingpipeline.domain.NodeType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PipelineNodeRequest {
    @NotNull
    private NodeType nodeType;

    @NotNull
    private Integer orderIndex;

    @NotNull
    private Map<String, Object> configuration;
}