# Contributing

Thanks for your interest in contributing to ViewCompose.

## Before You Start

1. Open an issue for large changes before implementation.
2. Keep changes small and focused.
3. Respect existing module boundaries and architecture constraints.

## Local Checks

Run these before opening a PR:

```bash
./gradlew qaQuick
./gradlew qaPreview
```

If your change affects UI behavior on device/emulator, also run:

```bash
./gradlew qaFull
```

## Coding Expectations

1. Follow repository docs: `ARCHITECTURE.md` and `WORKFLOW.md`.
2. Add/update tests for behavioral changes.
3. Keep docs in sync when behavior or architecture changes.

## Pull Request Guidelines

1. Explain what changed and why.
2. List validation commands and results.
3. Include screenshots/gifs for visual UI changes when helpful.

## License

By contributing to this repository, you agree that your contributions are
licensed under the MIT License in this repository.

