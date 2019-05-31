package com.awolity.trakr.repository.remote.model

import com.awolity.trakr.utils.Constants
import com.google.firebase.firestore.PropertyName

data class PointAltitudes(
        @get:PropertyName(Constants.DOCUMENT_ALTITUDES)
        @set:PropertyName(Constants.DOCUMENT_ALTITUDES)
        var altitudes: List<Double> = listOf())

