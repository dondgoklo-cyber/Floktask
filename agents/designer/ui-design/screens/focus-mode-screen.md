# Focus Mode Screen - UI Design Documentation
# **Floktask - Personal Life OS**
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Task: DE-005

---

## 📋 Overview

This document describes the **Focus Mode Screen** UI design for Floktask, including all visual elements, components, states, and interactions for a distraction-free work environment.

---

## 🎨 Screen Layout

### Structure (Hierarchy)

```
┌─────────────────────────────────────────┐
│  FOCUS MODE                    [✕]      │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━      │
│                                             │
│         ┌─────────────────────────┐        │
│         │                             │        │
│         │      ╭───────────╮       │        │
│         │     ╭╯           ╰╮      │        │
│         │    ╭╯ ████████ ╰╮     │        │
│         │   ╭╯ █        █ ╰╮    │        │
│         │  ╭╯ █    24:59   █ ╰╮   │        │
│         │ ╭╯ █        █ ╰╮  │        │
│         │ ╰╮ ████████ ╭╯   │        │
│         │  ╰╮           ╭╯    │        │
│         │   ╰───────────╯      │        │
│         │                             │        │
│         │   ⏱️  Pomodoro #1      │        │
│         │   Working...            │        │
│         │                             │        │
│         └─────────────────────────┘        │
│                                             │
│      ┌─────────────────────────────────┐   │
│      │  [Start]    [Pause]    [Stop]     │   │
│      └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │  📋 Next: Implement API              │  │
│  │  🎯 Goal: Complete project (40%)     │  │
│  │  📊 Today: 2/4 sessions               │  │
│  └─────────────────────────────────────┘  │
│                                             │
│  [🔒 Distraction Blocked]  [📈 Stats]      │
└─────────────────────────────────────────┘
```

---

## 🏗️ Components

### 1. Header Bar

**Purpose:** Navigation and screen title

```
┌─────────────────────────────────────────┐
│  FOCUS MODE                    [✕]      │
└─────────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 56dp | `spacing.xxxl` |
| Background | Transparent | - |
| Padding | 16dp horizontal | `spacing.md` |
| Elevation | None | - |

**Elements:**
- **Title:** "Focus Mode" (Title Large, 22sp Bold)
- **Close Button:** IconButton (24dp x 24dp) with X icon
- **Color:** On Surface (Light) / On Surface (Dark)

---

### 2. Timer Area

**Purpose:** Display time remaining in focus session

```
┌─────────────────────────┐
│                             │
│      ╭───────────╮       │
│     ╭╯           ╰╮      │
│    ╭╯             ╰╮     │
│   ╭╯               ╰╮    │
│  ╭╯    24:59      ╰╮   │
│ ╭╯                 ╰╮  │
│ ╰╮               ╭╯   │
│  ╰╮             ╭╯    │
│   ╰───────────╯      │
│                             │
│   ⏱️  Pomodoro #1      │
│   Working...            │
│                             │
└─────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Diameter | 200dp | Custom |
| Digit Size | 64sp | Custom |
| Digit Weight | Bold | - |
| Digit Color | `#FFFFFF` | `focus_mode.timer.text` |
| Progress Ring Width | 4dp | Custom |
| Progress Ring Color | `#FF9800` | `focus_mode.timer.active` |
| Progress Ring Background | `rgba(255,152,0,0.2)` | Custom |
| Label Size | 14sp | `typography.body.medium` |
| Label Color | `#FFFFFF` | `focus_mode.timer.text` |
| Status Size | 14sp | `typography.body.medium` |
| Status Color | `#CACACA` | `focus_mode.task.subtitle` |

**Timer States:**
- **Active (Work):** Digits = White, Ring = Orange (moving), Icon = ⏱️, Status = "Working..."
- **Paused:** Digits = White, Ring = Orange (frozen), Icon = ⏸️, Status = "Paused"
- **Completed:** Digits = White, Ring = Orange (full), Icon = ✅, Status = "Completed!"
- **Short Break:** Digits = White, Ring = Blue (moving), Icon = ☕, Status = "Relax..."
- **Long Break:** Digits = White, Ring = Purple (moving), Icon = 🛌, Status = "Take a break"

---

### 3. Task Display

**Purpose:** Show current task information

