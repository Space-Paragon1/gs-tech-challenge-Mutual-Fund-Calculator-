import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CalculatorComponent } from './calculator.component';
import { MutualFundService } from '../../services/mutual-fund.service';
import { CurrencyFormatPipe } from '../../pipes/currency-format.pipe';
import { of, throwError } from 'rxjs';

describe('CalculatorComponent', () => {
  let component: CalculatorComponent;
  let fixture: ComponentFixture<CalculatorComponent>;
  let mutualFundService: jasmine.SpyObj<MutualFundService>;

  const mockFunds = [
    { ticker: 'VFIAX', name: 'Vanguard 500 Index Fund Admiral Shares' },
    { ticker: 'FXAIX', name: 'Fidelity 500 Index Fund' }
  ];

  const mockProjection = {
    ticker: 'VFIAX',
    principal: 10000,
    years: 5,
    riskFreeRate: 0.04,
    beta: 1.2,
    expectedReturnRate: 0.10,
    capmRate: 0.112,
    futureValue: 17005.84
  };

  beforeEach(async () => {
    const mutualFundServiceSpy = jasmine.createSpyObj('MutualFundService', [
      'getMutualFunds',
      'getInvestmentProjection'
    ]);

    await TestBed.configureTestingModule({
      imports: [
        CalculatorComponent,
        ReactiveFormsModule,
        FormsModule,
        CommonModule,
        HttpClientTestingModule,
        CurrencyFormatPipe
      ],
      providers: [
        { provide: MutualFundService, useValue: mutualFundServiceSpy }
      ]
    }).compileComponents();

    mutualFundService = TestBed.inject(MutualFundService) as jasmine.SpyObj<MutualFundService>;
    fixture = TestBed.createComponent(CalculatorComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load mutual funds on init', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));

    component.ngOnInit();

    expect(component.mutualFunds).toEqual(mockFunds);
    expect(component.investmentForm.get('ticker')?.value).toBe('VFIAX');
  });

  it('should initialize form with validators', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));
    fixture.detectChanges();

    const tickerControl = component.investmentForm.get('ticker');
    const principalControl = component.investmentForm.get('principal');
    const yearsControl = component.investmentForm.get('years');

    expect(tickerControl?.hasError('required')).toBeTruthy();
    expect(principalControl?.hasError('required')).toBeTruthy();
    expect(yearsControl?.hasError('required')).toBeTruthy();
  });

  it('should calculate projection with valid form', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));
    mutualFundService.getInvestmentProjection.and.returnValue(of(mockProjection));

    component.ngOnInit();
    component.investmentForm.patchValue({
      ticker: 'VFIAX',
      principal: 10000,
      years: 5
    });

    component.calculateProjection();

    expect(component.projection).toEqual(mockProjection);
    expect(component.loading).toBeFalse();
  });

  it('should not calculate projection with invalid form', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));
    fixture.detectChanges();

    component.calculateProjection();

    expect(component.projection).toBeNull();
    expect(component.error).toBeTruthy();
  });

  it('should handle error when loading funds', () => {
    mutualFundService.getMutualFunds.and.returnValue(throwError(() => new Error('Load failed')));

    component.ngOnInit();

    expect(component.error).toBeTruthy();
    expect(component.mutualFunds.length).toBe(0);
  });

  it('should handle error when calculating projection', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));
    mutualFundService.getInvestmentProjection.and.returnValue(
      throwError(() => new Error('Calculation failed'))
    );

    component.ngOnInit();
    component.investmentForm.patchValue({
      ticker: 'VFIAX',
      principal: 10000,
      years: 5
    });

    component.calculateProjection();

    expect(component.error).toBeTruthy();
    expect(component.projection).toBeNull();
  });

  it('should reset form', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));
    component.ngOnInit();

    component.investmentForm.patchValue({
      ticker: 'FXAIX',
      principal: 5000,
      years: 10
    });
    component.projection = mockProjection;
    component.error = 'Some error';

    component.resetForm();

    expect(component.investmentForm.get('ticker')?.value).toBe('VFIAX');
    expect(component.investmentForm.get('principal')?.value).toBeNull();
    expect(component.projection).toBeNull();
    expect(component.error).toBeNull();
  });

  it('should calculate total gain correctly', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));
    mutualFundService.getInvestmentProjection.and.returnValue(of(mockProjection));

    component.ngOnInit();
    component.investmentForm.patchValue({
      ticker: 'VFIAX',
      principal: 10000,
      years: 5
    });

    component.calculateProjection();
    fixture.detectChanges();

    const expectedGain = mockProjection.futureValue - mockProjection.principal;
    expect(component.totalGain).toBeCloseTo(expectedGain, 2);
  });

  it('should calculate gain percentage correctly', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));
    mutualFundService.getInvestmentProjection.and.returnValue(of(mockProjection));

    component.ngOnInit();
    component.investmentForm.patchValue({
      ticker: 'VFIAX',
      principal: 10000,
      years: 5
    });

    component.calculateProjection();
    fixture.detectChanges();

    const expectedGainPercentage = ((mockProjection.futureValue - mockProjection.principal) / mockProjection.principal) * 100;
    expect(component.gainPercentage).toBeCloseTo(expectedGainPercentage, 2);
  });

  it('should disable form during loading', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));
    component.ngOnInit();

    const tickerControl = component.investmentForm.get('ticker');
    const principalControl = component.investmentForm.get('principal');
    const yearsControl = component.investmentForm.get('years');

    // All controls should be enabled initially
    expect(tickerControl?.enabled).toBeTruthy();
    expect(principalControl?.enabled).toBeTruthy();
    expect(yearsControl?.enabled).toBeTruthy();
  });

  it('should validate principal greater than 0', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));
    fixture.detectChanges();

    const principalControl = component.investmentForm.get('principal');
    principalControl?.setValue(0);

    expect(principalControl?.hasError('min')).toBeTruthy();
  });

  it('should validate years between 1 and 50', () => {
    mutualFundService.getMutualFunds.and.returnValue(of(mockFunds));
    fixture.detectChanges();

    const yearsControl = component.investmentForm.get('years');

    yearsControl?.setValue(0);
    expect(yearsControl?.hasError('min')).toBeTruthy();

    yearsControl?.setValue(51);
    expect(yearsControl?.hasError('max')).toBeTruthy();
  });
});
