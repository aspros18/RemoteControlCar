Git URL: https://github.com/fzoli/RemoteControlCar

Beszerzés
---------

Hordozható kiadás:
https://github.com/fzoli/RemoteControlCar/raw/master/bin/rel-portable.zip

Windows telepítő:
https://github.com/fzoli/RemoteControlCar/raw/master/Setup.msi

Linux telepítő:
https://github.com/fzoli/RemoteControlCar/raw/master/linux-installer.bsx

OS X telepítő:
https://github.com/fzoli/RemoteControlCar/raw/master/mac-installer.dmg

A projektet bemutató videófelvételek a videos könyvtárban találhatók meg.
A járműről fényképek a photos könyvtárban, képernyőképek a snapshots könyvtárban.

Bevezetés
---------

A projekt célja egy távirányítós autó vezérlése interneten át.

Az autó vezérlése Android operációs rendszerrel futó mobiltelefon segítségével valósul meg.

A telefon felhasznált eszközkészlete:
- hátsó kamera, ami segítségével lehet látni, mi van az autó előtt
- giroszkóp, ami segítségével meg lehet tudni a gyorsulás és kanyarodás mértékét valamint az út lejtését (ha nincs fék az autón, az emelkedő/lejtő adat alapján megoldható, hogy egy helyben álljon az autó bármikor, ha azt akarjuk)
- GPS vevőt, ami segítségével megbecsülhető a pillanatnyi sebesség és megtudható a kellően pontos tartózkodási hely
- USB port, ami segítségével - egy SparkFun által gyártott mikrovezérlő - az IOIO for Android (DEV-10748) köthető össze a telefonnal. A mikrovezérlőhöz tartozik egy Java nyelvre írt API, ami segítségével ADB (Android Debug Bridge) vagy ADK (Accessory Development Kit) kapcsolaton keresztül a telefon képes kommunikálni a külvilággal. Az API automatikusan kapcsolódik az eszközhöz, nem fontos, melyik kapcsolaton keresztül, de az ADB van előnyben részesítve.
- mobilhálózat, ami segítségével HSPA vagy 3G mobilinternetet használva elküldhető a videó jel, a statisztika és foghatóak a vezérlő jelek
- mobilakku, ami ellátja a mobiltelefont árammal. Azt is jó látni interneten keresztül, hány %-on áll az aksi.

A mikrovezérlő célja:
- vezérelni az autót a telefonnal
- az autó akkumulátorának töltöttségi szintjének megszerzése

Egyéb eszközök:
- kézzel állítható, kétállású kapcsoló, ami eldönti, hogy a beépített mikrovezérlő (ami a gyári távirányító jelének segítségével vezérli az autót) vagy a telefon mikrovezérlője (ami az internetről kapott utasításokkal vezérli az autót) legyen aktív. Erre azért van szükség, hogy ne történhessen meg az, hogy mindkét mikrovezérlő egyazon időben jelzést adjon le és túlfeszültséget okozzon az autó áramkörében
- a lista még bővülni fog olyan dolgokkal mint pl. ellenállás

A hálózati kommunikáció terve:
------------------------------

A kommunikáció SSL titkosítást használva socketek segítségével történik.
A tanúsítványokban szereplő CN (Common Name) mező alapján történik a felhasználó számítógépek és emberek azonosítása, tehát nincsenek felhasználónevek és jelszavak.
Az összes tanúsítvány egyetlen kiállítótól (CA) származik és mind a socket szerver, mind a socket kliensek csakis ebben az egy kiállítóban bízik meg.
Ez azt eredményezi, hogy az összes olyan tanúsítványt elfogadják, amiket a konkrét CA állított ki, de az összes többit elutasítják.

Az SSL többféle próbának is aláveti a socketeket mielőtt létrejönne a kapcsolat:
- megnézi, hogy megbízható-e a tanúsítvány (vagyis inkább a CA)
- megnézi, hogy nem járt-e le a tanúsítvány
- ellenőrzi a hostnevet

A hostnév-ellenőrzés ki van kapcsolva, mert a szabvány szerint a szerver hostnevének és a tanúsítványában szereplő CA mezőnek meg kell egyeznie.
Fentebb írtam, hogy a CA mezőt én a felhasználók azonosítására használom (amolyan felhasználónév szerűség), másrészt a szervernek nem feltétlenül van hostneve vagy a hostnév változó (Pl. IP cím alapján is lehet szerverhez kapcsolódni).