```
┌─────────────────────────┐
│   DESIGN UI             │
│   Mobile App            │
│   Priority: High        │
│   Due: Today            │
└─────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Width | 80% of screen | Custom |
| Max Width | 400dp | Custom |
| Padding | 24dp | `spacing.lg_2` |
| Background | `rgba(255,255,255,0.1)` | `focus_mode.task.background` |
| Border Radius | 12dp | `border_radius.md` |
| Elevation | Level 2 | `shadows.level_2` |
| Title Size | 20sp | `typography.title.medium` |
| Title Color | `#FFFFFF` | `focus_mode.task.title` |
| Title Weight | Semi-Bold | - |
| Subtitle Size | 14sp | `typography.body.medium` |
| Subtitle Color | `#CACACA` | `focus_mode.task.subtitle` |

**Content:**
- **Title:** Task title
- **Subtitle:** Additional info (priority, due date, project, etc.)
- **Icon:** Task category icon (optional)

---

### 4. Control Bar

**Purpose:** Control the focus session

```
┌─────────────────────────────────┐
│  [Start]    [Pause]    [Stop]   │
└─────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 48dp | `spacing.xxl_2` |
| Padding | 16dp | `spacing.md` |
| Background | Transparent | - |
| Button Size | 48dp x 48dp | Custom |
| Button Spacing | 16dp | `spacing.md` |
| Button Border Radius | 12dp | `border_radius.md` |
| Button Elevation | Level 1 | `shadows.level_1` |
| Icon Size | 24dp | `spacing.lg_2` |

**Buttons:**

| Button | Color | Icon | Visible When |
|--------|-------|------|--------------|
| **Start** | `#4CAF50` | ▶️ | Timer is stopped/reset |
| **Pause** | `#FFC107` | ⏸️ | Timer is active |
| **Stop** | `#F44336` | ⏹️ | Always |
| **Skip** | `#9E9E9E` | ⏭️ | During break (optional) |

---

### 5. Next Tasks Preview

**Purpose:** Show upcoming tasks

```
┌─────────────────────────────────┐
│  📋 Next:                       │
│  1. Implement API              │
│  2. Test functionality           │
│  3. Write documentation         │
└─────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 64dp | Custom |
| Padding | 16dp | `spacing.md` |
| Background | `rgba(255,255,255,0.1)` | `focus_mode.statistics.background` |
| Border Radius | 8dp | `border_radius.sm_2` |
| Icon Size | 20sp | Custom |
| Typography | 14sp Medium | `typography.body.medium` |
| Max Items | 3 | Custom |
| Item Height | 32dp | Custom |

**Content:**
- **Icon:** 📋 (list icon)
- **Title:** "Next:"
- **Items:** Up to 3 next tasks
- **Action:** Tap to select as current task

---

### 6. Goal Progress

**Purpose:** Show progress towards current goal

```
┌─────────────────────────────────┐
│  🎯 Goal: Complete project       │
│  ████████░░░░░░░░  40%           │
│  2/5 tasks completed            │
└─────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 40dp | `spacing.xxl_2` |
| Padding | 16dp | `spacing.md` |
| Background | `rgba(255,255,255,0.1)` | `focus_mode.statistics.background` |
| Border Radius | 8dp | `border_radius.sm_2` |
| Icon | 20sp | Custom |
| Icon Color | `#FF9800` | `focus_mode.statistics.progress` |
| Title Size | 14sp | `typography.body.medium` |
| Title Color | `#FFFFFF` | `focus_mode.task.title` |
| Progress Bar Height | 8dp | Custom |
| Progress Bar Color | `#FF9800` | `focus_mode.statistics.progress` |
| Progress Bar Background | `rgba(255,255,255,0.2)` | Custom |
| Progress Text Size | 12sp | `typography.body.small` |
| Progress Text Color | `#CACACA` | `focus_mode.statistics.text` |

---

### 7. Statistics Preview

**Purpose:** Quick focus statistics

```
┌─────────────────────────────────┐
│  📊 Today: 2/4 sessions           │
└─────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 32dp | Custom |
| Padding | 8dp | `spacing.sm` |
| Background | `rgba(255,255,255,0.1)` | `focus_mode.statistics.background` |
| Border Radius | 8dp | `border_radius.sm_2` |
| Icon | 20sp | Custom |
| Icon Color | `#FF9800` | `focus_mode.statistics.progress` |
| Text Size | 14sp | `typography.body.medium` |
| Text Color | `#FFFFFF` | `focus_mode.task.title` |

---

### 8. Distraction Blocking Toggle

**Purpose:** Enable/disable distraction blocking

```
[🔒 Distraction Blocked: ON]
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 32dp | Custom |
| Padding | 8dp horizontal, 4dp vertical | `spacing.sm`, `spacing.xs` |
| Background | `#FF9800` | `focus_mode.distraction_blocked.indicator` |
| Border Radius | 16dp | `border_radius.md_2` |
| Icon | 20sp | Custom |
| Icon Color | `#FFFFFF` | `focus_mode.distraction_blocked.text` |
| Text Size | 12sp | `typography.body.small` |
| Text Color | `#FFFFFF` | `focus_mode.distraction_blocked.text` |

