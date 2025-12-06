package io.mipangg.holidaykeeper.domain.country.repository;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

    boolean existsByCode(String code);

}
