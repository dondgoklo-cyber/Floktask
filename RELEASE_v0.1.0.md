# Release v0.1.0 - Floktask

First public release of Floktask - Task Manager Android app.

## What is Included

23 PRs merged with following features:
- Clean Architecture + MVVM foundation
- Auto-Schedule (greedy slot packing)
- Audit Log / Activity Trail
- Gesture Shortcuts (swipe snooze/delete)
- Focus Mode with DND
- Batch Operations
- Energy Tracking
- Keyboard Shortcuts
- Recurring Tasks
- Data Validation
- Pagination (Paging3)
- Migration Safety
- Subtask Progress
- Global Search
- Task Dependencies
- Battery Optimization
- Time Tracking
- Custom Fields
- File Attachments
- Lazy Loading
- Startup Optimization
- Image Optimization
- Tablet Support
- Picture-in-Picture
- Unit/UI/E2E Tests

## How to Build APK

### Option 1: GitHub Actions (Recommended)
1. Go to: https://github.com/dondgoklo-cyber/Floktask/actions/workflows/build-apk-gradle-action.yml
2. Click Run workflow - select branch main
3. Wait for completion (green checkmark)
4. Download APK from Artifacts section

### Option 2: Local Build
bash
  git clone https://github.com/dondgoklo-cyber/Floktask.git
  cd Floktask
  chmod +x gradlew
  ./gradlew assembleDebug
  APK will be at: app/build/outputs/apk/debug/app-debug.apk

## How to Create Release

1. Go to GitHub repository
2. Click Releases - Draft a new release
3. Tag version: v0.1.0
4. Release title: v0.1.0 - First Public Release
5. Description: Copy this text
6. Attach app-debug.apk file
7. Click Publish release

## Statistics
- Total PRs: 48
- Merged: 23
- In progress: 25
- Tests: 15+ unit/UI/E2E