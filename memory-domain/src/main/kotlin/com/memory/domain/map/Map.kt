package com.memory.domain.map

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import jakarta.persistence.*
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel

@Entity
@Table(name = "map")
class Map(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,

    var description: String?,

    var address: String?,

    @Column(columnDefinition = "geometry(Point,4326)")
    var location: Point,

    @Enumerated(EnumType.STRING)
    var mapType: MapType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member
) : BaseTimeEntity() {

    constructor(name: String, description: String?, address: String?, latitude: String?, longitude: String?, mapType: MapType, member: Member) : this(
        id = null,
        name = name,
        description = description,
        address = address,
        location = createPoint(longitude?.toDouble() ?: 0.0, latitude?.toDouble() ?: 0.0),
        mapType = mapType,
        member = member
    )

    companion object {
        private val GEOMETRY_FACTORY = GeometryFactory(PrecisionModel(), 4326)

        fun createPoint(longitude: Double, latitude: Double): Point {
            return GEOMETRY_FACTORY.createPoint(Coordinate(longitude, latitude))
        }
    }

    val latitude: String?
        get() = location.y.toString()

    val longitude: String?
        get() = location.x.toString()
}
