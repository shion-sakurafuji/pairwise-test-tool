# Pairwise Test Tool

ペアワイズ法を用いて、複数の要素と水準からテストケースの組み合わせを生成するWebアプリケーションです。

要素名と水準を入力すると、組み合わせ結果を生成し、CSV形式でダウンロードできます。

## 主な機能

- 複数の要素名と水準を入力
- 入力する要素の追加・削除
- ペアワイズ法によるテストケース生成
- CSV形式でのダウンロード

## 使用技術

### アプリケーション

- Java
- Spring Boot
- Thymeleaf
- HTML
- CSS
- JavaScript
- Maven

## テスト・CI

Playwrightを使用して、WebアプリケーションのE2Eテストを実装しています。

GitHub Actionsにより、mainブランチへのpushおよびPull Requestを契機として、Spring Bootアプリケーションを起動し、E2Eテストを自動実行します。

2026年9月22日のCI実行結果：5件すべて成功。

## 開発目標・目的

- 目標<br>
テスト設計業務でペアワイズ法を使用する際に、水準のペアを網羅した組み合わせを生成し、CSVファイルとして出力するWebアプリケーションを実装すること。
また、そのアプリに対するE2EテストをPlaywrightで実装し、CIによる自動テストまでを一連の構成として構築すること。
- 目的<br>
実務で使用したPlaywrightによるテスト自動化の経験と、
Javaを使用したWebアプリケーション開発を組み合わせることを目的としています。

## 必要な環境

- Java 26
- Node.js / npm（E2Eテストを実行する場合）

## 起動方法

プロジェクトのルートディレクトリで以下を実行します。

```bash
./mvnw spring-boot:run
```

起動後、ブラウザから `http://localhost:8080/` にアクセスします。

## テストの実行方法

アプリケーションを起動した状態で、
別のターミナルから以下を実行します。

### 依存関係のインストール

```bash
npm ci
npx playwright install chromium
```

### E2Eテストの実行

```bash
npx playwright test --project=chromium
```

## Webアプリを利用する場合

公開URL：未公開（フェーズ8で公開予定）