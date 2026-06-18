import { useState, useEffect } from "react";

export default function DeepSpaceFetcher() {
  const [activePlanet, setActivePlanet] = useState("Mars");
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;

    const fetchPlanet = async () => {
      try {
        setIsLoading(true);
        setError(null);

        const response = await fetch(
          `https://api.example.com/${activePlanet}`
        );

        const result = await response.json();

        if (isMounted) {
          setData(result);
        }
      } catch (err) {
        if (isMounted) {
          setError(err.message);
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    };

    fetchPlanet();

    return () => {
      isMounted = false;
    };
  }, [activePlanet]);

  return (
    <div>
      <button onClick={() => setActivePlanet("Mars")}>Mars</button>
      <button onClick={() => setActivePlanet("Jupiter")}>Jupiter</button>
      <button onClick={() => setActivePlanet("Saturn")}>Saturn</button>

      {isLoading ? (
        <h2>Loading...</h2>
      ) : error ? (
        <h2>{error}</h2>
      ) : (
        <pre>{JSON.stringify(data, null, 2)}</pre>
      )}
    </div>
  );
}