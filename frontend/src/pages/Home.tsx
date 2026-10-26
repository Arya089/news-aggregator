import Navbar from '../components/Navbar';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api/axios';
import { Article, MOCK_ARTICLES } from '../data/mockArticles';

const CATEGORIES = ['All', 'technology', 'sports', 'business', 'health', 'entertainment', 'world', 'india'];

const Meta = ({ a }: { a: Article }) => (
  <div className="flex gap-4 mt-3 text-xs text-gray-500 dark:text-gray-400">
    <span>{a.viewCount} views</span>
    <span>{a.likeCount} likes</span>
  </div>
);

const Card = ({ a, big = false, onClick }: { a: Article; big?: boolean; onClick: () => void }) => (
  <article
    onClick={onClick}
    className="bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-700 p-5 shadow-sm hover:-translate-y-1 hover:shadow-md transition cursor-pointer"
  >
    <div className="flex items-center justify-between mb-2">
      <span className="text-xs font-bold uppercase text-primary tracking-wide">{a.category}</span>
      <span className="text-xs text-gray-500">{a.source}</span>
    </div>
    <h2 className={`font-serif font-bold text-black dark:text-white ${big ? 'text-3xl' : 'text-lg'}`}>
      {a.title}
    </h2>
    <p className={`text-sm text-gray-600 dark:text-gray-300 mt-2 ${big ? 'line-clamp-5' : 'line-clamp-3'}`}>
      {a.content}
    </p>
    <Meta a={a} />
  </article>
);

const Home = () => {
  const navigate = useNavigate();
  const [articles, setArticles] = useState<Article[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeCategory, setActiveCategory] = useState('All');

  useEffect(() => {
    API.get('/api/news/trending')
      .then((res) => {
        const data = Array.isArray(res.data) ? res.data : [];
        setArticles(data.length > 0 ? data : MOCK_ARTICLES);
      })
      .catch(() => setArticles(MOCK_ARTICLES))
      .finally(() => setLoading(false));
  }, []);

  const filtered = activeCategory === 'All'
    ? articles
    : articles.filter((a) => a.category?.toLowerCase() === activeCategory);

  const [hero, ...rest] = filtered;
  const side = rest.slice(0, 1);
  const row = rest.slice(1, 4);
  const list = rest.slice(4);

  const open = (id: number) => navigate(`/article/${id}`);

  return (
    <div className="min-h-screen bg-newsprint dark:bg-[#1a1a1a] font-sans">
      <header className="border-b-4 border-black dark:border-gray-200 pt-8 pb-4 px-6 text-center">
        <p className="text-xs uppercase tracking-widest text-gray-500 dark:text-gray-400">
          {new Date().toDateString()}
        </p>
        <h1 className="font-serif text-5xl md:text-6xl font-black tracking-tight text-black dark:text-white mt-1">
          THE NEWS AGGREGATOR
        </h1>
        <Navbar />
        <div className="w-full border-t border-black dark:border-gray-200 mt-4" />
      </header>

      <div className="flex gap-2 overflow-x-auto px-6 py-4">
        {CATEGORIES.map((cat) => (
          <button
            key={cat}
            onClick={() => setActiveCategory(cat)}
            className={`px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap capitalize transition ${
              activeCategory === cat
                ? 'bg-primary text-white'
                : 'bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 border border-gray-300 dark:border-gray-600'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {loading && <p className="px-6 text-gray-500">Loading stories...</p>}
      {!loading && filtered.length === 0 && (
        <p className="px-6 py-10 text-center text-gray-500">No stories in this category yet.</p>
      )}

      {hero && (
        <main className="px-6 pb-10 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 items-start">
            <div className="md:col-span-2">
              <Card a={hero} big onClick={() => open(hero.id)} />
            </div>
            <div className="flex flex-col gap-6">
              {side.map((a) => <Card key={a.id} a={a} onClick={() => open(a.id)} />)}
            </div>
          </div>

          {row.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 items-start">
              {row.map((a) => <Card key={a.id} a={a} onClick={() => open(a.id)} />)}
            </div>
          )}

          {list.length > 0 && (
            <section className="border-t-4 border-black dark:border-gray-200 pt-4">
              <h3 className="font-serif text-xl font-black uppercase mb-2 text-black dark:text-white">
                More Stories
              </h3>
              {list.map((a) => (
                <div
                  key={a.id}
                  onClick={() => open(a.id)}
                  className="flex items-baseline justify-between gap-4 py-3 border-b border-gray-300 dark:border-gray-700 cursor-pointer hover:text-primary"
                >
                  <div>
                    <span className="text-xs font-bold uppercase text-primary mr-2">{a.category}</span>
                    <span className="font-serif font-bold text-black dark:text-white">{a.title}</span>
                  </div>
                  <span className="text-xs text-gray-500 whitespace-nowrap">{a.source}</span>
                </div>
              ))}
            </section>
          )}
        </main>
      )}
    </div>
  );
};

export default Home;