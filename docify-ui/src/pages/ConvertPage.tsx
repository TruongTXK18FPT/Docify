import { useEffect, useRef, useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { UploadDropzone } from '../components/upload/UploadDropzone';
import { ConversionSelector } from '../components/conversion/ConversionSelector';
import { JobStatusCard } from '../components/conversion/JobStatusCard';
import { FileType, ConversionJob } from '../lib/types';
import { AlertCircle, History, Info, Rocket } from 'lucide-react';
import { Link } from 'react-router-dom';
import { conversionService } from '../services/conversion-service';

const TERMINAL_STATUSES = new Set(['COMPLETED', 'FAILED', 'CANCELLED', 'EXPIRED']);

export function ConvertPage() {
  const [file, setFile] = useState<File | null>(null);
  const [targetType, setTargetType] = useState<FileType | null>(null);
  const [activeJob, setActiveJob] = useState<ConversionJob | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const uploadControllerRef = useRef<AbortController | null>(null);

  const sourceType = file ? normalizeSourceType(file.name.split('.').pop()?.toLowerCase()) : null;

  useEffect(() => {
    if (!activeJob || TERMINAL_STATUSES.has(activeJob.status)) {
      return;
    }

    const interval = window.setInterval(async () => {
      try {
        const nextJob = await conversionService.getJob(activeJob.id);
        setActiveJob(nextJob);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Could not refresh job status.');
      }
    }, 2000);

    return () => window.clearInterval(interval);
  }, [activeJob?.id, activeJob?.status]);

  const handleConvert = async () => {
    if (!file || !targetType) {
      setError('Please select a file and target format.');
      return;
    }

    setError(null);
    setIsUploading(true);
    uploadControllerRef.current = new AbortController();

    try {
      const job = await conversionService.createJob(file, targetType, uploadControllerRef.current.signal);
      setActiveJob(job);
    } catch (err) {
      if (err instanceof DOMException && err.name === 'AbortError') {
        setError('Upload cancelled.');
      } else {
        setError(err instanceof Error ? err.message : 'Could not start conversion.');
      }
    } finally {
      setIsUploading(false);
      uploadControllerRef.current = null;
    }
  };

  const handleCancel = async () => {
    uploadControllerRef.current?.abort();
    if (activeJob && !TERMINAL_STATUSES.has(activeJob.status)) {
      try {
        const cancelled = await conversionService.cancelJob(activeJob.id);
        setActiveJob(cancelled);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Could not cancel conversion.');
      }
    } else {
      resetAll();
    }
  };

  const resetAll = () => {
    setFile(null);
    setTargetType(null);
    setActiveJob(null);
    setError(null);
    setIsUploading(false);
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
                isLoading={isUploading}
              >
                Save & Convert
              </Button>
            </div>
          </Card>

          <div className="p-4 bg-blue-50 border border-blue-100 rounded-xl flex gap-3 text-blue-800">
            <Info className="w-5 h-5 shrink-0 mt-0.5" />
            <p className="text-xs leading-relaxed">
              <strong>Pro Tip:</strong> MVP supports Markdown to DOCX/PDF, DOCX to Markdown/PDF, and PPTX to PDF. Files are automatically deleted after 1 hour.
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
                  <JobStatusCard job={activeJob} onReset={resetAll} onCancel={handleCancel} />
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
          </div>
        </div>
      </div>
    </div>
  );
}

function normalizeSourceType(type?: string): FileType | null {
  if (!type) {
    return null;
  }
  return type === 'markdown' ? 'md' : type as FileType;
}
