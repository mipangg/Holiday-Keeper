package io.mipangg.holidaykeeper.domain.type.repository;

import io.mipangg.holidaykeeper.domain.type.entity.Type;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TypeRepository extends JpaRepository<Type, Long> {

    @Query("select t from Type t where t.type in :types")
    List<Type> findByTypeIn(@Param("types") Set<String> types);
}
