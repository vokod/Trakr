package com.awolity.trakr.repository.remote.model

import com.google.firebase.firestore.PropertyName

data class PointAltitudes(
        @get:PropertyName("pd")
        @set:PropertyName("pd")
        var altitudes: List<Double> = listOf())

