package com.example.healthhub.dao;

import com.example.healthhub.model.BmiRecord;
import java.util.List;

public interface BmiRecordDAO extends GenericDAO<BmiRecord> {
    List<BmiRecord> findByMember(Long memberId);
    List<BmiRecord> findRecent(int limit);
}
