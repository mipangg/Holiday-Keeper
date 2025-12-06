package io.mipangg.holidaykeeper.domain.county.repository;

import io.mipangg.holidaykeeper.domain.county.entity.County;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountyRepository extends JpaRepository<County, Long> {

    Optional<County> findByName(String county);
}
