# Guida Utente
Per far funzionare questa applicazione è necessario abilitare un permesso speciale tramite **ADB** o **ROOT**

## Perché?
Al posto di mantenere l'app costantemente in background e consumare batteria, preferiamo usare una seplice opzione che è presente su tutti i dispositivi Android, nascosta in ```Impostazioni > Opzioni sviluppatori > Simula spazio colore```, purtroppo però è molto scomodo se vuoi passare spesso dalla modalità Normale a quella in Scala di Grigi.
Dato che questa funzione fa parte del del sistema operativo Android e non è un'opzione normalmente disponibie per gli sviluppatori, richiede un permesso speciale.

## Come?
#### Root
Se il tuo dispositivo è stato rootato, è molto semplice, devi semplicemente premere il tasto **root** presente sulla finestra che appare quando provi ad abilitare la scala di grigi.
#### ADB
ADB (Android Debug Bridge) è uno strumento che chiunque può scaricare, permette di mandare dei comandi speciali dal tuo computer al tuo telefono (di soluto tramite cavo USB).

Qui puoi trovare la pagina ufficiale di [Android Debug Bridge](https://developer.android.com/studio/command-line/adb)

E qui puoi trovare una guida completa su come installarlo su MacOS, Windows o Linux: [XDA - How to install adb](https://www.xda-developers.com/install-adb-windows-macos-linux/).

Una volta che hai installato correttamente ADB sul tuo computer, hai quasi finito, ti basterà connettere il tuo smartphone al tuo computer, aprire il terminale e infine incollare questa stringa e premere invio 

    adb -d shell pm grant com.berenluth.grayscale android.permission.WRITE_SECURE_SETTINGS

Perfetto! Sei pronto ad utilizzare Greyscale+!
