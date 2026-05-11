import { Link } from 'react-router-dom';
import { FileText, Github, Twitter, Linkedin } from 'lucide-react';

export const AppFooter = () => {
  return (
    <footer className="bg-white border-t border-border mt-20">
      <div className="max-w-7xl mx-auto px-4 py-12 md:py-16">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-10">
          <div className="col-span-1 md:col-span-1">
            <Link to="/" className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center text-white">
                <FileText className="w-5 h-5" />
              </div>
              <span className="text-lg font-bold text-text-dark">Docify</span>
            </Link>
            <p className="text-sm text-text-muted leading-relaxed">
              Professional document conversion platform. Secure, fast, and high-quality results.
            </p>
            <div className="flex gap-4 mt-6">
              <a href="#" className="p-2 bg-slate-50 rounded-full text-text-muted hover:text-primary transition-colors">
                <Twitter className="w-4 h-4" />
              </a>
              <a href="#" className="p-2 bg-slate-50 rounded-full text-text-muted hover:text-primary transition-colors">
                <Github className="w-4 h-4" />
              </a>
              <a href="#" className="p-2 bg-slate-50 rounded-full text-text-muted hover:text-primary transition-colors">
                <Linkedin className="w-4 h-4" />
              </a>
            </div>
          </div>

          <div>
            <h4 className="font-semibold text-text-dark mb-4">Product</h4>
            <ul className="space-y-3 text-sm text-text-muted">
              <li><Link to="/convert" className="hover:text-primary transition-colors">Converter</Link></li>
              <li><Link to="/history" className="hover:text-primary transition-colors">History</Link></li>
              <li><a href="#" className="hover:text-primary transition-colors">API Release</a></li>
              <li><a href="#" className="hover:text-primary transition-colors">Desktop App</a></li>
            </ul>
          </div>

          <div>
            <h4 className="font-semibold text-text-dark mb-4">Resources</h4>
            <ul className="space-y-3 text-sm text-text-muted">
              <li><a href="#" className="hover:text-primary transition-colors">Documentation</a></li>
              <li><a href="#" className="hover:text-primary transition-colors">Help Center</a></li>
              <li><a href="#" className="hover:text-primary transition-colors">Supported Formats</a></li>
              <li><a href="#" className="hover:text-primary transition-colors">Status</a></li>
            </ul>
          </div>

          <div>
            <h4 className="font-semibold text-text-dark mb-4">Legal</h4>
            <ul className="space-y-3 text-sm text-text-muted">
              <li><a href="#" className="hover:text-primary transition-colors">Privacy Policy</a></li>
              <li><a href="#" className="hover:text-primary transition-colors">Terms of Service</a></li>
              <li><a href="#" className="hover:text-primary transition-colors">Cookie Policy</a></li>
              <li><a href="#" className="hover:text-primary transition-colors">GDPR</a></li>
            </ul>
          </div>
        </div>
        
        <div className="border-t border-slate-100 mt-12 pt-8 flex flex-col md:flex-row justify-between items-center gap-4">
          <p className="text-xs text-text-muted">
            © {new Date().getFullYear()} Docify Inc. All rights reserved.
          </p>
          <div className="flex gap-6 text-xs text-text-muted">
            <a href="#" className="hover:text-primary">English (US)</a>
            <a href="#" className="hover:text-primary">Vietnamese</a>
          </div>
        </div>
      </div>
    </footer>
  );
};
