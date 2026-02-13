package com.laris.dataprocessingpipeline.repository;

import com.laris.dataprocessingpipeline.domain.ExportedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExportedDataRepository extends JpaRepository<ExportedData, Long> {

    Optional<ExportedData> findFirstByExportKeyOrderByExportedAtDesc(String exportKey);
}
