import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MutualFundService } from './mutual-fund.service';
import { MutualFund, InvestmentProjection } from '../models/investment.model';

describe('MutualFundService', () => {
  let service: MutualFundService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MutualFundService]
    });
    service = TestBed.inject(MutualFundService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch mutual funds', () => {
    const mockFunds: MutualFund[] = [
      { ticker: 'VFIAX', name: 'Vanguard 500 Index Fund' },
      { ticker: 'FXAIX', name: 'Fidelity 500 Index Fund' }
    ];

    service.getMutualFunds().subscribe(funds => {
      expect(funds).toEqual(mockFunds);
      expect(funds.length).toBe(2);
    });

    const req = httpMock.expectOne('/api/mutual-funds');
    expect(req.request.method).toBe('GET');
    req.flush(mockFunds);
  });

  it('should get investment projection', () => {
    const mockProjection: InvestmentProjection = {
      ticker: 'VFIAX',
      principal: 10000,
      years: 5,
      riskFreeRate: 0.04,
      beta: 1.2,
      expectedReturnRate: 0.10,
      capmRate: 0.112,
      futureValue: 17005.84
    };

    service.getInvestmentProjection('VFIAX', 10000, 5).subscribe(projection => {
      expect(projection).toEqual(mockProjection);
      expect(projection.futureValue).toBeGreaterThan(projection.principal);
    });

    const req = httpMock.expectOne(req =>
      req.url === '/api/investments/future-value' &&
      req.params.get('ticker') === 'VFIAX' &&
      req.params.get('principal') === '10000' &&
      req.params.get('years') === '5'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockProjection);
  });

  it('should handle errors when fetching mutual funds', () => {
    service.getMutualFunds().subscribe(
      () => fail('should have failed'),
      error => {
        expect(error.status).toBe(500);
      }
    );

    const req = httpMock.expectOne('/api/mutual-funds');
    req.flush('Failed to load funds', { status: 500, statusText: 'Server Error' });
  });

  it('should handle errors when getting projection', () => {
    service.getInvestmentProjection('INVALID', 10000, 5).subscribe(
      () => fail('should have failed'),
      error => {
        expect(error.status).toBe(400);
      }
    );

    const req = httpMock.expectOne(req =>
      req.url === '/api/investments/future-value'
    );
    req.flush('Invalid ticker', { status: 400, statusText: 'Bad Request' });
  });
});
