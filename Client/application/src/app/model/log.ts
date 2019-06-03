export interface Log {
  id: number;
  type: string;
  category: string;
  source: string;
  timestamp: number;
  hostAddress: string;
  message: string;
}