---

### 9. Statistics Button

**Purpose:** Open detailed statistics

```
[📈 Stats]
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 32dp | Custom |
| Padding | 8dp horizontal, 4dp vertical | `spacing.sm`, `spacing.xs` |
| Background | `rgba(255,255,255,0.1)` | `focus_mode.statistics.background` |
| Border Radius | 16dp | `border_radius.md_2` |
| Icon | 20sp | Custom |
| Icon Color | `#CACACA` | `focus_mode.statistics.text` |
| Text Size | 12sp | `typography.body.small` |
| Text Color | `#CACACA` | `focus_mode.statistics.text` |

---

### 10. Task Selection Dialog

**Purpose:** Select task to focus on

```
┌─────────────────────────────────────────┐
│  SELECT TASK TO FOCUS ON         [✓]    │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━      │
│                                             │
│  Search: [____________________]           │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  📌 Pinned Tasks                 │   │
│  │  ┌───────────────────────────┐ │   │
│  │  │ 🏆 Design UI             │ │   │
│  │  │    Mobile App            │ │   │
│  │  │    Due: Today            │ │   │
│  │  └───────────────────────────┘ │   │
│  │  ┌───────────────────────────┐ │   │
│  │  │ 📝 Implement API        │ │   │
│  │  │    Backend integration    │ │   │
│  │  │    Due: Tomorrow          │ │   │
│  │  └───────────────────────────┘ │   │
│  └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  📁 All Tasks                     │   │
│  │  ┌───────────────────────────┐ │   │
│  │  │ 🎨 UI Redesign           │ │   │
│  │  │    High priority          │ │   │
│  │  └───────────────────────────┘ │   │
│  │  ┌───────────────────────────┐ │   │
│  │  │ 📊 Analytics Dashboard    │ │   │
│  │  │    Medium priority        │ │   │
│  │  └───────────────────────────┘ │   │
│  └─────────────────────────────────┘   │
│                                             │
│  [Start with Selected]  [Cancel]          │
└─────────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 80% of screen | Custom |
| Max Height | 600dp | Custom |
| Width | 90% of screen | Custom |
| Max Width | 500dp | Custom |
| Background | Surface | `colors.surface` |
| Border Radius | 24dp | `border_radius.lg_2` |
| Elevation | Level 5 | `shadows.level_5` |
| Padding | 24dp | `spacing.lg_2` |

**Sections:**
- **Search:** Filter tasks by name
- **Pinned Tasks:** Quick access to favorite/pinned tasks
- **All Tasks:** Full task list with categories
- **Actions:** Start with selected or Cancel

---

### 11. Settings Dialog

**Purpose:** Configure Focus Mode

```
┌─────────────────────────────────────────┐
│  FOCUS MODE SETTINGS            [✓]      │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━      │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  ⏱️  Session Durations            │   │
│  │  Work:        [25    ] min  │   │
│  │  Short Break: [5     ] min  │   │
│  │  Long Break:  [15    ] min  │   │
│  └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  🎯 Default Task                 │   │
│  │  Last used task          [✓]    │   │
│  │  Always ask              [  ]    │   │
│  └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  🔒 Distraction Blocking         │   │
│  │  Enable                [✓]    │   │
│  │  Blocked Apps:         [+ Add]  │   │
│  │  - Social Media                  │   │
│  │  - Games                        │   │
│  │  Silence Notifications  [✓]    │   │
│  └─────────────────────────────────┘   │
│                                             │
│  [Save]                                    │
└─────────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 80% of screen | Custom |
| Max Height | 600dp | Custom |
| Width | 90% of screen | Custom |
| Max Width | 500dp | Custom |
| Background | Surface | `colors.surface` |
| Border Radius | 24dp | `border_radius.lg_2` |
| Elevation | Level 5 | `shadows.level_5` |
| Padding | 24dp | `spacing.lg_2` |

**Sections:**
- **Session Durations:** Customizable Pomodoro timings
- **Default Task:** What task to focus on by default
- **Distraction Blocking:** App blocking and notification control
- **Actions:** Save settings

---

### 12. Statistics Dialog

**Purpose:** Show detailed focus statistics

