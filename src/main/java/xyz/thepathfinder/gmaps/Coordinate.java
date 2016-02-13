package xyz.thepathfinder.gmaps;

public class Coordinate {
    public double lat;
    public double lng;

    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override public String toString() {
        return "Coordinate("+lat+","+lng+")";
    }

    public static double distance(Coordinate c1, Coordinate c2) {
        return Math.sqrt(Math.pow(c1.lat - c2.lat, 2) + Math.pow(c1.lng - c2.lng, 2));
    }

    public static Coordinate moveTowards(Coordinate end, Coordinate start, double delta) {
        double lat = start.lat + (end.lat - start.lat) / distance(start, end) * delta;
        double lng = start.lng + (end.lng - start.lng) / distance(start, end) * delta;
        return new Coordinate(lat, lng);
    }
}
