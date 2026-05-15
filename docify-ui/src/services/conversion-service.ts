import { ConversionJob, FileType } from '../lib/types';
import { apiDownload, apiRequest } from './api-client';

export const conversionService = {
  createJob: (file: File, targetType: FileType, signal?: AbortSignal) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('targetType', targetType);

    return apiRequest<ConversionJob>('/api/conversions', {
      method: 'POST',
      body: formData,
      signal,
    });
  },

  getJob: (jobId: string) => apiRequest<ConversionJob>(`/api/conversions/${jobId}`),
  getHistory: () => apiRequest<ConversionJob[]>('/api/conversions/history'),
  retryJob: (jobId: string) => apiRequest<ConversionJob>(`/api/conversions/${jobId}/retry`, { method: 'POST' }),
  cancelJob: (jobId: string) => apiRequest<ConversionJob>(`/api/conversions/${jobId}/cancel`, { method: 'POST' }),
  downloadJob: (jobId: string) => apiDownload(`/api/conversions/${jobId}/download`),
};
