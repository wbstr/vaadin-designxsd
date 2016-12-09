# Vaadin DesignXsd

A projekt a Vaadin design fájlok szereksztését hivatott megkönnyíteni. A kódkiegészítés működéséhez áttértünk a Vaadin áltla kitalált .html szerű fájlokról .xml kiterjesztésű fájlokra. Így a független attól, hogy milyen fejlesztő eszközt szeretnénk használni. Csak be kell állítanunk a projekt által generált .xsd fájlt és máris lesz kódkiegészítésünk a design fájlok szerkesztéséhez.

Leírás a Vaadin Declaratively nevű találmányáról: [Designing UIs Declaratively](https://vaadin.com/docs/-/part/framework/application/application-declarative.html).

## DesignXSD Demo

Ez a modul segíti a projekt megértését, tesztelését, fejlesztését. Lehet benne találni pár példát rá mit hogyan lehet használni. Minden amit ez után leírok megtalálható ebben a modulban.

## DesignXSD XDesign

Ez a modul teszi lehetővé az .xml fájlra vlaó áttérést. 

Az .xml fájlokra való áttéréshez kellett hoznunk pár szabályt. 
* A Vaadin által kitalált fájl szerkezeten nem baj, ha egy attribútum után nem szerepel semmi. Ilyen pl. a size-full attribútum. Ez .xml fájlokban sajnos nem megengedett. Szerencsére úgy implementálták, hogy Mindegy, hogy utána van e, hogy ="" vagy sem. Működik az értékkel vagy anélkül is.
* Egy másik probléma, hogy a Vaadin bizonyos attribútumok elött megköveteli a kettőspontot. Pl. a :expand. Ami így magában annyit jelent, hogy az expandot állítsa egyre. Sajnos ez .xml-ben nem megengedett, mert a kettőspont a namespace-t jelenti. Ez helyett az "_" karaktert választottuk. Ennek a modulnak a feladata ezeket feldolgozni. Egy XDesign nevű osztály van benne, ami nagyon haszonlóan működik, mint a Vaadin által szállított Design nevű osztály. Annyival tud többet, hogy az aláhúzás karaktereket lecseréli kettőspontra és tovább adja a gyárinak. (Design)
* A :expand-on felül van még pár ilyen spéci attribútum, amikkel igazítani lehet a komponenseket. (:middle, :bottom, :center, :right) Természetesen ezeket is aláhúzással kell használnunk.

Tartalmaz ezen felül egy kényelmi funkciót is. A Design nevű osztály, ha azt látja, hogy a POJO, amire rátettük a @DesignRoot annotációt, az érétke nincs kitöltve, akkor keres ugyan abban a csomagban egy .html fájl, amiből megpróbálja betölteni a design-t. Mivel váltottunk .xml-re ez nem fog működni. Az XDesing osztályunk ugyan ezt teszi, csak .xml kiterjesztési fájlt keres. Így megúszhatjuk, hogy minden egyes osztály esetén meg kelljen adnunk, hogy hol van a design fájl.

Vegyük hát föl a függőségek közé! (Ez megtalálható a demó modul pom.xml-jében)
```
<dependency>
  <groupId>com.wcs.designxsd</groupId>
  <artifactId>designxsd-xdesign</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
* Java kódból pedig így lehet használni:
```
@DesignRoot
public class RootDesign extends VerticalLayout {

    public RootDesign() {
        XDesign.read(this);
    }
    
}
```
(Ezt az osztályt is meg lehet találni a demó modulban.)

## DesignXSD Maven Plugin modul

Ez a modul felel az .xsd fájl generálásáért. Érdemes csinálni egy külön profilt, hogy befolyásolni tudjuk, hogy mikor kell újra generálni az .xsd fájlt. (Az egyedi komponenseink is bele generálódnak az .xsd fájlba)
```
<profile>
<id>designxsd</id>
  <build>
    <plugins>
      <plugin>
        <groupId>com.wcs.designxsd</groupId>
        <artifactId>designxsd-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
          <execution>
            <configuration>
              <destination>${basedir}/src/main/designxsd</destination>
              <legacyPrefixEnabled>true</legacyPrefixEnabled>
            </configuration>
            <goals>
              <goal>generate</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</profile>
```
A konfigurációt emelném ki.
```
<configuration>
  <destination>${basedir}/src/main/designxsd</destination>
  <legacyPrefixEnabled>true</legacyPrefixEnabled>
</configuration>
```
* A `<destination>` résszel szabályozható, hogy hova kerüljön a generált .xsd fájl. Arra kell figyelni, hogy ha nem létezik a könyvtár, amit itt megadtunk, akkor nem generálódik le a fájl. Ha nincs beállítva semmi, akkor a projekt gyökérkönyvtárába kerül a generált .xsd fájl.
* Azt, hogy melyik komponens melyik csomagban található a tag nevek előtt lévő prefixummal lehet szabályozni. Ezzek kettő beépített van a `v-` és a `vaadin-` Mindkettő a `com.vaadin.ui` csomagot jelenti. Azt, hogy az .xsd fájl melyik verzióval generálja le a lehetséges tag neveket, a `<legacyPrefixEnabled>` résszel lehet szabályozni. `true` esetén a `v-`-et használja, egyébként meg a `vaadin-`-et. Ha nem adunk meg semmit, akkor `false`-nak vesszük.

# IDE beállítása
## NetBeans

Nyissuk meg a Tools/DTDs and XML Schemas menüpontot! Válasszuk a User Catalog-ot! Majd nyomjuk meg jobb oldalt az Add Locla DTD or Schema gombot! Válasszuk a System ID rádió gobmot és adjunk meg egy tetszőleges nevet! NetBeans esetén a név elé kell írni, hogy `hhtp://` a végére pedig, hogy `.xsd`. Csak így fog működni a kódkiegészítés. Az itt megadott nevet meg kell adnunk a desing-t tartalmazó .xml fájlunkban. Ezek után valahogy így fog kinézni a design fájlunk:
```
<?xml version="1.0" encoding="UTF-8"?>
<html
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://jeewcslib-idm-ui-vaadin-design.xsd">
    <head>
    </head>
    <body>
        <v-vertical-layout size-full="" spacing="true">
            
        </v-vertical-layout>
    </body>
</html>
```
Végül újra kell indítani a NetBeans-t és életre fog kelni a kódkiegészítés.
