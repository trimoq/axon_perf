package dev.amann.synapse.web

import dev.amann.synapse.domain.IssueCardCommand
import dev.amann.synapse.domain.UseCardCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/")
class CardResource(
    private val commandGateway: CommandGateway
) {

    @GetMapping("/issue/{id}")
    fun issueCard(@PathVariable id: UUID){
        commandGateway.send<Unit>(IssueCardCommand(id, 100))
    }

    @GetMapping("/use/{id}")
    fun useCard(@PathVariable id: UUID){
        commandGateway.send<Unit>(UseCardCommand(id, 43))
    }

}