Az egyszerűsített kapcsolati ábra:
Host <-> Bridge <*> Controller

Jelmagyarázat:
<-> egy-egy kapcsolat
<*> egy-sok kapcsolat
Host: a mobiltelefon által indított kliens socket (magyarban is host)
Bridge: egy olyan socket szerver, ami kliens socketek kapcsolódására vár (magyarra fordítva: híd)
Controller: azok a kliens programok, amik segítségével vezérelhető az autó (magyarra fordítva: vezérlő)

Host:
-----

Socket oldalról megközelítve:
A host az én esetemben egy mobiltelefon, ami legtöbbször mobilinternetet - ritkábban Wi-Fi hálózatot és akkor belső IP alapján is lehet címezni - használ  arra, hogy fel tudja venni a kapcsolatot a híddal. Socket szempontjából ő kliens szerepet tölt be. Ennek az az oka, hogy az összes internetes IPv4 cím ki lett már osztva és az internetszolgáltatók minden trükköt bevetnek, hogy ki tudják szolgálni az összes internetet használni akaró gépet. A mobilszolgáltatók ezért NAT-olt IP címet osztanak ki a mobilinternetet használó gépeknek. Ezzel a trükkel egy internetes IP cím (rövidebben WAN IP) több géphez is tartozhat, tehát több gépet ki lehet szolgálni, de van egy hátránya is - amiről a mobilinternetezők nagy része nem is tud - mégpedig az, hogy nincs egy elérhető aktív port sem. Mivel nincs aktív port, hiába üzemeltetünk szervert a telefonon, azt az internetről nem lehet elérni, vagyis csak kliens szerep lehetséges.

A kamera elérése:
A telefon kamerájának képkockái MJPEG streameléssel jutnak el a hídhoz.
Eredeti elképzelésem szerint saját magam olvastam volna a kamera képét és küldtem volna a socket servernek.
A probléma ott kezdődött, hogy az android API nem éppen programozóbarát ha médiáról van szó.
Egyrészt csak akkor hajlandó képkockákat adni, ha azt felületen is megjelenítem (és ennek nem sok értelme van) valamint az eseménykezelőben egy bájt tömböt kapok, amiben YUV formátumú kép van. Ezzel még nem is lenne gond, mert ha valakinek csak a fekete-fehér tartomány kell, akkor még örül is neki, de ha színes képet szeretne az én telefonom kb. 800 ezredmásodpercig dolgozik rajta és akkor még nem is méreteztem át, nem is tömörítettem JPEG formátumba. Olyan 0.7 FPS érhető el így összességében. Gondoltam utánanézek már kész androidos programoknak, amik tudnak videót streamelni. A sok androidos program közül egy megfelelően működőt találtam is, aminek a neve IP Webcam. Java csomag alapján keresve "com.pas.webcam". Ez volt az egyetlen olyan program, ami már megfelelő FPS-t tudott és nem észleltem késést sem. Meg is néztem az okát (hogy hogyan, az már más kérdés). Natív kódot használtak fel, amit C nyelven írtak meg. (Az Android is támogatja a natív kódokat. Bővebben: NDK - Native Development Kit)
Az IP Webcam program HTTP szervert használ és lehetőség van felhasználónév és jelszó beállítására, valamint megadhatjuk a szerver portját, a JPEG képek paramétereit (felbontás, FPS, képminőség) és hogy induljon-e el a telefon indulásakor a program, hogy bármikor hozzá lehessen férni a kamerához.

A telefonon futó program terve:
A telefonon egy Android service fog futni, amit egy egyszerű Activity felületről lehet majd elindítani ill. leállítani.
A service ha leállítódik valami ok miatt (pl. kevés a memória), úgy lesz beállítva, hogy amint lehetséges, induljon el.
Ha a telefon újraindulna miközben a service fut (pl. SD kártyáról fut vagy 100 program és szegény kártya nem bírja olyan gyorsan adni az adatokat mint kéne), a service az OS (operációs rendszer rövidítése) felállása után szintén elindul.
A service az elindulása után azonnal kapcsolódni próbál a hídhoz, ha nem sikerül percenként próbálkozik.
Ha a kapcsolat megszakad, azonnal elindul a kapcsolódás folyamata.
Ezzel minden nem várt hibára megtörtént az óvintézkedés.
A service elindulásakor, ha nem fut az IP Webcam program, akkor az API-ja segítségével a service elindítja azt. (Az ip-webcam könyvtárban további infó.)
A service elindítása előtt a felületen a menüből kezdeményezhető további beállítás.
Itt kell megadni:
- a híd címét (host vagy IP) és portját (amin a socket servlet figyel)
- a tanúsítvány publikus kulcsát (crt fájl) és privát kulcsát (key fájl), amivel a hídhoz kapcsolódik és titkosít.
- a tanúsítvány kiállítójának publikus kulcsát
- mennyi időközönként küldjön statisztikát a telefon
Opcionális beállításként megadható a híd tanúsítványának CN mezője, csak a biztonság kedvéért.
Előfordulhat, hogy rossz címet állítottunk be és nem a híd van címezve. Ez esetben ha a CN mező meg van adva, kiküszöbölhető a fölösleges kapcsolódás és azonnal egyértelmű hibajelzést kapunk. Ha nincs megadva a CN mező, a program percenként fölöslegesen fog próbálkozni, konkrét hibát nem jelezve.

