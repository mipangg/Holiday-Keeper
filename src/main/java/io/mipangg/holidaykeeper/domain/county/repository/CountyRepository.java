package io.mipangg.holidaykeeper.domain.county.repository;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import io.mipangg.holidaykeeper.domain.county.entity.County;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CountyRepository extends JpaRepository<County, Long> {

    Optional<County> findByName(String county);

    Optional<County> findByNameAndCountry(String name, Country country);
}
