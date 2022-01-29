package dev.amann.synapse.repo

import dev.amann.synapse.domain.ReusableAggregate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ReusableAggregateRepo : JpaRepository<ReusableAggregate, Long> {

}

