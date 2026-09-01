# Competitive UI Analysis: Gantt Charts
# Version: 1.0.0
# Author: UI/UX Designer Agent
# Date: 2026-09-05
# Related Task: DE-004

## 📋 Executive Summary

This document analyzes **Gantt Chart implementations** across popular project management tools to inform the design of Floktask's Gantt Charts feature. Gantt Charts are essential for visualizing project timelines, dependencies, and progress.

**Key Findings:**
- ✅ **Microsoft Project**: Most comprehensive, but complex
- ✅ **ClickUp**: Clean, modern, good balance of features
- ✅ **Jira**: Integration-focused, good for Agile
- ✅ **Monday.com**: Visual, colorful, user-friendly
- ✅ **Asana**: Simple, intuitive, good for teams
- ⚠️ Most tools are **overkill** for personal use
- ⚠️ Limited **mobile optimization**

**Our Improvement Opportunities:**
1. **Simplified for personal use** - Not enterprise-level complexity
2. **Mobile-first design** - Most tools have poor mobile Gantt
3. **Integration with Floktask features** - Tasks, Pomodoro, Time Blocking
4. **Smart suggestions** - AI-powered dependency recommendations
5. **Visual clarity** - Better color coding and hierarchy

---

## 🔍 Analysis Methodology

### Research Scope
- **Tools Analyzed:** Microsoft Project, ClickUp, Jira, Monday.com, Asana, Trello, Notion
- **Platform:** Web (primary), Desktop, Mobile
- **Analysis Period:** September 2026
- **Focus:** UI/UX patterns, not technical implementation

### Analysis Framework
1. **Visual Design** - Layout, colors, typography
2. **Interaction Design** - Drag-and-drop, editing, navigation
3. **Information Architecture** - Data hierarchy, grouping
4. **User Experience** - Usability, learnability, efficiency
5. **Mobile Adaptation** - Responsive design, touch interactions

---

## 🎨 Visual Design Analysis

### 1. Microsoft Project
**Strengths:** Industry standard, comprehensive features
**Weaknesses:** Complex, steep learning curve, outdated UI

| Element | Design Choice | Our Recommendation |
|---------|---------------|-------------------|
| **Layout** | Horizontal timeline, vertical task list | ✅ Adopt |
| **Task Bars** | Blue bars with progress fill | ✅ Similar, but with our colors |
| **Dependencies** | Arrow lines between tasks | ✅ Adopt |
| **Milestones** | Diamond shapes | ✅ Adopt |
| **Critical Path** | Red highlighting | ✅ Adopt (use our error color) |
| **Time Scale** | Top header with dates | ✅ Adopt |
| **Grid Lines** | Vertical lines for time units | ✅ Adopt |

**Color System:**
- Task bars: Blue (default)
- Progress: Green fill
- Critical path: Red
- Dependencies: Gray arrows
- Milestones: Diamond icons

---

### 2. ClickUp
**Strengths:** Modern, clean, customizable
**Weaknesses:** Can be overwhelming with options

| Element | Design Choice | Our Recommendation |
|---------|---------------|-------------------|
| **Layout** | Horizontal, clean spacing | ✅ Adopt |
| **Task Bars** | Rounded corners, category colors | ✅ Adopt |
| **Dependencies** | Dashed lines | ✅ Adopt |
| **Grouping** | Swimlanes for categories | ⚠️ Consider for Floktask |
| **Progress** | Inline progress bar | ✅ Adopt |
| **Time Scale** | Customizable (hours/days/weeks) | ✅ Adopt |

**Color System:**
- Uses **category colors** for task bars
- Progress: Green gradient
- Dependencies: Gray dashed lines
- Milestones: Special icons

---

### 3. Jira
**Strengths:** Agile-focused, good for teams
**Weaknesses:** Complex for personal use

| Element | Design Choice | Our Recommendation |
|---------|---------------|-------------------|
| **Layout** | Horizontal, sprint-based | ⚠️ Simplify for personal |
| **Task Bars** | Color by status (To Do, In Progress, Done) | ✅ Adopt |
| **Dependencies** | Solid lines with arrows | ✅ Adopt |
| **Epic Bars** | Grouped task bars | ⚠️ Consider for Projects |
| **Time Scale** | Sprint-based | ❌ Skip (not relevant) |

