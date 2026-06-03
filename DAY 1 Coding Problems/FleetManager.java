abstract class SpaceVessel {
    protected short shipId;
    protected boolean operationalStatus;
    protected char fleetClassification;

    public SpaceVessel(short shipId,
                       boolean operationalStatus,
                       char fleetClassification) {
        this.shipId = shipId;
        this.operationalStatus = operationalStatus;
        this.fleetClassification = fleetClassification;
    }
}

class MiningShip extends SpaceVessel {

    private float[][] cargoHold;

    public MiningShip(short shipId,
                      boolean operationalStatus,
                      char fleetClassification,
                      float[][] cargoHold) {

        super(shipId, operationalStatus, fleetClassification);
        this.cargoHold = cargoHold;
    }

    public float calculateTotalOreWeight() {
        float total = 0;

        for (int i = 0; i < cargoHold.length; i++) {
            for (int j = 0; j < cargoHold[i].length; j++) {
                total += cargoHold[i][j];
            }
        }

        return total;
    }

    public float findHeaviestContainer() {
        float max = Float.MIN_VALUE;

        for (int i = 0; i < cargoHold.length; i++) {
            for (int j = 0; j < cargoHold[i].length; j++) {
                if (cargoHold[i][j] > max) {
                    max = cargoHold[i][j];
                }
            }
        }

        return max;
    }
}

public class FleetManager {
    public static void main(String[] args) {

        SpaceVessel[] fleet = new SpaceVessel[3];

        float[][] cargo = {
            {1200.5f, 2500.75f},
            {3400.0f, 5000.25f},
            {1500.0f}
        };

        MiningShip ship1 =
            new MiningShip((short)1001, true, 'A', cargo);

        fleet[0] = ship1;

        System.out.println(
            "Total Ore Weight = "
            + ship1.calculateTotalOreWeight()
            + " kg");

        System.out.println(
            "Heaviest Container = "
            + ship1.findHeaviestContainer()
            + " kg");
    }
}