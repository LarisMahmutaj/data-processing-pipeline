package com.laris.dataprocessingpipeline.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "exported_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "export_key", nullable = false)
    private String exportKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String data;

    @CreationTimestamp
    @Column(name = "exported_at", nullable = false, updatable = false)
    private LocalDateTime exportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