**Color System:**
- To Do: Gray
- In Progress: Blue
- Done: Green
- Blocked: Red

---

### 4. Monday.com
**Strengths:** Visual, colorful, intuitive
**Weaknesses:** Can be too colorful/cluttered

| Element | Design Choice | Our Recommendation |
|---------|---------------|-------------------|
| **Layout** | Horizontal, timeline view | ✅ Adopt |
| **Task Bars** | Colorful, rounded | ✅ Adopt (with our palette) |
| **Dependencies** | Curved lines | ⚠️ Use straight for clarity |
| **Grouping** | By person/status | ⚠️ Consider for Floktask |
| **Time Scale** | Customizable | ✅ Adopt |
| **Animations** | Smooth hover effects | ✅ Adopt |

**Color System:**
- Each column can have different color
- Progress: Color fill
- Dependencies: Matching color lines

---

### 5. Asana
**Strengths:** Simple, team-focused
**Weaknesses:** Limited customization

| Element | Design Choice | Our Recommendation |
|---------|---------------|-------------------|
| **Layout** | Horizontal, clean | ✅ Adopt |
| **Task Bars** | Simple bars with colors | ✅ Adopt |
| **Dependencies** | Arrow lines | ✅ Adopt |
| **Milestones** | Flag icons | ⚠️ Use diamond like MS Project |
| **Progress** | Percentage in bar | ✅ Adopt |

---

## 🏗️ Information Architecture Comparison

### Layout Patterns

#### Microsoft Project Style
```
┌─────────────────────────────────────────────────────────────────┐
│  [File] [Edit] [View] [Insert] [Format] [Tools] [Project] [Help]   │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────┬─────────────────────────────────────┐│
│  │  Task List           │  Gantt Chart                           ││
│  │                     │  ┌───────────────────────────────┐  ││
│  │ 1. Task A           │  │ ████████████████████               │  ││
│  │ 2. Task B           │  │     ████████████                │  ││
│  │ 3. Task C           │  │         ████████████████       │  ││
│  │                     │  │               ██████████       │  ││
│  │                     │  └───────────────────────────────┘  ││
│  └─────────────────────┴─────────────────────────────────────┘│
├─────────────────────────────────────────────────────────────────┤
│  Status Bar: Progress, Dependencies, etc.                         │
└─────────────────────────────────────────────────────────────────┘
```

#### ClickUp Style
```
┌─────────────────────────────────────────────────────────────────┐
│  [Gantt] [List] [Board] [Calendar]    [Share] [Filter] [Group By]   │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  Time Scale: Jan 1 - Jan 31                                  ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │  ┌─────────────────────┬─────────────────────────────────┐││
│  │  │  Task Name           │  ┌─────────────────────────┐   │││
│  │  │  ━━━━━━━━━━━━━━━━━━  │  │ Task A          │   │││
│  │  │  Assignee  Due Date   │  │ ████████████    │   │││
│  │  │  ━━━━━━━━━━━━━━━━━━  │  └─────────────────────────┘   │││
│  │  │  Task B              │  ┌─────────────────────────┐   │││
│  │  │                     │  │ Task B          │   │││
│  │  │                     │  │ ████████████    │   │││
│  │  │                     │  └─────────────────────────┘   │││
│  │  └─────────────────────┴─────────────────────────────────┘││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

#### Our Proposed Layout (Simplified for Personal Use)
```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Today] [Week] [Month] [Year] │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE (Horizontal)                                      ││
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐       ││
│  │  │  Mon   │  Tue   │  Wed   │  Thu   │  Fri   │       ││
│  │  │ 1     │ 2     │ 3     │ 4     │ 5     │       ││
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘       ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌─────────────┐  ┌─────────────┐              │ ││
│  │  │  LIST   │  │ Task A      │  │ Task B      │              │ ││
│  │  │         │  │ Jan 1-3     │──▶│ Jan 3-5     │              │ ││
│  │  │  (20%)  │  └─────────────┘  └─────────────┘              │ ││
│  │  │         │                                                     │ ││
│  │  │  ┌─────┐│  ┌─────────────────────────────────────────┐    │ ││
│  │  │  │ M1 ││  │ Milestone: Project Deadline              │    │ ││
│  │  │  └─────┘│  │ ▼                                             │    │ ││
│  │  │         │  └─────────────────────────────────────────┘    │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                     │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🎨 Color System Analysis

