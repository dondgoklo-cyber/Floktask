# CHANGELOG

All notable changes to WOLFTASK (Floktask) project.

## [Unreleased]

### Refactoring
- Add Logger interface to replace android.util.Log in domain layer
- All UseCase now use Logger for logging
- Add TestLogger for unit testing
- Update mockk to version 1.13.9
- Add mockk-agent-jvm dependency

### Testing
- Fix and update all unit tests
- Add comprehensive tests for Logger
- Add TestModule for DI in tests
- Enable unit tests in CI pipeline

### Features
- Add Paging3 support for large lists
- Repositories now return Flow<PagingData<T>> for paginated queries

---

## [v1.2.1] - 2026-09-02

### Fixed
- Various bug fixes and improvements

## [v1.2.0] - 2026-09-02

### Added
- Additional features and improvements

## [v1.1.0] - 2026-09-02

### Added
- Core functionality improvements

## [v1.0.0-debug] - 2026-09-02

### Added
- Initial debug release
