import { useState } from "react";

function DosageInput({ dose, onDoseChange, exceeded }) {
  return (
    <div
      style={{
        border: exceeded ? "2px solid red" : "2px solid black",
        padding: "10px"
      }}
    >
      <input
        type="number"
        value={dose}
        disabled={exceeded}
        onChange={e => onDoseChange(Number(e.target.value))}
      />
    </div>
  );
}

export default function MedicalChart() {
  const [currentDose, setCurrentDose] = useState(0);

  return (
    <div>
      <DosageInput
        dose={currentDose}
        exceeded={currentDose > 500}
        onDoseChange={setCurrentDose}
      />

      {currentDose > 500 && (
        <h3>Maximum safe dosage exceeded</h3>
      )}
    </div>
  );
}