### Our Color Palette for Gantt Charts

```yaml
# Gantt-specific colors
gantt:
  # Task bar colors
  bar:
    default: "#4CAF50"          # Green (from our palette)
    hover: "#43A047"
    selected: "#388E3C"
    completed: "#8BC34A"
    
  # Progress colors
  progress:
    fill: "#8BC34A"             # Light green
    background: "#E8F5E8"        # Very light green
    
  # Dependency colors
  dependency:
    line: "#9E9E9E"             # Gray
    arrow: "#F44336"            # Red (for critical)
    
  # Milestone colors
  milestone:
    background: "#9C27B0"       # Purple
    border: "#FFFFFF"            # White
    text: "#FFFFFF"              # White
    
  # Critical path
  critical_path:
    background: "#FFEBEE"       # Light red
    border: "#F44336"           # Red
    
  # Time scale
  time_scale:
    background: "#FAFAFA"       # Very light gray
    text: "#424242"             # Dark gray
    divider: "#E0E0E0"          # Light gray
    
  # Grid
  grid:
    line: "#E0E0E0"            # Light gray
    major_line: "#BDBDBD"      # Medium gray
    
  # Today indicator
  today:
    background: "#FFF3E0"       # Premium subtle
    border: "#FF9800"           # Primary
```

---

## 📐 Typography Analysis

### Recommended Typography for Gantt Charts

| Element | Size | Weight | Color | Token |
|---------|------|--------|-------|-------|
| **Screen Title** | 22sp | Bold | On Surface | `typography.title.large` |
| **Task Name** | 14sp | Semi-Bold | On Surface | `typography.body.medium` |
| **Task Duration** | 12sp | Regular | On Surface Variant | `typography.body.small` |
| **Time Scale (Day)** | 12sp | Medium | On Surface Variant | `typography.label.medium` |
| **Time Scale (Month)** | 10sp | Medium | On Surface Variant | Custom |
| **Milestone Label** | 12sp | Bold | On Milestone | Custom |
| **Progress Text** | 10sp | Medium | On Progress | Custom |

---

## 🖱️ Interaction Design Analysis

### Gestures and Interactions

| Interaction | Microsoft Project | ClickUp | Jira | Our Implementation |
|-------------|-------------------|---------|------|-------------------|
| **Drag Task Bar** | ✅ Yes | ✅ Yes | ✅ Yes | ✅ **Implement** |
| **Resize Task Bar** | ✅ Yes | ✅ Yes | ✅ Yes | ✅ **Implement** |
| **Create Dependency** | ✅ Drag from edge | ✅ Drag from edge | ❌ No | ✅ **Implement** |
| **Click Task** | ✅ Details | ✅ Details | ✅ Details | ✅ **Implement** |
| **Double Click** | ✅ Edit | ✅ Edit | ❌ No | ⚠️ **Consider** |
| **Right Click** | ✅ Context menu | ✅ Context menu | ✅ Context menu | ✅ **Implement** |
| **Zoom** | ✅ Mouse wheel | ✅ Mouse wheel | ✅ Mouse wheel | ✅ **Implement** |
| **Pan** | ✅ Drag | ✅ Drag | ✅ Drag | ✅ **Implement** |
| **Pinch (Mobile)** | ❌ No | ⚠️ Limited | ❌ No | ✅ **Implement** |

### Recommended Interactions for Floktask

1. **Drag Task Bar**
   - Action: Move task in timeline
   - Feedback: Task follows finger, snap-to-grid
   - Haptic: Light on start, Selection on drop

2. **Resize Task Bar**
   - Action: Change task duration
   - Feedback: Drag handle appears on hover
   - Haptic: Light on resize

3. **Create Dependency**
   - Action: Drag from task edge to another task
   - Feedback: Dependency line preview
   - Haptic: Light on create

4. **Click Task**
   - Action: Show task details
   - Feedback: Ripple effect