```
┌─────────────────────────────────────────┐
│  FOCUS STATISTICS               [✕]      │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━      │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  📊 Today                         │   │
│  │  Sessions: 3/4                   │   │
│  │  Time: 1h 15m                    │   │
│  │  Streak: 5 days ✅               │   │
│  └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  📈 This Week                    │   │
│  │  Mon: ██████ 45m                  │   │
│  │  Tue: ██████████ 2h 15m            │   │
│  │  Wed: ████████ 1h 30m              │   │
│  │  Thu: ████ 30m                    │   │
│  │  Fri: ████████████ 3h ✅           │   │
│  └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  🏆 Achievements                  │   │
│  │  7-day streak!          🎉       │   │
│  │  10 hours this week!    🎉       │   │
│  └─────────────────────────────────┘   │
│                                             │
│  [Close]                                  │
└─────────────────────────────────────────┘
```

| Property | Value | Token |
|----------|-------|-------|
| Height | 80% of screen | Custom |
| Max Height | 600dp | Custom |
| Width | 90% of screen | Custom |
| Max Width | 500dp | Custom |
| Background | Surface | `colors.surface` |
| Border Radius | 24dp | `border_radius.lg_2` |
| Elevation | Level 5 | `shadows.level_5` |
| Padding | 24dp | `spacing.lg_2` |

**Sections:**
- **Today:** Current day's statistics
- **This Week:** Daily breakdown with bar chart
- **Achievements:** Milestones and records
- **Actions:** Close dialog

---

## 🎭 States

### Screen States

| State | Description | Visual |
|-------|-------------|--------|
| **Active** | Focus session in progress | Timer counting down, progress ring moving |
| **Paused** | Focus session paused | Timer frozen, progress ring frozen |
| **Completed** | Session completed | Timer at 00:00, full progress ring |
| **Break** | Break session active | Different color scheme, break icon |
| **Idle** | No active session | Timer at 00:00, Start button visible |

### Timer States

| State | Digits | Progress Ring | Icon | Status | Buttons |
|-------|--------|---------------|------|--------|---------|
| **Idle** | 25:00 | Empty | ⏱️ | Ready | Start |
| **Active (Work)** | Counting | Moving | ⏱️ | Working... | Pause, Stop |
| **Paused** | Frozen | Frozen | ⏸️ | Paused | Resume, Stop |
| **Completed** | 00:00 | Full | ✅ | Completed! | Next, Stop |
| **Short Break** | Counting | Moving | ☕ | Relax... | Skip, Stop |
| **Long Break** | Counting | Moving | 🛌 | Take a break | Skip, Stop |

---

## 🖱️ Interactions

### Gestures

| Gesture | Action | Feedback |
|---------|--------|----------|
| **Tap (Timer)** | Start/Pause/Resume | Ripple + color change + haptic |
| **Tap (Task Display)** | Show task details | Ripple + modal |
| **Tap (Next Task)** | Select as current | Ripple + transition |
| **Tap (Start)** | Start timer | Ripple + haptic |
| **Tap (Pause)** | Pause timer | Ripple + haptic |
| **Tap (Resume)** | Resume timer | Ripple + haptic |
| **Tap (Stop)** | Stop timer | Ripple + confirmation + haptic |
| **Tap (Skip)** | Skip to next | Ripple + haptic |
| **Tap (Stats)** | Open statistics | Ripple + dialog |
| **Tap (Distraction Toggle)** | Toggle blocking | Ripple + state change |
| **Tap (Close)** | Exit focus mode | Confirmation dialog |
| **Triple Tap** | Emergency exit | Haptic + immediate exit |
| **Shake** | Emergency exit | Haptic + immediate exit |

### Animations

| Animation | Duration | Easing | Trigger |
|-----------|----------|--------|---------|
| Timer Countdown | 1000ms | Linear | Every second |
| Progress Ring | 1000ms | Linear | Continuous |
| Task Fade In | 300ms | Ease-out | Task change |
| Control Button Press | 150ms | Ease-out | Button tap |
| Focus Mode Enter | 250ms | Ease-in-out | Activation |
| Focus Mode Exit | 200ms | Ease-in | Deactivation |
| Session Complete | 500ms | Ease-out | Timer reaches 0 |
| Break Start | 300ms | Ease-out | Auto-transition |
| Dialog Open | 200ms | Ease-out | Dialog appearance |
| Dialog Close | 150ms | Ease-in | Dialog dismissal |

---

## 🎯 Usage Guidelines

### Do's ✅
1. **Keep it minimal** - Remove all non-essential UI elements
2. **Make timer prominent** - Large, clear, easy to read
3. **Show current task clearly** - User should always know what they're focusing on
4. **Provide quick controls** - Easy to start, pause, stop
5. **Show progress** - Visual feedback on time and task progress
6. **Use subtle colors** - Avoid bright, distracting colors
7. **Optimize for mobile** - Large touch targets, simplified layout
8. **Provide emergency exit** - Quick way to exit if needed

