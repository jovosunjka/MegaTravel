import {Relation} from './enum/relation';
import {TimeUnit} from './enum/timeunit';

export interface LoginTemplate {
  loginSuccess: boolean;
  loginAttemptCount: number;
  hostRelation: Relation;
  sourceRelation: Relation;
  timeCount: number;
  timeUnit: TimeUnit;
}
