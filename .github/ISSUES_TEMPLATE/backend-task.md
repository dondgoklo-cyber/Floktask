# Backend Task Template

## 🗃️ Backend Task

### Basic Info
- **Task ID:** [e.g., BE-001]
- **Title:** [e.g., Sync Engine Implementation]
- **Priority:** [P0/P1/P2]
- **Assigned to:** @backend
- **Requested by:** [e.g., @architect]
- **Due Date:** [YYYY-MM-DD]

### Description
[Detailed description of the backend task]

### Requirements
- [ ] Requirement 1
- [ ] Requirement 2
- [ ] Requirement 3

### Dependencies
- [ ] API contracts from @architect
- [ ] Database schema from @architect
- [ ] Third-party services [if applicable]

### Technical Details
- **Database:** [Room/Retrofit/etc.]
- **API Endpoints:** [List of endpoints]
- **Data Models:** [List of data classes]
- **Sync Strategy:** [Offline-first, periodic, etc.]
- **Error Handling:** [Error scenarios]

### API Endpoints
```kotlin
// Example endpoints
@POST("api/tasks/sync")
suspend fun syncTasks(@Body request: SyncRequest): SyncResponse

@GET("api/tasks/timeline")
suspend fun getTimelineTasks(
    @Query("startDate") startDate: String,
    @Query("endDate") endDate: String
): List<TimelineTask>
```

### Database Schema
```kotlin
// Example Room entities
@Entity(tableName = "timeline_tasks")
data class TimelineTaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val startTime: Instant,
    val endTime: Instant,
    val categoryId: String?,
    val priority: Priority
)
```

### Acceptance Criteria
- [ ] API endpoints implemented
- [ ] Database schema defined
- [ ] Sync logic implemented
- [ ] Error handling complete
- [ ] Unit tests pass
- [ ] Integration tests pass

### Testing Requirements
- [ ] Test with empty database
- [ ] Test with existing data
- [ ] Test conflict resolution
- [ ] Test offline scenarios
- [ ] Test sync recovery

---

*Use this template for all backend-related issues*
