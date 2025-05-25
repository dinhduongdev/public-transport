import React, { useState } from 'react';
import { Switch } from '@headlessui/react';
import { MapPin, RefreshCw } from 'lucide-react';

export default function FindRoute() {
  const [startPoint, setStartPoint] = useState('');
  const [endPoint, setEndPoint] = useState('');
  const [activeOnly, setActiveOnly] = useState(true);
  const [maxTransfers, setMaxTransfers] = useState(1);

  const handleSwap = () => {
    setStartPoint(endPoint);
    setEndPoint(startPoint);
  };

  return (
    <div className="space-y-4 p-4">
      {/* Input section */}
      <div className="bg-gray-100 p-4 rounded-lg space-y-2">
        <div className="flex items-center gap-2">
          <MapPin className="text-green-600" size={18} />
          <input
            type="text"
            placeholder="Choose starting point"
            value={startPoint}
            onChange={(e) => setStartPoint(e.target.value)}
            className="w-full p-2 rounded border border-gray-300 focus:outline-none"
          />
        </div>

        <div className="flex items-center gap-2">
          <MapPin className="text-red-500" size={18} />
          <input
            type="text"
            placeholder="Choose destination"
            value={endPoint}
            onChange={(e) => setEndPoint(e.target.value)}
            className="w-full p-2 rounded border border-gray-300 focus:outline-none"
          />
        </div>

        <div className="flex justify-end">
          <button
            onClick={handleSwap}
            className="p-1.5 bg-white border rounded hover:bg-gray-200 transition"
            title="Swap points"
          >
            <RefreshCw size={16} />
          </button>
        </div>
      </div>

      {/* Active route toggle */}
      <div className="flex items-center justify-between text-sm font-semibold text-gray-700">
        <span>MAX NUMBER OF ROUTES</span>
        <div className="flex items-center gap-2">
          <span className="text-sm">Active routes only</span>
          <Switch
            checked={activeOnly}
            onChange={setActiveOnly}
            className={`${
              activeOnly ? 'bg-teal-500' : 'bg-gray-300'
            } relative inline-flex h-5 w-10 items-center rounded-full`}
          >
            <span className="sr-only">Toggle active routes</span>
            <span
              className={`${
                activeOnly ? 'translate-x-5' : 'translate-x-1'
              } inline-block h-3 w-3 transform rounded-full bg-white transition`}
            />
          </Switch>
        </div>
      </div>

      {/* Max routes options */}
      <div className="flex justify-between mt-2">
        {[1, 2, 3].map((n) => (
          <button
            key={n}
            onClick={() => setMaxTransfers(n)}
            className={`px-4 py-2 rounded-md border ${
              maxTransfers === n
                ? 'bg-teal-600 text-white'
                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
            }`}
          >
            {n} Route{n > 1 ? 's' : ''}
          </button>
        ))}
      </div>
    </div>
  );
}
