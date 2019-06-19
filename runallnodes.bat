start "blockchain_a_testnet 0" cmd /k "%cd%\tendermint node --consensus.create_empty_blocks=false --home blockchain_a_testnet\node0"
start "blockchain_a_testnet 1" cmd /k "%cd%\tendermint node --consensus.create_empty_blocks=false --home blockchain_a_testnet\node1"
start "blockchain_a_testnet 2" cmd /k "%cd%\tendermint node --consensus.create_empty_blocks=false --home blockchain_a_testnet\node2"

start "0" cmd /k "%cd%\tendermint node --consensus.create_empty_blocks=false --home mytestnet\node0"
start "1" cmd /k "%cd%\tendermint node --consensus.create_empty_blocks=false --home mytestnet\node1"
start "2" cmd /k "%cd%\tendermint node --consensus.create_empty_blocks=false --home mytestnet\node2"
start "3" cmd /k "%cd%\tendermint node --consensus.create_empty_blocks=false --home mytestnet\node3"

start "0" cmd /c "cd cordapp-template-kotlin\build\nodes & runnodes"