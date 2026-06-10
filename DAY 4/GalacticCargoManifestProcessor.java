import java.util.*;
import java.util.stream.Collectors;

abstract class Cargo {
    String containerId;
    double valueInCredits;
    boolean isHazardous;

    public Cargo(String containerId, double valueInCredits, boolean isHazardous) {
        this.containerId = containerId;
        this.valueInCredits = valueInCredits;
        this.isHazardous = isHazardous;
    }
}

class StandardCargo extends Cargo {
    public StandardCargo(String containerId, double valueInCredits, boolean isHazardous) {
        super(containerId, valueInCredits, isHazardous);
    }
}

class BiologicalCargo extends Cargo {
    boolean isShielded;

    public BiologicalCargo(String containerId, double valueInCredits,
                           boolean isHazardous, boolean isShielded) {
        super(containerId, valueInCredits, isHazardous);
        this.isShielded = isShielded;
    }
}

@FunctionalInterface
interface CargoInspector {
    boolean inspect(Cargo cargo);
}

@FunctionalInterface
interface CargoCompressor {
    String compress(Cargo cargo);
}

class ManifestProcessor {

    public List<String> processManifest(List<Cargo> manifest,
                                        CargoInspector inspector,
                                        CargoCompressor compressor) {

        return manifest.stream()
                .filter(inspector::inspect)
                .filter(c -> c.valueInCredits >= 1000.0)
                .map(compressor::compress)
                .collect(Collectors.toList());
    }
}

public class GalacticCargoManifestProcessor {

    public static void main(String[] args) {

        List<Cargo> manifest = Arrays.asList(
                new StandardCargo("ALPHA-99", 5000.50, false),
                new BiologicalCargo("BETA-77", 8000, true, false),
                new BiologicalCargo("GAMMA-88", 12000, true, true),
                new StandardCargo("DELTA-11", 500, false)
        );

        CargoInspector inspector = cargo -> {
            if (cargo.isHazardous &&
                    cargo instanceof BiologicalCargo &&
                    !((BiologicalCargo) cargo).isShielded) {
                return false;
            }
            return true;
        };

        CargoCompressor compressor =
                cargo -> cargo.containerId.substring(0, 4)
                        + "-" + (int) cargo.valueInCredits;

        ManifestProcessor processor = new ManifestProcessor();

        List<String> result =
                processor.processManifest(manifest, inspector, compressor);

        result.forEach(System.out::println);
    }
}