package com.coffeemap.coffeemap.repo;

import com.coffeemap.coffeemap.domain.Cafe;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CafeRepository extends JpaRepository<Cafe, UUID> {

    @Query("""
      select c from Cafe c
      where (:q is null
        or lower(c.name) like lower(concat('%', :q, '%'))
        or lower(c.memo) like lower(concat('%', :q, '%')))
        and (:minRating is null or c.rating >= :minRating)
    """)
    Page<Cafe> search(
            @Param("q") String q,
            @Param("minRating") Double minRating,
            Pageable pageable
    );
}