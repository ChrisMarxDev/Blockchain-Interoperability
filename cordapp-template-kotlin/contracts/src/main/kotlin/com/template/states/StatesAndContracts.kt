package com.template.states

import com.template.contracts.TemplateContract
import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.LedgerTransaction

// ************
// * Contract *
// ************
//class TemplateContract : Contract {
//    companion object {
//        // Used to identify our contract when building a transaction.
//        const val ID = "com.template.states.TemplateContract"
//    }
//
//    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
//    // does not throw an exception.
//    override fun verify(tx: LedgerTransaction) {
//        // Verification logic goes here.
//    }
//
//    // Used to indicate the transaction's intent.
//    interface Commands : CommandData {
//        class Action : Commands
//    }
//}

// *********
// * State *
// *********
@BelongsToContract(TemplateContract::class)
data class PriceState(val price: Int,
                      val itemId: String,
//                      val issuer: Party,
                      val otherParties: List<Party>) : LinearState {
    override val linearId: UniqueIdentifier
        get() = UniqueIdentifier(itemId)
    override val participants: List<AbstractParty> = otherParties
}
