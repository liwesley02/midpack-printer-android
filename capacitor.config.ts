import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.happykitchens.midpack2',
  appName: 'Midpack 2',
  webDir: 'dist',
  server: {
    url: 'https://www.happykitchens.io/midpack2',
    cleartext: false,
  },
};

export default config;
