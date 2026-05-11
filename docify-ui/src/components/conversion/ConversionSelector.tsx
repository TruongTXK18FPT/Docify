import { FileType, SUPPORTED_CONVERSIONS } from '../../lib/types';
import { cn } from '../../lib/utils';
import { ChevronRight } from 'lucide-react';

interface ConversionSelectorProps {
  sourceType: FileType | null;
  targetType: FileType | null;
  onTargetSelect: (type: FileType) => void;
  className?: string;
}

export const ConversionSelector = ({ sourceType, targetType, onTargetSelect, className }: ConversionSelectorProps) => {
  const supportedTargets = sourceType 
    ? SUPPORTED_CONVERSIONS.find(path => path.source === sourceType.toLowerCase())?.targets || []
    : [];

  return (
    <div className={cn("space-y-4", className)}>
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold text-text-dark">Conversion Settings</h3>
        <div className="text-[10px] uppercase tracking-wider font-bold text-text-muted bg-slate-100 px-2 py-0.5 rounded">
          {sourceType || 'Auto-detect'} Source
        </div>
      </div>

      <div className="flex flex-col gap-3">
        <span className="text-xs font-medium text-text-muted">Convert to:</span>
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
          {['pdf', 'docx', 'md', 'pptx'].map((type) => {
            const isSupported = supportedTargets.includes(type as FileType);
            const isSelected = targetType === type;
            
            return (
              <button
                key={type}
                disabled={!isSupported}
                onClick={() => onTargetSelect(type as FileType)}
                className={cn(
                  "p-3 rounded-xl border text-sm font-bold uppercase transition-all duration-200",
                  isSelected 
                    ? "bg-primary border-primary text-white shadow-lg shadow-primary/20 scale-[1.02]" 
                    : isSupported 
                      ? "bg-white border-slate-200 text-text-dark hover:border-primary/50 hover:bg-slate-50" 
                      : "bg-slate-50 border-slate-100 text-slate-300 cursor-not-allowed opacity-60"
                )}
              >
                {type}
              </button>
            );
          })}
        </div>
      </div>

      {!sourceType && (
        <div className="p-3 bg-amber-50 border border-amber-100 rounded-xl flex items-center gap-3 text-amber-700">
          <div className="w-8 h-8 rounded-lg bg-amber-100 flex items-center justify-center shrink-0">
            <ChevronRight className="w-4 h-4" />
          </div>
          <p className="text-[11px] leading-tight">Please upload a file first to reveal available conversion formats.</p>
        </div>
      )}
    </div>
  );
};
