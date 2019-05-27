package com.awolity.trakr.repository.remote.model

import com.google.firebase.firestore.PropertyName

data class PointTimes(
        @get:PropertyName("pd")
        @set:PropertyName("pd")
        var times: List<Long> = listOf())

