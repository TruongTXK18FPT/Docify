import { motion } from 'motion/react';
import { Button } from '../components/ui/Button';
import { Card } from '../components/ui/Card';
import { FileText, ArrowRight, Zap, Shield, Rocket, Download, CheckCircle, Smartphone } from 'lucide-react';
import { Link } from 'react-router-dom';
import { UploadDropzone } from '../components/upload/UploadDropzone';

export function LandingPage() {
  const stats = [
    { label: 'Files Converted', value: '1.2M+' },
    { label: 'Average Speed', value: '1.4s' },
    { label: 'Format Pairs', value: '45+' },
    { label: 'Uptime', value: '99.9%' },
  ];

  const features = [
    {
      title: 'Fast Conversion',
      description: 'Our cloud-optimized engine converts your documents in seconds, not minutes.',
      icon: <Zap className="w-6 h-6" />,
    },
    {
      title: 'Format Preserving',
      description: 'We keep your layout, fonts, and styles perfectly intact during conversion.',
      icon: <Rocket className="w-6 h-6" />,
    },
    {
      title: 'Secure Storage',
      description: 'All files are encrypted and automatically deleted from our servers after 1 hour.',
      icon: <Shield className="w-6 h-6" />,
    },
    {
      title: 'Progress Tracking',
      description: 'Monitor your conversion progress in real-time with our intuitive dashboard.',
      icon: <CheckCircle className="w-6 h-6" />,
    },
  ];

  const conversionPairs = [
    { from: 'pptx', to: 'pdf' },
    { from: 'markdown', to: 'docx' },
    { from: 'docx', to: 'pdf' },
    { from: 'pdf', to: 'markdown' },
  ];

  return (
    <div className="flex flex-col gap-24">
      {/* Hero Section */}
      <section className="pt-16 pb-20 relative overflow-hidden">
        <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-primary/5 rounded-full blur-3xl -z-10 translate-x-1/2 -translate-y-1/2" />
        <div className="absolute bottom-0 left-0 w-[300px] h-[300px] bg-secondary/5 rounded-full blur-3xl -z-10 -translate-x-1/2 translate-y-1/2" />
        
        <div className="max-w-7xl mx-auto px-4 grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <motion.div 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.6 }}
          >
            <div className="inline-flex items-center gap-2 px-3 py-1 bg-primary/10 text-primary rounded-full text-sm font-bold mb-6">
              <span className="relative flex h-2 w-2">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-primary opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-primary"></span>
              </span>
              New: PDF to Markdown conversion added
            </div>
            <h1 className="text-5xl md:text-6xl font-extrabold text-text-dark leading-[1.1] mb-6">
              Convert documents <span className="text-primary italic">instantly</span> with Docify
            </h1>
            <p className="text-xl text-text-muted mb-10 max-w-lg leading-relaxed">
              Professional SaaS platform for lightning-fast document conversion. Supporting PDF, DOCX, Markdown, and PPTX with perfect formatting.
            </p>
            <div className="flex flex-col sm:flex-row gap-4">
              <Link to="/convert">
                <Button size="lg" className="w-full sm:w-auto" rightIcon={<ArrowRight className="w-5 h-5" />}>
                  Convert your file
                </Button>
              </Link>
              <Button variant="outline" size="lg" className="w-full sm:w-auto">
                View supported formats
              </Button>
            </div>
            
            <div className="mt-12 flex items-center gap-8 border-t border-slate-100 pt-8 grayscale opacity-50">
              <div className="font-bold text-slate-400">Trusted by</div>
              <div className="text-lg font-black tracking-tighter">TECHCORP</div>
              <div className="text-lg font-black tracking-tighter">CLOUDCORE</div>
              <div className="text-lg font-black tracking-tighter">FUTUREOS</div>
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.2 }}
          >
            <Card className="p-2 border-2 border-primary/20 shadow-2xl relative">
              <div className="absolute -top-4 -right-4 bg-secondary text-white px-4 py-2 rounded-xl text-xs font-bold shadow-lg">
                Fast Processing
              </div>
              <div className="bg-slate-50 rounded-xl p-8 pb-12">
                <div className="max-w-sm mx-auto">
                  <UploadDropzone onFileSelect={() => {}} selectedFile={null} onClear={() => {}} className="bg-white shadow-sm" />
                </div>
              </div>
              <div className="absolute left-1/2 -bottom-6 -translate-x-1/2 w-[85%]">
                <Card className="py-4 px-6 flex items-center justify-between border-2 border-slate-100">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-lg bg-green-100 flex items-center justify-center text-green-600">
                      <Download className="w-4 h-4" />
                    </div>
                    <div>
                      <p className="text-[10px] text-text-muted uppercase font-bold tracking-wider">Ready to download</p>
                      <p className="text-xs font-bold">result.pdf (1.2 MB)</p>
                    </div>
                  </div>
                  <Button size="sm" variant="ghost" className="h-8 w-8 p-0 hover:bg-slate-100">
                    <ArrowRight className="w-4 h-4" />
                  </Button>
                </Card>
              </div>
            </Card>
          </motion.div>
        </div>
      </section>

      {/* Conversion Formats Grid */}
      <section className="max-w-7xl mx-auto px-4 w-full">
        <div className="text-center mb-16">
          <h2 className="text-3xl font-bold text-text-dark mb-4">Popular Conversion Paths</h2>
          <p className="text-text-muted">Optimized specifically for these document types.</p>
        </div>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {conversionPairs.map((pair, idx) => (
            <motion.div 
              key={idx}
              whileHover={{ y: -5 }}
              className="cursor-pointer"
            >
              <Card className="hover:border-primary/50 text-center flex flex-col items-center justify-center py-8">
                <div className="flex items-center gap-3 mb-4">
                  <span className="w-12 py-2 bg-slate-100 rounded-lg text-xs font-bold uppercase">{pair.from}</span>
                  <ArrowRight className="w-4 h-4 text-primary" />
                  <span className="w-12 py-2 bg-primary/10 text-primary rounded-lg text-xs font-bold uppercase">{pair.to}</span>
                </div>
                <h4 className="font-bold text-sm text-text-dark">High Quality {pair.to.toUpperCase()}</h4>
                <p className="text-[11px] text-text-muted mt-1">Preserves layout and tables</p>
              </Card>
            </motion.div>
          ))}
        </div>
      </section>

      {/* Features section */}
      <section className="py-24 bg-white border-y border-slate-100">
        <div className="max-w-7xl mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12">
            {features.map((feature, idx) => (
              <div key={idx} className="flex flex-col gap-4">
                <div className="w-12 h-12 rounded-2xl bg-primary/10 text-primary flex items-center justify-center mb-2">
                  {feature.icon}
                </div>
                <h3 className="text-lg font-bold text-text-dark">{feature.title}</h3>
                <p className="text-sm text-text-muted leading-relaxed">
                  {feature.description}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="max-w-7xl mx-auto px-4 w-full">
        <div className="bg-text-dark rounded-3xl p-12 text-white relative overflow-hidden">
          <div className="absolute top-0 right-0 w-64 h-64 bg-primary/10 rounded-full blur-[100px]" />
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-8">
            {stats.map((stat, idx) => (
              <div key={idx} className="text-center">
                <h3 className="text-4xl md:text-5xl font-black mb-2">{stat.value}</h3>
                <p className="text-blue-200 text-sm font-medium">{stat.label}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="max-w-3xl mx-auto px-4 w-full text-center pb-20">
        <h2 className="text-4xl font-extrabold text-text-dark mb-6 tracking-tight">Ready to transform your files?</h2>
        <p className="text-lg text-text-muted mb-10 leading-relaxed">
          Join thousands of professionals who trust Docify for their daily document tasks. No registration required for free plan.
        </p>
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Link to="/convert">
            <Button size="lg" className="px-12">Get started for free</Button>
          </Link>
          <Link to="/auth">
            <Button variant="outline" size="lg" className="px-12">Create an account</Button>
          </Link>
        </div>
      </section>
    </div>
  );
}
