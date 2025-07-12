package com.memory.domain.map;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Map extends BaseTimeEntity {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String address;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    @Enumerated(EnumType.STRING)
    private MapType mapType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Map(String name, String description, String address, String latitude, String longitude, MapType mapType, Member member) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.location = createPoint(Double.parseDouble(longitude), Double.parseDouble(latitude));
        this.mapType = mapType;
        this.member = member;
    }

    private Point createPoint(double longitude, double latitude) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
    }

    public String getLatitude() {
        return location != null ? String.valueOf(location.getY()) : null;
    }

    public String getLongitude() {
        return location != null ? String.valueOf(location.getX()) : null;
    }
}
