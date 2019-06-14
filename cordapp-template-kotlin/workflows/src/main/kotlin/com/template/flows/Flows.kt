package com.template.flows


import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.TemplateContract
import com.template.states.PriceState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker


// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class PriceFlow(val price: Int,
                val itemId: String) : FlowLogic<Unit>() {


    /** The progress tracker provides checkpoints indicating the progress of the flow to observers. */
    override val progressTracker = ProgressTracker()

    /** The flow logic is encapsulated within the call() method. */
    @Suspendable
    override fun call() {
        // We retrieve the notary identity from the network map.
        val notary = serviceHub.networkMapCache.notaryIdentities[0]


        val allNodes = serviceHub.networkMapCache.allNodes
        val allOtherParties = allNodes.map {
            serviceHub.identityService.wellKnownPartyFromX500Name(it.legalIdentities[0].name)
        }.filterNotNull().filter { !it.owningKey.equals(ourIdentity.owningKey) }


        // We create the transaction components.
        val outputState = PriceState(price, itemId, allOtherParties)
        val command = Command(TemplateContract.Commands.Action(), ourIdentity.owningKey)

        // We create a transaction builder and add the components.
        val txBuilder = TransactionBuilder(notary = notary)
                .addOutputState(outputState, TemplateContract.ID)
                .addCommand(command)

        // We sign the transaction.
        val signedTx = serviceHub.signInitialTransaction(txBuilder)

        // Creating a session with the other party.
        val sessions = allOtherParties.map { initiateFlow(it) }
        //val otherPartySession = initiateFlow(allParties)


        // We finalise the transaction and then send it to the counterparty.
        subFlow(FinalityFlow(signedTx, sessions))
    }
    ///** The progress tracker provides checkpoints indicating the progress of the flow to observers. */
    //override val progressTracker = ProgressTracker()

    ///** The flow logic is encapsulated within the call() method. */
    //@Suspendable
    //override fun call() {
    //    // We retrieve the notary identity from the network map.
    //    val notary = serviceHub.networkMapCache.notaryIdentities[0]

    //    val allNodes = serviceHub.networkMapCache.allNodes
    //    val allParties = allNodes.map {
    //        serviceHub.identityService.wellKnownPartyFromX500Name(it.legalIdentities[0].name)
    //    }.filterNotNull()


    //    // We create the transaction components.
    //    val outputState = PriceState(price, itemId, allParties)
    //    val command = Command(TemplateContract.Commands.Action(), ourIdentity.owningKey)

    //    // We create a transaction builder and add the components.
    //    val txBuilder = TransactionBuilder(notary = notary)
    //            .addOutputState(outputState, TemplateContract.ID)
    //            .addCommand(command)


    //    // We sign the transaction.
    //    val signedTx = serviceHub.signInitialTransaction(txBuilder)

    //    // For each non-local participant in the transaction we must initiate a flow session with them.
    //    //  val session = initiateFlow(allParties[0])

    //    // We finalise the transaction.
    //    subFlow(FinalityFlow(signedTx, emptyList()))
    // }
}

@InitiatedBy(PriceFlow::class)
class PriceResponder(private val otherSide: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(ReceiveFinalityFlow(otherSide))
    }
}

@InitiatingFlow
@StartableByRPC
class Initiator : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        // Initiator flow logic goes here.
    }
}

@InitiatedBy(Initiator::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Responder flow logic goes here.
    }
}
