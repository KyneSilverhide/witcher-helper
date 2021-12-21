import * as dayjs from 'dayjs';
import { ICampaign } from 'app/entities/campaign/campaign.model';
import { IPlayer } from 'app/entities/player/player.model';

export interface IScenario {
  id?: number;
  title?: string;
  description?: string | null;
  mapCoords?: string;
  date?: dayjs.Dayjs | null;
  mapIcon?: string;
  campaign?: ICampaign | null;
  players?: IPlayer[] | null;
}

export class Scenario implements IScenario {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string | null,
    public mapCoords?: string,
    public date?: dayjs.Dayjs | null,
    public mapIcon?: string,
    public campaign?: ICampaign | null,
    public players?: IPlayer[] | null
  ) {}
}

export function getScenarioIdentifier(scenario: IScenario): number | undefined {
  return scenario.id;
}
