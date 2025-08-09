import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelSessionsComponent } from './cancel-sessions.component';

describe('CancelSessionsComponent', () => {
  let component: CancelSessionsComponent;
  let fixture: ComponentFixture<CancelSessionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelSessionsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CancelSessionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
