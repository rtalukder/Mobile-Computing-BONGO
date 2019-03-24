package android.mybongo.com.bongo20;

/**
 * Cody Holmes
 * Raquib Talukder
 */

class dataSave {
    private final double GPSTS;
    final double lat;
    final double longit;
    final String routeName;
    final double speed;

    dataSave(double GPSTS, double lat, double longit, double speed, String routeName){
        this.GPSTS = GPSTS;
        this.lat = lat;
        this.longit = longit;
        this.speed = speed;
        this.routeName = routeName;
    }

    double distanceTo(dataSave myObj){
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(myObj.lat-this.lat);
        double lngDiff = Math.toRadians(myObj.longit-this.longit);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(myObj.lat)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;
        distance = distance * (double) 1609;
        return distance;
    }

    @Override
    public String toString(){
        return "GPS Timestamp: " + GPSTS +
                " Lat: " + lat +
                " Long: " + longit +
                " Speed: " + speed;
    }
}
