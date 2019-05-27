package com.awolity.trakr.repository.remote.model

import com.google.firebase.firestore.PropertyName

data class PointSpeeds(
        @get:PropertyName("pd")
        @set:PropertyName("pd")
        var speeds: List<Double> = listOf())

