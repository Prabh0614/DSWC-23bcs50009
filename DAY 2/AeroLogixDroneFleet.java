abstract class DeliveryDrone {
    protected String droneId;

    public DeliveryDrone(String droneId) {
        this.droneId = droneId;
    }

    public abstract void deliverPackage();
}

interface Airborne {

    void flyToDestination();

    default void requestAirTrafficClearance() {
        System.out.println("Air Traffic Clearance Granted.");
    }
}

interface GroundBased {
    void navigateSidewalks();
}

class Quadcopter extends DeliveryDrone implements Airborne {

    public Quadcopter(String droneId) {
        super(droneId);
    }

    @Override
    public void flyToDestination() {
        System.out.println("Quadcopter " + droneId + " flying to destination.");
    }

    @Override
    public void deliverPackage() {
        requestAirTrafficClearance();
        flyToDestination();
        System.out.println("Package delivered by Quadcopter.");
    }
}

class CityRover extends DeliveryDrone implements GroundBased {

    public CityRover(String droneId) {
        super(droneId);
    }

    @Override
    public void navigateSidewalks() {
        System.out.println("CityRover " + droneId + " navigating sidewalks.");
    }

    @Override
    public void deliverPackage() {
        navigateSidewalks();
        System.out.println("Package delivered by CityRover.");
    }
}

class HybridVTOL extends DeliveryDrone
        implements Airborne, GroundBased {

    public HybridVTOL(String droneId) {
        super(droneId);
    }

    @Override
    public void flyToDestination() {
        System.out.println("HybridVTOL " + droneId + " flying.");
    }

    @Override
    public void navigateSidewalks() {
        System.out.println("HybridVTOL " + droneId + " driving on ground.");
    }

    @Override
    public void deliverPackage() {
        requestAirTrafficClearance();
        flyToDestination();
        navigateSidewalks();
        System.out.println("Package delivered by HybridVTOL.");
    }
}

public class AeroLogixDroneFleet {

    public static void main(String[] args) {

        DeliveryDrone[] fleet = {
                new Quadcopter("Q101"),
                new CityRover("R101"),
                new HybridVTOL("H101")
        };

        for (DeliveryDrone drone : fleet) {
            drone.deliverPackage();
            System.out.println();
        }
    }
}