import { Component, EventEmitter, forwardRef, Input, Output, OnInit } from '@angular/core';
import { HallPlanDto, HallPlanEndpointService } from '../../../services/openapi';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-hallplan-autocomplete',
  templateUrl: './hall-plan-autocomplete.component.html',
  styleUrls: ['./hall-plan-autocomplete.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => HallPlanAutocompleteComponent),
    multi: true
  }]
})
export class HallPlanAutocompleteComponent implements ControlValueAccessor, OnInit {
  @Output() selectedHallPlan = new EventEmitter<HallPlanDto>();
  @Output() resetHallPlan = new EventEmitter<void>();
  @Input() initialHallPlan: HallPlanDto | null;

  hallPlanValue: HallPlanDto | null = null;

  constructor(private hallPlanService: HallPlanEndpointService) { }

  ngOnInit(): void {
    if (this.initialHallPlan) {
      this.writeValue(this.initialHallPlan);
    }
  }

  searchHallPlans = (query: string) => this.hallPlanService.findByName(query);

  onHallPlanSelected(hallPlan: HallPlanDto): void {
    if (hallPlan !== this.hallPlanValue) {
      this.hallPlanValue = hallPlan;
      this.selectedHallPlan.emit(hallPlan);
      this.onChange(this.hallPlanValue);
      this.onTouched();
    }
  }

  writeValue(obj: HallPlanDto): void {
    this.hallPlanValue = obj;
    if (obj) {
      this.onHallPlanSelected(obj);
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    // Handle the disabled state if necessary
  }

  private onChange: any = () => {};
  private onTouched: any = () => {};

  onHallPlanReset(): void {
    this.resetHallPlan.emit();
  }
}
