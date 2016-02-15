package xyz.thepathfinder.simulator;

import com.google.gson.JsonObject;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.thepathfinder.android.Cluster;
import xyz.thepathfinder.android.ClusterListener;
import xyz.thepathfinder.android.Pathfinder;
import xyz.thepathfinder.android.Transport;
import xyz.thepathfinder.android.TransportStatus;
import xyz.thepathfinder.gmaps.Coordinate;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class SimulatorMap extends Application implements MapComponentInitializedListener {
    private static final Logger log = LoggerFactory.getLogger(SimulatorMap.class);

    private static Configuration config;
    private static List<String> addresses;
    static {
        try {
            config = new PropertiesConfiguration("config.properties");
            addresses = config.getList("loop.address").stream().map(Object::toString).collect(toList());
            log.info("Loop is " + addresses);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            log.error("Failed to load config.properties");
        }
    }

    private Pathfinder pf;
    private SimulatedTransport simulatedTransport;

    GoogleMapView mapView;
    GoogleMap map;

    @Override public void start(Stage stage) throws Exception {
        mapView = new GoogleMapView();
        mapView.addMapInializedListener(this);
        Scene scene = new Scene(mapView);
        stage.setTitle("Google Maps");
        stage.setScene(scene);
        stage.show();
        simulatedTransport = SimulatedTransport.create(addresses);

        pf = Pathfinder.create("9869bd06-12ec-451f-8207-2c5f217eb4d0");
        Cluster c = pf.getCluster("/root/midwest/th");
        c.connect();
        c.addListener(new ClusterListener() {
            boolean connected = false;

            @Override
            public void connected(Cluster cluster) {
                connected = true;
            }

            @Override
            public void transportAdded(Transport transport) {
                if (connected) {
                    log.info("Transport was created in Pathfinder: " + transport);
                    transport.routeSubscribe();
                    simulatedTransport.addTransport(transport);
                    transport.addListener(simulatedTransport);
                    c.unsubscribe();
                }
            }
        });
        c.createTransport(simulatedTransport.start().lat, simulatedTransport.start().lng, TransportStatus.ONLINE, new JsonObject());

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), new EventHandler<ActionEvent>() {
            Marker m;
            @Override public void handle(ActionEvent event) {
                if (m != null) {
                    map.removeMarker(m);
                }
                if (map != null) {
                    try {
                        m = addMarker(simulatedTransport.next());
                    } catch (IOException e) {
                        log.error("Oops, I failed to get the next simulated transport coordinate");
                        e.printStackTrace();
                    }
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void mapInitialized() {
        Coordinate start = null;
        try {
            start = simulatedTransport.next();
        } catch (IOException e) {
            log.error("Oops, I failed to get the next simulated transport coordinate");
            e.printStackTrace();
        }
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

    private Marker addMarker(Coordinate c) {
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