5. **Long Press (Mobile)**
   - Action: Open context menu
   - Feedback: Haptic (Selection) + Menu

6. **Pinch Zoom**
   - Action: Zoom in/out timeline
   - Feedback: Smooth zoom animation

7. **Horizontal Pan**
   - Action: Scroll timeline horizontally
   - Feedback: Smooth scroll

---

## 🎯 Key Features to Implement

### 1. Task Bars
**Essential for:** Visualizing task duration and timing

```
┌─────────────────────────┐
│ Task A                  │
│ Jan 1 - Jan 3           │ ← Duration
│ ████████████████████    │ ← Progress (optional)
└─────────────────────────┘
```

**Properties:**
- **Position:** Based on start date
- **Width:** Based on duration
- **Height:** 32dp (default), 40dp (selected)
- **Color:** Category or priority color
- **Progress:** Optional fill for completed portion
- **Label:** Task name + duration

---

### 2. Dependencies
**Essential for:** Showing task relationships

```
┌─────────────┐     ┌─────────────┐
│ Task A      │─────▶│ Task B      │
└─────────────┘     └─────────────┘
     │
     ▼
┌─────────────┐
│ Task C      │
└─────────────┘
```

**Properties:**
- **Line:** Dashed or solid
- **Arrow:** Shows direction (Task A → Task B)
- **Color:** Gray (default), Red (critical)
- **Style:** Straight lines with right-angle turns
- **Interaction:** Click to edit, drag to create

---

### 3. Milestones
**Essential for:** Marking important dates

```
    ▼
┌─────────┐
│  M1     │ ← Milestone
│ Deadline│
└─────────┘
    │
```

**Properties:**
- **Shape:** Diamond
- **Size:** 24dp x 24dp (minimum)
- **Color:** Purple (from our palette)
- **Label:** Milestone name
- **Date:** Below milestone

---

### 4. Critical Path
**Essential for:** Highlighting tasks that affect project deadline

```
┌─────────────────────────────────────────────────────────────────┐
│  Critical Path:                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │ Task A      │──▶│ Task B      │──▶│ Task C      │              │
│  │ (Red border)│  │ (Red border)│  │ (Red border)│              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
└─────────────────────────────────────────────────────────────────┘
```

**Properties:**
- **Highlight:** Red border or background
- **Calculation:** Automatically determined
- **Visibility:** Toggle on/off

---

### 5. Time Scale
**Essential for:** Navigation and orientation

```
┌─────────────────────────────────────────────────────────────────┐
│  January 2026                                                  100% │
│  ┌─────────┬─────────┬─────────┬─────────┬─────────┐            │
│  │  1     │  2     │  3     │  4     │  5     │            │
│  │ Mon   │ Tue   │ Wed   │ Thu   │ Fri   │            │
│  └─────────┴─────────┴─────────┴─────────┴─────────┘            │
└─────────────────────────────────────────────────────────────────┘
```

**Properties:**
- **Levels:** Day, Week, Month, Year
- **Format:** Adaptive based on zoom level
- **Height:** 40dp (Day), 48dp (Week), 56dp (Month)
- **Dividers:** Vertical lines for time units

---

### 6. Today Indicator
**Essential for:** Orientation in time

```
    ┌───────┐
    │ TODAY │ ← Vertical line
    └───────┘
```

**Properties:**
- **Line:** Vertical, 2dp width
- **Color:** Primary color (Orange)
- **Label:** "TODAY" or "SEГОДНЯ"
- **Position:** Current date

---

### 7. Task List (Optional)
**Essential for:** Task information at a glance

```
┌─────────────────────┬─────────────────────────────────────┐
│  Task List           │  Gantt Chart                           │
│  ━━━━━━━━━━━━━━━━━━  │  ┌───────────────────────────────┐  │
│  1. Task A           │  │ ████████████████████               │  │
│     Jan 1 - Jan 3    │  └───────────────────────────────┘  │
│  2. Task B           │  ┌───────────────────────────────┐  │
│     Jan 3 - Jan 5    │  │ ████████████████████               │  │
│  3. Milestone        │  └───────────────────────────────┘  │
│     Jan 5            │                                     │
└─────────────────────┴─────────────────────────────────────┘
```

