import java.util.*;
import java.util.stream.Collectors;

abstract class TemporalEntity {
    String entityName;
    int originYear;

    public TemporalEntity(String entityName, int originYear) {
        this.entityName = entityName;
        this.originYear = originYear;
    }
}

class HumanEntity extends TemporalEntity {

    public HumanEntity(String entityName, int originYear) {
        super(entityName, originYear);
    }
}

class ArtifactEntity extends TemporalEntity {

    boolean isRadioactive;

    public ArtifactEntity(String entityName,
                          int originYear,
                          boolean isRadioactive) {
        super(entityName, originYear);
        this.isRadioactive = isRadioactive;
    }
}

class HistoricalEvent {

    List<TemporalEntity> entities;
    int eventYear;

    public HistoricalEvent(List<TemporalEntity> entities,
                           int eventYear) {
        this.entities = entities;
        this.eventYear = eventYear;
    }
}

@FunctionalInterface
interface ParadoxChecker {
    boolean check(TemporalEntity entity, int eventYear);
}

@FunctionalInterface
interface ThreatMapper {
    String map(TemporalEntity entity);
}

class EntityContext {

    TemporalEntity entity;
    int eventYear;

    public EntityContext(TemporalEntity entity, int eventYear) {
        this.entity = entity;
        this.eventYear = eventYear;
    }
}

class ParadoxAnalyzer {

    public List<String> detectParadoxes(
            List<HistoricalEvent> timeline,
            ParadoxChecker checker,
            ThreatMapper mapper) {

        return timeline.stream()
                .flatMap(event ->
                        event.entities.stream()
                                .map(entity ->
                                        new EntityContext(
                                                entity,
                                                event.eventYear)))
                .filter(context ->
                        checker.check(
                                context.entity,
                                context.eventYear))
                .map(context ->
                        mapper.map(context.entity))
                .collect(Collectors.toList());
    }
}

public class ChronoCorpTemporalParadoxDetector {

    public static void main(String[] args) {

        List<HistoricalEvent> timeline = Arrays.asList(

                new HistoricalEvent(
                        Arrays.asList(
                                new HumanEntity("John", 2050),
                                new ArtifactEntity(
                                        "Ancient Relic",
                                        1800,
                                        false)
                        ),
                        2025
                ),

                new HistoricalEvent(
                        Arrays.asList(
                                new HumanEntity("Sarah", 1990),
                                new HumanEntity("Future Agent", 2100)
                        ),
                        2000
                )
        );

        ParadoxChecker checker =
                (entity, eventYear) ->
                        entity.originYear > eventYear;

        ThreatMapper mapper =
                entity ->
                        entity.entityName
                                + " detected out of time!";

        ParadoxAnalyzer analyzer =
                new ParadoxAnalyzer();

        List<String> paradoxes =
                analyzer.detectParadoxes(
                        timeline,
                        checker,
                        mapper
                );

        paradoxes.forEach(System.out::println);
    }
}