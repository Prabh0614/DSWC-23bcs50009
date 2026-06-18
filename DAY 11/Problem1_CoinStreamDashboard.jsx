import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState
} from "react";

const CurrencyContext = createContext();

function PortfolioMetrics({ value }) {
  const { selectedCurrency } = useContext(CurrencyContext);

  const symbols = {
    USD: "$",
    EUR: "€",
    JPY: "¥"
  };

  return (
    <h2>
      {symbols[selectedCurrency]}
      {value}
    </h2>
  );
}

export default function PortfolioDashboard() {
  const [selectedCurrency, setCurrency] = useState("USD");
  const [livePrices, setLivePrices] = useState({});
  const [darkMode, setDarkMode] = useState(false);

  const transactions = [];

  useEffect(() => {
    const handleNewPrices = prices => {
      setLivePrices(prices);
    };

    const connectionId =
      LivePriceFeed.subscribe(handleNewPrices);

    return () => {
      LivePriceFeed.unsubscribe(connectionId);
    };
  }, []);

  const portfolioValue = useMemo(() => {
    return calculateMassivePortfolioValue(
      transactions,
      livePrices,
      selectedCurrency
    );
  }, [transactions, livePrices, selectedCurrency]);

  return (
    <CurrencyContext.Provider
      value={{ selectedCurrency, setCurrency }}
    >
      <button
        onClick={() => setDarkMode(prev => !prev)}
      >
        Toggle Dark Mode
      </button>

      <PortfolioMetrics value={portfolioValue} />
    </CurrencyContext.Provider>
  );
}