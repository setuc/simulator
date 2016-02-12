package xyz.thepathfinder.gmaps;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeocodedWaypoint {
    @SerializedName("geocoder_status")
    public String geocoderStatus;
    @SerializedName("place_id")
    public String placeId;
    public List<String> types;
}
