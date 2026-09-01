# Floktask Focus Mode - Low Fidelity Wireframes
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Task: DE-005

## 📋 Overview

This document contains **low-fidelity wireframes** for Floktask's Focus Mode. These wireframes focus on creating a distraction-free environment for deep work, with seamless integration with Floktask's task management.

---

## 🎯 Design Goals

1. **Minimal Distraction:** Remove all non-essential UI elements
2. **Clear Focus:** Large timer, prominent task display
3. **Quick Access:** Easy to start, pause, stop
4. **Integration:** Seamless with Floktask tasks
5. **Motivation:** Progress tracking and statistics
6. **Mobile-First:** Works well on all device sizes

---

## 📐 Wireframe Structure

### ASCII Wireframes

#### 1. Main Focus Mode Screen (Default)

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
│         │   ╭╯               ╰╮    │        │
│         │  ╭╯    25:00      ╰╮   │        │
│         │ ╭╯                 ╰╮  │        │
│         │ ╰╮               ╭╯   │        │
│         │  ╰╮             ╭╯    │        │
│         │   ╰╮           ╭╯     │        │
│         │    ╰───────────╯      │        │
│         │                             │        │
│         │   ⏱️  Pomodoro #1      │        │
│         │                             │        │
│         │   DESIGN UI             │        │
│         │   Mobile App            │        │
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

**Key Elements:**
- **Header:** Title + Close button
- **Timer:** Large circular timer (64sp digits) with progress ring
- **Task Display:** Current task title and subtitle
- **Control Bar:** Start, Pause, Stop buttons
- **Next Tasks:** Preview of upcoming tasks
- **Goal Progress:** Current goal and progress
- **Statistics:** Today's focus sessions
- **Distraction Blocking:** Toggle and indicator

---

#### 2. Focus Mode - Active Timer

```
┌─────────────────────────────────────────┐
│  FOCUS MODE                    [✕]      │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━      │
│                                             │
│         ┌─────────────────────────┐        │
│         │                             │        │
│         │      ╭───────────╮       │        │
│         │     ╭╯           ╰╮      │        │
│         │    ╭╯ ████████ ╰╮     │ ← Progress fill (moving)
│         │   ╭╯ █        █ ╰╮    │        │
│         │  ╭╯ █    24:03   █ ╰╮   │        │
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
│      │  [Pause]    [Stop]               │   │
│      └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │  📋 Next: Implement API              │  │
│  │  🎯 Goal: Complete project (40%)     │  │
│  └─────────────────────────────────────┘  │
│                                             │
│  [🔒 Distraction Blocked: ON]              │
└─────────────────────────────────────────┘
```

**Key Elements:**
- **Timer:** Counting down (25:00 → 00:00)
- **Progress Ring:** Filling clockwise as time passes
- **Status:** "Working..." or "Focusing..."
- **Controls:** Pause and Stop visible (Start hidden)

---

#### 3. Focus Mode - Paused

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
│         │   ╭╯    24:03    ╰╮   │ ← Timer paused
│         │ ╭╯                 ╰╮  │        │
│         │ ╰╮               ╭╯   │        │
│         │  ╰╮             ╭╯    │        │
│         │   ╰╮           ╭╯     │        │
│         │    ╰───────────╯      │        │
│         │                             │        │
│         │   ⏸️  Pomodoro #1      │ ← Pause icon
│         │   Paused                 │        │
│         │                             │        │
│         └─────────────────────────┘        │
│                                             │
│      ┌─────────────────────────────────┐   │
│      │  [Resume]    [Stop]               │   │
│      └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │  ⏳ Paused at: 24:03                │  │
│  │  📋 Next: Implement API              │  │
│  └─────────────────────────────────────┘  │
│                                             │
│  [🔒 Distraction Blocked: ON]              │
└─────────────────────────────────────────┘
```

**Key Elements:**
- **Timer:** Frozen at pause time
- **Progress Ring:** Frozen at current progress
- **Status:** "Paused"
- **Controls:** Resume and Stop visible
- **Pause Time:** Shows when paused

---

#### 4. Focus Mode - Short Break

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
│         │   ╭╯    04:59    ╰╮   │ ← Break timer
│         │ ╭╯                 ╰╮  │        │
│         │ ╰╮               ╭╯   │        │
│         │  ╰╮             ╭╯    │        │
│         │   ╰╮           ╭╯     │        │
│         │    ╰───────────╯      │        │
│         │                             │        │
│         │   ☕  Short Break        │ ← Break icon
│         │   Relax...               │        │
│         │                             │        │
│         └─────────────────────────┘        │
│                                             │
│      ┌─────────────────────────────────┐   │
│      │  [Skip]    [Stop]                 │   │
│      └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │  🎯 Next: Implement API in 25min    │  │
│  │  📊 Today: 2/4 sessions               │  │
│  └─────────────────────────────────────┘  │
│                                             │
│  [🔒 Distraction Blocked: ON]              │
└─────────────────────────────────────────┘
```

