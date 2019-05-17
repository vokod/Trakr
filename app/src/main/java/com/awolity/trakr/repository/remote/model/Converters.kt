package com.awolity.trakr.repository.remote.model

import com.awolity.trakr.data.entity.TrackEntity
import com.awolity.trakr.data.entity.TrackWithPoints
import com.awolity.trakr.data.entity.TrackpointEntity
import com.awolity.trakr.utils.MyLog
import com.google.firebase.firestore.GeoPoint

fun trackWithPointsToFirestoreTrack(input: TrackWithPoints): FirestoreTrack {
    return FirestoreTrack(input.firebaseId, input.title, input.startTime, input.distance,
            input.ascent, input.descent, input.elapsedTime, input.numOfTrackPoints,
            input.northestPoint, input.southestPoint, input.westernPoint, input.easternPoint,
            input.minAltitude, input.maxAltitude, input.maxSpeed, input.avgSpeed,
            input.metadata,
            trackPointsToTimesArray(input.trackPoints),
            trackPointsToGeopointArray(input.trackPoints),
            trackPointsToSpeedArray(input.trackPoints),
            trackPointsToAltitudeArray(input.trackPoints),
            trackPointsToDistanceArray(input.trackPoints))
}

fun trackEntityToFirestoreTrack(input: TrackEntity): FirestoreTrack {
    return FirestoreTrack(input.firebaseId, input.title, input.startTime, input.distance,
            input.ascent, input.descent, input.elapsedTime, input.numOfTrackPoints,
            input.northestPoint, input.southestPoint, input.westernPoint, input.easternPoint,
            input.minAltitude, input.maxAltitude, input.maxSpeed, input.avgSpeed,
            input.metadata)
}

private fun trackPointsToSpeedArray(input: List<TrackpointEntity>): List<Double> {
    return input.map { it.speed }
}

private fun trackPointsToTimesArray(input: List<TrackpointEntity>): List<Long> {
    return input.map { it.time }
}

private fun trackPointsToAltitudeArray(input: List<TrackpointEntity>): List<Double> {
    return input.map { it.altitude }
}

private fun trackPointsToGeopointArray(input: List<TrackpointEntity>): List<GeoPoint> {
    return input.map { GeoPoint(it.latitude, it.longitude) }
}

private fun trackPointsToDistanceArray(input: List<TrackpointEntity>): List<Double> {
    return input.map { it.distance }
}

fun firestoreTrackToTrackWithPoints(input: FirestoreTrack): TrackWithPoints {
    val result = TrackWithPoints()
    result.trackEntity = firestoreTrackToTrackEntity(input)
    result.trackPoints = firestoreTrackToTrackPointEntities(input)
    return result
}

fun firestoreTrackToTrackEntity(input: FirestoreTrack): TrackEntity {
    val result = TrackWithPoints()
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

private fun firestoreTrackToTrackPointEntities(input: FirestoreTrack): List<TrackpointEntity> {
    MyLog.d(TAG, "firestoreTrackToTrackPointEntities")
    val result = ArrayList<TrackpointEntity>(input.pointAltitudes.size)
    for (i in 0 until input.pointAltitudes.size) {
        val item = TrackpointEntity()
        item.speed = input.pointSpeeds[i]
        item.altitude = input.pointAltitudes[i]
        item.time = input.pointTimes[i]
        item.latitude = input.pointGeoPoints[i].latitude
        item.longitude = input.pointGeoPoints[i].longitude
        item.distance = input.pointDistances[i]
        result.add(item)
    }
    return result
}

const val TAG = "ConvertersKT"