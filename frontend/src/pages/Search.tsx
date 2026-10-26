import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api/axios';
import { Article, MOCK_ARTICLES } from '../data/mockArticles';

const Search = () => {
  const navigate = useNavigate();
  const [q, setQ] = useState('');
  const [results, setResults] = useState<Article[]>([]);
  const [searched, setSearched] = useState(false);
  const [loading, setLoading] = useState(false);

  const runSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    const term = q.trim();
    if (!term) return;
    setLoading(true);

    const local = MOCK_ARTICLES.filter((a) =>
      (a.title + ' ' + a.content).toLowerCase().includes(term.toLowerCase())
    );

    try {
      const res = await API.get('/api/search', { params: { q: term } });
      const data = Array.isArray(res.data) ? res.data : res.data?.content;
      setResults(Array.isArray(data) ? data : []);
    } catch (err: any) {
      // Server answered with an error: show nothing. Server unreachable: fall back to mock data.
      setResults(err.response ? [] : local);
    } finally {
      setSearched(true);
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-newsprint dark:bg-[#1a1a1a]">
      <header className="border-b-4 border-black dark:border-gray-200 py-4 px-6 flex items-center justify-between">
        <button
          onClick={() => navigate('/')}
          className="text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-primary"
        >
          ← Back
        </button>
        <span className="font-serif font-black text-xl text-black dark:text-white">SEARCH</span>
        <span className="w-12" />
      </header>

      <main className="max-w-3xl mx-auto px-6 py-8">
        <form onSubmit={runSearch} className="flex gap-2">
          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Search stories..."
            className="flex-1 p-2 border rounded"
          />
          <button type="submit" className="px-5 py-2 bg-primary text-white rounded">
            Search
          </button>
        </form>

        {loading && <p className="mt-8 text-center text-gray-500">Searching...</p>}
        {!loading && searched && results.length === 0 && (
          <p className="mt-8 text-center text-gray-500">No stories found.</p>
        )}

        <div className="mt-6 space-y-3">
          {results.map((a) => (
            <div
              key={a.id}
              onClick={() => navigate(`/article/${a.id}`)}
              className="bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-700 p-4 cursor-pointer hover:shadow-md transition"
            >
              <span className="text-xs font-bold uppercase text-primary">{a.category}</span>
              <h2 className="font-serif font-bold text-lg text-black dark:text-white">{a.title}</h2>
              <p className="text-sm text-gray-600 dark:text-gray-300 line-clamp-2">{a.content}</p>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
};

export default Search;