package xyz.thepathfinder.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.thepathfinder.android.Action;
import xyz.thepathfinder.android.Route;
import xyz.thepathfinder.android.Transport;
import xyz.thepathfinder.android.TransportListener;
import xyz.thepathfinder.gmaps.Coordinate;
import xyz.thepathfinder.gmaps.Directions;

import static xyz.thepathfinder.gmaps.Coordinate.distance;
import static xyz.thepathfinder.gmaps.Coordinate.moveTowards;

class SimulatedTransport extends TransportListener {
    private static final Logger log = LoggerFactory.getLogger(SimulatedTransport.class);
    private static final String gmapsApiKey = "AIzaSyAc73g_Rp73AdJQKRDgaI1ErvewEwbizP8";
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();
    private static final double DELTA = 0.001;
    private final List<Coordinate> path;
    private List<Action> actions;
    private Coordinate current;
    private int nextIndex;
    private Transport transport;

    private SimulatedTransport(List<String> addressLoop, List<Coordinate> path) {
        this.path = path;
        this.current = path.get(0);
        nextIndex = 1;
    }

    static SimulatedTransport create(List<String> addressLoop) throws IOException {
        return new SimulatedTransport(addressLoop, pathFromLoop(addressLoop));
    }

    static List<Coordinate> pathFromLoop(List<String> addressLoop) throws IOException {
        log.info(String.format("Creating loop from %d addresses: %s", addressLoop.size(), addressLoop));
        List<Coordinate> path = new ArrayList<>();
        for (int i = 0; i < addressLoop.size(); i++) {
            Directions d = getDirections(addressLoop.get(i), addressLoop.get((i + 1) % addressLoop.size()));
            path.addAll(d.routes.get(0).overviewPolyline.coordinates());
        }
        return path;
    }

    Coordinate start() {
        return this.path.get(0);
    }

    Coordinate next() {
        //if (transport == null) return start();
        move(DELTA);
        //notifyPathfinder();
        return this.current;
    }

    List<Coordinate> all() {
        return this.path;
    }

    void addTransport(Transport transport) {
        this.transport = transport;
    }

    private void notifyPathfinder() {
        transport.updateLocation(current.lat, current.lng);
    }

    private static Directions getDirections(String start, String end) throws IOException {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + start
                + "&destination="
                + end
                + "&key="
                + gmapsApiKey;
        log.info(String.format("Requesting directions from url: %s", url));
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return gson.fromJson(response.body().charStream(), Directions.class);
    }

    private void move(double delta) {
        if (delta < 0) return;
        double distanceToNext = distance(current, path.get(nextIndex));
        if (distanceToNext > delta) {
            current = moveTowards(path.get(nextIndex), current, delta);
        } else {
            current = path.get(nextIndex);
            nextIndex = (nextIndex + 1) % path.size();
            move(delta - distanceToNext);
        }
    }

    @Override
    public void routed(Route route) {
        log.info(String.format("Received new route: {}", route));
        route.getActions();
    }
}
