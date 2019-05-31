package com.awolity.trakr.repository.remote.model

import com.awolity.trakr.utils.Constants
import com.google.firebase.firestore.PropertyName

data class PointTimes(
        @get:PropertyName(Constants.DOCUMENT_TIMES)
        @set:PropertyName(Constants.DOCUMENT_TIMES)
        var times: List<Long> = listOf())

