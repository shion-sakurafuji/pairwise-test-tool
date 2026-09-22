import { test, expect } from '@playwright/test';
import { readFile } from 'fs/promises';

test('testNo.001-正常入力でCSVをダウンロードできる', async ({ page }) => {

  await page.goto('http://localhost:8080/');

  // 1個目の因子
  await page.locator('[name="factorName"]').nth(0).fill('OS');
  await page.locator('[name="levels"]').nth(0).fill('Win,Mac,Linux');

  // 2個目の因子を追加
  await page.locator('#addFactor').click();

  await page.locator('[name="factorName"]').nth(1).fill('ブラウザ');
  await page.locator('[name="levels"]').nth(1).fill('Chrome,Edge,Firefox');

  // ダウンロード開始を待つ
  const downloadPromise = page.waitForEvent('download');

  await page.getByRole('button', { name: '作成' }).click();

  const download = await downloadPromise;

  // ファイル名確認
  expect(download.suggestedFilename()).toBe('pairwise-test-cases.csv');

  const filePath = await download.path();

  const csv = await readFile(filePath, 'utf-8');

  expect(csv).toContain('OS,ブラウザ');
  expect(csv).toContain('Win,Chrome');
  expect(csv).toContain('Linux,Firefox');

});


test('testNo.002-因子が1個だけの場合はエラーを表示する', async ({ page }) => {

  await page.goto('http://localhost:8080/');

  await page.locator('[name="factorName"]').nth(0).fill('OS');
  await page.locator('[name="levels"]').nth(0).fill('Win,Mac,Linux');

  await page.getByRole('button', { name: '作成' }).click();

  await expect(page.getByText('因子は2個以上入力してください。')).toBeVisible();
});


test('testNo.003-因子名が空の場合はエラーを表示する', async ({ page }) => {

  await page.goto('http://localhost:8080/');

  await page.locator('#addFactor').click();

  await page.locator('[name="levels"]').nth(0).fill('Win,Mac');
  await page.locator('[name="factorName"]').nth(1).fill('ブラウザ');
  await page.locator('[name="levels"]').nth(1).fill('Chrome,Edge');

  await page.getByRole('button', { name: '作成' }).click();

  await expect(page.getByText('因子名を入力してください。')).toBeVisible();
});


test('testNo.004-水準が1個だけの場合はエラーを表示する', async ({ page }) => {

  await page.goto('http://localhost:8080/');

  await page.locator('[name="factorName"]').nth(0).fill('OS');
  await page.locator('[name="levels"]').nth(0).fill('Windows');

  await page.locator('#addFactor').click();

  await page.locator('[name="factorName"]').nth(1).fill('ブラウザ');
  await page.locator('[name="levels"]').nth(1).fill('Chrome,Edge');

  await page.getByRole('button', { name: '作成' }).click();

  await expect(page.getByText('各因子には水準を2個以上入力してください。')).toBeVisible();
});


test('testNo.005-追加した因子を削除すると、その因子は送信されない', async ({ page }) => {

  await page.goto('http://localhost:8080/');

  // 1個目
  await page.locator('[name="factorName"]').nth(0).fill('OS');
  await page.locator('[name="levels"]').nth(0).fill('Win,Mac');

  // 2個目
  await page.locator('#addFactor').click();
  await page.locator('[name="factorName"]').nth(1).fill('ブラウザ');
  await page.locator('[name="levels"]').nth(1).fill('Chrome,Edge');

  // 3個目
  await page.locator('#addFactor').click();
  await page.locator('[name="factorName"]').nth(2).fill('デバイス');
  await page.locator('[name="levels"]').nth(2).fill('PC,スマホ');

  // 2個目を削除(削除ボタンは最上段には存在しないのでnth(0))
  await page.getByRole('button', { name: '削除' }).nth(0).click();

  const downloadPromise = page.waitForEvent('download');

  await page.getByRole('button', { name: '作成' }).click();

  const download = await downloadPromise;

  const filePath = await download.path();
  const csv = await readFile(filePath, 'utf-8');

  expect(csv).toContain('OS,デバイス');
  expect(csv).not.toContain('ブラウザ');
});