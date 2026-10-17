# The News Aggregator (Frontend)

A newspaper-style news app built with React, TypeScript and Tailwind CSS.

**Live demo:** https://news-aggregator-app-frontend.vercel.app
(The backend runs on a laptop through ngrok, so the demo works only when it is online.)

## Features
- JWT login and registration with protected routes
- Trending feed with category filters
- Article detail page with like and bookmark
- Search, bookmarks list and profile page
- Dark and light mode
- Loading, error and offline fallback states

## Tech stack
React, TypeScript, Tailwind CSS, React Router, Axios (with token interceptors), Vercel

## Run locally
npm install
npm start

The backend is a Spring Boot, Kafka, Redis and Docker API (separate repo).