package org.example.sensenebula.repository;

import org.example.sensenebula.model.MaskDetectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MaskDetectionRepository extends JpaRepository<MaskDetectionRecord, Long>, JpaSpecificationExecutor<MaskDetectionRecord> {
    // 可以根据 snapId 查询是否存在，防止重复插入
    MaskDetectionRecord findBySnapId(String snapId);
}
