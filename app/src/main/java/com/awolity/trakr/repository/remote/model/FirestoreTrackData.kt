package com.awolity.trakr.repository.remote.model

import com.awolity.trakr.utils.Constants.STRING_DEFAULT_VALUE
import com.google.firebase.firestore.PropertyName

data class FirestoreTrackData(
        @get:PropertyName("id")
        @set:PropertyName("id")
        var firebaseId: String = STRING_DEFAULT_VALUE,
        @get:PropertyName("t")
        @set:PropertyName("t")
        var title: String = STRING_DEFAULT_VALUE,
        @get:PropertyName("ts")
        @set:PropertyName("ts")
        var startTime: Long = 0,
        @get:PropertyName("d")
        @set:PropertyName("d")
        var distance: Double = 0.toDouble(),
        @get:PropertyName("vu")
        @set:PropertyName("vu")
        var ascent: Double = 0.toDouble(),
        @get:PropertyName("vd")
        @set:PropertyName("vd")
        var descent: Double = 0.toDouble(),
        @get:PropertyName("te")
        @set:PropertyName("te")
        var elapsedTime: Long = 0,
        @get:PropertyName("n")
        @set:PropertyName("n")
        var numOfTrackPoints: Long = 0,
        @get:PropertyName("pn")
        @set:PropertyName("pn")
        var northestPoint: Double = 0.toDouble(),
        @get:PropertyName("ps")
        @set:PropertyName("ps")
        var southestPoint: Double = 0.toDouble(),
        @get:PropertyName("pw")
        @set:PropertyName("pw")
        var westernPoint: Double = 0.toDouble(),
        @get:PropertyName("pe")
        @set:PropertyName("pe")
        var easternPoint: Double = 0.toDouble(),
        @get:PropertyName("l")
        @set:PropertyName("l")
        var minAltitude: Double = 0.toDouble(),
        @get:PropertyName("h")
        @set:PropertyName("h")
        var maxAltitude: Double = 0.toDouble(),
        @get:PropertyName("vm")
        @set:PropertyName("vm")
        var maxSpeed: Double = 0.toDouble(),
        @get:PropertyName("va")
        @set:PropertyName("va")
        var avgSpeed: Double = 0.toDouble(),
        @get:PropertyName("m")
        @set:PropertyName("m")
        var metadata: String = STRING_DEFAULT_VALUE)
