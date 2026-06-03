class HardwareLockException extends Exception {

    public HardwareLockException(String message) {
        super(message);
    }
}

class SensorCorruptionException extends RuntimeException {

    public SensorCorruptionException(String message) {
        super(message);
    }
}

class TelemetryStream implements AutoCloseable {

    public void readData() {
        System.out.println("Reading telemetry data...");
    }

    @Override
    public void close() {
        System.out.println("Telemetry stream closed.");
    }
}

class DeepSeaTelemetryParser {

    public void parseTelemetry(boolean fileLocked,
                               int temperature)
            throws HardwareLockException {

        try (TelemetryStream stream = new TelemetryStream()) {

            stream.readData();

            if (fileLocked) {
                throw new HardwareLockException(
                        "Telemetry file is locked."
                );
            }

            if (temperature > 100) {
                throw new SensorCorruptionException(
                        "Impossible temperature detected: "
                                + temperature + " C"
                );
            }

            System.out.println(
                    "Telemetry parsed successfully."
            );
        }
    }
}

public class DeepSeaTelemetrySystem {

    public static void main(String[] args) {

        DeepSeaTelemetryParser parser =
                new DeepSeaTelemetryParser();

        try {

            parser.parseTelemetry(false, 500);

        } catch (HardwareLockException e) {

            System.out.println(
                    "Checked Exception: "
                            + e.getMessage()
            );

        } catch (SensorCorruptionException e) {

            System.out.println(
                    "Unchecked Exception: "
                            + e.getMessage()
            );
        }
    }
}