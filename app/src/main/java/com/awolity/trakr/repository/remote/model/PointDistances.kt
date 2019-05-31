package com.awolity.trakr.repository.remote.model

import com.awolity.trakr.utils.Constants
import com.google.firebase.firestore.PropertyName

data class PointDistances(
        @get:PropertyName(Constants.DOCUMENT_DISTANCES)
        @set:PropertyName(Constants.DOCUMENT_DISTANCES)
        var distances: List<Double> = listOf())

