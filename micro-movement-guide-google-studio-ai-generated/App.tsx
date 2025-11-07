
import React, { useState, useEffect, useRef } from 'react';
import { ExerciseOverlay } from './components/ExerciseOverlay';
import { SettingsOverlay } from './components/SettingsOverlay';
import { SettingsIcon } from './components/Icon';
import type { AppSettings } from './types';

// Define the bridge interface for TypeScript to recognize it
declare global {
  interface Window {
    AndroidBridge?: {
      pauseVideo: () => void;
      resumeVideo: () => void;
    };
  }
}

const DEFAULT_SETTINGS: AppSettings = {
  breakInterval: 20 * 60 * 1000, // 20 minutes
  exerciseDuration: 3 * 60, // 3 minutes (in seconds)
  condition: "Parkinson's",
};

const KILL_SWITCH_PRESS_COUNT = 5;
const KILL_SWITCH_INTERVAL_MS = 1000; // 1 second between presses

const App: React.FC = () => {
  const [hasOnboarded, setHasOnboarded] = useState(false);
  const [isActivated, setIsActivated] = useState(false);
  const [isExerciseTime, setIsExerciseTime] = useState(false);
  const [isKilled, setIsKilled] = useState(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [settings, setSettings] = useState<AppSettings>(DEFAULT_SETTINGS);
  
  const timerRef = useRef<number | null>(null);
  
  // Refs for tracking kill switch presses
  const killSwitchPresses = useRef(0);
  const lastKillSwitchPressTime = useRef(0);
  const activateButtonRef = useRef<HTMLButtonElement>(null);
  const settingsButtonRef = useRef<HTMLButtonElement>(null);


  useEffect(() => {
    // Load settings from storage, or use defaults
    const storedSettings = localStorage.getItem('appSettings');
    if (storedSettings) {
      // Merge with defaults to ensure new settings properties are included
      const loadedSettings = JSON.parse(storedSettings);
      setSettings({ ...DEFAULT_SETTINGS, ...loadedSettings });
    }

    // Check if the user has completed onboarding before
    const onboarded = localStorage.getItem('hasOnboarded');
    if (onboarded === 'true') {
      setHasOnboarded(true);
      setIsActivated(true); // Skip onboarding UI
    } else {
      // Focus the activate button on the onboarding screen
      activateButtonRef.current?.focus();
    }
  }, []);

  const startExerciseTimer = () => {
    if (timerRef.current) {
      clearInterval(timerRef.current);
    }
    console.log(`Starting timer for ${settings.breakInterval / 60000} minutes.`);
    timerRef.current = window.setInterval(() => {
      // This function call is the "hook" to the native Android TV wrapper.
      if (window.AndroidBridge && typeof window.AndroidBridge.pauseVideo === 'function') {
        window.AndroidBridge.pauseVideo();
      } else {
        console.log("Development mode: Faking video pause.");
      }
      console.log("Triggering micro-movement break.");
      setIsExerciseTime(true);
    }, settings.breakInterval);
  };

  const handleExerciseClose = () => {
    setIsExerciseTime(false);
    if (window.AndroidBridge && typeof window.AndroidBridge.resumeVideo === 'function') {
      window.AndroidBridge.resumeVideo();
    } else {
      console.log("Development mode: Faking video resume.");
    }
    console.log("Micro-movement break finished. Restarting timer.");
    startExerciseTimer();
  };
  
  const handleOnboardingComplete = () => {
    localStorage.setItem('hasOnboarded', 'true');
    setHasOnboarded(true);
    setIsActivated(true);
  };

  const handleSaveSettings = (newSettings: AppSettings) => {
    localStorage.setItem('appSettings', JSON.stringify(newSettings));
    setSettings(newSettings);
    setIsSettingsOpen(false);
    startExerciseTimer(); // Restart timer with new settings
    // Return focus to the settings button for better UX
    settingsButtonRef.current?.focus(); 
  };

  useEffect(() => {
    // Only start timers and listeners after activation
    if (!isActivated) return;

    startExerciseTimer();
    
    const handleKeyDown = (event: KeyboardEvent) => {
      // Do not trigger kill switch if an overlay is open
      if (isSettingsOpen || isExerciseTime) return;

      if (event.key === 'Backspace') {
        const now = Date.now();
        if (now - lastKillSwitchPressTime.current < KILL_SWITCH_INTERVAL_MS) {
          killSwitchPresses.current += 1;
        } else {
          killSwitchPresses.current = 1;
        }
        lastKillSwitchPressTime.current = now;

        if (killSwitchPresses.current >= KILL_SWITCH_PRESS_COUNT) {
          console.log("EMERGENCY KILL SWITCH ACTIVATED!");
          if (timerRef.current) clearInterval(timerRef.current);
          if (window.AndroidBridge && typeof window.AndroidBridge.resumeVideo === 'function') {
            window.AndroidBridge.resumeVideo();
          }
          setIsExerciseTime(false);
          setIsKilled(true);
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
      window.removeEventListener('keydown', handleKeyDown);
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isActivated]); // Re-running this effect when settings change is handled by the save handler

  if (isKilled) {
    return (
      <div className="w-screen h-screen bg-tv-bg flex items-center justify-center font-sans text-center">
        <div className="text-gray-400 text-3xl p-8">
          <p>Micro-Movement Guide has been paused.</p>
          <p className="mt-2">Please restart the app to enable it again.</p>
        </div>
      </div>
    );
  }

  if (!hasOnboarded) {
    return (
       <div className="w-screen h-screen bg-tv-bg flex items-center justify-center font-sans text-center">
        <div className="bg-tv-sidebar rounded-xl shadow-2xl w-full max-w-4xl text-center p-12 flex flex-col items-center">
          <h1 className="text-5xl font-bold mb-6">Welcome to the Micro-Movement Guide</h1>
          <p className="text-2xl text-gray-300 mb-12 max-w-2xl">This app runs quietly in the background. Every 20 minutes, it will gently pause what you're watching to guide you through a few simple movements.</p>
          <button 
            ref={activateButtonRef}
            onClick={handleOnboardingComplete} 
            className="px-12 py-6 text-3xl font-bold bg-tv-accent rounded-lg transform transition-transform duration-200 focus:bg-tv-accent-light focus:scale-110 focus:ring-4 ring-white outline-none">
            Activate Guide
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="w-screen h-screen bg-tv-bg flex items-center justify-center font-sans text-center relative">
      <button 
        ref={settingsButtonRef}
        onClick={() => setIsSettingsOpen(true)}
        className="absolute top-6 right-6 p-3 rounded-full transform transition-transform duration-200 focus:bg-tv-card focus:scale-110 focus:ring-4 ring-white outline-none"
        aria-label="Open Settings"
      >
        <SettingsIcon className="w-10 h-10 text-gray-400" />
      </button>

      {!isExerciseTime && !isSettingsOpen && (
        <div className="text-gray-600 text-2xl">
          <p>Micro-Movement Guide is active.</p>
          <p>Waiting for the next break...</p>
        </div>
      )}
      
      {isExerciseTime && (
        <ExerciseOverlay 
          onComplete={handleExerciseClose} 
          totalDuration={settings.exerciseDuration}
        />
      )}

      {isSettingsOpen && (
        <SettingsOverlay 
          initialSettings={settings}
          onSave={handleSaveSettings}
          onClose={() => {
            setIsSettingsOpen(false);
            settingsButtonRef.current?.focus();
          }}
        />
      )}
    </div>
  );
};

export default App;
