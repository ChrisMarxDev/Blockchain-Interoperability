# Interoperationsblockchain
Installiere Go und setze die Umgebungsvariable [GOPATH](https://github.com/tendermint/tendermint/wiki/Setting-GOPATH)
Installiere Tendermint [siehe](https://github.com/tendermint/tendermint/wiki/Installation) oder direkt mit 
```bash
go get -u github.com/tendermint/tendermint/cmd/tendermint
```

Stelle sicher, dass die Projekte in folgender Ordnerstruktur angelegt sind, damit die runscripts funktionieren:
```bash
root  
│
└─── cordapp-template-kotlin
│
└─── src

```

Baue die Blockchainknoten initial mit:
```bash
deploynodes
```
Alternativ kann das deepreset-Kommando ausgeführt werden, welches die gesamte Blockchain resetet und Knoten neu baut.
```bash
deepreset
```

Starte alle Knoten der Blockchain mit dem Kommando
```bash
runallnodes
```

Ggf. könnten Fehler entstehen. In diesem Fall sollte ein reset "deepreset" (s.o.) ausgeführt werden und die Knoten neugestartet werden. 

Starte dann die Anwendungen der einzelnen Blockchains:

Für Interoperabilitätsblockchain
```bash
interoperability.Config1
interoperability.Config2
interoperability.Config3
```
Für Blockchain A (Vertragsblockchain)
```bash
BlockchainA.Config1
BlockchainA.Config2
BlockchainA.Config3
```
Für Blockchain B (Preisblockchain)
```bash
com.template.CordaStarterConfig1
com.template.CordaStarterConfig2
```
Nachdem beide Knoten der Blockchain B gestartet wurden, kann der dritte Knoten inklusive der Dummy-Daten gestartet werden:
```bash
com.template.CordaStarterConfig3WithDummyData
```


## Entwicklerlog
### Geplannter Anwendungsverlauf:
+ Empfang von Datenanfrage von Blockchain A
+ Requeste Daten bei beliebigem Knoten von Blockchain B
+ Schreibe Transaktion bestehend aus Anfrage und Antwort in die Blockchain 
+ Andere Knoten validieren, indem sie selber die gleichen Daten bei einem beliebigen Knoten aus Blockchain B anfragen und bilden darüber einen Konsens

### Pseudocode Interoperabilitätsknoten bei Datenanfrage
+ Empfange Datenanfrage
+ Prüfe ob Datenanfrage bereits beantwortet
+ Falls Datenanfrage bereits beantwortet in der lokalen Datenbank (nach Konsensbildung)
  + Bentworte anfrage mit lokalen Daten
  + END
+ Frage Daten bei Knoten der angefragten Blockchain an
+ Bei Empfang, starte Konsensverfahren mit den Daten
+ Nach Konsensverfahren, beantworte Datenanfrage mit den Daten des Konsensverfahrens

### Pseudocode Interoperabilitätsknoten bei Transaktion
+ Erhalte neue zu validierende Transaktion
+ Validiere indem ein Knoten der angefragten Blockchain abgefragt wird
+ Nach konsensbildung, schreibe Interoperationsmapping in lokale Datenbank


### Transaktionsaufbau
Benötigte Felder:
+ Identifier anfragende Blockchain A
+ Identifier angefragte Blockchain B
+ Datenanfrage (ggf. neutrale query language?)
+ Datenantwort
+ Datenquelle (Erstaussteller der Daten bei initialem request) (?)
+ Validitätsdauer der Datenantwort

###Beispielapplikationen
##### BlockchainA
Hält Verträge von Güter-/Dienstleistungsaustausch fest.
Validiert nur Transaktionen bei denen die summierten Preise der Waren übereinstimmen

##### BlockchainB
Verwaltet Preise von Gütern/ Dienstleistungen.

##### Interoperationsszenraio
Blockchain A validiert über Blockchain B ob die Verträge nach aktuellem Preis einen fairen Austausch darstellen.

### Kommunikationsschicht

#### Benötigte Schnittstellen:
  + Eingehende Datenanfragen
  + Gestellte Anfragen auf Daten

## License
[MIT](https://choosealicense.com/licenses/mit/)

## Scratchpad
#### Example commit_tx response
Example broadcast_tx_commit
{
  "jsonrpc": "2.0",
  "id": "",
  "result": {
    "check_tx": {},
    "deliver_tx": {
      "data": "DAABCwABAAAAE0NvbnRyYWN0QmxvY2tjaGFpbkELAAIAAAASUHJpY2luZ0Jsb2NrY2hhaW5CCwADAAAABDYzMTAADAACCwABAAAAAzIwMwAA",
      "info": "\u000c\u0000\u0001\u000b\u0000\u0001\u0000\u0000\u0000\u0013ContractBlockchainA\u000b\u0000\u0002\u0000\u0000\u0000\u0012PricingBlockchainB\u000b\u0000\u0003\u0000\u0000\u0000\u00046310\u0000\u000c\u0000\u0002\u000b\u0000\u0001\u0000\u0000\u0000\u0003203\u0000\u0000"
    },
    "hash": "5E8DAD397EB1EEEA76BBB2940E09F7D98DA5F7445543CDC3506A85ADB81856F6",
    "height": "2"
  }
}

{
  "jsonrpc": "2.0",
  "id": "",
  "result": {
    "check_tx": {
      "data": "MGMwMDAxMGIwMDAxMDAwMDAwMTM0MzZmNmU3NDcyNjE2Mzc0NDI2YzZmNjM2YjYzNjg2MTY5NmU0MTBiMDAwMjAwMDAwMDEyNTA3MjY5NjM2OTZlNjc0MjZjNmY2MzZiNjM2ODYxNjk2ZTQyMGIwMDAzMDAwMDAwMDY2OTc0NjU2ZDMyMzAwMDBjMDAwMjBiMDAwMTAwMDAwMDAyMzEzMDAwMDA="
    },
    "deliver_tx": {
      "data": "MGMwMDAxMGIwMDAxMDAwMDAwMTM0MzZmNmU3NDcyNjE2Mzc0NDI2YzZmNjM2YjYzNjg2MTY5NmU0MTBiMDAwMjAwMDAwMDEyNTA3MjY5NjM2OTZlNjc0MjZjNmY2MzZiNjM2ODYxNjk2ZTQyMGIwMDAzMDAwMDAwMDY2OTc0NjU2ZDMyMzAwMDBjMDAwMjBiMDAwMTAwMDAwMDAyMzEzMDAwMDA="
    },
    "hash": "C076F3B6ED5FB2E654A8FCB7B4EB542B605953713AD75A165A8B522ED519A130",
    "height": "2"
  }
}

Example broadcast_tx_commit error:

{
  "jsonrpc": "2.0",
  "id": "",
  "error": {
    "code": -32603,
    "message": "Internal error",
    "data": "Error on broadcastTxCommit: Tx already exists in cache"
  }
}
