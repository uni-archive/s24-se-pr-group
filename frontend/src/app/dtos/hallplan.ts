export class HallplanCreateDto {
  name: string;
  backgroundImage: string;
  sectors: HallplanSectionCreateDto[];
}

export class HallplanSectionCreateDto {
  name: string;
  color: string;
  spots: HallplanSpotCreateDto[];
  standingOnly: boolean;
  frontendCoordinates: string;
}

export class HallplanSpotCreateDto {
  frontendCoordinates: string;
}
