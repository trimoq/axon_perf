package dev.amann.synapse.service

import dev.amann.synapse.domain.ReusableAggregate
import dev.amann.synapse.domain.UseCardCommand
import dev.amann.synapse.repo.ReusableAggregateRepo
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.io.FileWriter
import java.io.Writer
import java.util.*
import kotlin.math.pow
import kotlin.system.measureTimeMillis

/**
 * Creates a bunch of reuseable aggregates with a specified of events already existing in them.
 * Afterwards, the time needed to source and update the aggregate is measured and logged.
 */
@Service
class AggregateUpdateBenchmarkService(
        private val commandGateway: CommandGateway,
        private val reusableAggregateService: ReusableAggregateService,
        private val reusableAggregateRepo: ReusableAggregateRepo
) {

    fun runAggregateUpdateBenchmark() {

        println("start setup");
        setupDatabase()
        println("Finished setup");

        val aggregates = reusableAggregateRepo.findAll();
        val writer: Writer = FileWriter("aggregateUpdateBenchmark.csv")
        var printer = CSVPrinter(writer, CSVFormat.DEFAULT)
        println("start benchmark");

        for(aggregate in aggregates){
            val iterations = 5L;
            val timings = useCard(iterations, aggregate.uuid)
            for(time in timings) {
                persistResult(printer, aggregate, 1, time)
            }
        }

        printer.close(true);
        println("finsihed benchmark");


    }



    /**
     * iterations this sample is the sum of
     */
    fun persistResult(printer: CSVPrinter, reusableAggregate: ReusableAggregate, iterations: Long, millis: Long){
        printer.printRecord(
                reusableAggregate.uuid,
                reusableAggregate.eventsInAggregate,
                iterations,
                millis
        )
        printer.flush();
    }

    fun useCard(count: Long, uuid: UUID): List<Long>{
        var timings = mutableListOf<Long>()
        for (i in 0..count) {
            val time = measureTimeMillis {
                commandGateway.send<Unit>(UseCardCommand(uuid, 1))
            }
            timings.add(time)
        }
        return timings
    }

    private fun setupDatabase() {
        var sizeList = mutableListOf<Long>()
        for (exponent in 1..4) {
            for (i in 1..9) {
                sizeList.add(10L.pow(exponent) * i)
            }
        }

        reusableAggregateService.createReusableAggregates(
                2,
                sizeList.toLongArray()
        )

        println("Created aggregates")
    }

    fun Long.pow(exponent: Int): Long = toDouble().pow(exponent).toLong()

}