import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { UploadDropzone } from '../components/upload/UploadDropzone';
import { ConversionSelector } from '../components/conversion/ConversionSelector';
import { JobStatusCard } from '../components/conversion/JobStatusCard';
import { FileType, ConversionJob, JobStatus } from '../lib/types';
import { AlertCircle, History, Info } from 'lucide-react';
import { Link } from 'react-router-dom';

export function ConvertPage() {
  const [file, setFile] = useState<File | null>(null);
  const [targetType, setTargetType] = useState<FileType | null>(null);
  const [activeJob, setActiveJob] = useState<ConversionJob | null>(null);
  const [error, setError] = useState<string | null>(null);

  const sourceType = file ? file.name.split('.').pop()?.toLowerCase() as FileType : null;

  const handleConvert = () => {
    if (!file || !targetType) {
      setError("Please select a file and target format.");
      return;
    }

    setError(null);
    const jobId = (window.crypto?.randomUUID?.() || Math.random().toString(36).substring(2, 10)).slice(0, 8);
    
    // Initialize job
    const newJob: ConversionJob = {
      id: `job-${jobId}`,
      fileName: file.name,
      fileSize: file.size,
      sourceType: sourceType!,
      targetType,
      status: 'PENDING',
      progress: 0,
      createdAt: new Date().toISOString()
    };

    setActiveJob(newJob);

    // Simulate conversion process
    let progress = 0;
    const interval = setInterval(() => {
      progress += Math.random() * 15;
      
      if (progress >= 100) {
        progress = 100;
        clearInterval(interval);
        setActiveJob(prev => prev ? { 
          ...prev, 
          progress: 100, 
          status: 'COMPLETED',
          resultUrl: '#' 
        } : null);
      } else {
        setActiveJob(prev => prev ? { 
          ...prev, 
          progress, 
          status: progress > 5 ? 'PROCESSING' : 'PENDING' 
        } : null);
      }
    }, 400);
  };

  const resetAll = () => {
    setFile(null);
    setTargetType(null);
    setActiveJob(null);
    setError(null);
  };

  return (
    <div className="max-w-6xl mx-auto px-4 py-12">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-4">
        <div>
          <h1 className="text-3xl font-bold text-text-dark mb-2">Convert Document</h1>
          <p className="text-text-muted">Transform your documents with professional precision</p>
        </div>
        <Link to="/history">
          <Button variant="outline" size="sm" leftIcon={<History className="w-4 h-4" />}>
            View History
          </Button>
        </Link>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-6">
          <Card className="p-8">
            <UploadDropzone
              onFileSelect={(f) => {
                setFile(f);
                setTargetType(null);
                setError(null);
              }}
              selectedFile={file}
              onClear={() => {
                setFile(null);
                setTargetType(null);
              }}
            />

            <div className="mt-8 pt-8 border-t border-slate-100">
              <ConversionSelector
                sourceType={sourceType}
                targetType={targetType}
                onTargetSelect={setTargetType}
              />
            </div>

            <AnimatePresence>
              {error && (
                <motion.div
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, scale: 0.95 }}
                  className="mt-6 p-4 bg-red-50 border border-red-100 rounded-xl flex items-center gap-3 text-red-600 text-sm font-medium"
                >
                  <AlertCircle className="w-5 h-5 shrink-0" />
                  {error}
                </motion.div>
              )}
            </AnimatePresence>

            <div className="mt-8 flex justify-end">
              <Button
                size="lg"
                className="w-full sm:w-auto"
                disabled={!file || !targetType || !!activeJob}
                onClick={handleConvert}
                isLoading={!!activeJob && activeJob.status !== 'COMPLETED' && activeJob.status !== 'FAILED'}
              >
                Save & Convert
              </Button>
            </div>
          </Card>

          <div className="p-4 bg-blue-50 border border-blue-100 rounded-xl flex gap-3 text-blue-800">
            <Info className="w-5 h-5 shrink-0 mt-0.5" />
            <p className="text-xs leading-relaxed">
              <strong>Pro Tip:</strong> You can convert Markdown files to DOCX or PDF. If you have images in your PDF, they will be preserved in the resulting DOCX format.
            </p>
          </div>
        </div>

        <div className="lg:col-span-1">
          <div className="sticky top-24">
            <h2 className="text-sm font-bold uppercase tracking-widest text-text-muted mb-4 px-2">Job Status</h2>
            <AnimatePresence mode="wait">
              {activeJob ? (
                <motion.div
                  key="job-card"
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -20 }}
                >
                  <JobStatusCard job={activeJob} onReset={resetAll} />
                </motion.div>
              ) : (
                <motion.div
                  key="empty-job"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  exit={{ opacity: 0 }}
                >
                  <Card className="border-dashed border-2 py-16 text-center">
                    <div className="w-12 h-12 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-300">
                      <Rocket className="w-6 h-6" />
                    </div>
                    <p className="text-sm font-medium text-text-muted">No active conversion jobs.</p>
                    <p className="text-[10px] text-slate-400 max-w-[160px] mx-auto mt-2">Upload a file and choose format to start.</p>
                  </Card>
                </motion.div>
              )}
            </AnimatePresence>
            
            <div className="mt-8 px-4 py-3 bg-primary/5 rounded-xl border border-primary/10">
              <div className="flex items-center justify-between mb-2">
                <span className="text-[10px] font-bold text-primary uppercase">Current Quota</span>
                <span className="text-[10px] font-bold text-text-muted">5 / 10 daily</span>
              </div>
              <div className="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden">
                <div className="h-full bg-primary w-1/2 rounded-full" />
              </div>
              <p className="text-[9px] text-text-muted mt-2 text-center">Upgrade to Business for unlimited conversions.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

const Rocket = ({ className }: { className?: string }) => (
  <svg className={className} xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 0 0-2.91-.09z"/><path d="m12 15-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z"/><path d="M9 12H4s.55-3.03 2-5c1.62-2.2 5-3 5-3"/><path d="M12 15v5s3.03-.55 5-2c2.2-1.62 3-5 3-5"/></svg>
);
