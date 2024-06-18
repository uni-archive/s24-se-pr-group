import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnInit,
  Output,
} from "@angular/core";

@Component({
  selector: "app-user-confirm-delete-dialog",
  templateUrl: "./user-confirm-delete-dialog.component.html",
  styleUrl: "./user-confirm-delete-dialog.component.scss",
})
export class UserConfirmDeleteDialogComponent implements OnInit {
  @Input() deleteWhat = "?";
  @Output() confirm = new EventEmitter<void>();

  @HostBinding("class") cssClass = "modal fade";

  constructor() {}

  ngOnInit(): void {}
}
