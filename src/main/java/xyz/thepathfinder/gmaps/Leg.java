package xyz.thepathfinder.gmaps;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Leg {
    public Value distance;
    public Value duration;
    @SerializedName("end_address")
    public String endAddress;
    @SerializedName("end_location")
    public Coordinate endLocation;
    @SerializedName("start_address")
    public String startAddress;
    @SerializedName("start_location")
    public Coordinate startLocation;
    public List<Step> steps;
}
