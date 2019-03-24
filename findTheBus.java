package android.mybongo.com.bongo20;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Cody Holmes
 * Raquib Talukder
 */

class findTheBus implements Runnable {
    private List<LatLng> polyLine = new ArrayList<>();
    private BlockingQueue<dataSave> dataQueue;
    private MapsActivity activity;

    findTheBus(BlockingQueue<dataSave> dataQueue, MapsActivity activity){
        this.dataQueue = dataQueue;
        this.activity = activity;
    }

    private void handlePoint(LatLng roadPoint, routeOperations route){
        if(route.poly.size() > 1){
            route.putOnLine(roadPoint);
        }
        else{
            route.poly.add(roadPoint);
        }
    }

    public void run(){
        dataSave save;
        routeOperations route = new routeOperations();

        while(dataQueue.peek() != null){
            try{
                Thread.sleep(70);
            }catch (Exception e){
                return;
            }
            save = dataQueue.poll();
            final dataSave finalSave = save;

            activity.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    activity.foundBus.setPosition(new LatLng(finalSave.lat, finalSave.longit));
                }
            });
            handlePoint(new LatLng(finalSave.lat, finalSave.longit), route);

            if(route.poly.size() >= 200){
                route.smooth();
                this.polyLine.clear();
                this.polyLine.addAll(route.polyRoute);
                activity.runOnUiThread(new Runnable() {
                    public void run(){
                        activity.handlePoints(polyLine);
                    }
                });
            }
        }
    }
}