**Properties:**
- **Width:** 25% of screen (adjustable)
- **Columns:** Task name, Start date, End date, Duration, Assignee
- **Sorting:** By start date, name, priority
- **Grouping:** By category, priority, project

---

## 💡 Our Improvements for Floktask

### 1. Simplified for Personal Use
**Problem:** Most Gantt tools are designed for teams/projects

**Solution:**
- **Focus on personal tasks** - Not projects and teams
- **Simplified interface** - Remove unnecessary complexity
- **Quick creation** - Easy to add tasks directly from Gantt
- **Integration with Floktask** - Seamless with existing features

---

### 2. Mobile-First Design
**Problem:** Most Gantt tools have poor mobile experience

**Solution:**
- **Vertical scrolling** - For time scale
- **Horizontal scrolling** - For timeline
- **Pinch zoom** - For detailed view
- **Touch-friendly** - Large touch targets (48dp minimum)
- **Adaptive layout** - Different layouts for mobile/tablet/desktop

**Mobile Layout:**
```
┌─────────────────────┐
│  Gantt              │
├─────────────────────┤
│  ┌───────────────┐ │
│  │ Time Scale    │ │
│  └───────────────┘ │
│  ┌───────────────┐ │
│  │ Task Bars     │ │ (Scrollable horizontally)
│  │ ████████      │ │
│  │ ████████      │ │
│  └───────────────┘ │
│  [← Swipe →]        │
└─────────────────────┘
```

---

### 3. Integration with Floktask Features
**Problem:** Standalone Gantt tools don't integrate with task management

**Solution:**
- **Task synchronization** - Gantt reflects existing tasks
- **Pomodoro integration** - Show Pomodoro sessions in Gantt
- **Time Blocking** - Visualize time blocks
- **Finance integration** - Show budget-related tasks
- **Habits integration** - Show habit streaks

