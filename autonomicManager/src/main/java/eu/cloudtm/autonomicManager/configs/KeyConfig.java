package eu.cloudtm.autonomicManager.configs;

/**
 * User: Fabio Perfetti perfabio87 [at] gmail.com
 * Date: 7/22/13
 * Time: 11:20 AM
 */
public enum KeyConfig {

    WORKLOAD_ANALYZER_AUTOSTART("workloadAnalyzer.autoStart"),

    ALERT_MANAGER_POLICY("alertManager.policy"),


    /* ******* CLOUD-TM ACTUATOR ******* */
    DELTACLOUD_MAX_RETRIES("deltacloud.maxRetries"),
    DELTACLOUD_SECONDS_BETWEEN_RETRY("deltacloud.timeBetweenRetry"),


    DELTACLOUD_URL("deltacloud.url"),
    DELTACLOUD_USER("deltacloud.user"),
    DELTACLOUD_PASSWORD("deltacloud.password"),
    DELTACLOUD_IMAGE("deltacloud.image"),
    DELTACLOUD_FLAVOR("deltacloud.flavor"),
    DELTACLOUD_KEY("deltacloud.key"),

    ISPN_JMX_PORT("infinispan.jmxPort"),
    ISPN_DOMAIN("infinispan.domain"),
    ISPN_CACHE_NAME("infinispan.cacheName"),

    ISPN_ACTUATOR_FENIX_FRAMEWORK("infinispan.fenixFrameworkDomain"),
    ISPN_ACTUATOR_APPLICATION_NAME("infinispan.applicationName"),

    ISPN_ACTUATOR_FORCE_STOP("infinispan.forceStop"),
    ISPN_ACTUATOR_ABORT_ON_STOP("infinispan.abortOnStop"),

    RADARGUN_ACTUATOR("radargun.actuator"),
    RADARGUN_COMPONENT("radargun.component"),


    /* ******* RECONFIGURATOR ******* */
    RECONFIGURATOR_IGNORE_ERROR("reconfigurator.ignoreError"),
    RECONFIGURATOR_SIMULATE("reconfigurator.simulate"),
    RECONFIGURATOR_SECONDS_BETWEEN_RECONFIGURATIONS("reconfigurator.timeBetweenReconfiguration"),


    /* ******* CHANGE DETECTORs ******* */
    SLIDE_WINDOW_SIZE("changeDetector.slideWindowSize"),

    ;

    private final String key;

    private KeyConfig(String key){
        this.key = key;
    }

    public String key(){
        return this.key;
    }

}
