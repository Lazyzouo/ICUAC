# ICUAC Project Instructions

## Versioning

- Every completed code, configuration, behavior, or user-facing update must also update the plugin version in `build.gradle` before building the final JAR.
- Use semantic versioning in the form `MAJOR.MINOR.PATCH`.
- Bug fixes, message/style adjustments, and small internal changes increment `PATCH` (for example, `1.0.0` -> `1.0.1`).
- New backward-compatible features increment `MINOR` and reset `PATCH` (for example, `1.0.3` -> `1.1.0`).
- Major feature sets, architectural changes, or incompatible behavior increment `MAJOR` and reset the other fields (for example, `1.4.2` -> `2.0.0`).
- The generated JAR and `plugin.yml` version must match the version declared in `build.gradle`.

## Official Presets

- Never commit `src/main/resources/config.yml`; it is reserved for personal/local parameters and is ignored by Git.
- Track public defaults only in `presets/config.zh_CN.yml` and `presets/config.en_US.yml`.
- Parameter changes must be applied to both presets while preserving language-specific comments and messages.

## Documentation And Publishing

- Public repository documentation must present English first and Chinese second.
- Every behavior, configuration, permission, compatibility, or limitation change must update `CHANGELOG.md` and the relevant administrator documentation.
- Keep the tested server baseline visible as Paper/Folia `1.21.11` until a later version is explicitly tested.
- After a completed and verified update, commit all intended files and push `main` to `origin`. The local post-commit hook performs the push when GitHub authentication is available.
- GitHub Actions creates the matching `vMAJOR.MINOR.PATCH` Release automatically from `CHANGELOG.md`; do not manually upload unverified JARs.
