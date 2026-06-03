import java.util.concurrent.atomic.AtomicInteger;

class DroneHive {

    private AtomicInteger totalDronesReturned =
            new AtomicInteger(0);

    private volatile boolean emergencyAbort = false;

    public void droneLanded() {
        totalDronesReturned.incrementAndGet();
    }

    public int getTotalDronesReturned() {
        return totalDronesReturned.get();
    }

    public void triggerEmergencyAbort() {
        emergencyAbort = true;
    }

    public boolean isEmergencyAbort() {
        return emergencyAbort;
    }
}

class Drone extends Thread {

    private DroneHive hive;

    public Drone(DroneHive hive) {
        this.hive = hive;
    }

    @Override
    public void run() {

        if (hive.isEmergencyAbort()) {
            System.out.println(
                    Thread.currentThread().getName()
                            + " changing route due to storm."
            );
            return;
        }

        hive.droneLanded();
    }
}

public class DroneHiveSynchronization {

    public static void main(String[] args)
            throws InterruptedException {

        DroneHive hive = new DroneHive();

        int droneCount = 100;

        Drone[] drones = new Drone[droneCount];

        for (int i = 0; i < droneCount; i++) {
            drones[i] = new Drone(hive);
            drones[i].start();
        }

        // Simulate storm detection
        hive.triggerEmergencyAbort();

        for (int i = 0; i < droneCount; i++) {
            drones[i].join();
        }

        System.out.println(
                "Total Drones Returned: "
                        + hive.getTotalDronesReturned()
        );

        System.out.println(
                "Emergency Abort Status: "
                        + hive.isEmergencyAbort()
        );
    }
}