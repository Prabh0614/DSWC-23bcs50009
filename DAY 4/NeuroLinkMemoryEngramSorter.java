import java.util.*;
import java.util.stream.Collectors;

abstract class MemoryEngram {
    String engramId;
    double clarityScore;
    boolean isCorrupted;

    public MemoryEngram(String engramId,
                        double clarityScore,
                        boolean isCorrupted) {
        this.engramId = engramId;
        this.clarityScore = clarityScore;
        this.isCorrupted = isCorrupted;
    }
}

class StandardEngram extends MemoryEngram {
    public StandardEngram(String engramId,
                          double clarityScore,
                          boolean isCorrupted) {
        super(engramId, clarityScore, isCorrupted);
    }
}

class ClassifiedEngram extends MemoryEngram {
    int securityClearanceLevel;

    public ClassifiedEngram(String engramId,
                            double clarityScore,
                            boolean isCorrupted,
                            int securityClearanceLevel) {
        super(engramId, clarityScore, isCorrupted);
        this.securityClearanceLevel = securityClearanceLevel;
    }
}

@FunctionalInterface
interface EngramValidator {
    boolean validate(MemoryEngram engram);
}

@FunctionalInterface
interface EngramTranslator {
    String translate(MemoryEngram engram);
}

class CortexProcessor {

    public List<String> processMemories(
            List<MemoryEngram> engrams,
            EngramValidator validator,
            EngramTranslator translator) {

        return engrams.stream()
                .filter(validator::validate)
                .filter(e -> e.clarityScore >= 50.0)
                .map(translator::translate)
                .collect(Collectors.toList());
    }
}

public class NeuroLinkMemoryEngramSorter {

    public static void main(String[] args) {

        List<MemoryEngram> engrams = Arrays.asList(
                new StandardEngram("E101", 80, false),
                new ClassifiedEngram("E102", 90, false, 5),
                new ClassifiedEngram("E103", 70, false, 2),
                new StandardEngram("E104", 40, false),
                new StandardEngram("E105", 95, true)
        );

        EngramValidator validator = engram -> {
            if (engram.isCorrupted) {
                return false;
            }

            if (engram instanceof ClassifiedEngram &&
                ((ClassifiedEngram) engram)
                        .securityClearanceLevel > 3) {
                return false;
            }

            return true;
        };

        EngramTranslator translator =
                engram -> "ENGRAM-" + engram.engramId
                        + " | CLARITY: "
                        + engram.clarityScore + "%";

        CortexProcessor processor = new CortexProcessor();

        List<String> safeMemories =
                processor.processMemories(
                        engrams,
                        validator,
                        translator
                );

        safeMemories.forEach(System.out::println);
    }
}