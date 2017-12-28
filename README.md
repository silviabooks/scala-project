# Scala Rest APIs with Akka framework 
## An example of a box office tickets service
## University of Catania - DIEEI
###  Advanced Programming Languages
#### Silvia Sottile - Alessandro Di Stefano
L'applicazione gestisce la prenotazione e l’acquisto online di biglietti per qualsiasi tipo di evento: concerti, spettacoli teatrali ecc. E' implementata su architettura RESTful utilizzando JSON come formato standard dei messaggi per la comunicazione client-server.

Il backend è realizzato in linguaggio Scala, in particolare utilizzando il framework Akka per implementare il modello ad attori e la libreria Akka-HTTP per implementare le chiamare REST.

La persistenza è implementata con database noSql MongoDB, l'interfaccia grafica in AngularJS/Material: quest'ultima scelta permette di aver un client portabile dell'applicazione, come web app o, integrato con framework come Ionic, come mobile app.

L'applicazione, infine, si comporta da Broker di eventi verso i client connessi attraverso una webSocket: ogni evento pubblicato da un *admin* (Publisher) viene notificato ai client (Subscribers).

- [Deployment/Docker](https://github.com/silviabooks/scala-project#deployment-e-docker)
- [Mocked Data](https://github.com/silviabooks/scala-project#seeds-per-demo)
- [Interfaccia REST](https://github.com/silviabooks/scala-project#interfaccia-alle-api-rest)
- [Documentazione & Demo Online](https://github.com/silviabooks/scala-project#documentazione-e-demo)
#### Deployment e Docker

Il deploy puo' essere effettuato, out-of-the-box, con **[Docker](https://docs.docker.com/docker-for-mac/install/)** e **[Docker Compose](https://docs.docker.com/compose/install/)**:

```bash
$ cd docker/
$ docker-compose up
```

Il deploy con Docker espone i seguenti servizi/porte attraverso port forwarding su localhost:

- APIs (Backend Scala): http://localhost:8080
- UI AngularJS: http://localhost(:80)/
- MongoDB: mongodb://localhost

*Su sistemi \*nix fare attenzione alle configurazioni dei permessi (SELinux e group membership dell'utente).*

Il backend dipende da un host che ospita MongoDB per poter gestire la persistenza; il binding dell'host e' attualmente hard-coded a `mongodb://mongo`. Pertanto e' necessario che il nodo che esegue il backend possa raggiungere un database mongoDB all'indirizzo `mongo`. Se si vuole eseguire localmente MongoDB e' possibile modificare il codice dell'oggetto BoxOffice o aggiungere su `/etc/hosts` la riga

```
127.0.0.1 mongo
```

Infine, per il frontend, e' necessario avere a disposizione un web server capace di fornire le pagine html su public_html/.

In particolare, per l'esecuzione locale, e' possibile importare il progetto .iml su IntelliJ IDEA o usare sbt (Scala version: 2.11.6)

#### Seeds per Demo

Per popolare il database con dati mock, è possibile avviare lo script `src/devTests/MongoSeeds.scala`. 

Dopo aver eseguito il deploy con Docker eseguire:

```bash
 $ cd docker/
 $ docker exec -it $(docker-compose ps -q advproject_scala) sbt "runMain devTests.MongoSeed"
```

Come sopra, fare attenzione ai permessi e a SELinux.

#### Interfaccia alle API Rest

Qualunque client http puo' essere utilizzato per interfacciarsi con il servizio. ([Postman](https://www.getpostman.com/), curl, browsers...).


#### Documentazione e demo

La documentazione del codice e' disponibile [qui](http://silviabooks.github.io/scala-project).

La documentazione dettagliata delle API rest su [/apidocs](https://github.com/silviabooks/scala-project/tree/master/apidocs).

Una versione demo dell'applicazione e' disponibile [qui](http://aleskandro.lucylaika.ovh): espone, come descritto [sopra](deployment-e-docker) i due servizi backend (:8080) e web frontend (:80).

**NOTA**: Il database viene ripristinato allo stato iniziale e ripopolato con i [dati Mock](https://github.com/silviabooks/scala-project#seeds-per-demo) ogni ora.

Demo admins:
- aleskandro@scala
- silvia@scala

Demo users:
- pippo@scala
- pluto@scala

*Usare una **password** a caso*

Di seguito si riporta una sintesi delle API Rest disponibili.

| URL                    |  GET  |  POST  |  PUT  | DELETE |
| ---------------------- | ----- | ------ | ----- | ------ |
| /events                | Restituisce l'elenco di tutti gli eventi disponibili | Creazione di un nuovo evento (*)    | | |
| /events/(:id)          | Restituisce l’evento (:id)     | / | Modifica l'evento (:id) (*) | Elimina l'evento (:id) (*)                                |
| /events/search/(:query)| Ricerca degli eventi con pattern matching su (:query)                              | / | / | / |
| /events/categories     | Lista delle categorie di eventi disponibili                                        | / | / | / |
| /users                 | Restituisce l'elenco di tutti gli utenti (*)             | Creazione di un nuovo utente    | / | / |
| /users/(:id)           | Restituisce l’utente (:id) (*)     | / | Modifica l'utente (*) | Elimina l'utente (:id)                                |
| /tickets               | Restituisce l'elenco dei biglietti acquistati (*)            | Creazione di un nuovo biglietto (*) | / | / |
| /tickets/(:id)         | Restituisce il biglietto (:id) (*) | / | / | Eliminazione del biglietto (:id) (*)                      | 

Su `ws://backend:8080/ws` e' infine disponibile la WebSocket per la notifica degli eventi (Pubblish-Subscribe <=> Admin-Users)

I metodi marcati con *(\*)* richiedono autenticazione; alcuni di questi si comportano diversamente in funzione dei diritti dell'utente autenticato (admin/user). Fare riferimento alle API su  [/apidocs](https://github.com/silviabooks/scala-project/tree/master/apidocs)

In particolare l'autenticazione e' stata implementata per semplicita' attraverso HTTP Basic Authentication: pertanto l'applicazione non ha assolutamente alcun meccanismo "vero" di hardening.
