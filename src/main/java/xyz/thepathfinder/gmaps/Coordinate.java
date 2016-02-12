package xyz.thepathfinder.gmaps;

public class Coordinate {
    public double lat;
    public double lng;

    Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override public String toString() {
        return "Coordinate("+lat+","+lng+")";
    }
}
