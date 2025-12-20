import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DropStudentComponent } from './drop-student.component';

describe('DropStudentComponent', () => {
  let component: DropStudentComponent;
  let fixture: ComponentFixture<DropStudentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DropStudentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DropStudentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
