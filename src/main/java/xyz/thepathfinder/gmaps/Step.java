package xyz.thepathfinder.gmaps;

import com.google.gson.annotations.SerializedName;

public class Step {
    public Value distance;
    public Value duration;
    @SerializedName("end_location")
    public Coordinate endLocation;
    @SerializedName("html_instructions")
    public String htmlInstructions;
    public String maneuver;
    public Polyline polyline;
    @SerializedName("start_location")
    public Coordinate startLocation;
    @SerializedName("travel_mode")
    public String travelMode;
}
