import { cn } from '../../lib/utils';
import { JobStatus } from '../../lib/types';
import { CheckCircle2, Clock, Loader2, AlertCircle, Ban } from 'lucide-react';

interface StatusBadgeProps {
  status: JobStatus;
  className?: string;
}

export const StatusBadge = ({ status, className }: StatusBadgeProps) => {
  const configs = {
    PENDING: {
      color: 'bg-amber-50 text-amber-600 border-amber-200',
      icon: <Clock className="w-3.5 h-3.5" />,
      label: 'Pending'
    },
    QUEUED: {
      color: 'bg-amber-50 text-amber-600 border-amber-200',
      icon: <Clock className="w-3.5 h-3.5" />,
      label: 'Queued'
    },
    PROCESSING: {
      color: 'bg-blue-50 text-blue-600 border-blue-200',
      icon: <Loader2 className="w-3.5 h-3.5 animate-spin" />,
      label: 'Processing'
    },
    COMPLETED: {
      color: 'bg-green-50 text-green-600 border-green-200',
      icon: <CheckCircle2 className="w-3.5 h-3.5" />,
      label: 'Completed'
    },
    FAILED: {
      color: 'bg-red-50 text-red-600 border-red-200',
      icon: <AlertCircle className="w-3.5 h-3.5" />,
      label: 'Failed'
    },
    CANCELLED: {
      color: 'bg-slate-50 text-slate-600 border-slate-200',
      icon: <Ban className="w-3.5 h-3.5" />,
      label: 'Cancelled'
    },
    EXPIRED: {
      color: 'bg-slate-50 text-slate-600 border-slate-200',
      icon: <Clock className="w-3.5 h-3.5" />,
      label: 'Expired'
    }
  };

  const config = configs[status];

  return (
    <span className={cn(
      'inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-medium border',
      config.color,
      className
    )}>
      {config.icon}
      {config.label}
    </span>
  );
};
