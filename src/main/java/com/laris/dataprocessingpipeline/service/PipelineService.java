package com.laris.dataprocessingpipeline.service;

import com.laris.dataprocessingpipeline.domain.PermissionType;
import com.laris.dataprocessingpipeline.domain.Pipeline;
import com.laris.dataprocessingpipeline.domain.PipelineNode;
import com.laris.dataprocessingpipeline.domain.PipelinePermission;
import com.laris.dataprocessingpipeline.domain.Role;
import com.laris.dataprocessingpipeline.domain.User;
import com.laris.dataprocessingpipeline.dto.request.CreatePipelineRequest;
import com.laris.dataprocessingpipeline.dto.request.PipelinePermissionRequest;
import com.laris.dataprocessingpipeline.dto.request.PipelineSearchRequest;
import com.laris.dataprocessingpipeline.dto.response.PipelineResponse;
import com.laris.dataprocessingpipeline.mapper.PipelineMapper;
import com.laris.dataprocessingpipeline.repository.PipelineRepository;
import com.laris.dataprocessingpipeline.repository.RoleRepository;
import com.laris.dataprocessingpipeline.specification.PipelineSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;
    private final PipelineMapper mapper;

    public PipelineResponse createPipeline(CreatePipelineRequest request, User currentUser) {
        Pipeline pipeline = Pipeline.builder()
                .name(request.getName())
                .description(request.getDescription())
                .errorHandlingStrategy(request.getErrorHandlingStrategy())
                .owner(currentUser)
                .build();

        request.getNodes().forEach(nodeReq -> {
            PipelineNode node = mapper.toEntity(nodeReq, pipeline);
            pipeline.addNode(node);
        });

        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            for (PipelinePermissionRequest permReq : request.getPermissions()) {
                Role role = roleRepository.findById(permReq.getRoleId())
                        .orElseThrow(
                                () -> new EntityNotFoundException("Role not found with id: " + permReq.getRoleId()));

                PipelinePermission permission = PipelinePermission.builder()
                        .pipeline(pipeline)
                        .role(role)
                        .permissionType(permReq.getPermissionType())
                        .build();
                pipeline.addPermission(permission);
            }
        }

        Pipeline saved = pipelineRepository.save(pipeline);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PipelineResponse> searchPipelines(PipelineSearchRequest search, User currentUser) {
        Specification<Pipeline> spec = PipelineSpecification.withFilters(search);
        List<Pipeline> pipelines = pipelineRepository.findAll(spec);

        return pipelines.stream()
                .filter(p -> p.getOwner().getId().equals(currentUser.getId()) ||
                        permissionService.hasPermission(currentUser, p.getId(), PermissionType.READ))
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PipelineResponse getPipelineById(Long id, User currentUser) {
        Pipeline pipeline = pipelineRepository.findByIdWithNodes(id)
                .orElseThrow(() -> new EntityNotFoundException("Pipeline not found with id: " + id));

        permissionService.validatePermission(currentUser, id, PermissionType.READ);

        return mapper.toResponse(pipeline);
    }

    public PipelineResponse updatePipeline(Long id, CreatePipelineRequest request, User currentUser) {
        Pipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pipeline not found with id: " + id));

        permissionService.validatePermission(currentUser, id, PermissionType.WRITE);

        pipeline.setName(request.getName());
        pipeline.setDescription(request.getDescription());
        pipeline.setErrorHandlingStrategy(request.getErrorHandlingStrategy());

        pipeline.getNodes().clear();
        request.getNodes().forEach(nodeReq -> {
            PipelineNode node = mapper.toEntity(nodeReq, pipeline);
            pipeline.addNode(node);
        });

        Pipeline updated = pipelineRepository.save(pipeline);
        return mapper.toResponse(updated);
    }

    public void deletePipeline(Long id, User currentUser) {
        Pipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pipeline not found with id: " + id));

        if (!pipeline.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only pipeline owner can delete. Current user: " +
                    currentUser.getUsername() + ", Owner: " + pipeline.getOwner().getUsername());
        }

        pipelineRepository.delete(pipeline);
    }
}
