package me.seungyeol.reactive.JavaNonBlockingAfter8;

public class CarInfo {

    int id;
    String type;

    public CarInfo(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CarInfo{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }

}
