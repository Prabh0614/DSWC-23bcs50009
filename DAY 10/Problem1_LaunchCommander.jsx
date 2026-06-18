import { useState, useEffect } from "react";

function DashboardPanel({ title, children }) {
  return (
    <div>
      <h2>{title}</h2>
      {children}
    </div>
  );
}

function TelemetrySubsystem({ fuelLevel, onAbortSequence }) {
  return (
    <DashboardPanel title="Telemetry Subsystem">
      <p>Fuel Level: {fuelLevel}%</p>
      {fuelLevel < 20 && <h1 className="alert">CRITICAL FUEL</h1>}
      <button onClick={onAbortSequence}>Manual Abort</button>
    </DashboardPanel>
  );
}

export default function LaunchCommander() {
  const [countdown, setCountdown] = useState(10);
  const [isAborted, setIsAborted] = useState(false);
  const [fuelLevel] = useState(15);

  useEffect(() => {
    if (isAborted) return;

    const interval = setInterval(() => {
      setCountdown(prev => prev > 0 ? prev - 1 : 0);
    }, 1000);

    return () => clearInterval(interval);
  }, [isAborted]);

  return (
    <DashboardPanel title="Launch Commander">
      <h1>T-Minus {countdown}</h1>
      <p>Status: {isAborted ? "ABORTED" : "ACTIVE"}</p>
      <TelemetrySubsystem
        fuelLevel={fuelLevel}
        onAbortSequence={() => setIsAborted(true)}
      />
    </DashboardPanel>
  );
}