**Key Elements:**
- **Timer:** 5:00 counting down
- **Icon:** Coffee cup (☕) for break
- **Status:** "Relax..." or "Take a break"
- **Controls:** Skip and Stop
- **Next Task:** Shows what's next after break

---

#### 5. Focus Mode - Completed Session

```
┌─────────────────────────────────────────┐
│  FOCUS MODE                    [✕]      │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━      │
│                                             │
│         ┌─────────────────────────┐        │
│         │                             │        │
│         │      ╭───────────╮       │        │
│         │     ╭╯           ╰╮      │        │
│         │    ╭╯ ██████████ ╰╮     │ ← Full progress
│         │   ╭╯ ██████████ ╰╮    │        │
│         │  ╭╯ ██████████ ╰╮   │        │
│         │ ╭╯ ██████████ ╰╮  │        │
│         │ ╰╮           ╭╯   │        │
│         │  ╰───────────╯      │        │
│         │                             │        │
│         │   ✅  Pomodoro #1      │ ← Checkmark
│         │   Completed!            │        │
│         │                             │        │
│         └─────────────────────────┘        │
│                                             │
│      ┌─────────────────────────────────┐   │
│      │  [Next]    [Stop]                 │   │
│      └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │  🎉 +25min to Design UI           │  │
│  │  📊 Today: 3/4 sessions (+1)       │  │
│  │  🔥 Streak: 5 days ✅              │  │
│  └─────────────────────────────────────┘  │
│                                             │
│  [🔒 Distraction Blocked: ON]              │
└─────────────────────────────────────────┘
```

**Key Elements:**
- **Timer:** 00:00
- **Progress Ring:** Fully filled
- **Status:** "Completed!" with checkmark
- **Controls:** Next (starts next session) and Stop
- **Celebration:** Shows time added to task
- **Statistics:** Updated session count and streak

---

#### 6. Focus Mode - Task Selection

```
┌─────────────────────────────────────────┐
│  SELECT TASK TO FOCUS ON         [✕]      │
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
│  │  ┌───────────────────────────┐ │   │
│  │  │ 🔧 Fix bugs               │ │   │
│  │  │    Critical               │ │   │
│  │  └───────────────────────────┘ │   │
│  └─────────────────────────────────┘   │
│                                             │
│  [Start with Selected]  [Cancel]          │
└─────────────────────────────────────────┘
```

**Key Elements:**
- **Search:** Filter tasks by name
- **Pinned Tasks:** Quick access to favorite tasks
- **All Tasks:** Full task list
- **Task Info:** Title, description, due date, priority
- **Actions:** Start with selected or Cancel

---

#### 7. Focus Mode - Settings

```
┌─────────────────────────────────────────┐
│  FOCUS MODE SETTINGS            [✓]      │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━      │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  ⏱️  Session Durations            │   │
│  │  ┌───────────────────────────┐ │   │
│  │  │ Work:        [25    ] min  │ │   │
│  │  │ Short Break: [5     ] min  │ │   │
│  │  │ Long Break:  [15    ] min  │ │   │
│  │  └───────────────────────────┘ │   │
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
│  ┌─────────────────────────────────┐   │
│  │  🎨 Appearance                    │   │
│  │  Theme:           Dark [✓]    │   │
│  │  Background:      Default [✓]  │   │
│  │  Progress Ring:   On [✓]      │   │
│  └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  🚨 Emergency Exit                │   │
│  │  Triple Tap             [✓]    │   │
│  │  Shake Device           [  ]    │   │
│  │  Volume Down x3         [  ]    │   │
│  └─────────────────────────────────┘   │
│                                             │
│  [Save]                                    │
└─────────────────────────────────────────┘
```

**Key Elements:**
- **Session Durations:** Customizable Pomodoro timings
- **Default Task:** What task to focus on by default
- **Distraction Blocking:** App blocking and notification control
- **Appearance:** Theme, background, visual elements
- **Emergency Exit:** Quick exit methods

---

#### 8. Focus Mode - Statistics

