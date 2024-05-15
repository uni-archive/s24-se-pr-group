import { FormGroup } from '@angular/forms';

export function matchPasswords(group: FormGroup): { [key: string]: any } | null {
  const password = group.controls['password'];
  const passwordRepeat = group.controls['password_repeat'];

  // Check if both fields have values and if they match
  if (passwordRepeat.errors && !passwordRepeat.errors.mustMatch) {
    // return if another validator has already found an error on the passwordRepeat
    return null;
  }

  if (password.value !== passwordRepeat.value) {
    // If they don't match, set error on passwordRepeat
    passwordRepeat.setErrors({ mustMatch: true });
  } else {
    // If they do match, clear errors related to mustMatch
    passwordRepeat.setErrors(null);
  }
  return null;
}
