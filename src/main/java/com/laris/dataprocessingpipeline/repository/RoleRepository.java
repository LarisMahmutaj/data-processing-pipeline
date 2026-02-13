package com.laris.dataprocessingpipeline.repository;

import com.laris.dataprocessingpipeline.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