```
┌─────────────────────────────────────────┐
│  FOCUS STATISTICS               [✕]      │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━      │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  📊 Today                         │   │
│  │  ┌─────────┬─────────┬────────┐ │   │
│  │  │ Sessions│ Time    │ Streak │ │   │
│  │  │ 3/4     │ 1h 15m  │   5    │ │   │
│  │  └─────────┴─────────┴────────┘ │   │
│  └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  📈 This Week                    │   │
│  │  Mon: ██████ 45m                  │   │
│  │  Tue: ██████████ 2h 15m            │   │
│  │  Wed: ████████ 1h 30m              │   │
│  │  Thu: ████ 30m                    │   │
│  │  Fri: ████████████ 3h ✅           │   │
│  │  Sat: ─────────                    │   │
│  │  Sun: ─────────                    │   │
│  └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  🏆 Achievements                  │   │
│  │  7-day streak!          🎉       │   │
│  │  10 hours this week!    🎉       │   │
│  │  Longest session: 2h 30m 🎉      │   │
│  └─────────────────────────────────┘   │
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  📅 Time Analysis                 │   │
│  │  Most productive: 10:00-12:00    │   │
│  │  Best day: Friday                │   │
│  │  Average session: 25min          │   │
│  └─────────────────────────────────┘   │
│                                             │
│  [Close]                                  │
└─────────────────────────────────────────┘
```

**Key Elements:**
- **Today:** Current day's statistics
- **This Week:** Daily breakdown with bar chart
- **Achievements:** Milestones and records
- **Time Analysis:** Insights and patterns

---

#### 9. Focus Mode - Mobile Layout

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

**Key Elements:**
- **Compact Layout:** Optimized for small screens
- **Large Timer:** Still prominent
- **Simplified Controls:** Essential buttons only
- **Bottom Sections:** Next task, goal, toggles
- **Touch-Friendly:** Large touch targets

---

#### 10. Focus Mode - Exit Confirmation

```
┌─────────────────────────────────────────┐
│                                             │
│  ┌─────────────────────────────────┐   │
│  │  ⚠️  Exit Focus Mode?            │   │
│  │                                     │   │
│  │  Your current session will be     │   │
│  │  cancelled.                        │   │
│  │                                     │   │
│  │  Session: 24:03 remaining         │   │
│  │  Task: Design UI                  │   │
│  │                                     │   │
│  │  [Cancel]      [Exit Anyway]     │   │
│  └─────────────────────────────────┘   │
│                                             │
└─────────────────────────────────────────┘
```

**Key Elements:**
- **Warning:** Clear message about consequences
- **Session Info:** Shows current progress
- **Actions:** Cancel (stay in focus) or Exit Anyway

---

## 📊 Information Hierarchy

### Visual Priority

1. **Highest Priority (Most Visible)**
   - Timer (large digits)
   - Progress ring
   - Current task title

2. **High Priority**
   - Control buttons (Start/Pause/Stop)
   - Task subtitle
   - Status (Working/Paused/Completed)

3. **Medium Priority**
   - Next tasks preview
   - Goal progress
   - Statistics

4. **Low Priority**
   - Distraction blocking indicator
   - Settings button
   - Close button

---

## 🎯 User Flow

### Primary Flow: Start Focus Session
```
User opens Focus Mode
    ↓
User selects task (or uses default)
    ↓
User clicks Start
    ↓
Timer starts counting down
    ↓
User focuses on task
```

### Secondary Flow: Complete Pomodoro Session
```
Timer reaches 00:00
    ↓
Session marked as completed
    ↓
Time added to task progress
    ↓
Short break starts automatically (if enabled)
    ↓
User takes break
    ↓
Break ends, next work session starts
```

### Tertiary Flow: Manual Exit
```
User clicks Pause
    ↓
Timer pauses
    ↓
User can Resume or Stop
    ↓
If Stop clicked, show confirmation
    ↓
User confirms or cancels
```

---

## 📐 Layout Specifications

### Timer

| Property | Value | Token |
|----------|-------|-------|
| Diameter | 200dp | Custom |
| Digit Size | 64sp | Custom |
| Digit Weight | Bold | - |
| Digit Color | Timer Text | `focus_mode.timer.text` |
| Progress Ring Width | 4dp | Custom |
| Progress Ring Color | Timer Active | `focus_mode.timer.active` |
| Progress Ring Background | Timer Active (20%) | `focus_mode.timer.active` @ 0.2 |
| Label Size | 14sp | `typography.body.medium` |
| Label Color | Timer Text | `focus_mode.timer.text` |

### Task Display

