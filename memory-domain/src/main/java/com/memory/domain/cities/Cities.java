package com.memory.domain.cities;

import com.memory.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cities extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long geonameId;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "asciiname", length = 200)
    private String asciiName;

    @Column(name = "country_code", length = 10)
    private String countryCode;

    private Double latitude;

    private Double longitude;

    private Long population;

    @Builder
    public Cities(Long geonameId, String name, String asciiName, String countryCode, 
                  Double latitude, Double longitude, Long population) {
        this.geonameId = geonameId;
        this.name = name;
        this.asciiName = asciiName;
        this.countryCode = countryCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.population = population;
    }

    // 위치 관련 편의 메서드
    public boolean hasValidCoordinates() {
        return latitude != null && longitude != null;
    }

}
