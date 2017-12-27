# Scala Rest APIs with Akka framework - An example of a box office tickets service
## University of Catania - DIEEI
###  Advanced Programming Languages
Quest'applicazione gestisce la prenotazione e l’acquisto online di biglietti per qualsiasi tipo di evento: concerti, spettacoli teatrali ecc. Essa è implementata su architettura RESTful utilizzando JSON come formato standard dei messaggi per la comunicazione client-server.

Il backend è realizzato in linguaggio Scala, in particolare utilizzando il framework Akka per implementare il modello ad attori e la libreria Akka-HTTP per implementare le chiamare REST.

La persistenza è implementata con database non relazionale MongoDB, mentre l'interfaccia grafica è implementata con AngularJS.

#### Avvio
Per avviare l'applicazione, sono nececessari i seguenti sotfware:
* **[Docker](https://docs.docker.com/docker-for-mac/install/)** e **[Docker Compose](https://docs.docker.com/compose/install/)**, per avviare i container con l'applicazione, il server Apache e il database MongoDB ();
* **[Postman](https://www.getpostman.com/)**, o qualunque altra applicazione per effettuare chiamate HTTP per testare le API.

*ora come ora non mi viene altro in mente, credo che manco scala si debba installare*

Per avviare l'applicazione bisogna, dopo aver clonato il repository in locale, posizionarsi nella cartella ./scala-project/docker e dare il comando `sudo docker-compose up`. Verranno scaricate quindi le immagini dei container necessari (l'operazione potrebbe richiedere alcuni minuti).

Per popolare il database, è possibile avviare lo script /src/devTests/MongoSeeds.scala. 

*Come si avvia lo script ora che l'applicazione sta su Docker?*


#### API implementate nella GUI
| URL                    |  GET  |  POST  |  PUT  | DELETE |
| ---------------------- | ----- | ------ | ----- | ------ |
| /events                | Restituisce l'elenco di tutti gli eventi disponibili | Creazione di un nuovo evento    | / | / |
| /events/(:id)          | Restituisce l’evento (:id)     | / | / | Elimina l'evento (:id)                                |
| /events/search/(:query)| Ricerca degli eventi con pattern matching su (:query)                              | / | / | / |
| /events/categories     | Lista delle categorie di eventi disponibili                                        | / | / | / |
| /users                 | Restituisce l'elenco di tutti gli utenti             | Creazione di un nuovo utente    | / | / |
| /users/(:id)           | Restituisce l’utente (:id)     | / | / | Elimina l'utente (:id)                                |
| /tickets               | Restituisce l'elenco di tutti i biglietti            | Creazione di un nuovo biglietto | / | / |
| /tickets/(:id)         | Restituisce il biglietto (:id) | / | / | Eliminazione del biglietto (:id)                      |

Per la lista completa delle API disponibili andare su /apidocs.

#### Utenti admin
Gli utenti si differenziano in admin e non-admin. In base a questa distinzione, il risultato delle chiamate REST varia secondo le seguenti regole: 
* La richiesta GET all'URL /tickets restituirà l'elenco dei soli biglietti acquistati dall'utente se questo è non-admin oppure l'elenco totale dei biglietti acquistati da tutti gli utenti se questo è admin;
* La creazione di un biglietto va a buon fine se viene effettuata da un utente admin o dall'utente a cui appartiene il biglietto stesso, altrimenti viene restituito l'errore 401 (Unauthorized);
* La visualizzazione, la creazione, la modifica e l'eliminazione di un utente può essere eseguita solo da utenti admin;
* La cancellazione di un biglietto può essere fatta solo da utenti admin;
* Gli eventi possono essere creati, modificati e cancellati solo da utenti admin, ma visualizzati anche da quelli non-admin.

Per consultare l'elenco completo delle regole è possibile riferirsi a /apidocs.
