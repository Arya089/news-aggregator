import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api/axios';
import { Article, MOCK_ARTICLES } from '../data/mockArticles';

const Bookmarks = () => {
  const navigate = useNavigate();
  const [items, setItems] = useState<Article[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    API.get('/api/bookmarks')
      .then((res) => setItems(Array.isArray(res.data) ? res.data : []))
      .catch(() => setItems(MOCK_ARTICLES.slice(0, 3)))
      .finally(() => setLoading(false));
  }, []);

  const remove = (articleId: number) => {
    const prev = items;
    setItems(items.filter((a) => a.id !== articleId));
    API.delete(`/api/bookmarks/${articleId}`).catch((err) => {
      if (err.response) setItems(prev);
    });
  };

  return (
    <div className="min-h-screen bg-newsprint dark:bg-[#1a1a1a]">
      <header className="border-b-4 border-black dark:border-gray-200 py-4 px-6 flex items-center justify-between">
        <button onClick={() => navigate('/')} className="text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-primary">
          ← Back
        </button>
        <span className="font-serif font-black text-xl text-black dark:text-white">BOOKMARKS</span>
        <span className="w-12" />
      </header>

      <main className="max-w-3xl mx-auto px-6 py-8">
        {loading && <p className="text-gray-500">Loading...</p>}
        {!loading && items.length === 0 && (
          <p className="mt-8 text-center text-gray-500">No saved stories yet.</p>
        )}
        <div className="space-y-3">
          {items.map((a) => (
            <div
              key={a.id}
              className="bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-700 p-4 flex justify-between gap-4"
            >
              <div className="cursor-pointer" onClick={() => navigate(`/article/${a.id}`)}>
                <span className="text-xs font-bold uppercase text-primary">{a.category}</span>
                <h2 className="font-serif font-bold text-lg text-black dark:text-white">{a.title}</h2>
                <p className="text-sm text-gray-600 dark:text-gray-300 line-clamp-2">{a.content}</p>
              </div>
              <button onClick={() => remove(a.id)} className="text-sm underline text-gray-600 dark:text-gray-300 hover:text-primary self-start">
                Remove
              </button>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
};

export default Bookmarks;