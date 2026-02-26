import { TestBed } from '@angular/core/testing';
import { CurrencyFormatPipe } from './currency-format.pipe';

describe('CurrencyFormatPipe', () => {
  let pipe: CurrencyFormatPipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CurrencyFormatPipe]
    });
    pipe = TestBed.inject(CurrencyFormatPipe);
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should format number as currency', () => {
    const result = pipe.transform(1000);
    expect(result).toContain('$');
    expect(result).toContain('1,000');
  });

  it('should handle decimal values', () => {
    const result = pipe.transform(1000.50);
    expect(result).toContain('$');
    expect(result).toContain('1,000.50');
  });

  it('should handle zero', () => {
    const result = pipe.transform(0);
    expect(result).toContain('$');
    expect(result).toContain('0.00');
  });

  it('should handle large numbers', () => {
    const result = pipe.transform(1000000);
    expect(result).toContain('$');
    expect(result).toContain('1,000,000');
  });

  it('should format with exactly 2 decimal places', () => {
    const result1 = pipe.transform(100);
    const result2 = pipe.transform(100.5);
    const result3 = pipe.transform(100.55);

    expect(result1).toMatch(/\d+\.\d{2}/);
    expect(result2).toMatch(/\d+\.\d{2}/);
    expect(result3).toMatch(/\d+\.\d{2}/);
  });
});
