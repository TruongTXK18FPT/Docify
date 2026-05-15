export type FileType = 'pdf' | 'docx' | 'md' | 'markdown' | 'pptx';

export type JobStatus = 'PENDING' | 'QUEUED' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'CANCELLED' | 'EXPIRED';

export interface ConversionJob {
  id: string;
  fileName: string;
  fileSize: number | null;
  sourceType: FileType;
  targetType: FileType;
  status: JobStatus;
  progress: number;
  createdAt: string;
  completedAt?: string | null;
  expiresAt?: string | null;
  errorCode?: string | null;
  errorMessage?: string | null;
  downloadUrl?: string | null;
}

export interface User {
  id: string;
  name: string;
  email: string;
  avatarUrl?: string | null;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  user: User;
}

export interface ApiError {
  code: string;
  message: string;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: ApiError | null;
  meta: {
    timestamp: string;
  };
}

export type ConversionPath = {
  source: FileType;
  targets: FileType[];
};

export const SUPPORTED_CONVERSIONS: ConversionPath[] = [
  { source: 'pptx', targets: ['pdf'] },
  { source: 'md', targets: ['docx', 'pdf'] },
  { source: 'markdown', targets: ['docx', 'pdf'] },
  { source: 'docx', targets: ['md', 'pdf'] },
];
