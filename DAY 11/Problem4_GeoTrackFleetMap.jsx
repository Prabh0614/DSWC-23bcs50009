import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState
} from "react";

const MetricsContext = createContext();

function TruckTooltip({ speed }) {
  const unit = useContext(MetricsContext);

  return (
    <div>
      {speed} {unit}
    </div>
  );
}

export default function FleetMap() {
  const [trucks, setTrucks] = useState([]);
  const [zoomLevel, setZoomLevel] = useState(1);

  useEffect(() => {
    const handleKeyDown = e => {
      if (e.key.toLowerCase() === "c") {
        centerMap();
      }
    };

    window.addEventListener(
      "keydown",
      handleKeyDown
    );

    return () => {
      window.removeEventListener(
        "keydown",
        handleKeyDown
      );
    };
  }, []);

  const boundingBox = useMemo(() => {
    return calculateBoundingBox(trucks);
  }, [trucks]);

  return (
    <MetricsContext.Provider value="KM">
      <input
        type="range"
        min="1"
        max="20"
        value={zoomLevel}
        onChange={e =>
          setZoomLevel(Number(e.target.value))
        }
      />

      <TruckTooltip speed={60} />

      <pre>
        {JSON.stringify(boundingBox, null, 2)}
      </pre>
    </MetricsContext.Provider>
  );
}