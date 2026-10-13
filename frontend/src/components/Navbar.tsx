import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api/axios';

const Navbar = () => {
  const navigate = useNavigate();
  const [dark, setDark] = useState(document.documentElement.classList.contains('dark'));

  const toggleTheme = () => {
    const next = !dark;
    setDark(next);
    document.documentElement.classList.toggle('dark', next);
    localStorage.setItem('theme', next ? 'dark' : 'light');
  };

  const logout = () => {
    API.post('/api/auth/logout')
      .catch(() => {})
      .finally(() => {
        localStorage.removeItem('token');
        window.location.href = '/login';
      });
  };

  const link = 'underline text-gray-700 dark:text-gray-300 hover:text-primary';

  return (
    <nav className="mt-3 flex flex-wrap justify-center gap-x-6 gap-y-1 text-sm">
      <button onClick={() => navigate('/')} className={link}>Home</button>
      <button onClick={() => navigate('/search')} className={link}>Search</button>
      <button onClick={() => navigate('/bookmarks')} className={link}>Bookmarks</button>
      <button onClick={() => navigate('/profile')} className={link}>Profile</button>
      <button onClick={toggleTheme} className={link}>{dark ? 'Light mode' : 'Dark mode'}</button>
      <button onClick={logout} className={link}>Log out</button>
    </nav>
  );
};

export default Navbar;