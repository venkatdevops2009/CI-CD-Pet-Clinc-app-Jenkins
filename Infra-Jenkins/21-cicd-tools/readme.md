

### Jenkins master
* Access: `http://jenkins.<your-domain>:8080`
* Initial admin password:
  ```
  sudo cat /var/lib/jenkins/secrets/initialAdminPassword
  ```
* Create the admin user, then install plugins (Manage Jenkins → Plugins):
  * Pipeline Stage View
  * Pipeline Utility Steps
  * AWS Credentials
  * AWS Steps
  * SonarQube Scanner

### Jenkins agent node
* Manage Jenkins → Nodes → New Node → Permanent Agent
  * Remote root directory: `/home/ec2-user/jenkins-agent`
  * Launch method: **Launch agents via SSH**
  * Host: `jenkins-agent.<your-domain>` (private DNS — master and agent sit in
    the same VPC)
  * Credentials: `ssh-creds` (below)
  * Host key verification: Non-verifying (lab setup only)

### SonarQube server
* SSH in:
  ```
  ssh -i <private-key> ubuntu@sonar.ip
  ```
* Default credentials:
  ```
  /opt/default-sonar-login.txt
  ```
* Generate a token for Jenkins: **My Account → Security → Generate Token**
* Configure the webhook so SonarQube reports the quality-gate result back to
  the pipeline: **Administration → Configuration → Webhooks → Create**
  * Name: `Jenkins`
  * URL: `http://jenkins.<your-domain>:8080/sonarqube-webhook/`
* In Jenkins: **Manage Jenkins → Tools** — add the SonarQube Scanner
  installation. **Manage Jenkins → System** — add the SonarQube server (URL +
  the token as a secret-text credential).
* Mode should be standard. My Account -> Administration -> General -> Mode -> Standard

**Configure Quality Gates**

A Quality Gate is the pass/fail check SonarQube runs against every scan — if
any condition fails, the pipeline should stop the build instead of shipping it
(shift-left). Configure one at **Quality Gates → Create**, then add these
conditions, applied to both **Overall Code** and **New Code**:

| Metric | What it measures | Target |
|---|---|---|
| Bugs | Code that behaves incorrectly — a functional defect | 0 |
| Vulnerabilities | Security loopholes an attacker could exploit | 0 |
| Code Smells | Maintainability issues — doesn't break the app, but hard to safely change later | 0 |
| Maintainability Rating | A–E grade based on the effort to fix all code smells vs. the codebase size | A |
| Security Rating | A–E grade based on the most severe unresolved vulnerability | A |
| Reliability Rating | A–E grade based on the most severe unresolved bug | A |
| Code Coverage | % of code exercised by unit tests | 80% |

Set the gate as default (**Quality Gates → your gate → Set as Default**) so
every project inherits it. Combined with the webhook configured above, this is
what lets a failed gate fail the Jenkins pipeline stage instead of silently
passing.

### Credentials (Manage Jenkins → Credentials)
| id | type | used for |
|---|---|---|
| `ssh-creds` | Username with password | SSH into the Jenkins agent — user `ec2-user`, password `DevOps321` (the course AMI's default login) |
| `aws-creds` | AWS Credentials | pipeline steps that call AWS (ECR push, EKS deploy, etc.) — access key / secret key |
| sonar-creds | Secret text | SonarQube Scanner authentication, generated above |
| `github-token` | Secret text | GitHub fine-grained PAT for querying Dependabot alerts, generated below |

### Dependabot scan
SonarQube only scans code you wrote — it doesn't know if a third-party
library you depend on has a published CVE. Dependabot (built into GitHub)
covers that gap: it reads your dependency manifests (`package.json`,
`pom.xml`, etc.) and flags versions with known vulnerabilities.

| Term | Short for | What it is |
|---|---|---|
| CVE | Common Vulnerabilities and Exposures | A unique ID (e.g. `CVE-2024-12345`) assigned to one publicly disclosed vulnerability, so everyone refers to the same bug by the same name |
| CVSS | Common Vulnerability Scoring System | A 0–10 severity score for a CVE, based on things like how easy it is to exploit and what an attacker gains; the score buckets into the low/medium/high/critical severities you see in Dependabot alerts |
| NVD | National Vulnerability Database | The US government (NIST) database that publishes CVEs enriched with their CVSS scores — Dependabot and most scanners pull from it (or GitHub's own advisory database) under the hood |

**Enable it** — per repo: **Settings → Security → Code security**
* Turn on **Dependency graph** (usually on by default)
* Turn on **Dependabot alerts**

**Generate a fine-grained token** — Jenkins needs this to query the alerts
API. Fine-grained tokens are scoped to specific repos and permissions, unlike
classic PATs which grant broad account access.
* GitHub → Settings → Developer settings → Personal access tokens →
  Fine-grained tokens → **Generate new token**
* Resource owner / Repository access: the repo(s) this pipeline scans
* Permissions → Repository permissions → **Dependabot alerts: Read-only**
* Set an expiry, generate, and store it in Jenkins as a secret-text
  credential (e.g. `github-token`)

**Fail the build on high/critical** — same shift-left idea as the SonarQube
quality gate: add a pipeline stage that queries
`GET /repos/<owner>/<repo>/dependabot/alerts?state=open` with the token above,
and `exit 1` if any returned alert's severity is `high` or `critical`.

## Quick reference
| what | where |
|---|---|
| Jenkins UI | `http://jenkins.<your-domain>:8080` |
| SonarQube UI | `http://sonar.<your-domain>:9000` |
| Jenkins agent | `jenkins-agent.<your-domain>` (private, SSH only) |
| Jenkins admin password | `sudo cat /var/lib/jenkins/secrets/initialAdminPassword` |
| SonarQube default login | `/opt/default-sonar-login.txt` on the sonar box |