**Example:**
```
┌─────────────────────────────────────────────────────────────────┐
│  Task A (Pomodoro: 25min x 4)                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ████████ ████████ ████████ ████████  (4 Pomodoro sessions) │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

---

### 4. Smart Suggestions
**Problem:** Manual dependency creation is tedious

**Solution:**
- **AI-powered suggestions** - "Task B should start after Task A"
- **Automatic dependencies** - Based on task relationships
- **Conflict detection** - Warn about overlapping tasks
- **Optimization** - Suggest optimal scheduling

**Example:**
```
┌─────────────────────────────────────────────────────────────────┐
│  💡 Suggestion:                                                   │
│  Task B should start after Task A completes (Jan 3)              │
│  [Accept] [Dismiss]                                              │
└─────────────────────────────────────────────────────────────────┘
```

---

### 5. Visual Clarity
**Problem:** Gantt charts can be visually overwhelming

**Solution:**
- **Color coding** - By category, priority, or project
- **Progress visualization** - Fill bars for completed portion
- **Critical path highlighting** - Red border for critical tasks
- **Milestone emphasis** - Diamond shapes for important dates
- **Dependency clarity** - Clear arrow lines

---

## 📊 Zoom Levels

### Level 1: Day View
```
┌─────────────────────────────────────────────────────────────────┐
│  January 1, 2026                                                │
│  00:00  04:00  08:00  12:00  16:00  20:00                        │
│  ┌─────┐ ┌─────┐ ┌─────┐                                       │
│  │ A   │ │ B   │ │ C   │                                       │
│  └─────┘ └─────┘ └─────┘                                       │
└─────────────────────────────────────────────────────────────────┘
```

**Scale:** 1 hour = 40dp
**Use Case:** Detailed hourly planning

---

### Level 2: Week View (Default)
```
┌─────────────────────────────────────────────────────────────────┐
│  January 1 - 7, 2026                                            │
│  Mon   Tue   Wed   Thu   Fri   Sat   Sun                         │
│  ┌─────┐ ┌─────┐ ┌─────┐                                       │
│  │ A   │ │ B   │ │ C   │                                       │
│  └─────┘ └─────┘ └─────┘                                       │
└─────────────────────────────────────────────────────────────────┘
```

**Scale:** 1 day = 100dp
**Use Case:** Weekly planning

---

### Level 3: Month View
```
┌─────────────────────────────────────────────────────────────────┐
│  January 2026                                                  │
│  1  2  3  4  5  6  7  8  9  10 11 12 13 14...                    │
│  ┌─────┐ ┌─────┐ ┌─────┐                                       │
│  │ A   │ │ B   │ │ C   │                                       │
│  └─────┘ └─────┘ └─────┘                                       │
└─────────────────────────────────────────────────────────────────┘
```

**Scale:** 1 day = 32dp
**Use Case:** Monthly overview

---

### Level 4: Year View
```
┌─────────────────────────────────────────────────────────────────┐
│  2026                                                           │
│  Jan  Feb  Mar  Apr  May  Jun  Jul  Aug  Sep  Oct  Nov  Dec       │
│  ┌─────┐ ┌─────┐ ┌─────┐                                       │
│  │ A   │ │ B   │ │ C   │                                       │
│  └─────┘ └─────┘ └─────┘                                       │
└─────────────────────────────────────────────────────────────────┘
```

**Scale:** 1 month = 60dp
**Use Case:** Long-term planning

---

## 🎯 User Experience Analysis

### Strengths of Existing Solutions ✅

1. **Visual Clarity** (Microsoft Project, ClickUp)
   - Clear task bars with colors
   - Easy to understand dependencies
   - Good progress visualization

2. **Interaction Design** (ClickUp, Asana)
   - Intuitive drag-and-drop
   - Smooth animations
   - Good feedback on actions

3. **Information Density** (Jira, Monday.com)
   - Shows a lot of information compactly
   - Good use of color coding
   - Multiple view options

4. **Customization** (ClickUp, Monday.com)
   - Customizable colors
   - Adjustable time scales
   - Flexible grouping

### Weaknesses of Existing Solutions ❌

1. **Complexity** (Microsoft Project, Jira)
   - Too many features for personal use
   - Steep learning curve
   - Overwhelming for simple tasks

2. **Mobile Experience** (All)
   - Poor mobile adaptation
   - Small touch targets
   - Hard to use on phones

3. **Integration** (Most)
   - Standalone tools
   - Don't integrate with task management
   - No Pomodoro/Time Blocking integration

4. **Performance** (Some)
   - Slow with many tasks
   - Laggy animations
   - Poor optimization

---

## 📐 Technical Specifications

### Data Model

```kotlin
// Gantt Chart data structures
data class GanttTask(
    val id: String,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val duration: Duration,  // In days
    val progress: Float,    // 0.0 to 1.0
    val categoryId: String?,
    val priority: Priority,
    val color: String,      // Category or priority color
    val isMilestone: Boolean,
    val dependencies: List<String>,  // Task IDs
    val isCritical: Boolean,
    val projectId: String?
)

data class GanttMilestone(
    val id: String,
    val title: String,
    val date: LocalDate,
    val color: String,
    val projectId: String?
)

data class GanttDependency(
    val fromTaskId: String,
    val toTaskId: String,
    val type: DependencyType,  // FINISH_TO_START, START_TO_START, etc.
    val lag: Duration?        // Optional delay
)

enum class DependencyType {
    FINISH_TO_START,  // Task A must finish before Task B starts
    START_TO_START,   // Task A must start before Task B starts
    FINISH_TO_FINISH, // Task A must finish before Task B finishes
    START_TO_FINISH   // Task A must start before Task B finishes
}

enum class ZoomLevel {
    DAY, WEEK, MONTH, YEAR
}

