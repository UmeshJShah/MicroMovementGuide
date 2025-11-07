
import React, { useState, useEffect, useRef } from 'react';
import type { AppSettings, Condition } from '../types';
import { CONDITIONS } from '../types';

interface SettingsOverlayProps {
  initialSettings: AppSettings;
  onSave: (newSettings: AppSettings) => void;
  onClose: () => void;
}

const BREAK_INTERVAL_OPTIONS = [15, 20, 25, 30]; // in minutes
const EXERCISE_DURATION_OPTIONS = [2, 3, 4]; // in minutes

export const SettingsOverlay: React.FC<SettingsOverlayProps> = ({ initialSettings, onSave, onClose }) => {
  const [tempSettings, setTempSettings] = useState(initialSettings);
  const saveButtonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    // Focus the first interactive element when the overlay opens
    saveButtonRef.current?.focus();

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Backspace') {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [onClose]);

  const handleSettingChange = (setting: keyof AppSettings, direction: 'increase' | 'decrease') => {
    setTempSettings(prev => {
      if (setting === 'condition') {
        const currentIndex = CONDITIONS.indexOf(prev.condition);
        let nextIndex;
        if (direction === 'increase') {
          // Wrap around to the start if at the end
          nextIndex = (currentIndex + 1) % CONDITIONS.length;
        } else {
          // Wrap around to the end if at the start
          nextIndex = (currentIndex - 1 + CONDITIONS.length) % CONDITIONS.length;
        }
        return { ...prev, condition: CONDITIONS[nextIndex] };
      }
      
      let currentValue: number;
      let options: number[];
      let isMillis = false;

      if (setting === 'breakInterval') {
        currentValue = prev.breakInterval / 60000;
        options = BREAK_INTERVAL_OPTIONS;
        isMillis = true;
      } else {
        currentValue = prev.exerciseDuration / 60;
        options = EXERCISE_DURATION_OPTIONS;
      }

      let currentIndex = options.indexOf(currentValue);
      if (direction === 'increase') {
        currentIndex = Math.min(currentIndex + 1, options.length - 1);
      } else {
        currentIndex = Math.max(currentIndex - 1, 0);
      }
      
      const newValue = options[currentIndex];
      return {
        ...prev,
        [setting]: isMillis ? newValue * 60000 : newValue * 60,
      };
    });
  };
  
  const currentBreakMins = tempSettings.breakInterval / 60000;
  const currentDurationMins = tempSettings.exerciseDuration / 60;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50 text-white p-8">
      <div className="bg-tv-sidebar rounded-xl shadow-2xl w-full max-w-4xl h-auto text-center p-8 flex flex-col items-center animate-fade-in">
        <h1 className="text-5xl font-bold mb-10">Settings</h1>
        
        <div className="w-full max-w-2xl space-y-8 mb-12">
          {/* Time Between Breaks Setting */}
          <div className="flex items-center justify-between">
            <label className="text-3xl text-gray-300">Time Between Breaks</label>
            <div className="flex items-center space-x-4">
              <button onClick={() => handleSettingChange('breakInterval', 'decrease')} className="px-5 py-2 text-4xl font-bold bg-tv-card rounded-lg focus:bg-tv-accent focus:scale-110 focus:ring-4 ring-white outline-none">-</button>
              <span className="text-3xl font-mono w-40 text-center">{currentBreakMins} minutes</span>
              <button onClick={() => handleSettingChange('breakInterval', 'increase')} className="px-5 py-2 text-4xl font-bold bg-tv-card rounded-lg focus:bg-tv-accent focus:scale-110 focus:ring-4 ring-white outline-none">+</button>
            </div>
          </div>

          {/* Length of Break Setting */}
          <div className="flex items-center justify-between">
            <label className="text-3xl text-gray-300">Length of Break</label>
            <div className="flex items-center space-x-4">
              <button onClick={() => handleSettingChange('exerciseDuration', 'decrease')} className="px-5 py-2 text-4xl font-bold bg-tv-card rounded-lg focus:bg-tv-accent focus:scale-110 focus:ring-4 ring-white outline-none">-</button>
              <span className="text-3xl font-mono w-40 text-center">{currentDurationMins} minutes</span>
              <button onClick={() => handleSettingChange('exerciseDuration', 'increase')} className="px-5 py-2 text-4xl font-bold bg-tv-card rounded-lg focus:bg-tv-accent focus:scale-110 focus:ring-4 ring-white outline-none">+</button>
            </div>
          </div>
          
          {/* Condition Focus Setting */}
          <div className="flex items-center justify-between">
            <label className="text-3xl text-gray-300">Condition Focus</label>
            <div className="flex items-center space-x-4">
              <button onClick={() => handleSettingChange('condition', 'decrease')} className="px-5 py-2 text-4xl font-bold bg-tv-card rounded-lg focus:bg-tv-accent focus:scale-110 focus:ring-4 ring-white outline-none">-</button>
              <span className="text-3xl font-mono w-64 text-center">{tempSettings.condition}</span>
              <button onClick={() => handleSettingChange('condition', 'increase')} className="px-5 py-2 text-4xl font-bold bg-tv-card rounded-lg focus:bg-tv-accent focus:scale-110 focus:ring-4 ring-white outline-none">+</button>
            </div>
          </div>
        </div>

        <div className="flex space-x-8">
            <button 
              ref={saveButtonRef}
              onClick={() => onSave(tempSettings)}
              className="px-12 py-5 text-3xl font-bold bg-green-600 rounded-lg transform transition-transform duration-200 focus:bg-green-500 focus:scale-110 focus:ring-4 ring-white outline-none">
              Save
            </button>
            <button 
              onClick={onClose}
              className="px-12 py-5 text-3xl font-bold bg-gray-600 rounded-lg transform transition-transform duration-200 focus:bg-gray-500 focus:scale-110 focus:ring-4 ring-white outline-none">
              Cancel
            </button>
        </div>
      </div>
      <style>{`
        @keyframes fade-in {
          from { opacity: 0; transform: scale(0.9); }
          to { opacity: 1; transform: scale(1); }
        }
        .animate-fade-in { animation: fade-in 0.5s ease-out forwards; }
      `}</style>
    </div>
  );
};
