package eu.cloudtm.statistics;

import eu.cloudtm.commons.IPlatformConfiguration;
import eu.cloudtm.wpm.connector.WPMConnector;
import eu.cloudtm.wpm.logService.remote.listeners.WPMViewChangeRemoteListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 * User: fabio
 * Date: 7/11/13
 * Time: 6:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class WPMStatsManagerFactory {

    private static Log log = LogFactory.getLog(WPMStatsManagerFactory.class);

    private WPMStatisticsRemoteListernerFactory statisticsRemoteListernerFactory;

    private WPMViewChangeRemoteListener viewChangeRemoteListener;

    private IPlatformConfiguration platformConfiguration;

    public WPMStatsManagerFactory(IPlatformConfiguration platformConfiguration){
        this.platformConfiguration = platformConfiguration;

    }

    public WPMStatsManager build(){
        WPMConnector connector = null;

        while(connector == null){
            try {
                connector = new WPMConnector();
            } catch (Exception e) {
                log.warn("Check if the LogService is running and press enter to retry...");
                try {
                    System.in.read();
                } catch (IOException e1) {
                    throw new RuntimeException(e);
                }
                connector = null;
            }
        }

        WPMStatsManager wpmStatsManager = new WPMStatsManager();
        this.statisticsRemoteListernerFactory = new WPMStatisticsRemoteListernerFactory(connector, wpmStatsManager, platformConfiguration);
        viewChangeRemoteListener = new WPMViewChangeRemoteListenerImpl(connector, statisticsRemoteListernerFactory);

        return wpmStatsManager;
    }

    public WPMStatisticsRemoteListernerFactory getStatisticsRemoteListernerFactory(){
        return statisticsRemoteListernerFactory;
    }

}