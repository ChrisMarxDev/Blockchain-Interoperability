namespace java generated.visualization.thrift


struct BcNode {
  1: string name,
  2: string typeIdentifier,
}


struct VMessage {
  1:BcNode sender,
  2:BcNode receiver,
  3:string content
}

struct VEvent {
  1:BcNode node,
  2:bool valid,
}

struct Empty{

}

service VisualizationInterface{
   Empty visualizationMessage(1:VMessage vmessage),
   Empty visualizationEvent(1:VEvent vevent)
}
