package io.wollinger.webgame

import kotlinx.serialization.Serializable

@Serializable
data class Level(
    val name: String,
    val data: List<String>
)