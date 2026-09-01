# GitHub Workflows for Floktask

## 🚀 Available Workflows

### 1. **Debug APK Build** (`debug-build.yml`)
- **Trigger**: Push to `main` branch or manual dispatch
- **What it does**: Builds debug APK on every commit
- **Output**: APK artifact available for download
- **Retention**: 7 days

### 2. **Release APK Build** (`release-build.yml`)
- **Trigger**: Push tag `v*` or manual dispatch with version input
- **What it does**: Builds signed release APK
- **Output**: GitHub Release with APK
- **Requires**: Android signing secrets in GitHub Secrets

### 3. **CI Checks** (`ci-checks.yml`)
- **Trigger**: Push to `main`/`develop` or PR to `main`
- **What it does**: Runs lint, tests, build verification
- **Purpose**: Code quality gate

### 4. **Auto APK Upload** (`auto-apk-upload.yml`)
- **Trigger**: After successful Debug APK Build
- **What it does**: Uploads APK to Telegram (if configured)
- **Requires**: `TELEGRAM_BOT_TOKEN` and `TELEGRAM_CHAT_ID` secrets

## 📦 How to Get APK

### Option 1: Automatic (Recommended)
1. Push to `main` branch
2. Wait for GitHub Actions to complete
3. Download APK from:
   - **Artifacts**: Actions → Workflow Run → Artifacts
   - **Release**: Releases page (for tagged commits)

### Option 2: Manual Build
```bash
# Clone repo
git clone https://github.com/dondgoklo-cyber/Floktask.git
cd Floktask

# Build debug APK
./gradlew assembleDebug

# APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

### Option 3: Using Build Script
```bash
# Make executable
chmod +x scripts/build-apk.sh

# Build debug
./scripts/build-apk.sh debug

# Build release
./scripts/build-apk.sh release
```

## 🔧 Required GitHub Secrets

For **Release Build** and **Telegram Upload** to work, add these secrets:

### Android Signing (for Release APK)
- `ANDROID_SIGNING_KEY` - Base64 encoded keystore file
- `ANDROID_SIGNING_ALIAS` - Keystore alias
- `ANDROID_SIGNING_PASSWORD` - Keystore password
- `ANDROID_SIGNING_STORE_PASSWORD` - Store password

### Telegram Bot (for Auto Upload)
- `TELEGRAM_BOT_TOKEN` - Your bot token from @BotFather
- `TELEGRAM_CHAT_ID` - Chat ID to send APK to

## 📊 Workflow Triggers Summary

| Workflow | Trigger | Output | Retention |
|----------|---------|--------|-----------|
| debug-build | Push to main | APK Artifact | 7 days |
| release-build | Tag `v*` | GitHub Release | Forever |
| ci-checks | Push/PR | Test Results | 30 days |
| auto-apk-upload | After debug-build | Telegram Message | - |

## 🎯 Best Practices

1. **For Development**: Use `debug-build.yml` - gets APK on every commit
2. **For Testing**: Download from Actions artifacts
3. **For Release**: Create tag `v1.2.0` to trigger release build
4. **For CI**: All PRs to `main` run ci-checks automatically

## 💡 Tips

- **Fast feedback**: Debug builds run on every push to main
- **Save storage**: Old artifacts auto-delete after 7 days
- **Telegram notifications**: Configure bot to get APK links instantly
- **Version management**: Use semantic versioning for tags (v1.0.0, v1.1.0, etc.)

## 🔗 Useful Links

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Android Gradle Build](https://developer.android.com/studio/build)
- [Telegram Bot API](https://core.telegram.org/bots/api)
