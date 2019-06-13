package com.bsep_sbz.SIEMCenter.repository;

import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AlarmRepository extends PagingAndSortingRepository<Alarm, Long>
{
    Alarm save(Alarm alarm);

    @Query(value = "SELECT * FROM alarm order by timestamp desc", nativeQuery = true)
    Page<Alarm> findAllWithPagination(Pageable pageable);
}
