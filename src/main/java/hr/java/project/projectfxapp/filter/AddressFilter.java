package hr.java.project.projectfxapp.filter;

import hr.java.project.projectfxapp.enums.City;

public class AddressFilter {

    private String streetName;
    private String houseNumber;
    private City city;

    public AddressFilter(String streetName, String houseNumber, City city) {
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
