import { Link, useLocation } from 'react-router-dom';
import { Button } from '../ui/Button';
import { FileText, Home, Zap, History, Layout, User } from 'lucide-react';
import { cn } from '../../lib/utils';

export const AppHeader = () => {
  const location = useLocation();
  
  const navItems = [
    { label: 'Home', path: '/', icon: <Home className="w-4 h-4" /> },
    { label: 'Convert', path: '/convert', icon: <Zap className="w-4 h-4" /> },
    { label: 'History', path: '/history', icon: <History className="w-4 h-4" /> },
  ];

  return (
    <header className="sticky top-0 z-50 w-full glass-morphism border-b bg-white/80 transition-all duration-300">
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-3 group">
          <div className="w-10 h-10 bg-primary rounded-xl flex items-center justify-center text-white group-hover:bg-primary-light shadow-lg shadow-primary/20 transition-all group-hover:rotate-6">
            <FileText className="w-6 h-6" />
          </div>
          <div className="flex flex-col -gap-1">
            <span className="text-xl font-black tracking-tight text-text-dark">Docify</span>
            <span className="text-[10px] uppercase tracking-widest font-bold text-primary leading-none">Free Edition</span>
          </div>
        </Link>

        <nav className="hidden md:flex items-center bg-slate-100/50 p-1 rounded-2xl border border-slate-200/50">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={cn(
                "flex items-center gap-2 px-4 py-1.5 rounded-xl text-sm font-semibold transition-all duration-200",
                location.pathname === item.path 
                  ? "bg-white text-primary shadow-sm" 
                  : "text-text-muted hover:text-text-dark"
              )}
            >
              {item.icon}
              {item.label}
            </Link>
          ))}
        </nav>

        <div className="flex items-center gap-3">
          <Link to="/auth" className="hidden sm:block">
            <Button variant="ghost" size="sm" className="font-bold gap-2">
              <User className="w-4 h-4" />
              Sign in
            </Button>
          </Link>
          <Link to="/convert">
            <Button size="sm" className="hidden sm:flex font-bold shadow-lg shadow-primary/20">
              New Conversion
            </Button>
            <Button size="sm" className="sm:hidden w-10 p-0 rounded-xl">
              <Zap className="w-5 h-5 fill-current" />
            </Button>
          </Link>
        </div>
      </div>
    </header>
  );
};
