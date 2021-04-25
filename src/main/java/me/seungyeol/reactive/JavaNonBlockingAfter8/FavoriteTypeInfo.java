package me.seungyeol.reactive.JavaNonBlockingAfter8;

import java.util.List;
import java.util.Map;

public class FavoriteTypeInfo {
    List<String> distinctType;
    Map<String,Long> favoriteCountPerCar;

    public FavoriteTypeInfo(List<String> distinctType, Map<String, Long> favoriteCountPerCar) {
        this.distinctType = distinctType;
        this.favoriteCountPerCar = favoriteCountPerCar;
    }


}
