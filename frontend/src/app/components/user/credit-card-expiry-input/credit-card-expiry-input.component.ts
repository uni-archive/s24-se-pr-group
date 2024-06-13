import {Component, forwardRef, Input} from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  FormControl,
  FormsModule, NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators
} from "@angular/forms";
import {isArray} from "lodash";

@Component({
  selector: 'app-credit-card-expiry-input',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './credit-card-expiry-input.component.html',
  styleUrl: './credit-card-expiry-input.component.scss',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CreditCardExpiryInputComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CreditCardExpiryInputComponent),
      multi: true
    }
  ]
})
export class CreditCardExpiryInputComponent implements ControlValueAccessor, Validator {
  private _value: number[] = [NaN, NaN];
  // public month: FormControl<string> = new FormControl<string>('', []);
  // public year: FormControl<string> = new FormControl<string>('', []);
  month: string = "";
  year: string = "";

  @Input() markInvalid: boolean = false;
  @Input() markValid: boolean = false;

  constructor() {
    // this.month.setValidators([Validators.required]);
    // this.year.setValidators([Validators.required]);

    /*this.month.updateValueAndValidity();
    this.year.updateValueAndValidity();

    this.month.valueChanges.subscribe(v => {
      this.onChange(this.value);
    })

    this.year.valueChanges.subscribe(v => {
      this.onChange(this.value);
    })*/

  }

  validate(control: AbstractControl): ValidationErrors {
    const monthNum = parseInt(control.value[0]);
    const yearNum = parseInt(control.value[1]);
    if(!isNaN(monthNum) && !isNaN(yearNum)) {
      const validYear = yearNum >= 0 && yearNum < 100;
      const validMonth = monthNum > 0 && monthNum < 13;
      return (validMonth && validYear) ? null : { invalid: true };
    } else {
      return { required: true };
    }
  }

  public get value(): number[] {
    return this._value;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  writeValue(obj: any): void {
    if (isArray(obj)) {
      const m = obj[0];
      const y = obj[1];
      this.formatMonth("" + m);
      this.year = "" + y;
    }
    this._value = obj || [NaN, NaN];
  }

  onChange = (_: any) => {
  };
  onTouched = () => {
  };

  formatYear(input: string): void {
    this.onTouched();
    this._value[1] = parseInt(input);
    this.onChange(this.value);
    this.year = input;
  }

  formatMonth(input: string): void {
    this.onTouched()

    console.log(input);

    if (isNaN(input as any) || (input.length === 2 && input[0] === '0' && isNaN(input[1] as any))) {
      this._value[0] = NaN;
      this.month = "";
      this.onChange(this.value);
      return;
    }

    const val = parseInt(input);

    let updated = "";
    if (val === 0 && this.month === "00") {
      updated = "01";
    } else if (val > 1 && val < 10) {
      document.getElementById("year").focus();
      updated = `0${val}`;
    } else if (val >= 12) {
      document.getElementById("year").focus();
      updated = "12";
    } else {
      updated = input;
    }

    if (updated === "00") {
      document.getElementById("year").focus();
      updated = "01";
    }

    this._value[0] = parseInt(updated);
    this.onChange(this.value);
    this.month = updated;
  }

  protected readonly alert = alert;
}
