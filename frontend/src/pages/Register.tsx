import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import API from '../api/axios';

const Register = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      await API.post('/api/auth/register', { username, email, password });
      setSuccess('Account created. Redirecting to log in...');
      setTimeout(() => navigate('/login'), 1500);
    } catch (err: any) {
      if (!err.response) setError('Cannot reach the server. Please try again later.');
      else setError('Registration failed. Try a different username or email.');
    } finally {
      setLoading(false);
    }
  };

  const input =
    'w-full mb-3 p-3 border border-gray-400 dark:border-gray-600 bg-white dark:bg-gray-900 text-black dark:text-white focus:outline-none focus:border-primary';

  return (
    <div className="min-h-screen bg-newsprint dark:bg-[#1a1a1a] flex flex-col items-center justify-center px-4">
      <p className="text-xs uppercase tracking-widest text-gray-500 dark:text-gray-400">{new Date().toDateString()}</p>
      <h1 className="font-serif text-4xl md:text-5xl font-black tracking-tight text-black dark:text-white mt-1 text-center">
        THE NEWS AGGREGATOR
      </h1>
      <div className="w-full max-w-md border-t-4 border-black dark:border-gray-200 mt-4" />
      <div className="w-full max-w-md border-t border-black dark:border-gray-200 mt-1" />

      <form
        onSubmit={handleSubmit}
        className="w-full max-w-md mt-8 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-700 p-6 shadow-sm"
      >
        <h2 className="font-serif text-2xl font-black text-black dark:text-white mb-1">Create your account</h2>
        <p className="text-sm text-gray-600 dark:text-gray-300 mb-4">Stories from every source, in one place.</p>
        {error && <p className="text-sm text-primary mb-3">{error}</p>}
        {success && <p className="text-sm text-green-700 dark:text-green-400 mb-3">{success}</p>}

        <input className={input} placeholder="Username" value={username} onChange={(e) => setUsername(e.target.value)} autoComplete="username" required />
        <input className={input} type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} autoComplete="email" required />
        <input className={input} type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} autoComplete="new-password" required />

        <button type="submit" disabled={loading} className="w-full py-3 bg-primary text-white font-medium hover:opacity-90 disabled:opacity-60">
          {loading ? 'Creating account...' : 'Register'}
        </button>

        <p className="mt-4 text-sm text-center text-gray-600 dark:text-gray-300">
          Already have an account?{' '}
          <Link to="/login" className="underline text-primary">Log in</Link>
        </p>
      </form>
    </div>
  );
};

export default Register;