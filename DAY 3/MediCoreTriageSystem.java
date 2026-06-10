import java.util.PriorityQueue;

enum TriageLevel {
    CRITICAL,
    URGENT,
    STABLE
}

class Patient implements Comparable<Patient> {

    private String name;
    private TriageLevel severity;
    private long arrivalTime;

    public Patient(String name, TriageLevel severity, long arrivalTime) {
        this.name = name;
        this.severity = severity;
        this.arrivalTime = arrivalTime;
    }

    public String getName() {
        return name;
    }

    public TriageLevel getSeverity() {
        return severity;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public int compareTo(Patient other) {

        int severityCompare =
                Integer.compare(this.severity.ordinal(),
                                other.severity.ordinal());

        if (severityCompare != 0) {
            return severityCompare;
        }

        return Long.compare(this.arrivalTime, other.arrivalTime);
    }

    @Override
    public String toString() {
        return "Patient{name='" + name +
                "', severity=" + severity +
                ", arrivalTime=" + arrivalTime + "}";
    }
}

class TriageManager {

    private PriorityQueue<Patient> waitingRoom;

    public TriageManager() {
        waitingRoom = new PriorityQueue<>();
    }

    public void admitPatient(Patient p) {
        waitingRoom.offer(p);
    }

    public Patient getNextPatient() {
        return waitingRoom.poll();
    }
}

public class MediCoreTriageSystem {

    public static void main(String[] args) {

        TriageManager manager = new TriageManager();

        manager.admitPatient(
                new Patient("Alice", TriageLevel.URGENT, 1002)
        );

        manager.admitPatient(
                new Patient("Bob", TriageLevel.CRITICAL, 1005)
        );

        manager.admitPatient(
                new Patient("Charlie", TriageLevel.CRITICAL, 1001)
        );

        manager.admitPatient(
                new Patient("David", TriageLevel.STABLE, 1010)
        );

        while (true) {
            Patient next = manager.getNextPatient();

            if (next == null) {
                break;
            }

            System.out.println(next);
        }
    }
}