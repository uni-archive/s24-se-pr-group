import {Component, ViewChild} from '@angular/core';
import {HallplanComponent, HallSection} from "../hallplan.component";
import {CreateHelper, DrawableEntity, InteractableEntity} from "../helpers";
import {NgForOf, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {Observable} from "rxjs";

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
  selectedSection: HallSection;
  isHidden$: Observable<boolean>;


  constructor(private fb: FormBuilder) {}

  ngAfterViewInit() {
    this.hallplan.onSelectedEntitiesChange = this.onSelectedEntitiesChange.bind(this);
    this.sectionForm = this.fb.group({
      name: [''],
      price: [''],
      color: [''],
      standingOnly: [false]
    });
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
        standingOnly: false//this.selectedSection.standingOnly ?? false
      });
    }
  }

  previewFile(event) {
    console.log(event.target.files)
    console.log(this.hallplan)
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
    this.hallplan.sections.forEach(section => {
      section.seats = CreateHelper.generateSeats(section.points, this.hallplan.ctx).map(pos => ({ pos }));
    });
    this.hallplan.generateEntities();
    this.hallplan.refreshCanvas(true);
  }

  updateSection() {
    const name = this.sectionForm.value.name;
    const price = this.sectionForm.value.price;
    const color = this.sectionForm.value.color;
    const standingOnly = this.sectionForm.value.standingOnly;
    console.log(name, price, color, standingOnly);
    this.selectedSection.name = name;
    this.selectedSection.price = Number(price);
    this.selectedSection.color = color;
    // section.standingOnly = standingOnly;
    if (standingOnly) {
      this.selectedSection.seats.splice(0, this.selectedSection.seats.length);
    }
    this.hallplan.generateEntities();
    this.hallplan.refreshCanvas(true);
    console.log(this.sectionForm)
  }

}
