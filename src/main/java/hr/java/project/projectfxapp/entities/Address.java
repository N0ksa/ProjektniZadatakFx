package hr.java.project.projectfxapp.entities;


import hr.java.project.projectfxapp.enums.City;

import java.io.Serializable;
import java.util.Objects;

/**
 * Predstavlja adresu koja se sastoji od ulice, broja, grada i poštanskog broja.
 */
public class Address implements Serializable {

    private Long addressId;
    private String street;
    private String houseNumber;
    private City city;

    /**
     * Privatni konstruktor za stvaranje instance klase Adress.
     * @param street  Ulica
     * @param houseNumber Kućni broj
     * @param city Grad
     */
    private Address(Long  addressId, String street, String houseNumber, City city) {
        this.addressId = addressId;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
    }


    /**
     * Unutarnja klasa za izgradnju objekata tipa {@link Address}.
     */
    public static class AdressBuilder{
        private Long id;
        private String street;
        private String houseNumber;

        private City city;

        /**
         * Konstruktor koji postavlja grad.
         * @param city Grad.
         */
        public AdressBuilder(City city){
            this.city= city;
        }


        /**
         * Postavlja id kućne adrese.
         * @param id Id kućne adrese.
         * @return AdressBuilder - trenutni AdresBuilder za daljnje postavljanje atributa.
         */
        public AdressBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Postavlja ulicu kućne adrese.
         * @param street Ulica.
         * @return AdressBuilder - trenutni AdresBuilder za daljnje postavljanje atributa.
         */
        public AdressBuilder setStreet(String street) {
            this.street = street;
            return this;
        }


        /**
         * Postavlja broj kućne adrese.
         * @param houseNumber Kućni broj.
         * @return AdressBuilder - trenutni AddressBuilder za daljnje postavljanje atributa.
         */
        public AdressBuilder setHouseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }


        /**
         * Generira objekt tipa {@link Address}.
         * @return Generiran objekt tipa {@link Address}.
         */
        public Address build(){
            return new Address(id, street, houseNumber, city);
        }


    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(addressId, address.addressId) && Objects.equals(street, address.street) && Objects.equals(houseNumber, address.houseNumber) && city == address.city;
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressId, street, houseNumber, city);
    }

    @Override
    public String toString() {
        return getStreet() + " " + getHouseNumber() + ", " + getCity();
    }

}
