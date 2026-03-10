## Git Hooks


1. **en local** : empêcher le `commit` direct sur `main` et `dev`
2. **sur GitHub** : empêcher le `push` direct sur `main` et `dev`, et forcer le passage par pull request


### Rules

* **interdit de commit/push directement sur `main`**
* **interdit de push directement sur `dev`**
* tu travailles toujours sur une branche du style `dev-fixture`, `feature/...`, `fix/...`
* tu fais une **PR vers `dev`**
* puis une fois validé/testé, tu fais une **PR de `dev` vers `main`**


## 1. Bloquer les commits locaux sur `main` et `dev`

```bash
mkdir -p .githooks
```

Crée `.githooks/pre-commit` :

```bash
#!/bin/sh

branch="$(git rev-parse --abbrev-ref HEAD)"

if [ "$branch" = "main" ] || [ "$branch" = "dev" ]; then
  echo "❌ Commit interdit directement sur $branch"
  echo "Travaille sur une branche feature/fixture puis ouvre une PR."
  exit 1
fi
```

activer le hooks :
```bash
git config core.hooksPath .githooks
chmod +x .githooks/pre-commit
```

À partir de là, `main` ou `dev`, le commit sera bloqué localement.

[1]: https://git-scm.com/book/en/v2/Customizing-Git-Git-Hooks?utm_source=chatgpt.com "Git Hooks"
[2]: https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/managing-a-branch-protection-rule?utm_source=chatgpt.com "Managing a branch protection rule"
[3]: https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-rulesets/about-rulesets?utm_source=chatgpt.com "About rulesets"
