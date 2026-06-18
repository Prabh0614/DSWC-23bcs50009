import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState
} from "react";

const PlaybackContext = createContext();

function AudioTrack() {
  const isMuted = useContext(PlaybackContext);

  return (
    <div>
      {isMuted ? "MUTED" : "PLAYING"}
    </div>
  );
}

export default function MixingBoard() {
  const [buffers, setBuffers] = useState([]);
  const [tracks, setTracks] = useState([]);
  const [panBalance, setPanBalance] = useState(0);
  const [isMuted, setIsMuted] = useState(false);

  useEffect(() => {
    document.title = `${tracks.length} Active Tracks`;
  }, [tracks.length]);

  const waveformData = useMemo(() => {
    return generateWaveformVisuals(buffers);
  }, [buffers]);

  return (
    <PlaybackContext.Provider value={isMuted}>
      <button
        onClick={() =>
          setIsMuted(prev => !prev)
        }
      >
        Toggle Mute
      </button>

      <input
        type="range"
        min="-100"
        max="100"
        value={panBalance}
        onChange={e =>
          setPanBalance(Number(e.target.value))
        }
      />

      <AudioTrack />

      <pre>
        {JSON.stringify(waveformData, null, 2)}
      </pre>
    </PlaybackContext.Provider>
  );
}