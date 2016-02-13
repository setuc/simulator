package xyz.thepathfinder.simulator;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.thepathfinder.android.Pathfinder;
import xyz.thepathfinder.gmaps.Coordinate;

import java.util.Arrays;

public class SimulatorMap extends Application implements MapComponentInitializedListener {
    private static final Logger log = LoggerFactory.getLogger(SimulatorMap.class);
    private static final String a = "5500 Wabash Ave, Terre Haute, IN 47803";
    private static final String b = "3901 S 7th St, Terre Haute, IN 47802";
    private static final String c = "1300-1324 N 3rd St, Terre Haute, IN 47807";

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
        simulatedTransport = SimulatedTransport.create(Arrays.asList(a, b, c));
        /*
        pf = new Pathfinder("9869bd06-12ec-451f-8207-2c5f217eb4d0", "abc");
        pf.connect();
        Cluster c = pf.getCluster("/root/midwest/th");
        c.addListener(new ClusterListener() {
            public void transportAdded(Transport transport) {
                log.info("Transport was created in Pathfinder");
                simulatedTransport.addTransport(transport);
                transport.addListener(simulatedTransport);
            }
        });
        c.connect();
        c.createTransport("", path.get(0).lat, path.get(0).lng, TransportStatus.OFFLINE, new JsonObject());
        */
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
            int i = 0;
            Marker m;
            @Override public void handle(ActionEvent event) {
                if (m != null) {
                    map.removeMarker(m);
                }
                if (map != null) {
                    m = addMarker(i++);
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void mapInitialized() {
        Coordinate start = simulatedTransport.next();
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
        Coordinate c = simulatedTransport.next();
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
