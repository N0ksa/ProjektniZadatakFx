package hr.java.project.projectfxapp.enums;


import hr.java.project.projectfxapp.exception.NoSuchCityException;

import java.io.Serializable;

public enum City {
    CAKOVEC ("Čakovec", "40000"),
    DALMATINSKA_ZAGORA ("Dalmatinska Zagora", "21230"),
    DUBROVNIK("Dubrovnik", "20000"),
    KARLOVAC("Karlovac", "47000"),
    KASTELA ("Kaštela", "21210"),
    KRIZEVCI("Križevci", "48260"),
    KOPRIVNICA("Koprivnica", "48000"),
    NOVA_GRADISKA ("Nova Gradiška", "35400"),
    OMIS ("Omiš", "21310"),
    OSIJEK("Osijek", "31000"),
    PAZIN ("Pazin", "52000"),
    PAG ("Pag", "23250"),
    POREC("Poreč", "52440"),
    POŽEGA ("Požega", "34000"),
    PULA("Pula", "52100"),
    RIJEKA("Rijeka", "51000"),
    SIBENIK ("Šibenik", "22000"),
    SISAK("Sisak", "44000"),
    SLATINA("Slatina", "33520"),
    SLAVONSKI_BROD("Slavonski Brod", "35000"),
    SPLIT("Split", "21000"),
    VARAZDIN("Varaždin", "42000"),
    VIROVITICA("Virovitica", "33000"),
    VUKOVAR("Vukovar", "32000"),
    ZADAR("Zadar", "23000"),
    ZAGREB("Zagreb", "10000");


    private final String name;
    private final String postalCode;
    City(String name, String postalCode){
        this.name = name;
        this.postalCode = postalCode;
    }

    public String getName() {
        return name;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public static City getCityFromStringName(String cityName){
        for (City city: City.values()){
            if (city.getName().equalsIgnoreCase(cityName)){
                return city;
            }
        }
        throw new NoSuchCityException("Ne postoji " + cityName + " grad u našoj datoteci");
    }
    @Override
    public String toString() {
        return name + " " + postalCode;
    }
}
