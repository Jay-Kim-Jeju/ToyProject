# Environment Profile Deployment Guide

## 1. Strategy

This project uses Maven profiles to choose environment-specific `prop.properties` at build time.

- Local: `src/main/resources/env/local/prop.properties`
- Dev: `src/main/resources/env/dev/prop.properties`
- Prod: `src/main/resources/env/prod/prop.properties`

During build, selected env file is copied to:

- `target/classes/prop.properties`
- `WEB-INF/classes/prop.properties` inside WAR

## 2. Practical Standard

- Keep config separated by environment to reduce mistakes.
- Build with explicit profile (`-Plocal`, `-Pdev`, `-Pprod`).
- Do not commit real production secrets; use secure secret management.

## 3. Where To Change Values

1. Local: `src/main/resources/env/local/prop.properties`
2. Dev: `src/main/resources/env/dev/prop.properties`
3. Prod: `src/main/resources/env/prod/prop.properties`

Recommended `IS_LOCAL`:

- local: `Y`
- dev/prod: `N`

## 4. Build Commands

```bash
mvn clean package -Plocal
mvn clean package -Pdev
mvn clean package -Pprod
```

## 5. Verify WAR Contains Correct Values

```powershell
jar tf target/ToyProject.war | Select-String "WEB-INF/classes/prop.properties"
jar xf target/ToyProject.war WEB-INF/classes/prop.properties
Get-Content WEB-INF/classes/prop.properties
```

Check at least:

- `APP_ENV`
- `IS_LOCAL`
- `jdbc.url`

## 6. Deployment Checklist

1. Update correct env file values.
2. Build with matching Maven profile.
3. Verify `WEB-INF/classes/prop.properties` in WAR.
4. Deploy to Tomcat.
5. Smoke test login, DB connection, and SMS flow.

