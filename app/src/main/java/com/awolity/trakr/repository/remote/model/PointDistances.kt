package com.awolity.trakr.repository.remote.model

import com.google.firebase.firestore.PropertyName

data class PointDistances(
        @get:PropertyName("pd")
        @set:PropertyName("pd")
        var distances: List<Double> = listOf())

