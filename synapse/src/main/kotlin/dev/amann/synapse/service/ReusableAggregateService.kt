package dev.amann.synapse.service

import com.fasterxml.jackson.databind.ObjectMapper
import dev.amann.synapse.domain.CardUsedEvent
import dev.amann.synapse.domain.IssueCardCommand
import dev.amann.synapse.domain.ReusableAggregate
import dev.amann.synapse.repo.ReusableAggregateRepo
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.common.jpa.EntityManagerProvider
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.eventhandling.*
import org.axonframework.eventsourcing.eventstore.jpa.DomainEventEntry
import org.axonframework.messaging.MetaData
import org.axonframework.serialization.AnnotationRevisionResolver
import org.axonframework.serialization.json.JacksonSerializer
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReusableAggregateService (
        val commandGateway: CommandGateway,
        val entityManagerProvider: EntityManagerProvider,
        val transactionManager: TransactionManager,
        val objectMapper: ObjectMapper,
        val reusableAggregateRepo: ReusableAggregateRepo
){


    fun insertCreationEvent(aggregateId: UUID){
        commandGateway.send<Unit>(IssueCardCommand(aggregateId, 10000000))
    }

    fun insertUpdateEvents(aggregateId: UUID, countExclusive: Long ){

        val serializer = JacksonSerializer.builder()
                .revisionResolver(AnnotationRevisionResolver())
                .objectMapper(objectMapper)
                .build()


        var entityManager = entityManagerProvider.getEntityManager();

        var events = mutableListOf<GenericDomainEventMessage<CardUsedEvent>>();
        // start with 1, since we already have a creation event
        for (i in 1..countExclusive){
            val event = CardUsedEvent(
                    cardId = aggregateId,
                    amount = 10
            )
            val message =  GenericDomainEventMessage("GiftCard", aggregateId.toString(), i, event, MetaData.emptyInstance())
            events.add(message)
        }

        transactionManager.executeInTransaction {
            events.stream()
                .map { event -> DomainEventEntry(event, serializer) }
                .forEach { o -> entityManager.persist(o) }
        }

    }


    fun createReusableAggregates(numberPerSize: Long, sizes: LongArray) {

        for(size in sizes){
            for(i in 0 .. numberPerSize){
                val id = UUID.randomUUID()
                insertCreationEvent(id)
                insertUpdateEvents(id,size)
                reusableAggregateRepo.save(ReusableAggregate(id,size));
                println("saved agg $i for size $size")
            }
        }

    }

}