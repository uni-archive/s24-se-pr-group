export class HallplanCreateDto {
  name: string;
  backgroundImage: string;
  sectors: HallplanSectionCreateDto[];
}

export class HallplanSectionCreateDto {
  name: string;
  color: string;
  spots: HallplanSpotCreateDto[];
  isStandingOnly: boolean;
  frontendCoordinates: string;
}

export class HallplanSpotCreateDto {
  frontendCoordinates: string;
}
