# Minimo_1
Els prestigiosos estudiants de Telemàtica de l'Escola d'Enginyeria de
Telecomunicació i Aeroespacial de Castelldefels (EETAC), reconeguts arreu per
la seva habilitat innata digitalitzar processos complexos han rebut l'encàrrec de
dissenyar i implementar un sistema informàtic per a la gestió d’una biblioteca
per entorns rurals.
Tots els llibres que arriben són dipositats al magatzem de la biblioteca abans de
la seva posterior catalogació. Volen a més que la gestió de préstec de llibres es
faci amb recursos tecnològics moderns, deixant a un costat els tradicionals
mètodes "rurals" d'administració en paper.
El procediment de catalogació és el següent: primerament els llibres són
dipositats al magatzem en munts de 10. S’ha comprovat que munts més alts
tenen el risc d’ensorrar-se, per la qual cosa s’ha establert aquest nombre com
una quantitat òptima. El nombre de munts és desconegut, simplement es va
creant un nou munt amb els nous llibres que van arribant al darrera del darrer
munt format. Per crear un nou munt, els llibres es van apilant un damunt l'altre
fins a arribar a la quantitat de 10, moment en què aquest munt estarà complet.
Els munts es tracten en l’ordre d’arribada, és a dir, primer es cataloguen els
llibres del primer munt creat, després els del segon i així successivament. Dins
de cada munt, els llibres es cataloguen en ordre invers en què s’ha creat el
munt, és a dir, el primer llibre que es cataloga serà l’últim que es va posar en
aquest munt, el que està a la part més amunt, el segon que es catalogarà serà
el penúltim posat, etc. 
A continuació, es descriuen detalladament les operacions que s'han de
desenvolupar.

● Afegir un nou lector. De cada lector es coneix el seu identificador, el
seu nom, els seus cognoms, el seu dni, la seva data de naixement, el
seu lloc de naixement i la seva adreça. Si hi ha un lector amb aquest
identificador s'actualitzen les seves dades.

● Emmagatzemar un llibre. S’emmagatzema un nou llibre en l’estructura
de llibres emmagatzemats. De cada llibre se'n coneix l’identificador, el
seu ISBN, el seu títol, el seu editorial, el seu any de publicació, el seu
número d'edició, el seu autor i la seva temàtica. El llibre s'apila en l'últim
munt si aquest té menys de 10 llibres apilats. En cas contrari es crea un
nou munt buit i s'hi apila el llibre.

● Catalogar un llibre. S'extreu el llibre apilat a la part més alta del primer
munt actual. Si el munt queda buit el següent llibre que s’extraurà serà el
que estigui a la part més alta del següent munt que passa a ser el munt
actual. Si ja existeix un llibre amb el mateix ISBN al contenidor de llibres
catalogats, no es crea un nou element, sinó que s’incrementa la quantitat
d’exemplars disponibles d’aquest llibre. Si no hi ha cap llibre pendent de
catalogar s’indica un error.

● Prestar un llibre. De cada préstec es coneix l’identificador del préstec,
l’identificador del lector, l’identificador del llibre catalogat, la data de
préstec i la data final de devolució. El nombre d’exemplars del llibre
disponibles es decrementa en una unitat. El préstec es marca com "En
tràmit". Si no existeixen exemplars suficients del llibre s’indica un error.
Si el lector o el llibre no existeixen s’indica un error.

● Consultar tots els préstecs que ha realitzat un lector. Se sap per
endavant que l'identificador del lector existeix.

#Versió 1
Part 1:
S'ha aconseguit fer funcionar correctament els tests.

Part 2:
S'ha arrivat a crear el swagger y es pot rebre informacio del client, pero hi complicacions per processar-la.

#Versió 2

Part2: 
S'ha solucionat el problema del servidor i ya funciona perfectament.
