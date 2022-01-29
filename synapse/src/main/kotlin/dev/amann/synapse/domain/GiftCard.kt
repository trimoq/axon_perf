package dev.amann.synapse.domain

import dev.amann.synapse.AppStartupRunner
import io.micrometer.core.instrument.Metrics
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
class GiftCard{

    @AggregateIdentifier
    private var id: UUID? = null
    private var value: Int = 0


    constructor(){}

    @CommandHandler
    constructor(command: IssueCardCommand) {
            apply(
                CardIssuedEvent(
                    cardId = command.cardId,
                    amount = command.amount
                )
        )
    }

    @CommandHandler
    fun handleCardUse(command: UseCardCommand){
        if (this.value>=command.amount){
            apply(
                CardUsedEvent(
                    cardId = command.cardId,
                    amount = command.amount
                )
            )
        }
        else{
        }
    }


    @EventSourcingHandler
    fun on(event: CardIssuedEvent) {
        id = event.cardId
        value = event.amount

    }

    @EventSourcingHandler
    fun onUse(event: CardUsedEvent) {
        id = event.cardId
        value -= event.amount
    }
}
