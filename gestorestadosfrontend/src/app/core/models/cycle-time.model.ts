import { Estado } from './estado.model';

export interface TaskCycleTime {
  taskId: number;
  title: string;
  state: Estado;
  inProgressAt?: string;
  doneAt?: string;
  cycleTimeMinutes?: number;
  note?: string;
}

export interface CycleTime {
  workItemId: number;
  totalTasks: number;
  tasksWithCycleTime: number;
  avgCycleTimeMinutes?: number;
  tasks: TaskCycleTime[];
}
