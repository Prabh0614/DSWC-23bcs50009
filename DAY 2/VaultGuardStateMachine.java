enum DoorState {
    OPEN,
    CLOSED,
    LOCKED
}

class IllegalStateTransitionException extends RuntimeException {

    public IllegalStateTransitionException(String message) {
        super(message);
    }
}

class VaultDoor {

    private DoorState state;

    public VaultDoor() {
        state = DoorState.OPEN;
    }

    public void closeDoor() {

        if (state == DoorState.OPEN) {
            state = DoorState.CLOSED;
            System.out.println("Door closed.");
        } else {
            System.out.println("Door is already closed or locked.");
        }
    }

    public void lockDoor() {

        if (state != DoorState.CLOSED) {
            throw new IllegalStateTransitionException(
                    "Cannot lock the door while it is OPEN. Close it first."
            );
        }

        state = DoorState.LOCKED;
        System.out.println("Door locked.");
    }

    public void unlockDoor() {

        if (state == DoorState.LOCKED) {
            state = DoorState.CLOSED;
            System.out.println("Door unlocked.");
        } else {
            System.out.println("Door is not locked.");
        }
    }

    public DoorState getCurrentState() {
        return state;
    }
}

public class VaultGuardStateMachine {

    public static void main(String[] args) {

        VaultDoor vaultDoor = new VaultDoor();

        try {

            System.out.println("Current State: "
                    + vaultDoor.getCurrentState());

            vaultDoor.lockDoor(); // Illegal transition

        } catch (IllegalStateTransitionException e) {

            System.out.println("Exception: "
                    + e.getMessage());
        }

        vaultDoor.closeDoor();
        vaultDoor.lockDoor();

        System.out.println("Current State: "
                + vaultDoor.getCurrentState());

        vaultDoor.unlockDoor();

        System.out.println("Current State: "
                + vaultDoor.getCurrentState());
    }
}