import { ConversionJob } from './types';

export const mockJobs: ConversionJob[] = [
  {
    id: 'job-7283',
    fileName: 'Project-Proposal.docx',
    fileSize: 1024 * 512, // 512 KB
    sourceType: 'docx',
    targetType: 'md',
    status: 'COMPLETED',
    progress: 100,
    createdAt: new Date(Date.now() - 3600000).toISOString(),
    downloadUrl: '#'
  },
  {
    id: 'job-9124',
    fileName: 'Annual-Report-2023.pptx',
    fileSize: 1024 * 1024 * 5, // 5 MB
    sourceType: 'pptx',
    targetType: 'pdf',
    status: 'COMPLETED',
    progress: 100,
    createdAt: new Date(Date.now() - 86400000).toISOString(),
    downloadUrl: '#'
  }
];
