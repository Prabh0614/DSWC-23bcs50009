class PowerManager {

    private byte sectorStates = 0;

    public void turnOnSector(int sectorIndex) {
        sectorStates = (byte)(sectorStates | (1 << sectorIndex));
    }

    public void turnOffSector(int sectorIndex) {
        sectorStates = (byte)(sectorStates &
                              ~(1 << sectorIndex));
    }

    public boolean isSectorOn(int sectorIndex) {
        return (sectorStates & (1 << sectorIndex)) != 0;
    }

    public void printState() {
        for (int i = 7; i >= 0; i--) {
            System.out.print((sectorStates >> i) & 1);
        }
        System.out.println();
    }
}

public class ColonyGrid {
    public static void main(String[] args) {

        PowerManager pm = new PowerManager();

        pm.turnOnSector(0);
        pm.turnOnSector(3);
        pm.turnOnSector(7);

        System.out.print("After turning ON sectors: ");
        pm.printState();

        System.out.println(
            "Sector 3 ON? " +
            pm.isSectorOn(3)
        );

        System.out.println(
            "Sector 2 ON? " +
            pm.isSectorOn(2)
        );

        pm.turnOffSector(3);

        System.out.print("After turning OFF sector 3: ");
        pm.printState();
    }
}