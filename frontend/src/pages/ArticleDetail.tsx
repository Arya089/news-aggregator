import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import API from '../api/axios';
import { Article, MOCK_ARTICLES } from '../data/mockArticles';

const ArticleDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [article, setArticle] = useState<Article | null>(null);
  const [loading, setLoading] = useState(true);
  const [liked, setLiked] = useState(false);
  const [saved, setSaved] = useState(false);

  useEffect(() => {
    const mock = MOCK_ARTICLES.find((a) => String(a.id) === id) || null;

    API.get(`/api/news/${id}`)
      .then((res) => setArticle(res.data || mock))
      .catch(() => setArticle(mock))
      .finally(() => setLoading(false));

    API.post(`/api/news/${id}/read`).catch(() => {});

    API.get('/api/bookmarks')
      .then((res) => {
        if (Array.isArray(res.data)) {
          setSaved(res.data.some((b: any) => String(b.id) === String(id)));
        }
      })
      .catch(() => {});
  }, [id]);

  const handleLike = () => {
    if (!article || liked) return;
    setLiked(true);
    setArticle({ ...article, likeCount: article.likeCount + 1 });
    API.post(`/api/news/${article.id}/like`).catch(() => {
      setLiked(false);
      setArticle((a) => (a ? { ...a, likeCount: a.likeCount - 1 } : a));
    });
  };

  const handleBookmark = () => {
    if (!article) return;
    if (saved) {
      setSaved(false);
      API.delete(`/api/bookmarks/${article.id}`).catch(() => setSaved(true));
    } else {
      setSaved(true);
      API.post(`/api/news/${article.id}/bookmark`).catch(() => setSaved(false));
    }
  };

  if (loading) return <p className="p-6 text-gray-500">Loading story...</p>;
  if (!article) {
    return (
      <div className="p-6 text-center">
        <p className="text-gray-600 mb-4">Story not found.</p>
        <button onClick={() => navigate('/')} className="text-primary underline">Back to home</button>
      </div>
    );
  }

  const sentimentStyle =
    article.sentiment === 'POSITIVE' ? 'bg-green-100 text-green-800'
    : article.sentiment === 'NEGATIVE' ? 'bg-red-100 text-red-800'
    : 'bg-gray-200 text-gray-700';

  return (
    <div className="min-h-screen bg-newsprint dark:bg-[#1a1a1a]">
      <header className="border-b-4 border-black dark:border-gray-200 py-4 px-6 flex items-center justify-between">
        <button onClick={() => navigate('/')} className="text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-primary">
          ← Back
        </button>
        <span className="font-serif font-black text-xl text-black dark:text-white">THE NEWS AGGREGATOR</span>
        <span className="w-12" />
      </header>

      <main className="max-w-3xl mx-auto px-6 py-10">
        <div className="flex items-center gap-3 mb-3">
          <span className="text-xs font-bold uppercase text-primary tracking-wide">{article.category}</span>
          <span className={`text-xs px-2 py-0.5 rounded-full ${sentimentStyle}`}>
            {(article.sentiment || 'NEUTRAL').toLowerCase()}
          </span>
        </div>

        <h1 className="font-serif font-black text-4xl md:text-5xl leading-tight text-black dark:text-white">
          {article.title}
        </h1>

        <p className="mt-4 text-sm text-gray-600 dark:text-gray-400 border-y border-gray-300 dark:border-gray-700 py-2">
          {article.source || 'Unknown source'} · {new Date(article.fetchedAt).toDateString()} · {article.viewCount} views
        </p>

        <p className="mt-8 font-serif text-lg leading-8 text-gray-800 dark:text-gray-200 whitespace-pre-line">
          {article.content}
        </p>

        <div className="mt-10 flex items-center gap-4 flex-wrap">
          <button
            onClick={handleLike}
            className={`px-5 py-2 rounded-full text-sm font-medium transition ${
              liked ? 'bg-primary text-white' : 'border border-gray-400 text-gray-700 dark:text-gray-300 hover:border-primary'
            }`}
          >
            {liked ? '♥ Liked' : '♡ Like'} · {article.likeCount}
          </button>

          <button
            onClick={handleBookmark}
            className={`px-5 py-2 rounded-full text-sm font-medium transition ${
              saved ? 'bg-black text-white' : 'border border-gray-400 text-gray-700 dark:text-gray-300 hover:border-primary'
            }`}
          >
            {saved ? 'Saved' : 'Save'}
          </button>

          {article.url && article.url !== '#' && (
            <a href={article.url} target="_blank" rel="noreferrer" className="text-sm underline text-gray-700 dark:text-gray-300 hover:text-primary">
              Read original ↗
            </a>
          )}
        </div>
      </main>
    </div>
  );
};

export default ArticleDetail;