| Property | Value | Token |
|----------|-------|-------|
| Width | 80% of screen | Custom |
| Max Width | 400dp | Custom |
| Padding | 24dp | `spacing.lg_2` |
| Background | Task Background | `focus_mode.task.background` |
| Border Radius | 12dp | `border_radius.md` |
| Title Size | 20sp | `typography.title.medium` |
| Title Color | Task Title | `focus_mode.task.title` |
| Subtitle Size | 14sp | `typography.body.medium` |
| Subtitle Color | Task Subtitle | `focus_mode.task.subtitle` |

### Control Bar

| Property | Value | Token |
|----------|-------|-------|
| Height | 48dp | `spacing.xxl_2` |
| Padding | 16dp | `spacing.md` |
| Button Size | 48dp x 48dp | Custom |
| Button Spacing | 16dp | `spacing.md` |
| Icon Size | 24dp | `spacing.lg_2` |

**Buttons:**
- **Start:** Green (`#4CAF50`), Play icon
- **Pause:** Amber (`#FFC107`), Pause icon
- **Stop:** Red (`#F44336`), Stop icon
- **Skip:** Gray (`#9E9E9E`), Skip icon (optional)

### Next Tasks Preview

| Property | Value | Token |
|----------|-------|-------|
| Height | 64dp | Custom |
| Padding | 16dp | `spacing.md` |
| Background | Statistics Background | `focus_mode.statistics.background` |
| Item Height | 32dp | Custom |
| Typography | 14sp Medium | `typography.body.medium` |
| Max Items | 3 | Custom |

### Goal Progress

| Property | Value | Token |
|----------|-------|-------|
| Height | 40dp | `spacing.xxl_2` |
| Padding | 16dp | `spacing.md` |
| Icon | 20sp | Custom |
| Text Size | 14sp | `typography.body.medium` |
| Progress Bar Height | 8dp | Custom |
| Progress Bar Color | Statistics Progress | `focus_mode.statistics.progress` |

### Distraction Blocking Indicator

| Property | Value | Token |
|----------|-------|-------|
| Height | 32dp | Custom |
| Padding | 8dp | `spacing.sm` |
| Background | Distraction Blocked Indicator | `focus_mode.distraction_blocked.indicator` |
| Icon | 20sp | Custom |
| Text Size | 12sp | `typography.body.small` |
| Text Color | Distraction Blocked Text | `focus_mode.distraction_blocked.text` |

---

## 🎨 Visual Style Notes

### Timer States

| State | Digits Color | Progress Ring | Icon | Button |
|-------|--------------|---------------|------|--------|
| **Active (Work)** | White | Orange (moving) | ⏱️ | Pause visible |
| **Paused** | White | Orange (frozen) | ⏸️ | Resume visible |
| **Completed** | White | Orange (full) | ✅ | Next visible |
| **Break** | White | Blue (moving) | ☕ | Skip visible |

### Background Options

| Option | Color | Description |
|--------|-------|-------------|
| **Dark** | `#1A1A1A` | Default, reduces eye strain |
| **Premium Gradient** | Orange gradient | Matches brand |
| **Task Category** | Category color | Matches current task |
| **Custom Image** | User choice | Blurred background |
| **System** | System default | Follows system theme |

---

## ✅ Wireframe Validation

### Usability Checklist

- [x] Clear visual hierarchy
- [x] Timer is most prominent element
- [x] Intuitive layout
- [x] Logical information flow
- [x] Appropriate spacing
- [x] Consistent patterns
- [x] Accessible touch targets (48dp minimum)
- [x] Responsive to different screens
- [x] Mobile-friendly layout
- [x] Easy to start/pause/stop

### Design System Compliance

- [x] Uses existing color tokens
- [x] Uses existing typography tokens
- [x] Uses existing spacing tokens
- [x] Follows component patterns
- [x] Maintains brand identity

---

## 📁 Next Steps

1. **High-Fidelity Design** → Create detailed visual designs
2. **Prototype** → Build interactive prototype
3. **Design Spec** → Write comprehensive specification
4. **User Testing** → Validate with users
5. **Iteration** → Refine based on feedback

---

## 🎯 Related Documents

- [Focus Mode UI Analysis](../../research/competitive-ui-analysis/focus-mode-ui-analysis.md)
- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)
- [Pomodoro UI Analysis](pomodoro-ui-analysis.md) (future)
- [Timeline Design Spec](../output/design-specs/timeline-spec.md)
- [Gantt Design Spec](../output/design-specs/gantt-spec.md)

---

*Last updated: 2026-09-05*
*Status: Low-Fidelity Complete ✅*
*Next: High-Fidelity Design*
