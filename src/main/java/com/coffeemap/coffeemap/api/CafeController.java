package com.coffeemap.coffeemap.api;

import com.coffeemap.coffeemap.domain.Cafe;
import com.coffeemap.coffeemap.repo.CafeRepository;
import com.coffeemap.coffeemap.security.AdminKeyGuard;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cafes")
@RequiredArgsConstructor
public class CafeController {

    private final CafeRepository repo;
    private final AdminKeyGuard guard;

    // ✅ 누구나 조회 가능
    @GetMapping
    public List<Cafe> findAll() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    // ✅ 관리자만 추가 가능
    @PostMapping
    public Cafe create(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                       @RequestBody CreateCafeRequest req) {
        guard.requireValid(key);
        Cafe cafe = Cafe.builder()
                .name(req.name())
                .memo(req.memo())
                .lat(req.lat())
                .lng(req.lng())
                .rating(req.rating())
                .build();
        return repo.save(cafe);
    }

    // ✅ 관리자만 삭제 가능
    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                       @PathVariable UUID id) {
        guard.requireValid(key);
        repo.deleteById(id);
    }

    public record CreateCafeRequest(
            @NotBlank String name,
            String memo,
            double lat,
            double lng,
            double rating
    ) {}
}