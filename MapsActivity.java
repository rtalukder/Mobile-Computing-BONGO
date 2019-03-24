package android.mybongo.com.bongo20;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    int lineColor = Color.BLUE;
    Polyline polyLine;
    Marker foundBus;
    Button button;
    List<LatLng> points = new ArrayList<>();
    BlockingQueue<dataSave> dataQueue = new LinkedBlockingQueue<>(20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Thread collect = new Thread(new dataReader(dataQueue, getApplicationContext()));
        collect.start();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //new additions from james

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1);

        button = new Button(this);button.setText("Blue Route");button.setX(0); button.setY(0);
        addContentView(button, layoutParams);

//end of new additions


    }

    public void handlePoints(List<LatLng> points){
        this.points = points;
        this.polyLine.setPoints(this.points);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng cambus = new LatLng(41.659887,-91.543686);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cambus, 14));

        polyLine = googleMap.addPolyline(new PolylineOptions().width(5).color(lineColor));
        foundBus = googleMap.addMarker(new MarkerOptions().position(new LatLng(41.654780,-91.538152)).title("Bus").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));

        Thread findBus = new Thread(new findTheBus(this.dataQueue, this));
        findBus.start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                polyLine.setVisible(!polyLine.isVisible());
            }
        });
    }
}
