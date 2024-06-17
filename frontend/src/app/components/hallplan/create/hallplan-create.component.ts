import {Component, ViewChild} from '@angular/core';
import {HallplanComponent, HallSection} from "../hallplan.component";
import {CreateHelper, DrawableEntity, InteractableEntity} from "../helpers";
import {NgForOf, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Observable} from "rxjs";
import {HallplanCreateDto} from "../../../dtos/hallplan";
import {HallplanService} from "../../../services/hallplan.service";
import {MessagingService} from "../../../services/messaging.service";
import {Router} from "@angular/router";

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
              private router: Router,
              private hallplanService: HallplanService) {
    this.sectionForm = this.fb.group({
      name: ['', Validators.required],
      color: ['', Validators.required],
      standingOnly: [false],
      spotCount: ['', Validators.required]
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
    const selectedSections = this.hallplan.sections.filter(section => selectedEntities.map((entity: InteractableEntity & DrawableEntity) => entity.data).includes(section));
    this.selectedSection = selectedSections.length ? selectedSections[0] : null;
    this.updateSectionForm();
  }

  updateSectionForm() {
    if (this.selectedSection) {
      this.sectionForm.patchValue({
        name: this.selectedSection.name,
        color: this.selectedSection.color,
        standingOnly: this.selectedSection.isStandingOnly,
        spotCount: this.selectedSection.spotCount,
      });

      if (this.selectedSection.isStandingOnly) {
        this.sectionForm.get('spotCount').enable();
      } else {
        this.sectionForm.get('spotCount').disable();
      }
    }
  }

  onSubmit() {
    if (!this.mainForm.valid)
      return;
    this.hallplanService.createHallplan(this.mapHallplanToCreateDto()).subscribe({
      next: response => {
        this.messagingService.setMessage("Saalplan wurde erfolgreich erstellt", "success");
        this.router.navigate(['/user/home']);
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
        frontendCoordinates: JSON.stringify(section.points.map(point => ({x: Math.floor(point.x * 100) / 100, y: Math.floor(point.y * 100) / 100}))),
        spots: section.seats.map(seat => ({
          frontendCoordinates: JSON.stringify(seat.pos),
        })),
        spotCount: section.spotCount,
        standingOnly: section.isStandingOnly
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
      section.spotCount = section.seats.length;
    });
    this.hallplan.generateEntities();
    this.hallplan.refreshCanvas(true);
  }

  updateSection(e) {
    e.preventDefault();
    if (!this.sectionForm.valid)
      return;
    const name = this.sectionForm.value.name;
    const price = this.sectionForm.value.price;
    const color = this.sectionForm.value.color;
    const standingOnly = this.sectionForm.value.standingOnly;
    const spotCount = this.sectionForm.value.spotCount;
    this.selectedSection.name = name;
    this.selectedSection.price = Number(price);
    this.selectedSection.color = color;
    this.selectedSection.isStandingOnly = standingOnly;
    if (standingOnly) {
      this.selectedSection.seats.splice(0, this.selectedSection.seats.length);
      this.selectedSection.spotCount = Number(spotCount);
    } else {
      this.selectedSection.spotCount = this.selectedSection.seats.length;
    }
    this.updateSectionForm();
    this.hallplan.generateEntities();
    this.hallplan.refreshCanvas(true);
  }

}
