package com.awolity.trakr.utils

import com.awolity.trakr.repository.local.model.entity.TrackEntity
import com.awolity.trakr.repository.local.model.entity.TrackWithPoints
import com.awolity.trakr.repository.local.model.entity.TrackpointEntity
import com.awolity.trakr.repository.remote.model.*
import com.awolity.trakr.view.model.MapPoint
import com.awolity.trakr.view.model.TrackData
import com.google.firebase.firestore.GeoPoint

fun trackWithPointsToFirestoreTrackData(input: TrackWithPoints): FirestoreTrackData {
    return FirestoreTrackData(input.firebaseId, input.title, input.startTime, input.distance,
            input.ascent, input.descent, input.elapsedTime, input.numOfTrackPoints,
            input.northestPoint, input.southestPoint, input.westernPoint, input.easternPoint,
            input.minAltitude, input.maxAltitude, input.maxSpeed, input.avgSpeed,
            input.metadata)
}

fun firestorePointsToTrackPointEntities(altitudes: PointAltitudes, distances: PointDistances,
                                        geopoints: PointGeopoints, speeds: PointSpeeds,
                                        times: PointTimes): List<TrackpointEntity> {
    val result = ArrayList<TrackpointEntity>(altitudes.altitudes.size)
    for (i in 0 until altitudes.altitudes.size) {
        val item = TrackpointEntity()
        item.speed = speeds.speeds[i]
        item.altitude = altitudes.altitudes[i]
        item.time = times.times[i]
        item.latitude = geopoints.geoPoints[i].latitude
        item.longitude = geopoints.geoPoints[i].longitude
        item.distance = distances.distances[i]
        result.add(item)
    }
    return result
}

fun firestoreTrackDataToTrackEntity(input: FirestoreTrackData):TrackEntity{
    val result = TrackEntity()
    result.firebaseId = input.firebaseId
    result.title = input.title
    result.startTime = input.startTime
    result.distance = input.distance
    result.ascent = input.ascent
    result.descent = input.descent
    result.elapsedTime = input.elapsedTime
    result.numOfTrackPoints = input.numOfTrackPoints
    result.northestPoint = input.northestPoint
    result.southestPoint = input.southestPoint
    result.westernPoint = input.westernPoint
    result.easternPoint = input.easternPoint
    result.minAltitude = input.minAltitude
    result.maxAltitude = input.maxAltitude
    result.maxSpeed = input.maxSpeed
    result.avgSpeed = input.avgSpeed
    result.metadata = input.metadata
    return result
}

// TODO: ezeket itt alant valahogy generifik'lni

fun trackPointsToSpeedList(input: List<TrackpointEntity>): PointSpeeds {
    val speeds = input.map { it.speed }
    return PointSpeeds(speeds)
}

fun trackPointsToTimesList(input: List<TrackpointEntity>): PointTimes {
    val times = input.map { it.time }
    return PointTimes(times)
}

fun trackPointsToAltitudeList(input: List<TrackpointEntity>): PointAltitudes {
    val altitudes = input.map { it.altitude }
    return PointAltitudes(altitudes)
}

fun trackPointsToGeopointList(input: List<TrackpointEntity>): PointGeopoints {
    val geopoints = input.map { GeoPoint(it.latitude, it.longitude) }
    return PointGeopoints(geopoints)
}

fun trackPointsToDistancesList(input: List<TrackpointEntity>): PointDistances {
    val distances = input.map { it.distance }
    return PointDistances(distances)
}

fun trackEntityToTrackData(input:TrackEntity):TrackData{
    val result = TrackData()
    result.trackId = input.trackId
    result.firebaseId = input.firebaseId
    result.title = input.title
    result.startTime = input.startTime
    result.distance = input.distance
    result.ascent = input.ascent
    result.descent = input.descent
    result.elapsedTime = input.elapsedTime
    result.numOfTrackPoints = input.numOfTrackPoints
    result.northestPoint = input.northestPoint
    result.southestPoint = input.southestPoint
    result.westernPoint = input.westernPoint
    result.easternPoint = input.easternPoint
    result.minAltitude = input.minAltitude
    result.maxAltitude = input.maxAltitude
    result.maxSpeed = input.maxSpeed
    result.avgSpeed = input.avgSpeed
    result.metadata = input.metadata
    return result
}

fun trackPointToMapPoint(input:TrackpointEntity):MapPoint{
    val result = MapPoint()
    result.latitude = input.latitude
    result.longitude = input.longitude
    return result
}