A hang stream, eredetileg kikapcsolt és kérhető lesz bekapcsolásra. Kikapcsolása esetén elég a socket streamet lezárni és kezelni kell a kivételt.
A streamet az android beépített szolgáltatása (MediaRecorder.AudioEncoder.AAC) fogja szolgáltatni és a híd fogja tovább adni a vezérlőknek.


Bridge:
-------

A hídnak két szerepköre van:
- vár a hostra - amitől a videót és a statisztikát kapja és akinek a vezérlőjelet küldi majd
- kiszolgálja a vezérlőket - amik irányítani szeretnék az autót - és eldönti, hogy ki vezérelheti az autót, ha több kérés van egy időben

Úgy terveztem meg a socket alapú kommunikációkat, hogy a híd el tudja dönteni, mi célja lesz a kapcsolatnak és egy időben több kapcsolatot kezelhessen.
A híd esetén a host CN mezőjét kötelező megadni, mert a híd ez alapján tudja, hogy a socket kliens vezérlő-e vagy host. EZ NAGYON FONTOS!!!
A híd esetében megadható több CN mező is, amik segítségével lehet tudni, mely tanúsítványok lesznek a hostok ha esetleg több autó is távvezérelt lesz a jövőben. (Meg lehetett volna oldani úgy is, hogy a socket kliensek még a kapcsolatazonosító kérése előtt közölték volna, hogy ők hostok vagy vezérlők, de ez esetben a híd nem tudna pontos listát adni a hostokról.)

[Opcionális terv: A híd mellé lehetne egy webszervert is tenni. Regisztrálhatnának internetről mások, akiknek el lett adva egy ilyen autó és van hozzá mobiltelefonjuk. A webszerver is SSL-t használna az alapértelmezett 443-as porttal. Itt akár tanúsítványt is generáltathatnának (hostjukhoz és vezérlőjeikhez) így a híd socket servere megbízhatónak találná őket és szintén tudná ki a host és ki a vezérlő. A híd szolgáltatása havidíjas is lehetne és azoknak lenne jó, akik nem szeretnének saját híd szervert üzemeltetni. A host mikrovezérlő kódját ez esetben úgy kellene megtervezni, hogy interface alapú legyen és a felületről több autó típust lehessen választani az implementált osztályok alapján. Szép álom lenne, ha a mobilinternetes szolgáltatókkal meg lehetne egyezni, hogy ne mérjék a forgalmat és kapnak a bevételből :-) ]

Host felőli kapcsolatok:
1. MJPEG stream: hídnak letöltés, hostnak feltöltés
2. Statisztika adatok: objektumos stream, hídnak letöltés, hostnak feltöltés
3. Vezérlő parancsok a hídtól (az autónak és a telefonon futó programnak): objektumos stream, hídnak feltöltés, hostnak letöltés

Vezérlő felőli kapcsolatok:
1. MJPEG stream: hídnak feltöltés, vezérlőnek letöltés
2. Statisztika adatok (a hostról és a hídról): objektumos stream, hídnak feltöltés, vezérlőnek letöltés
3. Vezérlő parancsok a vezérlőtől (a hostnak és a hídnak): objektumos stream, hídnak letöltés, vezérlőnek feltöltés

Bővebb magyarázat:
- A kapcsolatok előtt a számozás kapcsolatazonosítót és prioritási sorrendet is jelent.
- A híd a hostnak főként autóvezérlő utasításokat közöl (a 3-mas kapcsolaton át), de a telefonnak közölheti azt is,
  hogy hagyja abba az MJPEG streamelést vagy éppen kezdje el.
