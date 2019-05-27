package com.awolity.trakr.repository.remote.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class PointGeopoints(
        @get:PropertyName("pp")
        @set:PropertyName("pp")
        var geoPoints: List<GeoPoint> = listOf())

