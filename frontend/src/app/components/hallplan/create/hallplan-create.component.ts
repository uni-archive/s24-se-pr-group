import {Component, ViewChild} from '@angular/core';
import {HallplanComponent, HallSection} from "../hallplan.component";
import {CreateHelper, DrawableEntity, InteractableEntity} from "../helpers";
import {NgForOf, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Observable} from "rxjs";
import {HallplanCreateDto} from "../../../dtos/hallplan";
import {HallplanService} from "../../../services/hallplan.service";
import {MessagingService} from "../../../services/messaging.service";

@Component({
  selector: 'app-hallplan-create',
  standalone: true,
  imports: [
    HallplanComponent,
    NgIf,
    NgForOf,
    ReactiveFormsModule
  ],
  templateUrl: './hallplan-create.component.html',
  styleUrl: './hallplan-create.component.scss'
})
export class HallplanCreateComponent {
  @ViewChild('hallplan') hallplan: HallplanComponent;
  sectionForm: FormGroup;
  mainForm: FormGroup;
  selectedSection: HallSection;
  isHidden$: Observable<boolean>;


  constructor(private fb: FormBuilder,
              private messagingService: MessagingService,
              private hallplanService: HallplanService) {
    this.sectionForm = this.fb.group({
      name: ['', Validators.required],
      price: ['', Validators.required],
      color: ['', Validators.required],
      standingOnly: [false]
    });
    this.mainForm = this.fb.group({
      hallname: ['', Validators.required],
      backgroundImage: ['', Validators.required]
    })
  }

  ngAfterViewInit() {
    this.hallplan.onSelectedEntitiesChange = this.onSelectedEntitiesChange.bind(this);
  }

  onSelectedEntitiesChange(selectedEntities: InteractableEntity[]) {
    //const selectedEntity = selectedEntities.length ? selectedEntities[0] : null;
    const selectedSections = this.hallplan.sections.filter(section => selectedEntities.map((entity: InteractableEntity & DrawableEntity) => entity.data).includes(section));
    this.selectedSection = selectedSections.length ? selectedSections[0] : null;
    if (this.selectedSection) {
      this.sectionForm.patchValue({
        name: this.selectedSection.name,
        price: this.selectedSection.price.toString(),
        color: this.selectedSection.color,
        standingOnly: this.selectedSection.isStandingOnly
      });
    }
  }

  onSubmit() {
    if (!this.mainForm.valid)
      return;
    console.log(this.mainForm.value.backgroundImage)
    console.log(this.mapHallplanToCreateDto())
    this.hallplanService.createHallplan(this.mapHallplanToCreateDto()).subscribe({
      next: response => {
        this.messagingService.setMessage("Hallplan wurde erfolgreich erstellt", "success");
        console.log('Response:', response);
      }
      ,
      error: error => {
        this.messagingService.setMessage('Error creating hallplan: ' + error.error, 'danger');
        console.error('Error:', error);
      }
    });
  }

  mapHallplanToCreateDto(): HallplanCreateDto {
    return {
      name: this.mainForm.value.hallname,
      backgroundImage: this.hallplan.backgroundImageUrl,
      sectors: this.hallplan.sections.map(section => ({
        name: section.name,
        color: section.color,
        frontendCoordinates: JSON.stringify(section.points),
        spots: section.seats.map(seat => ({
          frontendCoordinates: JSON.stringify(seat.pos),
        })),
        isStandingOnly: section.isStandingOnly
      }))
    };
  }

  previewFile(event) {
    const reader = new FileReader();
    reader.addEventListener('load', async (event) => {
      // @ts-ignore
      await this.hallplan.setBackgroundImage(event.target.result)
      this.hallplan.generateEntities();
      this.hallplan.refreshCanvas(true);
    });
    reader.readAsDataURL(event.target.files[0]);
  }

  regenerateSeats() {
    this.hallplan.sections.filter(section => !section.isStandingOnly).forEach(section => {
      section.seats = CreateHelper.generateSeats(section.points, this.hallplan.ctx).map(pos => ({pos}));
    });
    this.hallplan.generateEntities();
    this.hallplan.refreshCanvas(true);
  }

  updateSection() {
    if (!this.sectionForm.valid)
      return;
    const name = this.sectionForm.value.name;
    const price = this.sectionForm.value.price;
    const color = this.sectionForm.value.color;
    const standingOnly = this.sectionForm.value.standingOnly;
    console.log(name, price, color, standingOnly);
    this.selectedSection.name = name;
    this.selectedSection.price = Number(price);
    this.selectedSection.color = color;
    this.selectedSection.isStandingOnly = standingOnly;
    if (standingOnly) {
      this.selectedSection.seats.splice(0, this.selectedSection.seats.length);
    }
    this.hallplan.generateEntities();
    this.hallplan.refreshCanvas(true);
  }

}
