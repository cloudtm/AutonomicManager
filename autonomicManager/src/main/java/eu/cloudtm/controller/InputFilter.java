package eu.cloudtm.controller;

import eu.cloudtm.StatsManager;
import eu.cloudtm.stats.Sample;
import eu.cloudtm.wpm.parser.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 6/16/13
 */
public class InputFilter {

    private static Log log = LogFactory.getLog(InputFilter.class);

    private StatsManager statsManager;

    /* *** SOGLIE *** */
    private static final int DELTA_ARRIVAL_RATE = 2;
    private double lastAvgArrivalRate = 0.0;

    private static final int DELTA_ABORT_RATE = 2;
    private double lastAvgAbortRate = 0.0;

    private static final int DELTA_RESPONSE_TIME = 2;
    private double lastAvgResposeTime = 0.0;

//    /* *** VALORI ATTUALI *** */
//    private double currentArrivalRate;
//    private double current;
//    private double currentThroughput;

    public InputFilter(StatsManager _statsManager){
        statsManager = _statsManager;
    }

    public boolean doFilter(){
        List<Sample> lastNSamples = statsManager.getLastNSample( Controller.SAMPLE_WINDOW );
        boolean reconfigure;

        boolean arrivalRateResponse = evaluateArrivalRate(lastNSamples);
        boolean responseTimeResponse = evaluateResponseTime(lastNSamples);
        boolean abortRateResponse = evaluateAbortRate(lastNSamples);

        reconfigure =  arrivalRateResponse || responseTimeResponse || abortRateResponse;

        return reconfigure;
    }

    private boolean evaluateAbortRate(List<Sample> lastNSamples){
        double abortSum = 0.0;
        for (Sample sample : lastNSamples){
            abortSum += (1 - StatsManager.getAvgAttribute("CommitProbability", sample, ResourceType.JMX));
        }
        double currentAbortAvg =  abortSum / ((double) lastNSamples.size());
        log.trace("currentAbortAvg: " + currentAbortAvg);
        log.trace("lastAvgAbortRate: " + lastAvgAbortRate);

        if(lastAvgAbortRate == 0 || lastAvgAbortRate == Double.NaN){
            log.info("Updating && Skipping lastAvgAbortRate");
            lastAvgAbortRate = currentAbortAvg;
        } else {
            double rapporto = ( currentAbortAvg / lastAvgAbortRate ) * 100;
            log.info("rapporto: " + rapporto );

            double variazione = Math.abs(rapporto - 100);
            log.info("variazione: " + variazione );

            if( variazione >= DELTA_ABORT_RATE ){
                log.info("Update the lastAvgAbortRate");
                lastAvgAbortRate = currentAbortAvg;
                log.trace("SOGLIA RAGGIUNTA x AbortRate");
                return true;
            }
        }
        return false;
    }

    private boolean evaluateResponseTime(List<Sample> lastNSamples){
        return false;
    }

    private boolean evaluateArrivalRate(List<Sample> lastNSamples){
        double throughputSum = 0.0;
        for (Sample sample : lastNSamples){
            throughputSum += StatsManager.getAvgAttribute("Throughput", sample, ResourceType.JMX);
        }
        double currentThroughputAvg =  throughputSum / ((double) lastNSamples.size());
        log.trace("currentThroughputAvg: " + currentThroughputAvg);
        log.trace("lastAvgAbortRate: " + lastAvgArrivalRate);

        if(lastAvgArrivalRate == 0 || lastAvgArrivalRate == Double.NaN){
            log.info("Updating && Skipping lastAvgArrivalRate");
            lastAvgArrivalRate = currentThroughputAvg;
        } else {
            double rapporto = ( currentThroughputAvg / lastAvgArrivalRate ) * 100;
            log.info("rapporto: " + rapporto );

            double variazione = Math.abs(rapporto - 100);
            log.info("variazione: " + variazione );

            if( variazione >= DELTA_ARRIVAL_RATE ){
                log.info("Update the lastAvgArrivalRate");
                lastAvgArrivalRate = currentThroughputAvg;
                log.trace("SOGLIA RAGGIUNTA x ArrivalRate");
                return true;
            }
        }
        return false;
    }


    /* *** GETTER PER I VALORI ATTUALI *** */

    public double getLastAvgResposeTime() {
        return lastAvgResposeTime;
    }

    public double getLastAvgAbortRate() {
        return lastAvgAbortRate;
    }

    public double getLastAvgArrivalRate() {
        return lastAvgArrivalRate;
    }

}