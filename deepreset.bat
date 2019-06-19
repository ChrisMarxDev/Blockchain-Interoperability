start "killjava" cmd /c "taskkill /im java.exe /F"

start "deploy" cmd /c "deployNodes"

start "0" cmd /c "%cd%\tendermint unsafe_reset_all --home mytestnet\node0"
start "1" cmd /c "%cd%\tendermint unsafe_reset_all --home mytestnet\node1"
start "2" cmd /c "%cd%\tendermint unsafe_reset_all --home mytestnet\node2"
start "3" cmd /c "%cd%\tendermint unsafe_reset_all --home mytestnet\node3"

start "blockchain_a_testnet 0" cmd /c "%cd%\tendermint unsafe_reset_all --home blockchain_a_testnet\node0"
start "blockchain_a_testnet 1" cmd /c "%cd%\tendermint unsafe_reset_all --home blockchain_a_testnet\node1"
start "blockchain_a_testnet 2" cmd /c "%cd%\tendermint unsafe_reset_all --home blockchain_a_testnet\node2"

