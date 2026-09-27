# Pairwise Test Tool

ペアワイズ法を用いて、複数の要素と水準からテストケースの組み合わせを生成するWebアプリケーションです。

要素名と水準を入力すると、組み合わせ結果を生成し、CSV形式でダウンロードできます。


## 公開アプリ

https://tk2-111-56591.vs.sakura.ne.jp/

ブラウザから利用できます。インストールは不要です。


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

### テスト・CI/CD

- Playwright
- GitHub Actions

### インフラ

- Docker
- さくらのVPS（Ubuntu）
- Caddy
- HTTPS


## CI/CD

GitHub Actionsを利用し、E2Eテストから公開環境へのデプロイまでを自動化しています。

### CI：自動テスト

mainブランチへのpush、またはmainブランチ宛てのPull Requestを契機に、以下の処理を実行します。

1. JavaおよびNode.jsの実行環境を準備
2. Spring Bootアプリケーションをビルド
3. アプリケーションを起動
4. PlaywrightでE2Eテストを実行

現時点では、CIのJavaビルド時にJUnitテストをスキップしています。

### CD：自動デプロイ

mainブランチへのpushによってCIが成功すると、続けてデプロイ処理を実行します。

1. GitHub ActionsからSSHでVPSに接続
2. テスト済みのコミットをVPSに取得
3. Dockerイメージをビルド
4. Dockerコンテナを更新
5. アプリケーションの起動を確認

Pull Requestでは自動デプロイを実行しません。また、CIが失敗した場合はデプロイJobを実行しない構成です。

### 公開環境

さくらのVPS上でDockerコンテナを稼働させています。

Caddyをリバースプロキシとして使用し、HTTPSで公開しています。


## 開発目標・目的

### 目標
テスト設計業務でペアワイズ法を使用する際に、水準のペアを網羅した組み合わせを生成し、CSVファイルとして出力するWebアプリケーションを実装すること。
また、そのアプリに対するE2EテストをPlaywrightで実装し、CIによる自動テストまでを一連の構成として構築すること。

### 目的
実務で使用したPlaywrightによるテスト自動化の経験と、
Javaを使用したWebアプリケーション開発を組み合わせることを目的としています。

### 必要な環境

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


## 現在の実装状況

- Webアプリケーションの実装
- CSVダウンロード
- PlaywrightによるE2Eテスト
- GitHub ActionsによるCI
- Dockerによるコンテナ化
- VPSへのHTTPS公開
- CI成功後の自動デプロイ


## 今後の追加実装

- JUnitによる入力バリデーションのテスト
- テストケース生成機能の拡張
- 入力画面の使いやすさの改善