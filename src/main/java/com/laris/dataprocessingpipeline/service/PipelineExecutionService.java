package com.laris.dataprocessingpipeline.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laris.dataprocessingpipeline.context.UserContext;
import com.laris.dataprocessingpipeline.domain.ErrorHandlingStrategy;
import com.laris.dataprocessingpipeline.domain.NodeType;
import com.laris.dataprocessingpipeline.domain.PermissionType;
import com.laris.dataprocessingpipeline.domain.Pipeline;
import com.laris.dataprocessingpipeline.domain.PipelineNode;
import com.laris.dataprocessingpipeline.domain.User;
import com.laris.dataprocessingpipeline.dto.response.PipelineExecutionResult;
import com.laris.dataprocessingpipeline.exception.NodeProcessingException;
import com.laris.dataprocessingpipeline.pipeline.NodeProcessor;
import com.laris.dataprocessingpipeline.repository.PipelineRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PipelineExecutionService {

    private final PipelineRepository pipelineRepository;
    private final PermissionService permissionService;
    private final List<NodeProcessor> processors;
    private final ObjectMapper objectMapper;

    private final Map<NodeType, NodeProcessor> processorMap = new HashMap<>();

    @PostConstruct
    public void init() {
        processors.forEach(p -> processorMap.put(p.getNodeType(), p));
    }

    @Transactional(readOnly = true)
    public PipelineExecutionResult executePipeline(Long pipelineId, User currentUser) {
        permissionService.validatePermission(currentUser, pipelineId, PermissionType.EXECUTE);

        Pipeline pipeline = pipelineRepository.findByIdWithNodes(pipelineId)
                .orElseThrow(() -> new EntityNotFoundException("Pipeline not found with id: " + pipelineId));

        List<PipelineNode> sortedNodes = pipeline.getNodes().stream()
                .sorted(Comparator.comparing(PipelineNode::getOrderIndex))
                .toList();

        List<Map<String, Object>> data = new ArrayList<>();
        List<PipelineExecutionResult.NodeExecutionLog> logs = new ArrayList<>();
        boolean success = true;
        String errorMessage = null;

        try {
            UserContext.setCurrentUser(currentUser);

            for (PipelineNode node : sortedNodes) {
                try {
                    Map<String, Object> config = objectMapper.readValue(
                            node.getConfiguration(),
                            new TypeReference<>() {
                            }
                    );

                    NodeProcessor processor = processorMap.get(node.getNodeType());
                    if (processor == null) {
                        throw new NodeProcessingException("No processor found for node type: " + node.getNodeType());
                    }

                    int inputCount = data.size();
                    data = processor.process(data, config);
                    int outputCount = data.size();

                    logs.add(PipelineExecutionResult.NodeExecutionLog.builder()
                            .nodeType(node.getNodeType())
                            .orderIndex(node.getOrderIndex())
                            .success(true)
                            .message("Processed successfully")
                            .inputCount(inputCount)
                            .outputCount(outputCount)
                            .build());

                } catch (Exception e) {
                    logs.add(PipelineExecutionResult.NodeExecutionLog.builder()
                            .nodeType(node.getNodeType())
                            .orderIndex(node.getOrderIndex())
                            .success(false)
                            .message(e.getMessage())
                            .inputCount(data.size())
                            .outputCount(0)
                            .build());

                    if (pipeline.getErrorHandlingStrategy() == ErrorHandlingStrategy.STOP_ON_ERROR) {
                        throw e;
                    }
                }
            }

        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
        } finally {
            UserContext.clear();
        }

        return PipelineExecutionResult.builder()
                .pipelineId(pipelineId)
                .pipelineName(pipeline.getName())
                .success(success)
                .output(data)
                .executionLogs(logs)
                .errorMessage(errorMessage)
                .executedAt(LocalDateTime.now())
                .build();
    }
}