### Don'ts ❌
1. **Don't overload with features** - Keep it simple and focused
2. **Don't use bright colors** - Can be distracting
3. **Don't hide essential controls** - Always show Start/Pause/Stop
4. **Don't make it complex** - Should be intuitive and easy to use
5. **Don't ignore accessibility** - Ensure good contrast and touch targets

---

## 📱 Responsive Design

### Mobile (0dp - 599dp)
```
┌─────────────────────┐
│  FOCUS              │
│  [✕]                │
├─────────────────────┤
│                     │
│     ┌───────────┐   │
│     │  24:59   │   │
│     │ ⏱️ Pom   │   │
│     └───────────┘   │
│                     │
│  DESIGN UI          │
│  Mobile App         │
│                     │
├─────────────────────┤
│  [Start] [Pause]     │
├─────────────────────┤
│  Next: Implement    │
│  Goal: 40%          │
├─────────────────────┤
│  [🔒] [📈]          │
└─────────────────────┘
```

**Characteristics:**
- **Compact Layout:** Optimized for small screens
- **Large Timer:** Still prominent (150dp diameter)
- **Simplified Controls:** Essential buttons only
- **Bottom Sections:** Next task, goal, toggles
- **Touch-Friendly:** Large touch targets (48dp minimum)
- **Task List:** Hidden by default, accessible via button

### Tablet (600dp - 1023dp)
```
┌─────────────────────────────────────────┐
│  FOCUS MODE                    [✕]      │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━      │
│                                             │
│         ┌─────────────────────────┐        │
│         │    24:59             │        │
│         │   ⏱️  Pomodoro #1      │        │
│         │                             │        │
│         │   DESIGN UI             │        │
│         │   Mobile App            │        │
│         └─────────────────────────┘        │
│                                             │
│      ┌─────────────────────────────────┐   │
│      │  [Start]    [Pause]    [Stop]     │   │
│      └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │  📋 Next: Implement API              │  │
│  │  🎯 Goal: Complete project (40%)     │  │
│  │  📊 Today: 2/4 sessions               │  │
│  └─────────────────────────────────────┘  │
│                                             │
│  [🔒 Distraction Blocked]  [📈 Stats]      │
└─────────────────────────────────────────┘
```

**Characteristics:**
- **Medium Timer:** 180dp diameter
- **All Elements:** Visible and accessible
- **Touch-Friendly:** Large touch targets
- **Task List:** Accessible via button

### Desktop (1024dp+)
```
┌─────────────────────────────────────────┐
│  FOCUS MODE                    [✕]      │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━      │
│                                             │
│         ┌─────────────────────────┐        │
│         │                             │        │
│         │      ╭───────────╮       │        │
│         │     ╭╯           ╰╮      │        │
│         │    ╭╯             ╰╮     │        │
│         │   ╭╯    24:59      ╰╮   │        │
│         │ ╭╯                 ╰╮  │        │
│         │ ╰╮               ╭╯   │        │
│         │  ╰───────────╯      │        │
│         │                             │        │
│         │   ⏱️  Pomodoro #1      │        │
│         │   Working...            │        │
│         │                             │        │
│         └─────────────────────────┘        │
│                                             │
│      ┌─────────────────────────────────┐   │
│      │  [Start]    [Pause]    [Stop]     │   │
│      └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │  📋 Next: Implement API              │  │
│  │  🎯 Goal: Complete project (40%)     │  │
│  │  📊 Today: 2/4 sessions               │  │
│  └─────────────────────────────────────┘  │
│                                             │
│  [🔒 Distraction Blocked]  [📈 Stats]      │
└─────────────────────────────────────────┘
```

**Characteristics:**
- **Large Timer:** 200dp diameter
- **All Features:** Full functionality
- **Keyboard Support:** Full keyboard navigation
- **Mouse Support:** All mouse interactions

---

## 🔗 Related Files

- [Design Specification](../output/design-specs/focus-mode-spec.md)
- [Wireframes](../wireframes/low-fidelity/focus-mode-wireframe.md)
- [Competitive Analysis](../../research/competitive-ui-analysis/focus-mode-ui-analysis.md)
- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)
- [Timeline Screen](./timeline-screen.md)
- [Gantt Screen](./gantt-screen.md)

---

*Document Version: 1.0.0*
*Last Updated: 2026-09-05*
*Status: In Progress*
