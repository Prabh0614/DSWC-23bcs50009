import java.util.*;

abstract class EngineLog {
    String timestamp;
    double coreTemperature;
    boolean isAnomaly;

    public EngineLog(String timestamp,
                     double coreTemperature,
                     boolean isAnomaly) {
        this.timestamp = timestamp;
        this.coreTemperature = coreTemperature;
        this.isAnomaly = isAnomaly;
    }
}

class NominalLog extends EngineLog {

    public NominalLog(String timestamp,
                      double coreTemperature,
                      boolean isAnomaly) {
        super(timestamp, coreTemperature, isAnomaly);
    }
}

class CriticalLog extends EngineLog {

    String errorCode;

    public CriticalLog(String timestamp,
                       double coreTemperature,
                       boolean isAnomaly,
                       String errorCode) {
        super(timestamp, coreTemperature, isAnomaly);
        this.errorCode = errorCode;
    }
}

@FunctionalInterface
interface LogAuditor {
    boolean audit(EngineLog log);
}

@FunctionalInterface
interface HeatExtractor {
    double extract(EngineLog log);
}

class TelemetryProcessor {

    public double getPeakCriticalTemp(
            List<EngineLog> logs,
            LogAuditor auditor,
            HeatExtractor extractor) {

        return logs.stream()
                .filter(auditor::audit)
                .mapToDouble(extractor::extract)
                .max()
                .orElse(0.0);
    }
}

public class AstroDyneThrusterTelemetryAnalyzer {

    public static void main(String[] args) {

        List<EngineLog> logs = Arrays.asList(
                new NominalLog("10:00", 450.0, false),
                new CriticalLog("10:05", 900.0, false, "OVERHEAT"),
                new CriticalLog("10:10", 850.0, true, "ERR-101"),
                new NominalLog("10:15", 500.0, false)
        );

        LogAuditor auditor = log ->
                log.isAnomaly ||
                (log instanceof CriticalLog &&
                 ((CriticalLog) log).errorCode.equals("OVERHEAT"));

        HeatExtractor extractor = log -> log.coreTemperature;

        TelemetryProcessor processor = new TelemetryProcessor();

        double peakTemperature =
                processor.getPeakCriticalTemp(
                        logs,
                        auditor,
                        extractor
                );

        System.out.println(
                "Peak Critical Temperature: "
                        + peakTemperature
        );
    }
}