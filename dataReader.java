package android.mybongo.com.bongo20;

/**
 * Cody Holmes
 * Raquib Talukder
 */

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

class dataReader implements Runnable{
    private BlockingQueue<dataSave> dataQueue;
    private Context context;

    dataReader(BlockingQueue<dataSave> myQueue, Context context) {
        this.dataQueue = myQueue;
        this.context = context;
    }

    public void run(){
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("Test1.csv")))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] lineData = line.split(cvsSplitBy);
                double GPSTS = 0;
                double lat = Double.valueOf(lineData[3]);
                double longit = Double.valueOf(lineData[4]);
                double speed = Double.valueOf(lineData[5]);
                String routeName = lineData[8];
                dataSave curData = new dataSave(GPSTS, lat, longit, speed, routeName);
                try {
                    dataQueue.put(curData);
                }catch(Exception e){
                    return;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}