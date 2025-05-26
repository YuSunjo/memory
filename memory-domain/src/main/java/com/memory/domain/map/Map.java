package com.memory.domain.map;

import com.memory.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Map extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String latitude;

    private String longitude;

    @Enumerated(EnumType.STRING)
    private MapType mapType;

    public Map(String latitude, String longitude, MapType mapType) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.mapType = mapType;
    }

}
