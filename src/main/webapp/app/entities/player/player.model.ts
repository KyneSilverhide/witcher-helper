import { ICampaign } from 'app/entities/campaign/campaign.model';
import { Race } from 'app/entities/enumerations/race.model';
import { Profession } from 'app/entities/enumerations/profession.model';

export interface IPlayer {
  id?: number;
  name?: string;
  race?: Race;
  profession?: Profession;
  pictureContentType?: string | null;
  picture?: string | null;
  description?: string | null;
  campaign?: ICampaign | null;
}

export class Player implements IPlayer {
  constructor(
    public id?: number,
    public name?: string,
    public race?: Race,
    public profession?: Profession,
    public pictureContentType?: string | null,
    public picture?: string | null,
    public description?: string | null,
    public campaign?: ICampaign | null
  ) {}
}

export function getPlayerIdentifier(player: IPlayer): number | undefined {
  return player.id;
}
