package eu.cloudtm.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/19/13
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SampleProducer {

    private List<SampleListener> listeners = new ArrayList<SampleListener>();

    public void removeListener(SampleListener listener){
        listeners.remove(listener);
    }

    public void addListener(SampleListener listener){
        listeners.add(listener);
    }

    public void notify(ProcessedSample sample){
        for(SampleListener sampleListener : listeners){
            sampleListener.onNewSample(sample);
        }
    }

}
