package xyz.thepathfinder.gmaps;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Directions {
    @SerializedName("geocoded_waypoints")
    public List<GeocodedWaypoint> geocodedWaypoints;
    public List<Route> routes;
    public String status;
}
