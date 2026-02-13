package com.laris.dataprocessingpipeline.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pipelines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pipeline {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "error_handling_strategy", nullable = false)
    @Builder.Default
    private ErrorHandlingStrategy errorHandlingStrategy = ErrorHandlingStrategy.STOP_ON_ERROR;
    
    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<PipelineNode> nodes = new ArrayList<>();
    
    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PipelinePermission> permissions = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public void addNode(PipelineNode node) {
        nodes.add(node);
        node.setPipeline(this);
    }
    
    public void removeNode(PipelineNode node) {
        nodes.remove(node);
        node.setPipeline(null);
    }
    
    public void addPermission(PipelinePermission permission) {
        permissions.add(permission);
        permission.setPipeline(this);
    }
    
    public void removePermission(PipelinePermission permission) {
        permissions.remove(permission);
        permission.setPipeline(null);
    }

}
