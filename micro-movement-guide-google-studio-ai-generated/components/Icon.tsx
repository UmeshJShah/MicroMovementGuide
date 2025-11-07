
import React from 'react';

interface IconProps {
  className?: string;
}

export const SettingsIcon: React.FC<IconProps> = ({ className }) => (
  <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.5">
    <path strokeLinecap="round" strokeLinejoin="round" d="M10.343 3.94c.09-.542.56-1.007 1.11-1.226l.558-.223c.55-.22 1.158.22 1.158.822v.077c0 .457.223.86.622 1.126l.498.332c.54.362.82.973.82 1.623v.077c0 .65-.28 1.26-.82 1.623l-.498.332c-.399.266-.622.67-.622 1.126v.077c0 .602-.608 1.042-1.158.822l-.558-.223c-.55-.22-1.02-.684-1.11-1.226L9.4 9.404c-.09-.542.22-1.158.822-1.158l.498-.166c.457 0 .86-.223 1.126-.622l.332-.498c.362-.54.077-1.26-.332-1.623l-.498-.332c-.266-.4-.67-.622-1.126-.622h-.077c-.602 0-1.042-.608-.822-1.158l.223-.558z" />
    <path strokeLinecap="round" strokeLinejoin="round" d="M12 6.75a5.25 5.25 0 100 10.5 5.25 5.25 0 000-10.5zM12 8.25a3.75 3.75 0 100 7.5 3.75 3.75 0 000-7.5z" />
  </svg>
);


export const NeckStretchIcon: React.FC<IconProps> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.5">
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 6a3 3 0 100 6 3 3 0 000-6z" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 12v6" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M9 18h6" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M18 11.5a15.8 15.8 0 00-12 0" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M4 14l2-2" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M20 14l-2-2" />
    </svg>
);

export const HandClenchIcon: React.FC<IconProps> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.5">
      <path strokeLinecap="round" strokeLinejoin="round" d="M15 8.25a.75.75 0 01.75-.75h1.5a.75.75 0 01.75.75v7.5a.75.75 0 01-.75.75h-1.5a.75.75 0 01-.75-.75V8.25z" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M11.25 8.25a.75.75 0 01.75-.75h1.5a.75.75 0 01.75.75v4.5a.75.75 0 01-.75.75h-1.5a.75.75 0 01-.75-.75V8.25z" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M7.5 8.25a.75.75 0 01.75-.75h1.5a.75.75 0 01.75.75v7.5a.75.75 0 01-.75.75h-1.5a.75.75 0 01-.75-.75V8.25z" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12a.75.75 0 01.75-.75h1.5a.75.75 0 01.75.75v3a.75.75 0 01-.75.75h-1.5a.75.75 0 01-.75-.75v-3z" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 15a6 6 0 016-6h6a3 3 0 013 3v3" />
    </svg>
);

export const AnkleRotationIcon: React.FC<IconProps> = ({ className }) => (
    <svg xmlns="http://www.w3.org/2000/svg" className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.5">
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 12v8" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 20a4 4 0 01-4-4h8a4 4 0 01-4 4z" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M16 4.052A8 8 0 108 4.052" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M16 8l-4-4-4 4" />
    </svg>
);
