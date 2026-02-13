package com.laris.dataprocessingpipeline.controller;

import com.laris.dataprocessingpipeline.domain.ErrorHandlingStrategy;
import com.laris.dataprocessingpipeline.domain.NodeType;
import com.laris.dataprocessingpipeline.domain.User;
import com.laris.dataprocessingpipeline.dto.request.CreatePipelineRequest;
import com.laris.dataprocessingpipeline.dto.request.PipelineSearchRequest;
import com.laris.dataprocessingpipeline.dto.response.PipelineExecutionResult;
import com.laris.dataprocessingpipeline.dto.response.PipelineResponse;
import com.laris.dataprocessingpipeline.security.UserPrincipal;
import com.laris.dataprocessingpipeline.service.PipelineExecutionService;
import com.laris.dataprocessingpipeline.service.PipelineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pipelines")
@RequiredArgsConstructor
public class PipelineController {

    private final PipelineService pipelineService;
    private final PipelineExecutionService executionService;

    @PostMapping
    public ResponseEntity<PipelineResponse> createPipeline(
            @Valid @RequestBody CreatePipelineRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.user();
        PipelineResponse response = pipelineService.createPipeline(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PipelineResponse>> searchPipelines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) NodeType nodeType,
            @RequestParam(required = false) ErrorHandlingStrategy errorHandlingStrategy,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.user();

        PipelineSearchRequest search = new PipelineSearchRequest();
        search.setName(name);
        search.setOwnerId(ownerId);
        search.setNodeType(nodeType);
        search.setErrorHandlingStrategy(errorHandlingStrategy);

        List<PipelineResponse> pipelines = pipelineService.searchPipelines(search, currentUser);
        return ResponseEntity.ok(pipelines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PipelineResponse> getPipeline(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.user();
        PipelineResponse response = pipelineService.getPipelineById(id, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PipelineResponse> updatePipeline(
            @PathVariable Long id,
            @Valid @RequestBody CreatePipelineRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.user();
        PipelineResponse response = pipelineService.updatePipeline(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePipeline(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.user();
        pipelineService.deletePipeline(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/run/{id}")
    public ResponseEntity<PipelineExecutionResult> executePipeline(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.user();
        PipelineExecutionResult result = executionService.executePipeline(id, currentUser);
        return ResponseEntity.ok(result);
    }
}
