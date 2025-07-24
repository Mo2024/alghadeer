import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-delete',
  standalone: true,
  imports: [],
  templateUrl: './confirm-delete.component.html',
  styleUrl: './confirm-delete.component.css'
})
export class ConfirmDeleteComponent {



  @Output() closeClickedDelete: EventEmitter<void> = new EventEmitter<void>();
  @Input() closeClicked: EventEmitter<void> = new EventEmitter<void>();
  @Input() staffId!: number;


  isDisabled: boolean = false;

  emitCloseClicked(): void {
    this.closeClickedDelete.emit();
  }

  deleteJv() {
    // this.isDisabled = true;
    // let staffObj = {
    //   id: this.staffId,
    //   site: {
    //     id: this.selectedSiteForm
    //   },
    //   page: this.page - 1, //deducting 1 because the count in spring starts from 0 unlike the frontend which starts with 1 
    //   size: this.size,
    //   siteQuery: this.siteQuery,
    //   selectedSite: this.selectedSite
    // }
    // this.jvService.deleteJv(jvObject).subscribe({
    //   next: (response) => {
    //     this.isDisabled = false;
    //     this.newJvList.emit(response);
    //     this.closeClicked.emit();
    //     this.closeClickedDelete.emit();
    //   },
    //   error: (error) => {
    //     this.isDisabled = false
    //     if (error.error.error) {
    //       alert(error.error.error)
    //     } else {
    //       alert('unknown error occured')
    //     }
    //   }
    // });
  }

}
