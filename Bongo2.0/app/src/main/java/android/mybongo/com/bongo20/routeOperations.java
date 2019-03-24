package android.mybongo.com.bongo20;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Cody Holmes
 * Raquib Talukder
 */

class routeOperations {
    List<LatLng> poly = new ArrayList<>();
    private List<LatLng> polySave = new ArrayList<>();
    List<LatLng> polyRoute = new ArrayList<>();
    private LatLng point;
    private LatLng polyClosest = null;
    private LatLng polyClosest2 = null;
    private Boolean firstround = true;
    private Boolean offroute = false;

    routeOperations() {}

    List<LatLng> putOnLine(LatLng point){
        this.point = point;
        if(firstround) {
            this.poly.add(this.point);
            if(!this.offroute) {this.polyRoute.add(this.point);}
            if (this.getDistanceBetweenPoints(this.point, this.poly.get(0)) < 50 && this.poly.size() > 5) {
                firstround = false;
                this.polyRoute.add(this.polyRoute.get(0));
                this.polyClosest = this.poly.get(0);
                this.polyClosest2 = this.poly.get(1);
                if(!this.offroute) {
                    this.polySave.clear();
                    this.polySave.addAll(this.polyRoute);
                }
            }
        } else{
            this.nextPoint();
            if(distanceToLine(this.polyClosest, this.polyClosest2) > 60){
                this.firstround = true;
                this.offroute = true;
                this.poly.clear();
            }else if(!this.polyClosest.equals(this.polyClosest2)){
                this.poly.add(this.poly.indexOf(this.polyClosest2), this.point);
            }
        }
        return this.poly;
    }

    List<LatLng> smooth() {
        List<LatLng> smoothedPoly = new ArrayList<>();
        List<LatLng> tempPoly = new ArrayList<>();
        LatLng first = this.poly.get(0);
        double latSum = 0;
        double lngSum = 0;

        for (int j = 0; j < this.poly.size(); j++) {
            if (this.getDistanceBetweenPoints(first, this.poly.get(j)) < 50) {
                tempPoly.add(this.poly.get(j));
            } else {
                for (int k = 0; k < tempPoly.size(); k++) {
                    latSum += tempPoly.get(k).latitude;
                    lngSum += tempPoly.get(k).longitude;
                }
                smoothedPoly.add(new LatLng(latSum / (double) tempPoly.size(), lngSum / (double) tempPoly.size()));
                latSum = 0;
                lngSum = 0;
                tempPoly = new ArrayList<>();
                first = this.poly.get(j);
                tempPoly.add(first);
            }
        }
        for (int k = 0; k < tempPoly.size(); k++) {
            latSum += tempPoly.get(k).latitude;
            lngSum += tempPoly.get(k).longitude;
        }
        smoothedPoly.add(new LatLng(latSum / (double) tempPoly.size(), lngSum / (double) tempPoly.size()));

        smoothedPoly.add(smoothedPoly.get(0));
        this.polyRoute.clear();
        this.polyRoute.addAll(smoothedPoly);
        this.poly.clear();
        this.poly.addAll(smoothedPoly);
        if(this.polySave.isEmpty() || !this.offroute) {
            this.polySave.clear();
            this.polySave.addAll(smoothedPoly);
        }else{
            this.offroute = false;
            this.poly.clear();
            this.poly.addAll(this.polySave);
        }
        return smoothedPoly;
    }

    private void nextPoint(){
        this.polyClosest = this.poly.get(0);
        for(int i = 0 ; i < this.poly.size(); i++){
            if(this.getDistanceBetweenPoints(point, this.poly.get(i)) <= this.getDistanceBetweenPoints(point, polyClosest)){
                polyClosest = this.poly.get(i);
            }//else break;
        }
        if(this.poly.indexOf(polyClosest) == 0){
            if(isBetween(point, polyClosest, this.poly.get(this.poly.size()-1))){
                polyClosest2 = polyClosest;
                polyClosest = this.poly.get(this.poly.size()-1);
            }
            else if(isBetween(point, polyClosest, this.poly.get(this.poly.indexOf(polyClosest)+1))){
                polyClosest2 = this.poly.get(this.poly.indexOf(polyClosest)+1);
            }
            else polyClosest2 = polyClosest;
        }else if(this.poly.indexOf(polyClosest) == this.poly.size()-1){
            if(isBetween(point, polyClosest, this.poly.get(0))){
                polyClosest2 = polyClosest;
                polyClosest = this.poly.get(0);
            }
            else if(isBetween(point, polyClosest, this.poly.get(this.poly.indexOf(polyClosest)-1))){
                polyClosest = this.poly.get(this.poly.indexOf(polyClosest)-1);
            }
            else polyClosest2 = polyClosest;
        }else if(isBetween(point, polyClosest, this.poly.get(this.poly.indexOf(polyClosest)-1))){
            polyClosest2 = polyClosest;
            polyClosest = this.poly.get(this.poly.indexOf(polyClosest)-1);
        }else if(isBetween(point, polyClosest, this.poly.get(this.poly.indexOf(polyClosest)+1))){
            polyClosest2 = this.poly.get(this.poly.indexOf(polyClosest)+1);
        }else{
            polyClosest2 = polyClosest;
        }
    }

    private boolean isBetween(LatLng point,LatLng point2, LatLng point3){
        double m = (point2.latitude - point3.latitude)/(point2.longitude - point3.longitude);
        double mp = ((double) 1 / m) * (double) -1;
        double bp = point.latitude - point.longitude * mp;
        double b1 = point2.latitude - point2.longitude * mp;
        double b2 = point3.latitude - point2.longitude * mp;
        return (bp < Math.max(b1,b2)) && (bp > Math.min(b1,b2));
    }

    private double getDistanceBetweenPoints(LatLng a, LatLng b){
        dataSave p1 = new dataSave(0, a.latitude, a.longitude, 0, "");
        return p1.distanceTo(new dataSave(0, b.latitude, b.longitude, 0, ""));
    }

    private double distanceToLine(LatLng close, LatLng close2){
        double m = (close.latitude - close2.latitude)/(close.longitude - close2.longitude);
        double mp = ((double) 1 / m) * (double) -1;
        double b = close2.latitude - close2.longitude * m;
        double bp = this.point.latitude - this.point.longitude * mp;
        double xn = (b-bp)/(mp-m);
        double yn = m * xn + b;
        LatLng pointOnLine = new LatLng(yn, xn);
        return this.getDistanceBetweenPoints(this.point, pointOnLine);
    }
}
