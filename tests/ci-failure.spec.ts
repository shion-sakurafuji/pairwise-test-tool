import { test, expect } from '@playwright/test';

test('CI failure verification', () => {
    expect(1).toBe(2);
});