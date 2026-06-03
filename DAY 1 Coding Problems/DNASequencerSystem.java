class AlienDNAAnalyzer {

    private StringBuilder dnaSequence;

    public AlienDNAAnalyzer(int expectedSize) {
        dnaSequence = new StringBuilder(expectedSize);
    }

    public void ingestSequence(char[] sensorData) {
        for (char c : sensorData) {
            dnaSequence.append(c);
        }
    }

    public void mutateDNA(String target, String replacement) {
        int index = dnaSequence.indexOf(target);

        if (index != -1) {
            dnaSequence.replace(
                index,
                index + target.length(),
                replacement
            );
        }
    }

    public String getSequence() {
        return dnaSequence.toString();
    }
}

public class DNASequencerSystem {

    public static void main(String[] args) {

        AlienDNAAnalyzer analyzer =
            new AlienDNAAnalyzer(100000);

        char[] sensorData =
            {'A', 'C', 'T', 'G', 'A', 'C', 'T'};

        analyzer.ingestSequence(sensorData);

        System.out.println(
            "Original DNA: " +
            analyzer.getSequence()
        );

        analyzer.mutateDNA("ACT", "GGG");

        System.out.println(
            "Mutated DNA: " +
            analyzer.getSequence()
        );
    }
}