- A vezérlő a hídnak főként autóvezérlő utasításokat ad, de üzenhet a hídnak is, hogy szeretné átvenni az autó irányítását.
- A híd is üzenhet a vezérlőnek, ha éppen elvették tőle az irányítást, jelezze a felületen.

A kapcsolat kialakítása a következő módon valósul meg:
A kliens három kapcsolatot alakít ki ugyan azzal a sockettel, a kapcsolat létrejötte után új szálban kezdődik meg a feldolgozás.
A szerver amíg aktívan fut (tehát a Socket server nincs bezárva) cikluson belül mindig várja a klienseket és ha létrejön egy SSLSocket, ő is külön szálban dolgozik vele tovább, hogy képes legyen több kapcsolatot kiszolgálni egy időben.

A kapcsolat kialakításának lépései: (Szerver alatt a híd socket szerverét értem.)
0. A kliens socket kapcsolódik a szerver sockethez és sikerül az SSL kézfogás, létrejön a kapcsolat és mindkét oldalon egy SSLSocket objektum.
1. a) A szerver a kliens-tanúsítvány CN mezejének alapján megtudja, hogy a kliens host vagy vezérlő és hogy pontosan kivel is van dolga.
   b) A szerver megnézi, hogy az adott szerepkörben az adott névhez hány kapcsolat van már kialakítva.
   c) A szerver inkrementálja a B pontban kapott számot és így megkapja az aktuális kapcsolat azonosítóját
      és a fenti felsorolás alapján már tudja is, mire lesz használva a kapcsolat.
2. A szerver elküldi a kliensnek a kapcsolatazonosítót. Másképp fogalmazva: A kliens lekéri a kapcsolatazonosítót.
3. Mindkét oldal a kapcsolatazonosító alapján együttműködve kezeli a kapcsolatot.
4. Bármelyik oldal lezárhatja a kapcsolatot, ha úgy dönt. Ekkor befejeződik a kommunikáció.

Ha a hídra ugyan azzal a tanúsítvánnyal kapcsolódnak több helyről, a híd a régebbi kapcsolatokat bezárja.
Így ki van kerülve az, hogy két helyről vezérelhessék az autót ellentmondásos üzenetet leadva mint pl. menj jobbra és balra is.

Jogkezelés:
Előfordulhat olyan eset, hogy egy autót több vezérlő is szeretne irányítani.
Erre azt találtam ki, hogy csinálok a hídra egy tanúsítvány-prioritás listát.
A nagyobb prioritású vezérlő bármikor elveheti a vezérlést a kisebb prioritású vezérlőktől, de egyébként minden vezérlő kapcsolódhat a hídhoz és minden
információt láthat, csak a vezérléstől vannak megfosztva.
Amint felszabadul a vezérlés, bárki kérheti az autó vezérlését prioritástól függetlenül és meg is kapja.
(Kérni lehet akkor is, ha éppen vezérlik az autót, de az csak kérvényt jelent, semmi többet. Amint felszabadul a vezérlés az első kérvényt benyújtó meg is kapja.)
(Ha a prioritáslistában egyetlen név szerepel, akkor úgy fog funkcionálni, mint ha root lenne, mivel mindenki felett áll ez esetben.)
Ha nagyobb prioritású kapcsolódik a hídhoz, mint aki vezérli az autót, addig amíg nem kéri a vezérlést, nem veszi el a híd az aktuális vezérlőtől az irányítást.
Aki nem szerepel a prioritáslistában, az olyan, mint ha a legutolsó utolsó helyen szerepelne a listában és a többi - listában nem szereplő - felhasználóval egyenrangúnak számít, tehát ők egymástól nem tudják elvenni a vezérlést.
Ha a vezérlő sokáig nem cselekszik, tehát nincs a gép előtt, a vezérlést elveszi a híd tőle.

Internet sávszélesség:
A híd létrejöttének fő célja az volt, hogy egyetlen egy kapcsolat (ami valójában három socket kapcsolat) legyen a mobilinternet oldaláról és az is takarékos legyen mivel forgalom- és sebességkorlátos az internet.
A hídba be van téve egy olyan sávszélesség csökkentő funkció is, hogy csak akkor kéri az MJPEG streamet a hosttól, ha van legalább egy vezérlő kapcsolódva hozzá.

