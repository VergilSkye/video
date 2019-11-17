import { Moment } from 'moment';

export interface IConsulta {
  id?: number;
  uuid?: string;
  created_at?: Moment;
  ended_at?: Moment;
  duration?: number;
  psicologo_idLogin?: string;
  psicologo_idId?: number;
  paciente_idLogin?: string;
  paciente_idId?: number;
}

export class Consulta implements IConsulta {
  constructor(
    public id?: number,
    public uuid?: string,
    public created_at?: Moment,
    public ended_at?: Moment,
    public duration?: number,
    public psicologo_idLogin?: string,
    public psicologo_idId?: number,
    public paciente_idLogin?: string,
    public paciente_idId?: number
  ) {}
}
