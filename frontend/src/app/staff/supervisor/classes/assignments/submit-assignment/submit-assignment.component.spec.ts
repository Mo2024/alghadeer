import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmitAssignmentComponent } from './submit-assignment.component';

describe('SubmitAssignmentComponent', () => {
  let component: SubmitAssignmentComponent;
  let fixture: ComponentFixture<SubmitAssignmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubmitAssignmentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SubmitAssignmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
