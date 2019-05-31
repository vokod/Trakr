package com.awolity.trakr.repository.remote.model

import com.awolity.trakr.utils.Constants
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class PointGeopoints(
        @get:PropertyName(Constants.DOCUMENT_GEOPOINTS)
        @set:PropertyName(Constants.DOCUMENT_GEOPOINTS)
        var geoPoints: List<GeoPoint> = listOf())

