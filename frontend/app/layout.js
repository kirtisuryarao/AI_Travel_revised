import './globals.css';

export const metadata = {
  title: 'AI Travel Planner - Plan Your Perfect Trip',
  description: 'Plan your perfect trip with AI-powered itineraries, flights, hotels, and saved trips.'
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="" />
        <link rel="stylesheet" href="/style.css" />
        <link rel="stylesheet" href="/itinerary.css" />
      </head>
      <body>{children}</body>
    </html>
  );
}
