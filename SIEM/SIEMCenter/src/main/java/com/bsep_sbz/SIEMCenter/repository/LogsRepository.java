package com.bsep_sbz.SIEMCenter.repository;

import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsRepository extends JpaRepository<Log, Long>
{
    Log save(Log log);
}
