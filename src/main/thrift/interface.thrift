namespace java generated.interoperability.thrift

enum MessageType{
  INTEROPERABILITY_TRANSACTION,
  ADDRESS_COLLECTION,
  BLOCKCHAIN_REGISTRATION
}

struct DataRequest {
  1: string requestingChain,
  2: required string requestedChain,
  3: required string dataQuery,
//  4: binary targetPublicKey,

}

struct DataResponse {
  1: required binary responseData,
  2: optional i64 validityTime,
// 3: binary publicKey,
// 4: binary signature,
}

struct InteroperabilityTransaction
{
  1: DataRequest dataRequest,
  2: DataResponse dataResponse
}

struct Address{
1: string address,
2: i32 port,
3: string identifier,
}

struct AddressCollection
{
  1: map<string,list<Address>> identifierAddressMap
}

struct Empty{

}

struct Valid{
1: bool valid,
}

struct WrapperTransaction{
 1: required MessageType type,
 2: optional binary signature,
 3: optional binary key,
 4: optional InteroperabilityTransaction interoperabilityTransaction,
 5: optional AddressCollection addressMessage
}

struct InterfaceDefinitionMessage{
 1: required InterfaceDefinition definition,
2: required string blockchainId,
}

struct InterfaceDefinition{

}

service DataInterface{
   DataResponse queryData(1:DataRequest dataRequest),
}

service OracleDataInterface {
   DataResponse queryData(1:DataRequest request),
   Valid registerNodes(1:AddressCollection adresses),
   Empty interfaceDefinitions(1:InterfaceDefinitionMessage definition),
      InterfaceDefinition getInterfaceDefinition(1: string id),

}

