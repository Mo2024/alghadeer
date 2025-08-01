import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransferStudentComponent } from './transfer-student.component';

describe('TransferStudentComponent', () => {
  let component: TransferStudentComponent;
  let fixture: ComponentFixture<TransferStudentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferStudentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TransferStudentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
