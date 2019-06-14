namespace java generated.application.thrift

struct ProductPricing {
  1: string productIdentifier,
  2: double price,
}


struct Contract {
  1:string initiator,
  2:string receiver,
  3: map<string,double> offeredGoods,
  4: map<string,double> receivedGoods,
}