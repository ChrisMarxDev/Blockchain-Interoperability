package interoperability.models

data class TxCommitResponse(
        val id: String,
        val jsonrpc: String,
        val result: Result
)