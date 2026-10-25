export interface Article {
  id: number;
  title: string;
  content: string;
  url: string;
  category: string;
  source: string;
  sentiment: 'POSITIVE' | 'NEGATIVE' | 'NEUTRAL';
  viewCount: number;
  likeCount: number;
  readCount: number;
  fetchedAt: string;
}

const mk = (
  id: number, title: string, content: string, category: string,
  source: string, viewCount: number, likeCount: number
): Article => ({
  id, title, content, url: '#', category, source, sentiment: 'NEUTRAL',
  viewCount, likeCount, readCount: Math.floor(viewCount * 0.6),
  fetchedAt: '2026-10-05T10:30:00',
});

export const MOCK_ARTICLES: Article[] = [
  mk(1, 'Scientists Discover Earth-Sized Exoplanet in Habitable Zone', 'Researchers have identified a new exoplanet orbiting within its host star\'s habitable zone, raising hopes for signs of liquid water and potentially life beyond our solar system.', 'technology', 'TechCrunch', 1500, 234),
  mk(2, 'Markets Rally as Inflation Data Beats Expectations', 'Global markets surged today following better-than-expected inflation figures, with investors betting on a softer monetary policy stance.', 'business', 'Reuters', 980, 112),
  mk(3, 'National Team Clinches Dramatic Final-Over Victory', 'In a nail-biting finish, the national cricket team secured a last-ball win, sending fans into wild celebration across the country.', 'sports', 'ESPN', 3200, 876),
  mk(4, 'New Study Links Daily Walking to Longer Life', 'A large multi-year study finds that even modest daily walking significantly lowers the risk of heart disease.', 'health', 'Healthline', 760, 98),
  mk(5, 'Streaming Giants Race to Sign Regional Language Hits', 'Platforms are investing heavily in regional content as audiences outside metro cities grow rapidly.', 'entertainment', 'Variety', 1210, 301),
  mk(6, 'Leaders Gather for Climate Summit Amid Rising Tensions', 'Delegates from over 100 nations opened talks on emissions targets, with funding for developing nations the main sticking point.', 'world', 'BBC', 2100, 410),
  mk(7, 'India Launches Next Phase of Digital Public Infrastructure', 'The government announced new services aimed at simplifying access to credit and documents for small businesses.', 'india', 'The Hindu', 1340, 205),
  mk(8, 'Chipmakers Unveil Energy-Efficient AI Processors', 'New processors promise to cut data-centre power use by nearly a third while improving training speeds.', 'technology', 'The Verge', 890, 143),
  mk(9, 'Young Squad Impresses in Opening Football Fixture', 'A debut-heavy lineup delivered a confident performance, raising expectations for the rest of the season.', 'sports', 'ESPN', 640, 87),
];