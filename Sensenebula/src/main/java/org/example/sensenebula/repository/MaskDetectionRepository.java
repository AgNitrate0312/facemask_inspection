package org.example.sensenebula.repository;

import org.example.sensenebula.model.MaskDetectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface MaskDetectionRepository extends JpaRepository<MaskDetectionRecord, Long>, JpaSpecificationExecutor<MaskDetectionRecord> {
    // 可以根据 snapId 查询是否存在，防止重复插入
    MaskDetectionRecord findBySnapId(String snapId);

    // 统计某段时间内的总数
    long countByCreateTimeBetween(Date startTime, Date endTime);

    // 统计某段时间内不同口罩状态的数量
    long countByCreateTimeBetweenAndMaskStatus(Date startTime, Date endTime, String maskStatus);

    // 统计待处理数量
    long countByHandleStatus(Integer handleStatus);
}
