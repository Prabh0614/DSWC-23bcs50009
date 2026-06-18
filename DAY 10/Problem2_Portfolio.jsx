import { useState } from "react";

function AssetRow({ asset, onBuy }) {
  return (
    <div>
      <span>{asset.name} - {asset.amount}</span>
      <button onClick={() => onBuy(asset.id)}>Buy 1</button>
    </div>
  );
}

export default function Portfolio() {
  const [assets, setAssets] = useState([
    { id: "btc", name: "Bitcoin", amount: 0, price: 50000 },
    { id: "eth", name: "Ethereum", amount: 0, price: 3000 },
    { id: "sol", name: "Solana", amount: 0, price: 150 }
  ]);

  const handleBuy = id => {
    setAssets(
      assets.map(asset =>
        asset.id === id
          ? { ...asset, amount: asset.amount + 1 }
          : asset
      )
    );
  };

  const totalValue = assets.reduce(
    (sum, asset) => sum + asset.amount * asset.price,
    0
  );

  return (
    <div>
      <h1>Total Portfolio Value: ${totalValue}</h1>
      {assets.map(asset => (
        <AssetRow key={asset.id} asset={asset} onBuy={handleBuy} />
      ))}
    </div>
  );
}