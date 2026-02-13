package com.laris.dataprocessingpipeline.repository;

import com.laris.dataprocessingpipeline.domain.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long>, JpaSpecificationExecutor<Pipeline> {

    @Query("SELECT DISTINCT p FROM Pipeline p " +
           "LEFT JOIN FETCH p.nodes " +
           "WHERE p.id = :id")
    Optional<Pipeline> findByIdWithNodes(@Param("id") Long id);
}
