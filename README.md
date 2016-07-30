# Groupicture
### Descriere succinta
Groupicture este o aplicatie prin care utilizatorii isi pot organiza diferite poze in foldere si grupuri. Fiecare utilizator avand propriul cont, poate crea grupuri publice sau grupuri private, setand o parola. In interiorul acestor grupuri, utilizatorii pot incarca imagini, organizandu-le in foldere. Pozele pot fi preluate din memoria telefonului si de pe cardul de memorie, sau pot fi adaugate direct cu ajutorul camerei de fotografiat a telefonului. In cazul in care sunt mai multi utilizatori in acelasi grup, acestia pot vedea toate modificarile facute in grupul respectiv. De asemenea, utilizatorii pot descarca pozele incarcate in grupurile in care au acces. In concluzie, Groupicture este un soft utilitar prin care utilizatorii pot impartasi poze intre ei.

### Tehnologii folosite
Partea de client, aplicatia pentru android, a fost programata in Java, folosind IntelliJ IDEA ca IDE. 
Partea de server a fost programata folosind Java in Netbeans. Aceasta este hostata prin Openshift si foloseste Tomcat 7 si o baza de date MySQL 5.5.
Pentru request-urile catre server ne-am folosit de biblioteca Volley. De asemenea am folosit android-support-v4 si v7 pentru ca aplicatia sa poata fi folosita pe cat mai multe device-uri.
Pentru a putea lucra in echipa mai convenabil am folosit ca sistem de versionare git, cu repository-ul sincronizat prin GitHub.

### Resurse externe
Cateva 'code snippets' de pe Stack Overflow.                                                                                    
Bibliotecile Volley si org.apache.http pentru a usura crearea de request-uri.                                  
android-support-v4 si android-support-v7-appcompat pentru compatibilitate cu versiunile mai vechi de android.                   
Clasa Base64 autor:  Robert Harder rob@iharder.                              
Clasa CustomRequest autor: LOG_TAG                
Clasa CustomNetworkImageView autor: param            
De asemenea m-am inspirat din: http://benjii.me/2010/08/endless-scrolling-listview-in-android                         
http://stackoverflow.com/questions/20639464/actionbaractivity-with-actionbardrawertoggle-not-using-drawerimageres          
http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures                  


### Rolul in echipa - atributii principale
Ardelean Timotei: -> Partea Server-Side si legatura dintre Client si Server.                                    
Goran Cornel: -> Partea Client-Side, UI, realizarea elementelor de grafica si design.
