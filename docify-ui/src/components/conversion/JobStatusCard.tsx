import { ConversionJob } from '../../lib/types';
import { Card } from '../ui/Card';
import { StatusBadge } from '../ui/StatusBadge';
import { ProgressBar } from '../ui/ProgressBar';
import { Button } from '../ui/Button';
import { Download, RefreshCcw, FileText, Calendar, Clock } from 'lucide-react';
import { formatBytes } from '../../lib/utils';

interface JobStatusCardProps {
  job: ConversionJob;
  onReset?: () => void;
}

export const JobStatusCard = ({ job, onReset }: JobStatusCardProps) => {
  const isCompleted = job.status === 'COMPLETED';
  const isProcessing = job.status === 'PROCESSING' || job.status === 'PENDING';

  return (
    <Card className="overflow-hidden border-2 border-primary/10">
      <div className="flex flex-col gap-6">
        <div className="flex justify-between items-start">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-slate-50 rounded-xl flex items-center justify-center text-text-muted">
              <FileText className="w-6 h-6" />
            </div>
            <div>
              <h3 className="font-bold text-text-dark truncate max-w-[180px]">{job.fileName}</h3>
              <p className="text-xs text-text-muted">{formatBytes(job.fileSize)}</p>
            </div>
          </div>
          <StatusBadge status={job.status} />
        </div>

        <div className="flex items-center gap-2 p-2 bg-slate-50 rounded-lg">
          <div className="flex-1 text-center">
            <p className="text-[10px] text-text-muted uppercase font-bold">Source</p>
            <p className="text-sm font-bold text-text-dark">{job.sourceType.toUpperCase()}</p>
          </div>
          <div className="w-px h-6 bg-slate-200" />
          <div className="flex-1 text-center text-primary">
            <p className="text-[10px] text-text-muted uppercase font-bold">Target</p>
            <p className="text-sm font-bold">{job.targetType.toUpperCase()}</p>
          </div>
        </div>

        <ProgressBar progress={job.progress} />

        <div className="grid grid-cols-1 gap-3">
          {isCompleted ? (
            <div className="flex gap-2">
              <Button 
                className="flex-1" 
                leftIcon={<Download className="w-4 h-4" />}
                onClick={() => window.open(job.resultUrl, '_blank')}
              >
                Download result
              </Button>
              <Button 
                variant="outline" 
                className="px-4"
                onClick={onReset}
                title="Convert another"
              >
                <RefreshCcw className="w-4 h-4" />
              </Button>
            </div>
          ) : (
            <div className="flex flex-col gap-2">
              <p className="text-[11px] text-text-muted text-center animate-pulse">
                {job.status === 'PENDING' ? 'Waiting in queue...' : 'Processing your document...'}
              </p>
              <Button variant="outline" onClick={onReset}>Cancel</Button>
            </div>
          )}
        </div>

        <div className="pt-4 border-t border-slate-100 space-y-2">
          <div className="flex items-center justify-between text-[11px] text-text-muted">
            <div className="flex items-center gap-1">
              <Calendar className="w-3.5 h-3.5" />
              <span>Created at:</span>
            </div>
            <span>{new Date(job.createdAt).toLocaleTimeString()}</span>
          </div>
          <div className="flex items-center justify-between text-[11px] text-text-muted">
            <div className="flex items-center gap-1">
              <Clock className="w-3.5 h-3.5" />
              <span>Auto-delete:</span>
            </div>
            <span className="font-medium text-amber-600">In 59 mins</span>
          </div>
          <p className="text-[10px] text-slate-400 italic text-center mt-2">Job ID: {job.id}</p>
        </div>
      </div>
    </Card>
  );
};
