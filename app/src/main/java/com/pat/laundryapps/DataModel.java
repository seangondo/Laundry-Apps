package com.pat.laundryapps;

public class DataModel {
    int id;
    String nama;
    String password;
    String address;

    public DataModel(int id, String nama, String password, String address){

        this.id = id;
        this.nama = nama;
        this.password = password;
        this.address = address;
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String add) {
        this.address = add;
    }
}
