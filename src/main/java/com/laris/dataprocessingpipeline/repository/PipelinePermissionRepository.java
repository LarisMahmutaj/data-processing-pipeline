package com.laris.dataprocessingpipeline.repository;

import com.laris.dataprocessingpipeline.domain.PipelinePermission;
import com.laris.dataprocessingpipeline.domain.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelinePermissionRepository extends JpaRepository<PipelinePermission, Long> {

    @Query("SELECT CASE WHEN COUNT(pp) > 0 THEN true ELSE false END FROM PipelinePermission pp " +
           "WHERE pp.pipeline.id = :pipelineId " +
           "AND pp.role.id IN :roleIds " +
           "AND pp.permissionType IN :permissionTypes")
    boolean existsByPipelineIdAndRoleIdInAndPermissionTypeIn(
            @Param("pipelineId") Long pipelineId,
            @Param("roleIds") List<Long> roleIds,
            @Param("permissionTypes") List<PermissionType> permissionTypes);
}
