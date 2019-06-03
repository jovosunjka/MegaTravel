import {Log} from './log';

export interface LogsPage {
  content: Log[];
  last: boolean;
  first: boolean;
  totalPages: number;
}
