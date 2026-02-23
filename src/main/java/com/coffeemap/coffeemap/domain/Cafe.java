package com.coffeemap.coffeemap.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본생성자는 열어두되 외부 new 방지
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String memo;

    private double lat;
    private double lng;

    private double rating;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    /** 생성은 통제: 외부에서는 이 메서드로만 생성 */
    public static Cafe create(String name, String memo, double lat, double lng, double rating) {
        Cafe c = new Cafe();
        c.changeName(name);
        c.changeMemo(memo);
        c.changeLocation(lat, lng);
        c.changeRating(rating);
        return c;
    }

    /** 업데이트 편의용(원하면 개별 change 메서드만 쓰면 됨) */
    public void update(String name, String memo, double lat, double lng, double rating) {
        changeName(name);
        changeMemo(memo);
        changeLocation(lat, lng);
        changeRating(rating);
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        this.name = name.trim();
    }

    public void changeMemo(String memo) {
        this.memo = (memo == null) ? null : memo.trim();
    }

    public void changeLocation(double lat, double lng) {
        // 좌표 검증은 필요하면 더 엄격하게 넣어도 됨
        // 위도: -90..90, 경도: -180..180
        if (lat < -90 || lat > 90) throw new IllegalArgumentException("lat must be -90..90");
        if (lng < -180 || lng > 180) throw new IllegalArgumentException("lng must be -180..180");
        this.lat = lat;
        this.lng = lng;
    }

    public void changeRating(double rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("rating must be 0..5");
        }
        this.rating = rating;
    }
}