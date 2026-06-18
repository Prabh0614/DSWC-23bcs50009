import { useState, useEffect } from "react";

export default function RadarMap() {
  const [pings, setPings] = useState([]);
  const [isStealthMode, setIsStealthMode] = useState(false);

  useEffect(() => {
    if (isStealthMode) return;

    const handleClick = e => {
      setPings(prev => [
        ...prev,
        { x: e.clientX, y: e.clientY }
      ]);
    };

    window.addEventListener("click", handleClick);

    return () => {
      window.removeEventListener("click", handleClick);
    };
  }, [isStealthMode]);

  return (
    <div>
      <button onClick={() => setIsStealthMode(prev => !prev)}>
        Toggle Stealth
      </button>

      {pings.map((ping, index) => (
        <div key={index}>
          {ping.x}, {ping.y}
        </div>
      ))}
    </div>
  );
}