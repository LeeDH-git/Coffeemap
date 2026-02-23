package com.coffeemap.coffeemap.api;

import com.coffeemap.coffeemap.domain.Cafe;
import com.coffeemap.coffeemap.dto.CafeListResponse;
import com.coffeemap.coffeemap.dto.CafeSummary;
import com.coffeemap.coffeemap.repo.CafeRepository;
import com.coffeemap.coffeemap.security.AdminKeyGuard;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
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

        Cafe cafe = Cafe.create(
                req.name(),
                req.memo(),
                req.lat(),
                req.lng(),
                req.rating()
        );

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

    @GetMapping
    public CafeListResponse list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Sort s = parseSort(sort); // 아래 메서드 추가
        Pageable pageable = PageRequest.of(Math.max(page, 0), clamp(size, 1, 50), s);

        Page<Cafe> result = repo.search(blankToNull(q), minRating, pageable);

        var items = result.getContent().stream()
                .map(c -> new CafeSummary(
                        c.getId(), c.getName(), c.getMemo(),
                        c.getLat(), c.getLng(), c.getRating(), c.getCreatedAt()
                ))
                .toList();

        return new CafeListResponse(items, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static Sort parseSort(String sort) {
        // sort 예시: "createdAt,desc" / "rating,desc" / "name,asc"
        try {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            Sort.Direction dir = (parts.length > 1) ? Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.DESC;
            return Sort.by(dir, field);
        } catch (Exception e) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }
}
