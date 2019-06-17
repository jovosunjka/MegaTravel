package com.bsep_sbz.SIEMCenter.repository;

import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.LockModeType;

public interface AlarmRepository extends PagingAndSortingRepository<Alarm, Long>
{
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Alarm save(Alarm alarm);

    @Query(value = "SELECT * FROM alarm order by timestamp desc", nativeQuery = true)
    Page<Alarm> findAllWithPagination(Pageable pageable);
}
