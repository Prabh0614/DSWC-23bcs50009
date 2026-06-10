import java.util.*;
import java.util.stream.Collectors;

abstract class DNASample {
    String sampleId;
    double purityPercentage;

    public DNASample(String sampleId, double purityPercentage) {
        this.sampleId = sampleId;
        this.purityPercentage = purityPercentage;
    }
}

class HumanSample extends DNASample {

    String bloodType;

    public HumanSample(String sampleId,
                       double purityPercentage,
                       String bloodType) {
        super(sampleId, purityPercentage);
        this.bloodType = bloodType;
    }
}

class AlienSample extends DNASample {

    boolean isSiliconBased;

    public AlienSample(String sampleId,
                       double purityPercentage,
                       boolean isSiliconBased) {
        super(sampleId, purityPercentage);
        this.isSiliconBased = isSiliconBased;
    }
}

@FunctionalInterface
interface ViabilityChecker {
    boolean check(DNASample sample);
}

@FunctionalInterface
interface GenomeMapper {
    String map(DNASample sample);
}

class Sequencer {

    public Map<String, List<String>> classifyGenomes(
            List<DNASample> samples,
            ViabilityChecker checker,
            GenomeMapper mapper) {

        return samples.stream()
                .filter(checker::check)
                .collect(Collectors.groupingBy(
                        sample -> sample.getClass().getSimpleName(),
                        Collectors.mapping(
                                mapper::map,
                                Collectors.toList()
                        )
                ));
    }
}

public class GeneWeaverDNASequencer {

    public static void main(String[] args) {

        List<DNASample> samples = Arrays.asList(
                new HumanSample("001", 90.0, "O-"),
                new HumanSample("002", 75.0, "A+"),
                new AlienSample("003", 88.0, true),
                new AlienSample("004", 65.0, false)
        );

        ViabilityChecker checker =
                sample -> sample.purityPercentage >= 80.0;

        GenomeMapper mapper = sample -> {

            if (sample instanceof HumanSample) {
                HumanSample h = (HumanSample) sample;
                return "ID: " + h.sampleId +
                        " (Type: " + h.bloodType + ")";
            }

            AlienSample a = (AlienSample) sample;
            return "ID: " + a.sampleId +
                    " (Silicon: " + a.isSiliconBased + ")";
        };

        Sequencer sequencer = new Sequencer();

        Map<String, List<String>> result =
                sequencer.classifyGenomes(
                        samples,
                        checker,
                        mapper
                );

        result.forEach((key, value) -> {
            System.out.println(key + " -> " + value);
        });
    }
}