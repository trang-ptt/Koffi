package com.example.koffi.models;

public class User {
    public String Ten, Email, Sdt, NgaySinh, GioiTinh;
    public User()
    {

    }

    public User(String ten, String email, String sdt) {
        Ten = ten;
        Email = email;
        Sdt = sdt;
    }

    public User (String ten, String email, String sdt, String ngaysinh, String gioitinh)
    {

        this.Ten=ten;
        this.Email=email;
        this.Sdt=sdt;
        this.NgaySinh=ngaysinh;
        this.GioiTinh=gioitinh;
    }
}
