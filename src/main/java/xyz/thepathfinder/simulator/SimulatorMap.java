package xyz.thepathfinder.simulator;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import xyz.thepathfinder.gmaps.Coordinate;
import xyz.thepathfinder.gmaps.Directions;

public class SimulatorMap extends Application implements MapComponentInitializedListener {
    private static final String origin = "5500 Wabash Ave, Terre Haute, IN 47803";
    private static final String destination = "3901 S 7th St, Terre Haute, IN 47802";
    GoogleMapView mapView;
    GoogleMap map;
    List<Coordinate> path;

    @Override public void start(Stage stage) throws Exception {
        mapView = new GoogleMapView();
        mapView.addMapInializedListener(this);
        Scene scene = new Scene(mapView);
        stage.setTitle("Google Maps");
        stage.setScene(scene);
        stage.show();
        Simulator sim = new Simulator(new OkHttpClient(), "AIzaSyAc73g_Rp73AdJQKRDgaI1ErvewEwbizP8");
        Directions d = sim.getDirections(origin, destination);
        path = d.routes.get(0).overviewPolyline.coordinates();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            int i = 0;
            @Override public void handle(ActionEvent event) {
                if (map != null) {
                    addMarker(i++);
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void mapInitialized() {
        Coordinate start = path.get(0);
        MapOptions mapOptions = new MapOptions();
        mapOptions.center(new LatLong(start.lat, start.lng))
            .mapType(MapTypeIdEnum.ROADMAP)
            .overviewMapControl(false)
            .panControl(false)
            .rotateControl(false)
            .scaleControl(false)
            .streetViewControl(false)
            .zoomControl(false)
            .zoom(12);
        map = mapView.createMap(mapOptions);
    }

    private Marker addMarker(int index) {
        Coordinate c = path.get(index);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLong(c.lat, c.lng))
            .visible(Boolean.TRUE);
        Marker marker = new Marker(markerOptions);
        map.addMarker(marker);
        return marker;
    }

    public static void main(String args[]) {
        launch(args);
    }
}
