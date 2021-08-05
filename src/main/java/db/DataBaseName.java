package db;

public enum DataBaseName {

    DEFAULT_DATA_BASE_NAME("default-data-base");

    private String name;

    DataBaseName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
