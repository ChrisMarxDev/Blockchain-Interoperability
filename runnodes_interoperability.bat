start "0" cmd /k "tendermint node --consensus.create_empty_blocks=false --home mytestnet\node0"
start "1" cmd /k "tendermint node --consensus.create_empty_blocks=false --home mytestnet\node1"
start "2" cmd /k "tendermint node --consensus.create_empty_blocks=false --home mytestnet\node2"

