package io.mipangg.holidaykeeper.domain.country.repository;

import io.mipangg.holidaykeeper.domain.country.entity.Country;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CountryRepository extends JpaRepository<Country, Long> {

    boolean existsBy();

    Optional<Country> findByCountryCode(String countryCode);

    @Query("select c from Country c where c.countryCode in :countryCodes")
    List<Country> findByCountryCodeIn(@Param("countryCodes") Collection<String> countryCodes);
}
