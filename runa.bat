start "blockchain_a_testnet 0" cmd /k "tendermint node --consensus.create_empty_blocks=false --home blockchain_a_testnet\node0"
start "blockchain_a_testnet 1" cmd /k "tendermint node --consensus.create_empty_blocks=false --home blockchain_a_testnet\node1"
start "blockchain_a_testnet 2" cmd /k "tendermint node --consensus.create_empty_blocks=false --home blockchain_a_testnet\node2"