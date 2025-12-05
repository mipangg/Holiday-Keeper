package io.mipangg.holidaykeeper.domain.country.repository;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CountryRepository extends JpaRepository<Country, Long> {

    boolean existsByCode(String code);

    @Query("select c.code from Country c")
    List<String> findAllCodes();
}
