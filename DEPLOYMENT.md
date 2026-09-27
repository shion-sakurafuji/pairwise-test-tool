
# デプロイ・運用手順

## 1. システム構成

ペアワイズテストツールは、さくらのVPS上でDockerを使用して公開している。

公開URL：
https://tk2-111-56591.vs.sakura.ne.jp/

使用環境：
- Ubuntu 24.04
- Docker
- Java 26 / Spring Boot
- Caddy
- GitHub Actions

CaddyがHTTPS通信を受け付け、VPS内部のDockerコンテナ（127.0.0.1:8080）へ転送する。

## 2. 自動デプロイの流れ

mainブランチへpushすると、GitHub Actionsが以下を実行する。

1. Javaアプリケーションをビルド
2. PlaywrightによるE2Eテスト
3. テスト成功後、SSHでVPSに接続
4. デプロイスクリプトを実行
5. テスト済みのコミットを取得
6. Dockerイメージをビルド
7. Dockerコンテナを更新
8. HTTPによる起動確認

Playwrightが失敗した場合、デプロイは実行しない。
Pull Requestでもデプロイは実行しない。

GitHub Actionsの設定ファイル：
`.github/workflows/ci.yml`

## 3. VPSの構成

VPSへの接続ユーザー：

- ubuntu：普段の管理作業に使用
- deploy：GitHub ActionsからのSSH接続に使用

主なファイル・ディレクトリ：

- `/srv/pairwise-test-tool`：デプロイ用ソースコード
- `/usr/local/sbin/pairwise-deploy`：デプロイスクリプト
- `/etc/sudoers.d/pairwise-deploy`：スクリプトの実行権限
- `/etc/caddy/Caddyfile`：HTTPSとリバースプロキシの設定

## 4. SSH認証

GitHub Actionsは、専用SSH鍵を使用してdeployユーザーとしてVPSに接続する。

GitHub Secrets：

- `VPS_SSH_PRIVATE_KEY`：SSH接続用の秘密鍵
- `VPS_SSH_KNOWN_HOSTS`：接続先VPSのホスト公開鍵

秘密鍵やパスワードは、このファイルやGitHubのソースコードに記載しない。

## 5. 稼働状態の確認

以下のコマンドはVPSのubuntuユーザーで実行する。

### Dockerコンテナの確認

```bash
sudo docker ps
```

`pairwise-test-tool` が起動していることを確認する。

### アプリケーションのログ

```bash
sudo docker logs --tail 100 pairwise-test-tool
```

### Caddyの状態

```bash
sudo systemctl status caddy
```

### Caddyのログ

```bash
sudo journalctl -u caddy -n 100 --no-pager
```

### VPS内部からの応答確認

```bash
curl -I http://127.0.0.1:8080/
```

HTTP 200など、正常な応答が返ることを確認する。

## 6. 手動デプロイ

通常はGitHub Actionsによる自動デプロイを使用する。

自動デプロイが利用できない場合は、GitHub Actionsでテスト成功済みのコミットIDを確認する。

VPSのubuntuユーザーで、デプロイスクリプトにそのコミットID（40文字）を指定して実行する。

デプロイに失敗すると、スクリプトは以前のDockerイメージへの復旧を試みる。

※ 自動復旧がすべての障害に対応するわけではない。復旧処理が失敗した場合は、ログと旧Dockerイメージを確認する。

## 7. 障害発生時

まずGitHub Actionsの実行履歴から、失敗したJobとStepを特定する。

- test失敗：ビルド結果、Spring Bootの起動ログ、Playwrightの結果を確認
- deploy失敗：SSH接続、VPSのデプロイスクリプト、Dockerの状態を確認
- 公開サイトに接続できない：DockerとCaddyの稼働状態を確認

公開中のアプリに問題が発生した場合、追加のデプロイを繰り返す前に、Dockerの状態とログを記録する。

## 8. 運用上の注意

- SSH秘密鍵をGitHubのソースコードに保存しない。
- VPS上のファイルを直接修正せず、原則としてGitHub経由で変更する。
- mainへのpushは、READMEなどの文書だけの変更でもCI/CDを起動する。
- デプロイ後は、公開画面で主要機能が動作することを確認する。
- VPSのOSとDockerを定期的に更新する。
