abstract class SmartDevice {
    protected String deviceId;
    protected String deviceName;

    public SmartDevice(String deviceId, String deviceName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    public abstract void runDiagnostic();
}

interface BatteryOperated {
    int getBatteryLevel();
    void triggerRechargeAlert();
}

class SmartLight extends SmartDevice {

    public SmartLight(String deviceId, String deviceName) {
        super(deviceId, deviceName);
    }

    @Override
    public void runDiagnostic() {
        System.out.println(deviceName + " Light Diagnostic: Turning ON/OFF");
    }
}

class SmartCamera extends SmartDevice implements BatteryOperated {
    private int batteryLevel;

    public SmartCamera(String deviceId, String deviceName, int batteryLevel) {
        super(deviceId, deviceName);
        this.batteryLevel = batteryLevel;
    }

    @Override
    public void runDiagnostic() {
        System.out.println(deviceName + " Camera Diagnostic: Recording Test");
    }

    @Override
    public int getBatteryLevel() {
        return batteryLevel;
    }

    @Override
    public void triggerRechargeAlert() {
        System.out.println("Recharge Alert: " + deviceName +
                " battery is low (" + batteryLevel + "%)");
    }
}

class SmartLock extends SmartDevice implements BatteryOperated {
    private int batteryLevel;

    public SmartLock(String deviceId, String deviceName, int batteryLevel) {
        super(deviceId, deviceName);
        this.batteryLevel = batteryLevel;
    }

    @Override
    public void runDiagnostic() {
        System.out.println(deviceName + " Lock Diagnostic: Lock/Unlock Test");
    }

    @Override
    public int getBatteryLevel() {
        return batteryLevel;
    }

    @Override
    public void triggerRechargeAlert() {
        System.out.println("Recharge Alert: " + deviceName +
                " battery is low (" + batteryLevel + "%)");
    }
}

class HomeHub {

    public void executeNightlyRoutine(SmartDevice[] devices) {

        for (SmartDevice device : devices) {

            device.runDiagnostic();

            if (device instanceof BatteryOperated) {

                BatteryOperated batteryDevice =
                        (BatteryOperated) device;

                if (batteryDevice.getBatteryLevel() < 20) {
                    batteryDevice.triggerRechargeAlert();
                }
            }
        }
    }
}

public class EcoSmartHomeHub {
    public static void main(String[] args) {

        SmartDevice[] devices = {
                new SmartLight("L101", "Living Room Light"),
                new SmartCamera("C101", "Front Door Camera", 15),
                new SmartLock("S101", "Main Door Lock", 35),
                new SmartCamera("C102", "Garage Camera", 10)
        };

        HomeHub hub = new HomeHub();
        hub.executeNightlyRoutine(devices);
    }
}