Konfig fájl:
A híd esetén is meg kell adni a tanúsítvány publikus és titkos kulcsának az elérhetőségét és a kiállító publikus kulcsának az elérhetőségét.
A tanúsítvány prioritásokat és a hostlistát is itt kell megadni, de ha bevezetésre kerül egy webes felület, átkerülnek egy közös adatbázisba.

Controller:
-----------

A vezérlő az a program, ami megjeleníti az autó adatait és ha van lehetősége vezérli az autót.
Ez egy PC-n futó java alapú desktop alkalmazás lényegében, ami szintén a hídhoz kapcsolódik socket kliensként.
A felületen lehetőség van - és kötelező is első induláskor - beállítani a híd címét és portját valamint a tanúsítvány publikus és titkos kulcsát és a kiállító publikus kulcsát.
A kiállító kulcsára azért van szükség mert kliens oldalon is ellenőrizve van a szerver tanúsítvány megbízhatósága, nem csak szerver oldalon, hogy a man-in-middle támadás lehetetlen legyen.
A vezérlés billentyűzet és egér segítségével történik.
Az egérrel pontos utasítást lehet adni mind sebesség, mind irány terén.
A billentyűzettel egyszerűbb, de durvább irányítást érhet el a felhasználó és több vezérlés mód közül választhat.
(A vezérlési módok a durvaságot szabályozzák pl. előre nyílra egyenletesen gyorsuljon fel vagy földkaparással max grafttal induljon el)

Egy kis trükk:
--------------

Egyes mobilszolgáltatók csomagjaiban a webböngészés nem számít bele a forgalomkorlátba.

Ez esetben socketek helyett mondjuk Tomcat webszervert lehetne használni.
- A terv lényege ugyan az, csak a kapcsolatazonosítók helyett HTTP címzés lenne használva.
- Az MJPEG stream HTTP POST-tal streamelhető a hídnak - ami a Tomcat webszerver.
- A vezérlő utasítások JSON-nal objektummá alakíthatók Stringből és objektumból Stringre, és a HTTP tartalom is gond nélkül használható streamelés céljára.

UPDATE: Utólag kiderült, hogy csak a letöltés nem számít bele, és mivel a telefonon a kliens 99%-ban feltöltést bonyolít le, elvetett ötlet.

Továbbfejlesztés
----------------

További két socket kapcsolat segítségével megoldható lenne, hogy a telefon mikrofonjából megkapjuk a hangot is a kép mellé és a vezérlő számítógép mikrofonjából meg a telefonon játsszunk le hangot.
A telefonból érkező hang stream ki/be kapcsolható lenne, alapértelmezetten ki lenne kapcsolva takarékosság miatt.
Az autót vezérlő kliens tudná csak ki/be kapcsolni a streamet és csakis ő tudna hang streamet küldeni a telefonnak.
A sávszélesség kímélése érdekében a Javara írt Tritonus API-t használva lehetne tömöríteni GSM vagy MP3 formátumba a monó 16 bites PCM-ről. (Mondjuk GSMFormatConversionProvider segítségével.)
Egyéb pluginok: http://www.tritonus.org/plugins.html
Példa: http://www.jsresources.org/examples/GSMEncoder.java.html
Ha a Tritonus még sem használható út, marad a GZipInputStream és társa a GZipOutputStream.
Vagy egy másik út: http://www.jsresources.org/apps/9159_TimeTurner.pdf

Az autón lévő tartó, amiben a mobil lesz, lehetne motor által vezérelhető, hogy a kereszteződéseket be lehessen látni és igény szerint hátra is lehessen tekinteni.
A giroszkóp adatait a telefon programja csak az alapállásban adná le a megbízhatóság miatt. (Vagy ha szükség van rá, akkor csak a motor mozgása közben lenne inaktív és az algoritmus meg lenne írva úgy, hogy több helyzetből is jó adatokat közöljön.)

A telefont tartó eszköz lehetőleg védje a telefont a külső támadások ellen és az eső ellen, kulccsal zárható legyen.
Ha ez meg lett oldva, a kábeleket is jó lenne biztonságosan elvezetni szintén védve és a kapcsolót, ami átkapcsolhat internetes vezérlésről, azt is a kulccsal
zárt, védett helyen belülre kéne elhelyezni.

Egy harmadik kliens alkalmazásként lehetne írni egy admin felületet, amivel a belépett felhasználókat lehet menedzselni (priorítás, ban, kick).
Lehetne egy SSL alapú weboldal is, ahonnan regisztráció után letölthető lenne a kliensekhez használható tanúsítvány.

