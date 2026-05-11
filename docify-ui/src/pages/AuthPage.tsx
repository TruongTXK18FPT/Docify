import React, { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { FileText, Mail, Lock, User, ArrowRight, Github, Chrome } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';

export function AuthPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    // Simulate auth
    setTimeout(() => {
      setIsLoading(false);
      navigate('/convert');
    }, 1500);
  };

  return (
    <div className="min-h-[calc(100vh-64px)] flex items-center justify-center py-12 px-4 bg-slate-50 relative overflow-hidden">
      {/* Background Decor */}
      <div className="absolute top-0 left-0 w-full h-full -z-10">
        <div className="absolute top-1/4 -left-20 w-96 h-96 bg-primary/10 rounded-full blur-3xl animate-pulse" />
        <div className="absolute bottom-1/4 -right-20 w-96 h-96 bg-secondary/10 rounded-full blur-3xl animate-pulse" />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="w-full max-w-[450px]"
      >
        <div className="text-center mb-8">
          <Link to="/" className="inline-flex items-center gap-2 mb-6 group">
            <div className="w-12 h-12 bg-primary rounded-2xl flex items-center justify-center text-white group-hover:rotate-6 transition-transform">
              <FileText className="w-7 h-7" />
            </div>
            <span className="text-2xl font-bold text-text-dark">Docify</span>
          </Link>
          <h1 className="text-2xl font-bold text-text-dark">
            {isLogin ? 'Welcome back to Docify' : 'Create your free account'}
          </h1>
          <p className="text-text-muted mt-2">
            {isLogin ? 'Enter your details to access your dashboard' : 'Join thousands of users converting files daily'}
          </p>
        </div>

        <Card className="p-8 border-2 border-slate-100 shadow-xl">
          <form onSubmit={handleSubmit} className="space-y-4">
            <AnimatePresence mode="wait">
              {!isLogin && (
                <motion.div
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  className="space-y-1"
                >
                  <label className="text-sm font-bold text-text-dark flex items-center gap-2">
                    <User className="w-4 h-4 text-text-muted" />
                    Full Name
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="John Doe"
                    className="w-full px-4 h-11 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
                  />
                </motion.div>
              )}
            </AnimatePresence>

            <div className="space-y-1">
              <label className="text-sm font-bold text-text-dark flex items-center gap-2">
                <Mail className="w-4 h-4 text-text-muted" />
                Email Address
              </label>
              <input
                type="email"
                required
                placeholder="yours@example.com"
                className="w-full px-4 h-11 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
              />
            </div>

            <div className="space-y-1">
              <div className="flex justify-between items-center">
                <label className="text-sm font-bold text-text-dark flex items-center gap-2">
                  <Lock className="w-4 h-4 text-text-muted" />
                  Password
                </label>
                {isLogin && <a href="#" className="text-xs text-primary font-bold hover:underline">Forgot?</a>}
              </div>
              <input
                type="password"
                required
                placeholder="••••••••"
                className="w-full px-4 h-11 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
              />
            </div>

            <Button
              type="submit"
              className="w-full mt-6"
              isLoading={isLoading}
              rightIcon={!isLoading && <ArrowRight className="w-4 h-4" />}
            >
              {isLogin ? 'Sign in to account' : 'Create account'}
            </Button>
          </form>

          <div className="relative my-8">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-slate-100"></div>
            </div>
            <div className="relative flex justify-center text-xs uppercase">
              <span className="bg-white px-4 text-text-muted font-bold">Or continue with</span>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Button variant="outline" className="w-full py-0 h-11 text-sm bg-white" leftIcon={<Chrome className="w-4 h-4 text-red-500" />}>
              Google
            </Button>
            <Button variant="outline" className="w-full py-0 h-11 text-sm bg-white" leftIcon={<Github className="w-4 h-4" />}>
              GitHub
            </Button>
          </div>
        </Card>

        <p className="text-center mt-8 text-sm text-text-muted">
          {isLogin ? "Don't have an account?" : "Already have an account?"}{' '}
          <button
            onClick={() => setIsLogin(!isLogin)}
            className="text-primary font-bold hover:underline"
          >
            {isLogin ? 'Sign up for free' : 'Sign in instead'}
          </button>
        </p>
      </motion.div>
    </div>
  );
}
