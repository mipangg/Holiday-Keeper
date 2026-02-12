package io.mipangg.holidaykeeper.domain.county.repository;

import io.mipangg.holidaykeeper.domain.county.entity.County;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CountyRepository extends JpaRepository<County, Long> {

    @Query("select c from County c where c.county in :counties")
    List<County> findByCountyIn(@Param("counties") Set<String> counties);
}
