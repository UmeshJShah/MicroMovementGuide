
import type React from 'react';

export interface Exercise {
  id: string;
  name: string;
  instruction: string;
  duration: number; // in seconds
  icon: React.FC<{ className?: string }>;
}

export const CONDITIONS = [
  "Parkinson's",
  'Diabetes',
  'Arthritis',
  'General Wellness',
  'Post-Surgery Recovery',
] as const;

export type Condition = typeof CONDITIONS[number];

export interface AppSettings {
  breakInterval: number; // in milliseconds
  exerciseDuration: number; // in seconds
  condition: Condition;
}
