import { useEffect, useState } from 'react';
import { Card } from '../components/ui/Card';
import { StatusBadge } from '../components/ui/StatusBadge';
import { Button } from '../components/ui/Button';
import { Search, Download, RefreshCcw, Filter, FileCode, FileText, History, Clock, AlertCircle } from 'lucide-react';
import { formatBytes } from '../lib/utils';
import { ConversionJob } from '../lib/types';
import { conversionService } from '../services/conversion-service';

export function HistoryPage() {
  const [jobs, setJobs] = useState<ConversionJob[]>([]);
  const [search, setSearch] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadHistory();
  }, []);

  const filteredJobs = jobs.filter(job =>
    (job.fileName || '').toLowerCase().includes(search.toLowerCase()) ||
    job.id.toLowerCase().includes(search.toLowerCase())
  );

  const loadHistory = async () => {
    setIsLoading(true);
    setError(null);
    try {
      setJobs(await conversionService.getHistory());
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Could not load history.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDownload = async (job: ConversionJob) => {
    const blob = await conversionService.downloadJob(job.id);
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = buildDownloadName(job);
    document.body.appendChild(anchor);
    anchor.click();
    anchor.remove();
    URL.revokeObjectURL(url);
  };

  const handleRetry = async (job: ConversionJob) => {
    try {
      const retried = await conversionService.retryJob(job.id);
      setJobs(prev => prev.map(item => item.id === job.id ? retried : item));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Could not retry job.');
    }
  };

  return (
    <div className="max-w-6xl mx-auto px-4 py-12">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-end mb-10 gap-6">
        <div>
          <h1 className="text-3xl font-bold text-text-dark mb-2">Job History</h1>
          <p className="text-text-muted">Manage your previous conversion jobs and downloads</p>
        </div>

        <div className="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
          <div className="relative flex-1 sm:w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-text-muted" />
            <input
              type="text"
              placeholder="Search filename..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-10 pr-4 h-11 bg-white border border-border rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
            />
          </div>
          <Button variant="outline" className="h-11" onClick={loadHistory}>
            <Filter className="w-4 h-4 mr-2" />
            Refresh
          </Button>
        </div>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-100 rounded-xl flex items-center gap-3 text-red-600 text-sm font-medium">
          <AlertCircle className="w-5 h-5 shrink-0" />
          {error}
        </div>
      )}

      <Card className="p-0 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-50 border-b border-border">
                <th className="px-6 py-4 text-xs font-bold uppercase tracking-wider text-text-muted">File Details</th>
                <th className="px-6 py-4 text-xs font-bold uppercase tracking-wider text-text-muted">Type</th>
                <th className="px-6 py-4 text-xs font-bold uppercase tracking-wider text-text-muted">Status</th>
                <th className="px-6 py-4 text-xs font-bold uppercase tracking-wider text-text-muted">Created</th>
                <th className="px-6 py-4 text-xs font-bold uppercase tracking-wider text-text-muted text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {isLoading ? (
                <tr>
                  <td colSpan={5} className="px-6 py-16 text-center text-sm text-text-muted">Loading history...</td>
                </tr>
              ) : filteredJobs.length > 0 ? (
                filteredJobs.map((job) => (
                  <tr key={job.id} className="hover:bg-slate-50/50 transition-colors group">
                    <td className="px-6 py-5">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-xl bg-white border border-slate-100 flex items-center justify-center text-text-muted group-hover:bg-primary/5 group-hover:text-primary transition-colors">
                          {job.sourceType === 'md' ? <FileCode className="w-5 h-5" /> : <FileText className="w-5 h-5" />}
                        </div>
                        <div>
                          <p className="text-sm font-bold text-text-dark truncate max-w-[200px]">{job.fileName || 'Untitled file'}</p>
                          <p className="text-[10px] text-text-muted uppercase tracking-tighter">Size: {formatBytes(job.fileSize || 0)} | ID: {job.id}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-5">
                      <div className="flex items-center gap-2">
                        <span className="text-[10px] font-black bg-slate-100 px-2 py-0.5 rounded text-text-muted uppercase">{job.sourceType}</span>
                        <span className="text-slate-400">to</span>
                        <span className="text-[10px] font-black bg-primary/10 px-2 py-0.5 rounded text-primary uppercase">{job.targetType}</span>
                      </div>
                    </td>
                    <td className="px-6 py-5">
                      <StatusBadge status={job.status} />
                    </td>
                    <td className="px-6 py-5">
                      <div className="text-xs text-text-dark font-medium">
                        {job.createdAt ? new Date(job.createdAt).toLocaleDateString() : '-'}
                      </div>
                      <div className="text-[10px] text-text-muted">
                        {job.createdAt ? new Date(job.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '-'}
                      </div>
                    </td>
                    <td className="px-6 py-5 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <Button size="sm" variant="ghost" className="h-8 w-8 p-0" disabled={job.status !== 'COMPLETED'} title="Download" onClick={() => handleDownload(job)}>
                          <Download className="w-4 h-4" />
                        </Button>
                        <Button size="sm" variant="ghost" className="h-8 w-8 p-0" disabled={job.status !== 'FAILED'} title="Retry" onClick={() => handleRetry(job)}>
                          <RefreshCcw className="w-4 h-4" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5} className="px-6 py-24 text-center">
                    <div className="flex flex-col items-center gap-4">
                      <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center text-slate-200">
                        <History className="w-8 h-8" />
                      </div>
                      <div>
                        <h3 className="font-bold text-text-dark">No records found</h3>
                        <p className="text-sm text-text-muted mt-1">Start converting files to see your history here.</p>
                      </div>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>

      <div className="mt-8 p-4 bg-slate-50 rounded-2xl border border-slate-100 flex items-center justify-center gap-2">
        <p className="text-[10px] text-text-muted uppercase font-bold tracking-widest italic flex items-center gap-2">
          <Clock className="w-3 h-3" />
          Converted files expire after 1 hour; metadata remains in history.
        </p>
      </div>
    </div>
  );
}

function buildDownloadName(job: ConversionJob) {
  const filename = job.fileName || `converted_file.${job.targetType}`;
  if (!filename.includes('.')) {
    return `${filename}.${job.targetType}`;
  }
  return `${filename.substring(0, filename.lastIndexOf('.'))}.${job.targetType}`;
}
