export interface ICampaign {
  id?: number;
  name?: string;
}

export class Campaign implements ICampaign {
  constructor(public id?: number, public name?: string) {}
}

export function getCampaignIdentifier(campaign: ICampaign): number | undefined {
  return campaign.id;
}
