import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalahComponent } from './salah.component';

describe('SalahComponent', () => {
  let component: SalahComponent;
  let fixture: ComponentFixture<SalahComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SalahComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SalahComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
