import React, { useEffect, useState } from 'react';
import API from '../api/axios';
import Navbar from '../components/Navbar';

const CATEGORIES = ['technology', 'sports', 'business', 'health', 'entertainment', 'world', 'india'];

const Profile = () => {
  const [profile, setProfile] = useState<any>(null);
  const [selected, setSelected] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState('');

  useEffect(() => {
    API.get('/api/users/profile')
      .then((res) => {
        setProfile(res.data);
        const p = res.data?.preferredCategories ?? res.data?.preferences?.categories;
        if (Array.isArray(p)) setSelected(p.map((c: string) => c.toLowerCase()));
      })
      .catch(() => setProfile(null))
      .finally(() => setLoading(false));
  }, []);

  const toggle = (cat: string) =>
    setSelected((s) => (s.includes(cat) ? s.filter((c) => c !== cat) : [...s, cat]));

  const save = async () => {
    setMsg('');
    try {
      await API.put('/api/users/preferences', { preferredCategories: selected });
      setMsg('Preferences saved.');
    } catch {
      setMsg('Could not save preferences. Please try again.');
    }
  };

  return (
    <div className="min-h-screen bg-newsprint dark:bg-[#1a1a1a]">
      <header className="border-b-4 border-black dark:border-gray-200 pt-6 pb-4 px-6 text-center">
        <h1 className="font-serif text-3xl font-black text-black dark:text-white">PROFILE</h1>
        <Navbar />
      </header>

      <main className="max-w-xl mx-auto px-6 py-8">
        {loading && <p className="text-gray-500">Loading...</p>}

        {!loading && !profile && (
          <p className="text-gray-500">Profile unavailable right now.</p>
        )}

        {profile && (
          <div className="bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-700 p-5">
            <p className="text-xs uppercase tracking-wide text-gray-500">Username</p>
            <p className="font-serif text-xl font-bold text-black dark:text-white">{profile.username}</p>
            <p className="mt-3 text-xs uppercase tracking-wide text-gray-500">Email</p>
            <p className="text-gray-800 dark:text-gray-200">{profile.email}</p>
          </div>
        )}

        <h2 className="mt-8 mb-3 font-serif text-xl font-black text-black dark:text-white">
          Favourite categories
        </h2>
        <div className="flex flex-wrap gap-2">
          {CATEGORIES.map((cat) => (
            <button
              key={cat}
              onClick={() => toggle(cat)}
              className={`px-4 py-1.5 rounded-full text-sm font-medium capitalize transition ${
                selected.includes(cat)
                  ? 'bg-primary text-white'
                  : 'bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 border border-gray-300 dark:border-gray-600'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        <button onClick={save} className="mt-6 px-6 py-2 bg-primary text-white rounded">
          Save preferences
        </button>
        {msg && <p className="mt-3 text-sm text-gray-600 dark:text-gray-300">{msg}</p>}
      </main>
    </div>
  );
};

export default Profile;