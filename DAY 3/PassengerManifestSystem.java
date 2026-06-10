import java.util.*;

class Passenger {
    private String passportNumber;
    private String fullName;
    private String nationality;

    public Passenger(String passportNumber, String fullName, String nationality) {
        this.passportNumber = passportNumber;
        this.fullName = fullName;
        this.nationality = nationality;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getNationality() {
        return nationality;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Passenger)) return false;

        Passenger p = (Passenger) obj;

        return Objects.equals(passportNumber, p.passportNumber)
                && Objects.equals(nationality, p.nationality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passportNumber, nationality);
    }
}

class ManifestManager {

    private Set<Passenger> globalNoFlyList = new HashSet<>();

    private Map<String, List<Passenger>> flightRosters = new HashMap<>();

    private Map<Passenger, String> globalPassengerDirectory = new HashMap<>();

    public void addToNoFlyList(Passenger p) {
        globalNoFlyList.add(p);
    }

    public void checkInPassenger(String flightNumber, Passenger p) {
        flightRosters
                .computeIfAbsent(flightNumber, k -> new ArrayList<>())
                .add(p);
    }

    public boolean processCheckIn(String flightNumber, Passenger p) {

        if (globalNoFlyList.contains(p)) {
            return false;
        }

        checkInPassenger(flightNumber, p);
        globalPassengerDirectory.put(p, flightNumber);

        return true;
    }

    public String locatePassengerFlight(Passenger p) {
        return globalPassengerDirectory.get(p);
    }
}

public class PassengerManifestSystem {

    public static void main(String[] args) {

        ManifestManager manager = new ManifestManager();

        Passenger alice = new Passenger(
                "P12345",
                "Alice Johnson",
                "USA"
        );

        Passenger banned = new Passenger(
                "P99999",
                "John Doe",
                "USA"
        );

        manager.addToNoFlyList(banned);

        System.out.println(
                "Alice Check-In: "
                        + manager.processCheckIn("AI101", alice)
        );

        System.out.println(
                "Banned Passenger Check-In: "
                        + manager.processCheckIn("AI101", banned)
        );

        Passenger searchAlice = new Passenger(
                "P12345",
                "Alice Smith",
                "USA"
        );

        System.out.println(
                "Alice Flight: "
                        + manager.locatePassengerFlight(searchAlice)
        );
    }
}