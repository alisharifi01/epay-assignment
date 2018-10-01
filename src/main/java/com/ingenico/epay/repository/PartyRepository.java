package com.ingenico.epay.repository;

import com.ingenico.epay.domain.Party;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends CrudRepository<Party, Long> {
    Party findByName(String name);

    @Query("SELECT " +
            "CASE WHEN COUNT(p) > 0 THEN 'true' ELSE 'false' END " +
            "FROM Party p " +
            "WHERE p.name = :name")
    Boolean existsByName(@Param("name") String name);
}
