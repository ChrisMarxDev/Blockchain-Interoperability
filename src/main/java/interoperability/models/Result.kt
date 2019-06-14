package interoperability.models

data class Result(
        val check_tx: CheckTx,
        val deliver_tx: DeliverTx,
        val hash: String,
        val height: String
)