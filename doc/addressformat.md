# Adressformat

> This documentation is copied from the previous version of the repository.
> There will be an English translation as soon as possible.

Adressen sind grundsätzlich in folgendem Format einzugeben:

```
Titel
Straße #/#/...
PLZ Stadt
Zusatzinformationen
```

* Jede Zeile ist optional, die Reihenfolge muss jedoch eingehalten werden.
* Der Titel ist vor allem bei POI (Points of interest) relevant, kann aber bei jeder Adresse beliebig hinzugefügt werden.
* Der Nummernteil einer Adresse ist optional und besteht aus:
  * Hausnummer: Einzelne Zahl oder einzelne Zahl gefolgt von Großbuchstabe oder Zahlenbereich (zB 15, 2A, 37-41)
  * durch Slash abgetrennte Stiegennummer oder Angabe des Gebäudes (meist einzelne Zahl)
  * Nach einem weiteren Slash können beliebige weitere Details angegeben werden, zB Stockwerk, Türnummer etc.
* PLZ (4- oder 5-stellige Zahl) und Stadt werden nur erkannt, wenn beides angegeben wurde.
* Die Zusatzinformationen können mehrzeilig sein.

## Beispiele

Sämtliche Adressen von Wohnungen sind zufällig ausgewählt und müssen nicht unbedingt in dieser Form existieren.

Angabe einer normalen Adresse:

```
Hernalser Hauptstraße 1A/3/4
1170 Wien
```

Angabe einer Türnummer ohne Stiege:

```
Brünner Straße 15/-/5
1210 Wien
```

Angabe von zusätzlichen Informationen:

```
Erdbergstraße 40
1030 Wien
Kreuzung Wassergasse, auf der Straße
```

Angabe einer Spitalsambulanz, zusätzlich mit Fachrichtung (s.u.):

```
WIL Ev
Montleartstraße 37/Pav.80
1160 Wien
(--chir)
```

## POI

Stiegen und einige einsatztaktisch relevanten Punkte im EHS, die Notfallambulanzen der Spitäler und RK-Stationen sind als Points of Interest hinterlegt,
werden also bei Eingaben bevorzugt vorgeschlagen.
Außerdem sind für die meisten dieser Punkte GPS-Koordinaten hinterlegt, wodurch sie unabhängig von der Straßenadresse verortet werden (zB obiges Beispiel
WIL Ev wird direkt auf dem entsprechenden Pavillon angezeigt).
Damit die POI bei der Verortung richtig erkannt werden, darf der Text nicht verändert werden,
sondern nur am Schluss angehängt werden (bei mehrzeiligen POI erst an der letzten Zeile!).

## Ortsangaben im Ernst-Happel-Stadion

Sämtlichen Ortsangaben im EHS sollte EHS/ vorangestellt werden. Damit wird sichergestellt, dass die Bezeichnung eindeutig ist. Danach können verschiedene Angaben folgen:

* Spezielle Punkte: EHS/MLS, EHS/Blaulichtparkplatz, EHS/HPA C, EHS/HPA F, EHS/Spielfeld, EHS/VIP-Parkplatz, EHS/Stadionbadparkplatz
* Stiege: zuerst ist der Sektor anzugeben, dann die Stiege; zB EHS/B/St5, EHS/C/St310
* Vorfeld: zuerst das Stichwort Vorfeld, dann U-Bahn, Rundfahrt oder Sektor A-F, B, C-D, E; zB EHS/Vorfeld/C-D

Die Angabe einer Straße ist für diese Punkte nicht erforderlich. Weitere Informationen (zB Block, Rang, "unten" etc) können hinten oder in einer neuen Zeile angefügt werden.

## Ortsangaben beim VCM

Sämtliche POI beginnen mit VCM/.

* Streckenposten: Streckenposten beinhalten die Kilometerangabe(n) sowie eine Beschreibung des Standorts.

```
VCM/Posten km5,5 km36
Stadionbrücke # Schüttelstraße
```

* Kilometrierung: Für alle Streckenkilometer ohne Posten ist ebenfalls ein POI hinterlegt:

```
VCM/km14
Linke Wienzeile # Morizgasse
```

* Behandlungsstelle: `VCM/Triage`, `VCM/I/II`, `VCM/IIIa`, `VCM/IIIb`, `VCM/IIIc`, `VCM/IIId`, `VCM/SanHiSt`, `VCM/WaHaPl Minoritenplatz`
* Umfeld Heldenplatz: Angabe nach dem Rasterplan (von C10 bis R18), zB VCM/Heldenplatz/F15
* Kinderläufe: `VCM/Kinderläufe Start 4,2`, `VCM/Kinderläufe Start 2,0`

Weitere Informationen können ebenfalls in einer neuen Zeile angefügt werden. Bei Einsätzen an der Strecke ist die normale Angabe einer Straße mit Hausnummer meist sinnvoller, weil die Adresse genauer verortet werden kann.

## Spitäler

Spitäler sind jeweils mit der EDV-Abkürzung hinterlegt, dazu jeweils die Aufnahmestation (Unf bzw EU, Ev, Kinder).
Bei separater Rettungszufahrt (Chirurgie im KHH) für spezielle Ambulanzen sind diese extra hinterlegt.
Die Fachrichtung ist in einer neuen Zeile hinten anzuhängen.
Für den Fall, dass eine Station oder ein Privatspital direkt angefahren wird, kann natürlich auch eine manuelle Eingabe erfolgen. Durch das Adressabfragesystem der Stadt Wien können meist auch Pavillons korrekt verortet werden, wenn diese nach der Hausnummer angegeben werden:

```
KFJ Uro Amb
Kundratstraße 3/Pav.G1
1100 Wien
```
