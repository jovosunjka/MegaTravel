package com.bsep_sbz.SIEMCenter.repository;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.*;

public interface LogsRepository extends PagingAndSortingRepository<Log, Long>
{
    Log save(Log log);

    @Query(value = "SELECT * FROM log WHERE id rlike ?1 order by timestamp desc", nativeQuery = true)
    Page<Log> findByIdRegexAndPagination(String regExp, Pageable pageable);

    @Query(value = "SELECT * FROM log WHERE type rlike ?1 order by timestamp desc", nativeQuery = true)
    Page<Log> findByTypeRegexAndPagination(String regExp, Pageable pageable);

    @Query(value = "SELECT * FROM log WHERE category rlike ?1 order by timestamp desc", nativeQuery = true)
    Page<Log> findByCategoryRegexAndPagination(String regExp, Pageable pageable);

    @Query(value = "SELECT * FROM log WHERE source rlike ?1 order by timestamp desc", nativeQuery = true)
    Page<Log> findBySourceRegexAndPagination(String regExp, Pageable pageable);

    @Query(value = "SELECT * FROM log WHERE timestamp rlike ?1 order by timestamp desc", nativeQuery = true)
    Page<Log> findByTimestampRegexAndPagination(String regExp, Pageable pageable);

    @Query(value = "SELECT * FROM log WHERE host_address rlike ?1 order by timestamp desc", nativeQuery = true)
    Page<Log> findByHostAddressRegexAndPagination(String regExp, Pageable pageable);

    @Query(value = "SELECT * FROM log WHERE message rlike ?1 order by timestamp desc", nativeQuery = true)
    Page<Log> findByMessageRegexAndPagination(String regex, Pageable pageable);

    List<Log> findByCategoryAndTimestampGreaterThan(LogCategory logCategory, Date timestamp);
}
