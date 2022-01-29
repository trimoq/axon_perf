package dev.amann.synapse.service

import dev.amann.synapse.domain.IssueCardCommand
import dev.amann.synapse.domain.ReusableAggregate
import dev.amann.synapse.domain.UseCardCommand
import dev.amann.synapse.repo.ReusableAggregateRepo
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.io.FileWriter
import java.io.Writer
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread
import kotlin.math.pow
import kotlin.system.measureTimeMillis


@Service
class AggregateCreationBenchmarkService(
        private val commandGateway: CommandGateway,
        private val meterRegistry: MeterRegistry,
        ) {

    lateinit var aggregateCounter: Counter;

    data class ThreadResult(
        val id: Int,
        val batchNum: Int,
        val time: Long
    )
    fun runParallelAggregateCreationBenchmark() {

        aggregateCounter = meterRegistry.counter("aggregate_counter");


        val warmupAggregates = 1_000;
        val totalAggregates = 500_000;
        val timeMeasureBatchSize = 1000;
        val numThreads = 12;

        // warmup phase to allow for JVM optimization
        createCards(warmupAggregates, aggregateCounter)

        val writer: Writer = FileWriter("threadedAggregateCreationBenchmark.csv")
        var printer = CSVPrinter(writer, CSVFormat.DEFAULT)
        println("start benchmark");

        val results = ConcurrentLinkedQueue<ThreadResult>();

        var handles = mutableListOf<Thread>()
        for ( t in 1 .. numThreads) {
            val handle = thread {
                for (i in 0..totalAggregates step timeMeasureBatchSize) {
                    println("Running batch #$i")
                    val time = measureTimeMillis {
                        createCards(timeMeasureBatchSize,aggregateCounter)
                    }
                    results.add(ThreadResult(t,i,time))
                }
            }
            handles.add(handle)
        }

        for (handle in handles){
            handle.join();
        }

        for(res in results){
            persistResult(printer, res.id, res.batchNum,  timeMeasureBatchSize, res.time)
        }
        printer.close(true);
        println("Finished benchmark");

    }

    fun runAggregateCreationBenchmark() {

        aggregateCounter = meterRegistry.counter("aggregate_counter");

		val warmupAggregates = 1_000;
		val totalAggregates = 100_000_000;
        val timeMeasureBatchSize = 1000;


		// warmup phase to allow for JVM optimization
		createCards(warmupAggregates, aggregateCounter)

        val writer: Writer = FileWriter("speedyAggregateCreationBenchmark.csv")
        var printer = CSVPrinter(writer, CSVFormat.DEFAULT)
        println("start benchmark");

        for (i in 0..totalAggregates step timeMeasureBatchSize){
            println("Running batch #$i")
            val time = measureTimeMillis{
				createCards(timeMeasureBatchSize, aggregateCounter)
			}
            persistResult(printer, i,  timeMeasureBatchSize, time)
        }

        printer.close(true);
        println("Finished benchmark");

	}

    fun createCards(count: Int ,ctr: Counter){
        for (i in 0..count) {
            commandGateway.send<Unit>(IssueCardCommand(UUID.randomUUID(), 1000000000))
            ctr.increment()
        }
    }

    /**
     * iterations this sample is the sum of
     */
    fun persistResult(printer: CSVPrinter,batchNumber: Int, batchSize: Int, millis: Long){
        printer.printRecord(
                batchNumber,
                batchSize,
                millis
        )
        printer.flush();
    }

    fun persistResult(printer: CSVPrinter,threadId: Int, batchNumber: Int, batchSize: Int, millis: Long){
        printer.printRecord(
                threadId,
                batchNumber,
                batchSize,
                millis
        )
        printer.flush();
    }


}