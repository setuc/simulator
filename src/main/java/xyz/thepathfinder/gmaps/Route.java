package xyz.thepathfinder.gmaps;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Route {
    public Bounds bounds;
    public String copyrights;
    public List<Leg> legs;
    @SerializedName("overview_polyline")
    public Polyline overviewPolyline;
    public String summary;
    public List<String> warnings;
    @SerializedName("waypoint_order")
    public List<String> waypointOrder;
}
