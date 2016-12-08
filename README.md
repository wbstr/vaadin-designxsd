# Vaadin DesignXsd

A projekt a Vaadin design fájlok szereksztését hivatott megkönnyíteni. A kódkiegészítés működéséhez áttértünk a Vaadin áltla kitalált .html szerű fájlokról .xml kiterjesztésű fájlokra. Így a független attól, hogy milyen fejlesztő eszközt szeretnénk használni. Csak be kell állítanunk a projekt által generált .xsd fájlt és máris lesz kódkiegészítésünk a design fájlok szerkesztéséhez.

Leírás a Vaadin Declaratively nevű találmányáról: [Designing UIs Declaratively](https://vaadin.com/docs/-/part/framework/application/application-declarative.html).

## DesignXSD Demo

Ez a modul segíti a tesztelés, fejlesztést. Lehet benne találni pár példát mit hogyan lehet használni. Minden amit ez után leírok megtalálható ebben a modulban.

## DesignXSD XDesign

Ez a modul teszi lehetővé az .xml fájlra vlaó áttérést. Vegyük hát föl a függőségek közé! (Ez megtalálható a demó modul pom.xml-jében)
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
Ezt az osztályt is meg lehet találni a demó modulban.

Az .xml fájlokra való áttéréshez kellett hoznunk pár szabályt. 
* A Vaadin által kitalált fájl szerkezeten nem baj, ha egy attribútum után nem szerepel semmi. Ilyen pl. a size-full attribútum. Ez .xml fájlokban sajnos nem megengedett. Szerencsére úgy implementálták, hogy Mindegy, hogy utána van e, hogy ="" vagy sem. Működik az értékkel vagy anélkül.
* Egy másik probléma, hogy a Vaadin bizonyos attribútumok elött megköveteli a kettőspontot. Pl. a :expand. Ami így magában annyit jelent, hogy az expandot állítsa egyre. Sajnos ez .xml-ben nem megengedett, mert a kettőspont a namespace-t jelenti. Ez helyett az "_" karaktert választottuk. Ennek a modulnak a feladata ezeket feldolgozni. Egy XDesign nevű osztály van benne, ami nagyon haszonlóan működik, mint a Vaadin által szállított Design nevű osztály. Annyival tud többet, hogy az aláhúzás karaktereket lecseréli kettőspontra és tovább adja a gyárinak. (Design)

Tartalmaz ezen felül egy kényelmi funkciót is. A Design nevű osztály, ha azt látja, hogy a POJO, amire rátettük a @DesignRoot annotációt az érétke nincs kitöltve, akkor keres ugyan abban a csomagban egy .html fájl, amiből megpróbálja betölteni a design-t. Mivel váltottunk .xml-re ez nem fog működni. Az XDesing osztályunk ugyan ezt teszi, csak .xml kiterjesztési fájlt keres. Így megúszhatjuk, hogy minden egyes osztály esetén meg kelljen adnunk, hogy hol van a design fájl.

## DesignXSD Maven Plugin modul