data class GanttViewState(
    val zoomLevel: ZoomLevel,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val selectedTaskId: String?,
    val showCriticalPath: Boolean,
    val showDependencies: Boolean,
    val showProgress: Boolean
)
```

---

### Performance Requirements

| Metric | Target | Notes |
|--------|--------|-------|
| **Initial Load** | < 500ms | For < 100 tasks |
| **Task Render** | < 16ms | Per frame |
| **Drag Latency** | < 10ms | Start delay |
| **Zoom Animation** | 250ms | Smooth |
| **Scroll FPS** | 60fps | Minimum |
| **Memory Usage** | < 150MB | For 500 tasks |

---

## ✅ Implementation Checklist

### Phase 1: Core Gantt Chart
- [ ] Time scale component
- [ ] Task bar component
- [ ] Milestone component
- [ ] Basic drag-and-drop
- [ ] Basic resize
- [ ] Week view layout

### Phase 2: Enhanced Features
- [ ] Dependencies
- [ ] Critical path calculation
- [ ] Day, Month, Year views
- [ ] Zoom functionality
- [ ] Pan functionality
- [ ] Today indicator

### Phase 3: Floktask Integration
- [ ] Task synchronization
- [ ] Pomodoro integration
- [ ] Time Blocking support
- [ ] Category colors
- [ ] Priority indicators

### Phase 4: Advanced Features
- [ ] Smart suggestions
- [ ] Conflict detection
- [ ] Optimization
- [ ] Export/Import
- [ ] Print support

---

## 🎨 Our Gantt Chart Design

### Visual Design

```yaml
# Our Gantt Chart Color Tokens
gantt:
  bar:
    default: "#4CAF50"
    hover: "#43A047"
    selected: "#388E3C"
    completed: "#8BC34A"
    
  progress:
    fill: "#8BC34A"
    background: "#E8F5E8"
    
  dependency:
    line: "#9E9E9E"
    arrow: "#F44336"
    
  milestone:
    background: "#9C27B0"
    border: "#FFFFFF"
    text: "#FFFFFF"
    
  critical_path:
    background: "#FFEBEE"
    border: "#F44336"
    
  time_scale:
    background: "#FAFAFA"
    text: "#424242"
    divider: "#E0E0E0"
    
  today:
    background: "#FFF3E0"
    border: "#FF9800"
    
  grid:
    line: "#E0E0E0"
    major_line: "#BDBDBD"
```

### Layout

```
┌─────────────────────────────────────────────────────────────────┐
│  GANTT CHART                    [+] [Week] [Month] [Year] [Today] │
├─────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  TIME SCALE: January 1 - 31, 2026                              ││
│  │  ┌─────────┬─────────┬─────────┬─────────┬─────────┐       ││
│  │  │  1     │  2     │  3     │  4     │  5     │       ││
│  │  │ Mon   │ Tue   │ Wed   │ Thu   │ Fri   │       ││
│  │  └─────────┴─────────┴─────────┴─────────┴─────────┘       ││
│  ├─────────────────────────────────────────────────────────────┤│
│  │                                                                 ││
│  │  ┌─────────┬─────────────────────────────────────────────┐ ││
│  │  │         │                                                 │ ││
│  │  │  TASK   │  ┌─────────────┐  ┌─────────────┐              │ ││
│  │  │  LIST   │  │ Task A      │──▶│ Task B      │              │ ││
│  │  │  (25%)  │  │ Jan 1-3     │  │ Jan 3-5     │              │ ││
│  │  │         │  │ ████████    │  │ ████████    │              │ ││
│  │  │         │  └─────────────┘  └─────────────┘              │ ││
│  │  │         │       ↓ (Today Line - Orange)                   │ ││
│  │  │         │  ┌─────────────────────────────────────────┐  │ ││
│  │  │         │  │  💎 Milestone: Project Deadline         │  │ ││
│  │  │         │  │  Jan 5                                        │  │ ││
│  │  │         │  └─────────────────────────────────────────┘  │ ││
│  │  │         │                                                     │ ││
│  │  └─────────┴─────────────────────────────────────────────┘ ││
│  │                                                                 ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                     │
│  [+ Add Task]  [Critical Path: ON]  [Dependencies: ON]             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📁 Related Files

- [Design System Tokens](../../design-system/tokens/)
- [Component Library](../../design-system/components/)
- [Responsive Guidelines](../../design-system/guidelines/responsive.md)
- [Accessibility Guidelines](../../design-system/guidelines/accessibility.md)
- [Timeline Design Spec](../output/design-specs/timeline-spec.md)

---

## 🎯 Next Steps

1. **Create wireframes** for our Gantt Chart
2. **Design high-fidelity mockups**
3. **Build interactive prototype**
4. **Write design specification**
5. **Collaborate with Frontend** on implementation

---

*Last updated: 2026-09-05*
*Related to: DE-004 Gantt Charts UI*
