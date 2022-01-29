package dev.amann.synapse.domain

import org.axonframework.commandhandling.RoutingKey
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*


data class IssueCardCommand(
    @TargetAggregateIdentifier
    val cardId: UUID,
    val amount: Int
)
data class UseCardCommand(
    @TargetAggregateIdentifier
    val cardId: UUID,
    val amount: Int
)