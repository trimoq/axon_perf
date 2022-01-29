package dev.amann.synapse.domain

import java.util.*


data class CardIssuedEvent(
    val cardId: UUID,
    val amount: Int
    )

data class CardUsedEvent(
    val cardId: UUID,
    val amount: Int
)