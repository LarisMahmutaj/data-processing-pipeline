package com.laris.dataprocessingpipeline.dto.request;

import com.laris.dataprocessingpipeline.domain.ErrorHandlingStrategy;
import com.laris.dataprocessingpipeline.domain.NodeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PipelineSearchRequest {
    private String name;
    private Long ownerId;
    private NodeType nodeType;
    private ErrorHandlingStrategy errorHandlingStrategy;
    private List<Long> roleIds;
}