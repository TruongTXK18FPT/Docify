import { cn } from '../../lib/utils';

interface ProgressBarProps {
  progress: number;
  className?: string;
  showText?: boolean;
}

export const ProgressBar = ({ progress, className, showText = true }: ProgressBarProps) => {
  return (
    <div className={cn("w-full", className)}>
      <div className="flex justify-between items-center mb-1.5">
        {showText && <span className="text-xs font-medium text-text-muted">Progress</span>}
        {showText && <span className="text-xs font-bold text-primary">{Math.round(progress)}%</span>}
      </div>
      <div className="h-2 w-full bg-slate-100 rounded-full overflow-hidden">
        <div
          className="h-full bg-primary transition-all duration-300 ease-out rounded-full"
          style={{ width: `${progress}%` }}
        />
      </div>
    </div>
  );
};
