package hr.java.project.projectfxapp.entities;


import hr.java.project.projectfxapp.enums.City;

import java.io.Serializable;
import java.util.Objects;

public class Address implements Serializable {

    private Long addressId;
    private String street;
    private String houseNumber;
    private City city;

    private Address(Long  addressId, String street, String houseNumber, City city) {
        this.addressId = addressId;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
    }


    public Address(Address other) {
        this.addressId = other.addressId;
        this.street = other.street;
        this.houseNumber = other.houseNumber;
        this.city = other.city;
    }


    public static class AddressBuilder {
        private Long id;
        private String street;
        private String houseNumber;

        private City city;


        public AddressBuilder(City city){
            this.city= city;
        }

        public AddressBuilder setId(Long id) {
            this.id = id;
            return this;
        }


        public AddressBuilder setStreet(String street) {
            this.street = street;
            return this;
        }


        public AddressBuilder setHouseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }

        public AddressBuilder setAddressId(Long addressId) {
            this.id = addressId;
            return this;
        }


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
        return Objects.equals(addressId, address.addressId) &&
                Objects.equals(street, address.street) &&
                Objects.equals(houseNumber, address.houseNumber)
                && city == address.city;
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
