const val BLOCKCHAIN_A_ID = "ContractBlockchainA"
const val BLOCKCHAIN_B_ID = "PricingBlockchainB"
const val BLOCKCHAIN_ORACLE = "OracleBlockchain"

val INTEROP_DATA_SERVER_PORTS : Array<Int> = arrayOf(9010, 9020, 9030)
val BLOCKCHAIN_A_ABCI_PORTS = arrayOf("24658", "24668", "24678")
val BLOCKCHAIN_A_TX_BROADCAST_PORTS = arrayOf(24657, 24667, 24677)
val BLOCKCHAIN_B_SERVER_PORTS = arrayOf(4015, 4025, 4035)
val INTEROPERABILITY_ABCI_PORTS = arrayOf(26658, 26668, 26678)
val INTEROPERABILITY_BROADCAST_TRANSACTIONS_PORTS = arrayOf(26657, 26667, 26677)


// DUMMY DATA
val DUMMY_PRODUCTS = mapOf<String, Double>(
        "Electricity" to 2.0,
        "Water" to 2.5,
        "Apples" to 1.0
)
