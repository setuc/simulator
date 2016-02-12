package xyz.thepathfinder.simulator;

import com.google.gson.Gson;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import xyz.thepathfinder.gmaps.Coordinate;
import xyz.thepathfinder.gmaps.Directions;

public class Simulator {
    private static final Gson gson = new Gson();

    private final String apiKey;
    private final OkHttpClient client;

    Simulator(OkHttpClient client, String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    Directions getDirections(String start, String end) throws IOException {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
            + start
            + "&destination="
            + end
            + "&key="
            + apiKey;
        Request request = new Request.Builder()
            .url(url)
            .build();
        Response response = client.newCall(request).execute();
        return gson.fromJson(response.body().charStream(), Directions.class);
    }

    public static void main(String args[]) throws IOException {
        Simulator sim = new Simulator(
            new OkHttpClient(), "AIzaSyAc73g_Rp73AdJQKRDgaI1ErvewEwbizP8");
        Directions d = sim.getDirections("75 9th Ave, New York, NY", "MetLife Stadium Dr East Rutherford, NJ 07073");
        System.out.println(d);
        System.out.println(d.routes.size());
        System.out.println(d.routes.get(0).overviewPolyline);
        System.out.println(d.routes.get(0).overviewPolyline.points);
        List<Coordinate> coordinates = d.routes.get(0).overviewPolyline.coordinates();
        System.out.println(coordinates.size());
        System.out.println(coordinates);
    }
}
