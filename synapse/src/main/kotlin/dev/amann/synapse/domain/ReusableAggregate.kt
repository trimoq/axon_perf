package dev.amann.synapse.domain

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ReusableAggregate(
        @Id
        val uuid: UUID,
        val eventsInAggregate: Long
)
