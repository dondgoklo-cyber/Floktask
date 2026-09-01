# Architect Task Template

## 🏗️ Architect Task

### Basic Info
- **Task ID:** [e.g., AR-001]
- **Title:** [e.g., Timeline View API Design]
- **Priority:** [P0/P1/P2]
- **Assigned to:** @architect
- **Requested by:** [e.g., @designer]
- **Due Date:** [YYYY-MM-DD]

### Description
[Detailed description of the architecture task]

### Requirements
- [ ] Requirement 1
- [ ] Requirement 2
- [ ] Requirement 3

### Dependencies
- [ ] Design specifications from @designer
- [ ] Backend requirements from @backend
- [ ] Frontend capabilities from @frontend

### API Contract
```kotlin
// Example API interface
data class TimelineRequest(
    val dateRange: DateRange,
    val zoomLevel: ZoomLevel
)

data class TimelineResponse(
    val tasks: List<TimelineTask>,
    val timeBlocks: List<TimeBlock>,
    val currentTime: Instant
)
```

### Technical Considerations
- **Clean Architecture Layer:** [Domain/Data/Presentation]
- **Dependencies:** [List of dependencies]
- **Interfaces:** [List of interfaces]
- **Error Handling:** [Error scenarios]
- **Testing:** [Unit test requirements]

### Acceptance Criteria
- [ ] API contract documented
- [ ] Interface definitions complete
- [ ] Error handling specified
- [ ] Integration points defined
- [ ] Documentation updated

### Related Documents
- **Design Spec:** [Link to design specification]
- **PRD:** [Link to product requirements]
- **Backend Spec:** [Link to backend specification]

---

*Use this template for all architecture-related issues*
