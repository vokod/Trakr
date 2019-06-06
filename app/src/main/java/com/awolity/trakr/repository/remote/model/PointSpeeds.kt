package com.awolity.trakr.repository.remote.model

import com.awolity.trakr.utils.Constants
import com.google.firebase.firestore.PropertyName

data class PointSpeeds(
        @get:PropertyName(Constants.DOCUMENT_SPEEDS)
        @set:PropertyName(Constants.DOCUMENT_SPEEDS)
        var speeds: List<Double> = listOf())

