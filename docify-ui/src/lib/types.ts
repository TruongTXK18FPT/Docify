export type FileType = 'pdf' | 'docx' | 'md' | 'pptx';

export type JobStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';

export interface ConversionJob {
  id: string;
  fileName: string;
  fileSize: number;
  sourceType: FileType;
  targetType: FileType;
  status: JobStatus;
  progress: number;
  createdAt: string;
  resultUrl?: string;
}

export type ConversionPath = {
  source: FileType;
  targets: FileType[];
};

export const SUPPORTED_CONVERSIONS: ConversionPath[] = [
  { source: 'pptx', targets: ['pdf'] },
  { source: 'md', targets: ['docx', 'pdf'] },
  { source: 'docx', targets: ['md'] },
  { source: 'pdf', targets: ['md', 'docx'] },
];
