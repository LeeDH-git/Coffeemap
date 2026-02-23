package com.coffeemap.coffeemap.repo;

import com.coffeemap.coffeemap.domain.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CafeRepository extends JpaRepository<Cafe, UUID> {
}