package eu.cloudtm.autonomicManager.commons;


/**
 * Created by: Fabio Perfetti
 * E-mail: perfabio87@gmail.com
 * Date: 5/30/13
 */
public enum Forecaster {
    NONE(false, null),
    COMMITTEE(true, ""),
    ANALYTICAL(true, "tasOracle.TasOracle"),
    SIMULATOR(true, "eu.cloudtm.autonomicManager.oracles.FakeOracle"),
    MACHINE_LEARNING(true, "Morpher");

    private final boolean autoTuning;
    private final String oracleClass;

    private Forecaster(final boolean _autoScale, String _oracleClass) {
        autoTuning = _autoScale;
        oracleClass = _oracleClass;
    }

    public boolean isAutoTuning() {
        return autoTuning;
    }

    public String getOracleClass(){
        return oracleClass;
    }
}
