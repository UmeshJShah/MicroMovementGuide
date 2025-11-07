import React, { useState, useEffect, useRef } from 'react';
import type { Exercise } from '../types';
import { NeckStretchIcon, HandClenchIcon, AnkleRotationIcon } from './Icon';

const getExercises = (totalDuration: number): Exercise[] => {
  const baseExercises = [
    {
      id: 'neck',
      name: 'Neck Stretches',
      instruction: 'Gently tilt your head from side to side, holding each stretch for a few seconds.',
      icon: NeckStretchIcon,
    },
    {
      id: 'hands',
      name: 'Hand Clenches',
      instruction: 'Slowly open and close your hands, stretching your fingers wide and then making a gentle fist.',
      icon: HandClenchIcon,
    },
    {
      id: 'ankles',
      name: 'Ankle Rotations',
      instruction: 'Lift one foot slightly and gently rotate your ankle in circles, first one way, then the other. Repeat with the other foot.',
      icon: AnkleRotationIcon,
    },
  ];

  const durationPerExercise = Math.floor(totalDuration / baseExercises.length);

  return baseExercises.map(ex => ({ ...ex, duration: durationPerExercise }));
};


interface ExerciseOverlayProps {
  onComplete: () => void;
  totalDuration: number; // in seconds
}

export const ExerciseOverlay: React.FC<ExerciseOverlayProps> = ({ onComplete, totalDuration }) => {
  const [view, setView] = useState<'prompt' | 'exercising' | 'finished'>('prompt');
  const [timeLeft, setTimeLeft] = useState(totalDuration);
  const [currentExerciseIndex, setCurrentExerciseIndex] = useState(0);

  const exercises = getExercises(totalDuration);

  const okButtonRef = useRef<HTMLButtonElement>(null);
  const dismissButtonRef = useRef<HTMLButtonElement>(null);
  
  useEffect(() => {
    // Fix: Corrected typo from `okButton.current` to `okButtonRef.current`.
    if (view === 'prompt' && okButtonRef.current) {
      okButtonRef.current.focus();
    }
  }, [view]);

  useEffect(() => {
    if (view !== 'exercising') return;

    const countdownTimer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          clearInterval(countdownTimer);
          setView('finished');
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    
    let exerciseTimeout: number;
    const scheduleNextExercise = (index: number) => {
      if(index >= exercises.length - 1) return;
      
      exerciseTimeout = window.setTimeout(() => {
        setCurrentExerciseIndex(index + 1);
        scheduleNextExercise(index + 1);
      }, exercises[index].duration * 1000);
    }
    
    scheduleNextExercise(0);

    return () => {
      clearInterval(countdownTimer);
      clearTimeout(exerciseTimeout);
    };
  }, [view, exercises]);

  useEffect(() => {
    if (view === 'finished') {
      const timeout = setTimeout(onComplete, 3000); // Auto-close after 3 seconds
      return () => clearTimeout(timeout);
    }
  }, [view, onComplete]);

  const handleStart = () => {
    setView('exercising');
  };

  const currentExercise = exercises[currentExerciseIndex];
  const Icon = currentExercise.icon;
  
  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60).toString().padStart(2, '0');
    const secs = (seconds % 60).toString().padStart(2, '0');
    return `${mins}:${secs}`;
  }

  return (
    <div className="fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-50 text-white p-8">
      <div className="bg-tv-sidebar rounded-xl shadow-2xl w-full max-w-3xl h-auto text-center p-8 flex flex-col items-center animate-fade-in">
        {view === 'prompt' && (
          <>
            <h1 className="text-5xl font-bold mb-4">Time for a movement break!</h1>
            <p className="text-2xl text-gray-300 mb-12">Let's do a few gentle exercises to keep you moving.</p>
            <div className="flex space-x-8">
              <button 
                ref={okButtonRef}
                onClick={handleStart} 
                className="px-12 py-6 text-3xl font-bold bg-green-600 rounded-lg transform transition-transform duration-200 focus:bg-green-500 focus:scale-110 focus:ring-4 ring-white outline-none">
                OK
              </button>
              <button 
                ref={dismissButtonRef}
                onClick={onComplete}
                className="px-12 py-6 text-3xl font-bold bg-red-600 rounded-lg transform transition-transform duration-200 focus:bg-red-500 focus:scale-110 focus:ring-4 ring-white outline-none">
                DISMISS
              </button>
            </div>
          </>
        )}

        {view === 'exercising' && (
          <div className="w-full flex flex-col items-center">
             <div className="absolute top-8 right-8 text-5xl font-mono bg-black bg-opacity-50 px-4 py-2 rounded-lg">{formatTime(timeLeft)}</div>
             <Icon className="w-48 h-48 mb-8 text-tv-accent-light" />
             <h2 className="text-5xl font-bold mb-4">{currentExercise.name}</h2>
             <p className="text-2xl text-gray-300 max-w-xl">{currentExercise.instruction}</p>
          </div>
        )}

        {view === 'finished' && (
            <>
                <h1 className="text-6xl font-bold text-green-400 mb-4">Well done!</h1>
                <p className="text-3xl text-gray-200">You're doing great. See you next time.</p>
            </>
        )}
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
