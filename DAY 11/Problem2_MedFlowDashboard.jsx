import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState
} from "react";

const ProtocolContext = createContext();

function PatientCard() {
  const hospitalCode = useContext(ProtocolContext);

  return (
    <div className={hospitalCode.toLowerCase()}>
      Patient
    </div>
  );
}

export default function TriageDashboard() {
  const [patients, setPatients] = useState([]);
  const [nurseNotes, setNurseNotes] = useState("");
  const hospitalCode = "Code Red";

  useEffect(() => {
    const timer = setInterval(() => {
      fetchPatients().then(setPatients);
    }, 5000);

    return () => {
      clearInterval(timer);
    };
  }, []);

  const riskScore = useMemo(() => {
    return calculateAggregateRisk(patients);
  }, [patients]);

  return (
    <ProtocolContext.Provider value={hospitalCode}>
      <input
        value={nurseNotes}
        onChange={e => setNurseNotes(e.target.value)}
      />

      <h2>Risk Score: {riskScore}</h2>

      <PatientCard />
    </ProtocolContext.Provider>
  );
}