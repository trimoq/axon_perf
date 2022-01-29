package dev.amann.synapse

import dev.amann.synapse.domain.IssueCardCommand
import dev.amann.synapse.domain.ReusableAggregate
import dev.amann.synapse.domain.UseCardCommand
import dev.amann.synapse.repo.ReusableAggregateRepo
import dev.amann.synapse.service.AggregateCreationBenchmarkService
import dev.amann.synapse.service.AggregateUpdateBenchmarkService
import dev.amann.synapse.service.ReusableAggregateService
import io.micrometer.core.instrument.Counter
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.io.FileWriter
import java.io.Writer
import java.util.*
import kotlin.math.log
import kotlin.math.pow
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis


@SpringBootApplication
class SynapseApplication

fun main(args: Array<String>) {
	runApplication<SynapseApplication>(*args)
}

@Component
class AppStartupRunner (
	private val aggregateUpdateBenchmarkService: AggregateUpdateBenchmarkService,
	private val aggregateCreationBenchmarkService: AggregateCreationBenchmarkService
) : ApplicationRunner {

	@Throws(Exception::class)
	override fun run(args: ApplicationArguments) {

//		Thread.sleep(5_000)
		//aggregateUpdateBenchmarkService.runAggregateUpdateBenchmark();
		aggregateCreationBenchmarkService.runAggregateCreationBenchmark()
//		aggregateCreationBenchmarkService.runParallelAggregateCreationBenchmark()
		exitProcess(0)

	}

}