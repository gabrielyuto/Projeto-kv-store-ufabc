package db;

import services.ServicesDatabase;

public class PreparacaoDB {
    public static void main(String[] args) {
        ServicesDatabase servicesDatabase = new ServicesDatabase();
        servicesDatabase.createTable();
    }
}
