import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState
} from "react";

const RegionContext = createContext();

function ResultItem({ item }) {
  const region = useContext(RegionContext);

  return (
    <div>
      {item.name} - {region === "EU" ? "€" : "$"}
      {item.price}
    </div>
  );
}

export default function OmniSearch() {
  const [searchTerm, setSearchTerm] = useState("");
  const [results, setResults] = useState([]);
  const [isFilterMenuOpen, setIsFilterMenuOpen] =
    useState(false);

  useEffect(() => {
    const controller = new AbortController();

    const fetchResults = async () => {
      try {
        const response = await fetch(
          `/api/search?q=${searchTerm}`,
          { signal: controller.signal }
        );

        const data = await response.json();
        setResults(data);
      } catch (error) {
        if (error.name !== "AbortError") {
          console.error(error);
        }
      }
    };

    if (searchTerm) {
      fetchResults();
    }

    return () => {
      controller.abort();
    };
  }, [searchTerm]);

  const sortedResults = useMemo(() => {
    return sortResultsByRelevance(results);
  }, [results]);

  return (
    <RegionContext.Provider value="EU">
      <input
        value={searchTerm}
        onChange={e => setSearchTerm(e.target.value)}
      />

      <button
        onClick={() =>
          setIsFilterMenuOpen(prev => !prev)
        }
      >
        Filters
      </button>

      {sortedResults.map(item => (
        <ResultItem
          key={item.id}
          item={item}
        />
      ))}
    </RegionContext.Provider>
  );
}