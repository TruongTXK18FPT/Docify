import React, { useRef, useState } from 'react';
import { Upload, X, FileText, AlertCircle } from 'lucide-react';
import { cn, formatBytes } from '../../lib/utils';
import { FileType } from '../../lib/types';

interface UploadDropzoneProps {
  onFileSelect: (file: File) => void;
  selectedFile: File | null;
  onClear: () => void;
  className?: string;
}

export const UploadDropzone = ({ onFileSelect, selectedFile, onClear, className }: UploadDropzoneProps) => {
  const [isDragging, setIsDragging] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    const files = e.dataTransfer.files;
    if (files.length > 0) {
      onFileSelect(files[0]);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      onFileSelect(e.target.files[0]);
    }
  };

  if (selectedFile) {
    return (
      <div className={cn("p-4 bg-primary/5 border border-primary/20 rounded-2xl flex items-center justify-between animate-in fade-in zoom-in duration-300", className)}>
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 bg-white rounded-xl flex items-center justify-center shadow-sm text-primary">
            <FileText className="w-6 h-6" />
          </div>
          <div>
            <h4 className="text-sm font-semibold text-text-dark truncate max-w-[200px]">{selectedFile.name}</h4>
            <p className="text-xs text-text-muted">{formatBytes(selectedFile.size)} • {selectedFile.name.split('.').pop()?.toUpperCase()}</p>
          </div>
        </div>
        <button 
          onClick={onClear}
          className="p-2 hover:bg-white rounded-full text-text-muted hover:text-red-500 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>
      </div>
    );
  }

  return (
    <div
      onDragOver={handleDragOver}
      onDragLeave={handleDragLeave}
      onDrop={handleDrop}
      onClick={() => inputRef.current?.click()}
      className={cn(
        "relative rounded-2xl border-2 border-dashed transition-all duration-300 cursor-pointer flex flex-col items-center justify-center p-8 text-center group",
        isDragging ? "border-primary bg-primary/5" : "border-slate-200 hover:border-primary/50 hover:bg-slate-50",
        className
      )}
    >
      <input
        type="file"
        ref={inputRef}
        onChange={handleFileChange}
        className="hidden"
        accept=".pdf,.docx,.md,.pptx"
      />
      <div className={cn(
        "w-16 h-16 mb-6 rounded-2xl flex items-center justify-center transition-transform duration-300",
        isDragging ? "scale-110 bg-primary text-white" : "bg-slate-100 text-slate-400 group-hover:scale-110 group-hover:bg-primary/10 group-hover:text-primary"
      )}>
        <Upload className="w-8 h-8" />
      </div>
      <div>
        <h3 className="text-lg font-bold text-text-dark mb-1">Select or drag file</h3>
        <p className="text-sm text-text-muted mb-4">PDF, DOCX, MD, PPTX supported (max 20MB)</p>
      </div>
      
      <div className="flex items-center gap-2 py-2 px-4 bg-slate-100 rounded-full text-xs font-semibold text-slate-500 group-hover:bg-primary group-hover:text-white transition-colors">
        Choose from device
      </div>

      <div className="mt-6 flex items-center gap-2 text-xs text-text-muted">
        <AlertCircle className="w-3.5 h-3.5" />
        Files are automatically deleted after 1 hour
      </div>
    </div>
  );
};
